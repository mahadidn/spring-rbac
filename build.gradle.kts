plugins {
	kotlin("jvm") version "2.4.21"
	kotlin("plugin.spring") version "2.4.21"
	id("org.springframework.boot") version "4.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.asciidoctor.jvm.convert") version "4.0.3"

	// Plugin RESMI jOOQ untuk code generation.
	// Versinya harus sama dengan versi jOOQ yang dibawa Spring Boot
	// (cek di External Libraries: org.jooq:jooq:x.y.z).
	id("org.jooq.jooq-codegen-gradle") version "3.21.5"

	jacoco
}

group = "com.test"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// ===== Web & validasi =====
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// ===== Database: jOOQ + PostgreSQL =====
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	runtimeOnly("org.postgresql:postgresql")

	// Driver PostgreSQL KHUSUS untuk proses codegen (dipakai saat build, bukan saat aplikasi jalan)
	jooqCodegen("org.postgresql:postgresql")

	// ===== Security + JWT (bawaan Spring Security, tanpa jjwt) =====
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-security-oauth2-resource-server")

	// ===== Cache lokal =====
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("com.github.ben-manes.caffeine:caffeine")

	// ===== Kotlin & JSON (Jackson 3) =====
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")

	// ===== Dokumentasi API (Scalar) =====
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.1.1")

	// ===== Rate limiter (opsional — dipakai nanti untuk endpoint login) =====
	implementation("com.bucket4j:bucket4j-core:8.10.0")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// ===== Test =====
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.springframework.boot:spring-boot-starter-jooq-test")
	testImplementation("org.springframework.boot:spring-boot-starter-security-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll(
			"-Xjsr305=strict",
			"-Xannotation-default-target=param-property",
		)
	}
}

// ===== jOOQ code generation: baca tabel LANGSUNG dari database PostgreSQL =====
// Syarat: PostgreSQL menyala dan tabel sudah dibuat sebelum menjalankan jooqCodegen.
jooq {
	configuration {
		jdbc {
			driver = "org.postgresql.Driver"
			url = "jdbc:postgresql://localhost:5433/portal"
			user = "postgres"
			password = "postgres"
		}
		generator {
			database {
				name = "org.jooq.meta.postgres.PostgresDatabase"
				inputSchema = "public"
			}
			target {
				// Class hasil generate jOOQ hanya boleh dipakai di layer infrastructure
				packageName = "com.test.mdn.infrastructure.persistence.jooq.generated"
			}
		}
	}
}

// Pastikan class jOOQ sudah di-generate sebelum Kotlin di-compile
tasks.named("compileKotlin") {
	dependsOn(tasks.named("jooqCodegen"))
}

val snippetsDir = layout.buildDirectory.dir("generated-snippets")

tasks.withType<Test> {
	useJUnitPlatform()
	outputs.dir(snippetsDir)
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required = true
		html.required = true
	}
	// Hanya class Kotlin buatan sendiri; class Java hasil generate jOOQ otomatis tidak ikut dihitung
	classDirectories.setFrom(layout.buildDirectory.dir("classes/kotlin/main"))
}

tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
	inputs.dir(snippetsDir)
	dependsOn(tasks.test)
	sourceDir(file("src/docs/asciidoc"))
	attributes(mapOf("snippets" to snippetsDir.get().asFile.absolutePath))
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	workingDir = projectDir
}

fun gitInfo(envVar: String, vararg gitArgs: String): String =
	System.getenv(envVar)?.takeIf { it.isNotBlank() }
		?: try {
			providers.exec { commandLine("git", *gitArgs) }.standardOutput.asText.get().trim()
		} catch (e: Exception) {
			"unknown"
		}

springBoot {
	buildInfo {
		properties {
			additional.set(
				mapOf(
					"commit" to gitInfo("CI_COMMIT_SHORT_SHA", "rev-parse", "--short", "HEAD"),
					"branch" to gitInfo("CI_COMMIT_REF_NAME", "rev-parse", "--abbrev-ref", "HEAD"),
				)
			)
		}
	}
}
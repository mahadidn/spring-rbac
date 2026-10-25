package com.test.mdn.infrastructure.persistence.config

import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import javax.sql.DataSource

@Configuration
class InventoryDatabaseConfig {

    @Bean(defaultCandidate = false)
    @Qualifier("inventory")
    @ConfigurationProperties("app.datasource.inventory")
    fun inventoryDataSource(): HikariDataSource {
        return DataSourceBuilder.create()
            .type(HikariDataSource::class.java)
            .build()
    }

    @Bean(defaultCandidate = false)
    @Qualifier("inventory")
    fun inventoryTransactionManager(
        @Qualifier("inventory") inventoryDataSource: DataSource,
    ): DataSourceTransactionManager {
        return DataSourceTransactionManager(inventoryDataSource)
    }

    @Bean(defaultCandidate = false)
    @Qualifier("inventory")
    fun inventoryDslContext(
        @Qualifier("inventory") inventoryDataSource: DataSource,
    ): DSLContext {
        return DSL.using(
            TransactionAwareDataSourceProxy(inventoryDataSource),
            SQLDialect.POSTGRES,
        )
    }
}
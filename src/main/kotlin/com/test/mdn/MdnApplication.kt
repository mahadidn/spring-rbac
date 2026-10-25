package com.test.mdn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MdnApplication

fun main(args: Array<String>) {
	runApplication<MdnApplication>(*args)
}

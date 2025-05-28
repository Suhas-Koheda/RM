package dev.haas.rm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RmApplication

fun main(args: Array<String>) {
	runApplication<RmApplication>(*args)
}

package com.vito.sanel

import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.LocalDateTime

fun main() {
    LocalDateTime.now().also {
        println("Starting at: $it")
        runBlocking {
            BrianLuke().generateBoardAndPrint()
        }
        println("Finished in: " + Duration.between(it, LocalDateTime.now()).toMillis() + " milliseconds")
    }
}

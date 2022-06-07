package com.vito.sanel

import java.time.Duration
import java.time.LocalDateTime

fun main() {
    LocalDateTime.now().also {
        println("Starting at: $it")
        BrianLuke().generateBoardAndPrint()
        println("Finished in: " + Duration.between(it, LocalDateTime.now()).toMillis() + " milliseconds")
    }
}

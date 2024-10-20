package com.vito.sanel

import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun main() {
    measureTimeMillis {
        runBlocking {
            BrianLuke().generateBoardAndPrintSolution()
        }
    }.also {
        println("Time measure for generateBoardAndPrint is: ${TimeUnit.MILLISECONDS.toMillis(it)} milliseconds")
    }
}

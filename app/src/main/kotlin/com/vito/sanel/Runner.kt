package com.vito.sanel

import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

fun main() {
    measureTimeMillis {
        BrianLuke().generateBoardAndPrintSolution()
    }.also {
        println("Time measure for generateBoardAndPrint is: ${TimeUnit.MILLISECONDS.toMillis(it)} milliseconds")
    }
}

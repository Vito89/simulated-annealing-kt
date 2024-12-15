package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_SIZE = 100

class BrianLuke {

    /**
     * @method generateBoardAndPrintSolution - calculate and print solution according input, consists common logic of
     * Bryan Luke algorithm include external cycle and some delta depend on accepting new solutionXtoY etc.
     * @param boardSize length of solutionXtoY as QueenBoard length
     */
    fun generateBoardAndPrintSolution(boardSize: Int = DEFAULT_MAX_BOARD_SIZE) {
        var current = QueenBoard.randomInit(size = boardSize)
        var best = current

        for (temperature in temperatures) {
            val new = current.randomSwapQueens()
            val acceptNewScore = exp((current.score - new.score) / temperature) >= Random.nextFloat()
            if (acceptNewScore) {
                current = new
                if (current.score < best.score) {
                    println("Moving best, new score: ${new.score}, temperature is: $temperature")
                    best = current
                    if (best.score == 0) {
                        println(best)
                        return
                    }
                }
            }
        }

        println("Warn: no any solution has found for board size: $boardSize")
    }

    private val temperatures = sequence {
        var t = 0.7 // initial temperature
        while (t > 0.02) { // final temperature
            repeat(400) { // steps per change
                yield(t)
            }
            t *= 0.99 // alpha coefficient
        }
    }

    private val QueenBoard.score get() = this.threatingQueenCount
}

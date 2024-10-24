package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_SIZE = 1000

class BrianLuke {

    /**
     * @method generateBoardAndPrintSolution - calculate and print solution according input, consists common logic of
     * Bryan Luke algorithm include external cycle and some delta depend on accepting new solutionXtoY etc.
     * @param boardSize length of solutionXtoY as QueenBoard length
     */
    fun generateBoardAndPrintSolution(boardSize: Int = DEFAULT_MAX_BOARD_SIZE) {
        var current = QueenBoard.randomInit(size = boardSize)
        var best: QueenBoard? = null

        for (temperature in temperatures) {
            val new = current.randomSwapQueens()

            val shouldTolerateNewEnergy = exp((current.energy - new.energy) / temperature) > Random.nextFloat()

            if (new.energy <= current.energy || shouldTolerateNewEnergy) {
                current = new
                if (current.energy < best.energy) {
                    println("Moving best, new energy: ${new.energy}, temperature is: $temperature")
                    best = current
                    if (best.energy == 0) {
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

    private val QueenBoard?.energy get() = this?.conflictQueensOnDiagonals ?: Int.MAX_VALUE
}

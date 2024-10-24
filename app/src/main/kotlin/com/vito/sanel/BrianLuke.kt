package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_SIZE = 2000
const val INITIAL_TEMPERATURE = 0.4
const val FINAL_TEMPERATURE = 0.27
const val ALPHA = 0.98
const val STEPS_PER_CHANGE = 400

class BrianLuke {

    /**
     * @method generateBoardAndPrintSolution - calculate and print solution according input, consists common logic of
     * Bryan Luke algorithm include external cycle and some delta depend on accepting new solutionXtoY etc.
     * @param boardSize length of solutionXtoY as QueenBoard length
     */
    fun generateBoardAndPrintSolutionOld(boardSize: Int = DEFAULT_MAX_BOARD_SIZE) {
        var anySolutionFound = false
        var currentQueenBoardSolution = QueenBoard.randomInit(size = boardSize)

        var newBoardSolution = currentQueenBoardSolution.clone()
        var bestQueenBoardSolution = QueenBoard.randomInit(size = boardSize)
        var bestTemperature = -1.0
        var currentTemperature = INITIAL_TEMPERATURE

        // this parameter doesn't depend on algorithm but interested in statistics and will be printed to log
        var acceptedByToleranceCount = 0

        while ((currentTemperature > FINAL_TEMPERATURE) && (bestQueenBoardSolution.energy != 0)) {
            println("Current temperature is: $currentTemperature")

            repeat(STEPS_PER_CHANGE) {
                var mustUseNewBoardSolution = false
                newBoardSolution = newBoardSolution.randomSwapQueens()

                if (newBoardSolution.energy <= currentQueenBoardSolution.energy) {
                    mustUseNewBoardSolution = true
                } else {
                    val randomFloat = Random.nextFloat()
                    val delta = newBoardSolution.energy - currentQueenBoardSolution.energy
                    if (exp(-delta / currentTemperature) > randomFloat) {
                        mustUseNewBoardSolution = true
                        acceptedByToleranceCount++
                    }
                }

                if (mustUseNewBoardSolution) {
                    currentQueenBoardSolution = newBoardSolution.clone()
                    if (currentQueenBoardSolution.energy < bestQueenBoardSolution.energy) {
                        println("Moving best, new energy: ${currentQueenBoardSolution.energy}, temperature is: $currentTemperature")
                        bestQueenBoardSolution = currentQueenBoardSolution.clone()
                        bestTemperature = currentTemperature
                        anySolutionFound = true
                    }
                } else {
                    newBoardSolution = currentQueenBoardSolution.clone()
                }
            }
            currentTemperature *= ALPHA
        }
        if (anySolutionFound) {
            println("Founded solution has bestTemperature: $bestTemperature, acceptedByToleranceCount: $acceptedByToleranceCount")
            println(bestQueenBoardSolution)
        } else {
            println("Warn: no any solution has found for board size: $boardSize")
        }
    }

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
        var t = 0.4 // initial temperature
        while (t > 0.27) { // final temperature
            repeat(400) { // steps per change
                yield(t)
            }
            t *= 0.98 // alpha coefficient
        }
    }

    private val QueenBoard?.energy get() = this?.conflictQueensOnDiagonals ?: Int.MAX_VALUE
}

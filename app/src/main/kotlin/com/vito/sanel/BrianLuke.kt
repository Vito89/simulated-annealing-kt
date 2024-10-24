package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_SIZE = 50
const val INITIAL_TEMPERATURE = 0.4
const val FINAL_TEMPERATURE = 0.27
const val ALPHA = 0.98
const val STEPS_PER_CHANGE = 400
//const val DEFAULT_ENERGY = 100

class BrianLuke {

    /**
     * @method generateBoardAndPrintSolution - calculate and print solution according input, consists common logic of
     * Bryan Luke algorithm include external cycle and some delta depend on accepting new solutionXtoY etc.
     * @param boardSize length of solutionXtoY as QueenBoard length
     */
    fun generateBoardAndPrintSolution(boardSize: Int = DEFAULT_MAX_BOARD_SIZE) {
        var anySolutionFound = false
        var currentQueenBoardSolution = QueenBoard.randomInit(size = boardSize)
//            .apply {
//            computeAndSetEnergy()
//        }

        var newBoardSolution = currentQueenBoardSolution.clone()
        var bestQueenBoardSolution = QueenBoard(solutionXtoY = intArrayOf(boardSize))
        var bestTemperature = -1.0
        var currentTemperature = INITIAL_TEMPERATURE

        // this parameter doesn't depend on algorithm but interested in statistics and will be printed to log
        var acceptedByToleranceCount = 0

        while ((currentTemperature > FINAL_TEMPERATURE) && (bestQueenBoardSolution.conflictQueensOnDiagonals != 0)) {
            println("Current temperature is: $currentTemperature")

            repeat(STEPS_PER_CHANGE) {
                var mustUseNewBoardSolution = false
                newBoardSolution = newBoardSolution.apply {
                    randomSwapQueens()
//                    computeAndSetEnergy()
                }

                if (newBoardSolution.conflictQueensOnDiagonals <= currentQueenBoardSolution.conflictQueensOnDiagonals) {
                    mustUseNewBoardSolution = true
                } else {
                    val randomFloat = Random.nextFloat()
                    val delta = newBoardSolution.conflictQueensOnDiagonals - currentQueenBoardSolution.conflictQueensOnDiagonals
                    if (exp(-delta / currentTemperature) > randomFloat) {
                        mustUseNewBoardSolution = true
                        acceptedByToleranceCount++
                    }
                }

                if (mustUseNewBoardSolution) {
                    currentQueenBoardSolution = newBoardSolution.clone()
                    if (currentQueenBoardSolution.conflictQueensOnDiagonals < bestQueenBoardSolution.conflictQueensOnDiagonals) {
                        println("Moving best solution, new energy: ${currentQueenBoardSolution.conflictQueensOnDiagonals}")
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
}

package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_SIZE = 50
const val INITIAL_TEMPERATURE = 0.4
const val FINAL_TEMPERATURE = 0.27
const val ALPHA = 0.98
const val STEPS_PER_CHANGE = 400
const val DEFAULT_ENERGY = 100

/**
 * @method generateBoardAndPrintSolution - calculate and print solution according input, consists common logic of
 * Bryan Luke algorithm include external cycle and some delta depend on accepting new solution etc.
 * @param boardLength length of solution as board length
 *
 * @method tryTweakAndCompute - tweak current board solution & compute energy after to set it
 *
 * @method tweakWork - router method. Depend on PARALLEL_MODE_IS_ON conf processing may work in parallel mode
 *
 * @method doTweakWork - wrapper, concurrent modification based on Kotlin Coroutines witch use tryTweakAndCompute
 */

class BrianLuke {

    fun generateBoardAndPrintSolution(boardSize: Int = DEFAULT_MAX_BOARD_SIZE) {
        var anySolutionFound = false
        var currentQueenBoardSolution = QueenBoard(solutionXtoY = IntArray(boardSize), energy = DEFAULT_ENERGY).apply {
            initSolution()
            computeAndSetEnergy()
        }

        var newBoardSolution = currentQueenBoardSolution.clone()
        var bestQueenBoardSolution = QueenBoard(solutionXtoY = intArrayOf(boardSize), energy = DEFAULT_ENERGY)
        var bestTemperature = -1.0
        var currentTemperature = INITIAL_TEMPERATURE

        // this parameter doesn't depend on algorithm but interested in statistics and will be printed to log
        var acceptedByToleranceCount = 0

        while ((currentTemperature > FINAL_TEMPERATURE) && (bestQueenBoardSolution.energy != 0)) {
            println("Current temperature is: $currentTemperature")

            repeat(STEPS_PER_CHANGE) {
                var mustUseNewBoardSolution = false
                newBoardSolution = tweakWork(workSolution = newBoardSolution)

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
                        println("Moving best solution, new energy: ${currentQueenBoardSolution.energy}")
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
            println("Info: bestTemperature: $bestTemperature, acceptedByToleranceCount: $acceptedByToleranceCount")
            println(bestQueenBoardSolution)
        } else {
            println("Warn: no any solution has found for board size: $boardSize")
        }
    }

    private fun tweakWork(workSolution: QueenBoard): QueenBoard =
            workSolution.apply {
                tweakSolution()
                computeAndSetEnergy()
            }
}

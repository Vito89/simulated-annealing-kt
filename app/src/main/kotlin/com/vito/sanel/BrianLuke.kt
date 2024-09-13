package com.vito.sanel

import com.vito.sanel.models.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.random.Random

const val DEFAULT_MAX_BOARD_LENGTH = 50
const val INITIAL_TEMPERATURE = 0.4
const val FINAL_TEMPERATURE = 0.27
const val ALPHA = 0.98
const val STEPS_PER_CHANGE = 400
const val DEFAULT_ENERGY = 100F

const val PARALLEL_MODE_IS_ON = false
const val THREAD_COUNT = 5

/**
 * generateBoardAndPrintSolution - calculate and print solution according input argument, the common logic of Bryan
 * Luke algorithm is here include external cycle, logic with delta depend on accepting new solution etc.
 * params: @boardLength length of solution as board length
 *
 * tryTweakAndCompute - tweak current board solution & compute energy after to set it
 *
 * tweakWork - this wrapper router method. Depend on PARALLEL_MODE_IS_ON configuration the processing may work in
 * parallel mode
 *
 * doTweakWork - concurrent modification for tryTweakAndCompute based on Kotlin Coroutines
 */

class BrianLuke {

    suspend fun generateBoardAndPrintSolution(boardLength: Int = DEFAULT_MAX_BOARD_LENGTH) {
        var anySolutionFound = false
        var currentBoardSolution = Board(solution = IntArray(boardLength), energy = DEFAULT_ENERGY)
        currentBoardSolution.initSolution()
        currentBoardSolution.computeAndSetEnergy()

        var newBoardSolution = currentBoardSolution.clone()
        var bestBoardSolution = Board(solution = intArrayOf(boardLength), energy = DEFAULT_ENERGY)
        var bestTemperature = -1.0
        var acceptedByToleranceTimes = 0
        var currentTemperature = INITIAL_TEMPERATURE

        while ((currentTemperature > FINAL_TEMPERATURE) && (bestBoardSolution.energy != 0F)) {
            println("Current temperature is: $currentTemperature")

            repeat(STEPS_PER_CHANGE) {
                var mustUseNewBoardSolution = false
                newBoardSolution = tweakWork(workSolution = newBoardSolution)

                if (newBoardSolution.energy <= currentBoardSolution.energy) {
                    mustUseNewBoardSolution = true
                } else {
                    val randomFloat = Random.nextFloat()
                    val delta = newBoardSolution.energy - currentBoardSolution.energy
                    if (exp(-delta / currentTemperature) > randomFloat) {
                        mustUseNewBoardSolution = true
                        acceptedByToleranceTimes++
                    }
                }

                if (mustUseNewBoardSolution) {
                    currentBoardSolution = newBoardSolution.clone()
                    if (currentBoardSolution.energy < bestBoardSolution.energy) {
                        println("Moving best solution, new energy: ${currentBoardSolution.energy}")
                        bestBoardSolution = currentBoardSolution.clone()
                        bestTemperature = currentTemperature
                        anySolutionFound = true
                    }
                } else {
                    newBoardSolution = currentBoardSolution.clone()
                }
            }
            currentTemperature *= ALPHA
        }
        if (anySolutionFound) {
            bestBoardSolution.printPrettySolution(
                temperature = bestTemperature,
                acceptedCount = acceptedByToleranceTimes,
            )
        } else {
            println("Warn: no any solution found for board with size: $boardLength") // unreachable code =)
        }
    }

    private suspend fun tweakWork(workSolution: Board): Board =
        if (PARALLEL_MODE_IS_ON) {
            doTweakWork(List(THREAD_COUNT) { workSolution.clone() })
        } else {
            workSolution.apply {
                tweakSolution()
                computeAndSetEnergy()
            }
        }

    private suspend fun doTweakWork(workSolutions: Collection<Board>): Board = withContext(Dispatchers.Default) {
        val tweakedBoards = workSolutions.map { async { return@async tryTweakAndCompute(it) } }.awaitAll()
        val minEnergy = tweakedBoards.minOf { it.energy }

        return@withContext tweakedBoards.first { it.energy == minEnergy }
    }

    private suspend fun tryTweakAndCompute(board: Board): Board =
        try {
            withContext(Dispatchers.Default) {
                board.apply {
                    tweakSolution()
                    computeAndSetEnergy()
                }
            }
        } catch (e: Exception) {
            println("TryTweakAndCompute error handled: ${e.message}")
            throw IllegalStateException("TryTweakAndCompute error handled: ${e.message}")
        }
}

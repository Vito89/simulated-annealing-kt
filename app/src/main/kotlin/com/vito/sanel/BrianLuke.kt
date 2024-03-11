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
// 6June: 2473 2137 2382 2391 2274 ms
// 7June AM: 1200-1300 ms (min 950) (after optimization)
// 7June via gradle: 862 1K 746 844 816 ms
// 8June with parallel mode: 1855 1979 1876 1946 ms
// 9June without parallel mode but improved conf: 189 - 376 ms
// 21Feb without parallel mode: 103ms (with parallel mode 605ms)

class BrianLuke {

    suspend fun generateBoardAndPrint(boardLength: Int = DEFAULT_MAX_BOARD_LENGTH) {
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
                newBoardSolution = forkAndWork(workSolution = newBoardSolution)

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
                acceptedCount = acceptedByToleranceTimes
            )
        } else {
            println("Warn: no any solution found for board with size: $boardLength") // unreachable code =)
        }
    }

    private suspend fun forkAndWork(workSolution: Board): Board =
        if (PARALLEL_MODE_IS_ON) {
            doWork(List(THREAD_COUNT) { workSolution.clone() })
        } else {
            workSolution.apply {
                tweakSolution()
                computeAndSetEnergy()
            }
        }

    private suspend fun doWork(workSolutions: Collection<Board>): Board {
        return withContext(Dispatchers.Default) {
            val tweakedBoards = workSolutions.map { async { return@async tryTweakAndCompute(it) } }.awaitAll()
            val minOldEnergy = workSolutions.minOf { it.energy }
            val minEnergy = tweakedBoards.minOf { it.energy }
            if (minOldEnergy != minEnergy) {
                with ("Error handled: minOldEnergy $minOldEnergy differentThan minEnergy $minEnergy") {
                    println(this)
                    throw IllegalStateException(this)
                }
            }
            return@withContext tweakedBoards.first { it.energy == minEnergy }
        }
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
            println("Error handled: ${e.message}")
            throw IllegalStateException("Error handled: ${e.message}")
        }
}

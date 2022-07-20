package com.vito.sanel

import com.vito.sanel.models.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.random.Random

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

class BrianLuke {

    suspend fun generateBoardAndPrint() {
        var temperature = INITIAL_TEMPERATURE
        var solutionFound = false
        var current = Board().also {
            it.initSolution()
            it.computeAndSetEnergy()
        }
        var working = current.clone()
        var best = Board(solution = intArrayOf(), energy = DEFAULT_ENERGY)
        var bestTemperature = -1.0
        var acceptedByToleranceTimes = 0

        while (temperature > FINAL_TEMPERATURE) {
            println("Current temperature is: $temperature")

            repeat(STEPS_PER_CHANGE) {
                var useNew = false
                working = forkAndWork(working)

                if (working.energy <= current.energy) {
                    useNew = true
                } else {
                    val randomFloat = Random.nextFloat()
                    val delta = working.energy - current.energy
                    if (exp(-delta / temperature) > randomFloat) {
                        useNew = true
                        acceptedByToleranceTimes++
                    }
                }

                if (useNew) {
                    current = working.clone()
                    if (current.energy < best.energy) {
                        println("Moving best solution with new energy: ${current.energy}")
                        best = current.clone()
                        bestTemperature = temperature
                        solutionFound = true
                    }
                } else {
                    working = current.clone()
                }
            }
            temperature *= ALPHA
        }
        if (solutionFound) {
            best.printPrettySolution(bestTemperature, acceptedByToleranceTimes)
        }
    }

    private suspend fun forkAndWork(working: Board): Board =
        if (PARALLEL_MODE_IS_ON) {
            doWork(List(THREAD_COUNT) { working.clone() }).first()
        } else {
            working.apply {
                tweakSolution()
                computeAndSetEnergy()
            }
        }

    private suspend fun doWork(workings: Collection<Board>): List<Board> {
        return withContext(Dispatchers.Default) {
            workings.map { async { return@async tryTweakAndCompute(it) } }
                .awaitAll().also { boards ->
                    boards.minOf { it.energy }.also { minEnergy ->
                        return@withContext boards.filter { it.energy == minEnergy }
                    }
                }
        }
    }

    private suspend fun tryTweakAndCompute(working: Board): Board =
        try {
            withContext(Dispatchers.Default) {
                working.apply {
                    tweakSolution()
                    computeAndSetEnergy()
                }
            }
        } catch (e: Exception) {
            println("Error handled: ${e.message}")
            Board(solution = intArrayOf(), energy = DEFAULT_ENERGY)
        }
}

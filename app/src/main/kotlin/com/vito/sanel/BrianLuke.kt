package com.vito.sanel

import com.vito.sanel.models.Board
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlin.math.exp
import kotlin.random.Random
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

const val INITIAL_TEMPERATURE = 30.0
const val FINAL_TEMPERATURE = 0.4 // 0.5 for board size = 30
const val ALPHA = 0.90
const val STEPS_PER_CHANGE = 200 // 200 for board size = 30
// Finished in: 6June: 2473 2137 2382 2391 2274
// 7June AM: 1200-1300 (950)
// 7June via gradle: 862 1K 746 844 816
//

class BrianLuke {

    suspend fun generateBoardAndPrint() {
        var temperature = INITIAL_TEMPERATURE
        var solutionFound = false
        var current = Board().also {
            it.initSolution()
            it.computeAndSetEnergy()
        }
        var working = current.clone()
        var best = Board(solution = intArrayOf(), energy = 100F)

        while (temperature > FINAL_TEMPERATURE) {
            println("Current temperature is: $temperature")

            repeat(STEPS_PER_CHANGE) {
                var useNew = false

                working = forkAndWork(working)

                if (working.energy <= current.energy) {
                    useNew = true
                } else {
                    val randomFloat = Random.nextFloat() // warn about how much it will need
                    val delta = working.energy - current.energy
                    if (exp(-delta / temperature) > randomFloat) {
                        useNew = true
                    }
                }

                if (useNew) {
                    current = working.clone()
                    if (current.energy < best.energy) {
                        println("Moving best solution with new energy: ${current.energy}")
                        best = current.clone()
                        solutionFound = true
                    }
                } else {
                    working = current.clone()
                }
            }
            temperature *= ALPHA
        }
        if (solutionFound) {
            best.printPrettySolution()
        }
    }

    private suspend fun forkAndWork(working: Board): Board {
        return doWork(List(5) { working.clone() }).first()
    }

    private suspend fun doWork(workings: Collection<Board>): List<Board> {
        return withContext(Dispatchers.Default) {
            workings.map { async { return@async tryTweakAndCompute(it) } }
                .awaitAll().also { jbs ->
                    jbs.minOf { it.energy }.also { minEnergy ->
                        return@withContext jbs.filter { it.energy == minEnergy }
                    }
                }
        }
    }

    private suspend fun tryTweakAndCompute(working: Board): Board =
        try {
            coroutineScope {
                withContext(Dispatchers.Default) {
                    working.tweakSolution()
                    working.computeAndSetEnergy()
                }
                return@coroutineScope working
            }
        } catch (e: Exception) {
            println("Error handled: ${e.message}")
            Board(solution = intArrayOf(), energy = 100F)
        }
}

package com.vito.sanel

import com.vito.sanel.models.MemberType
import kotlin.math.exp
import kotlin.random.Random

const val INITIAL_TEMPERATURE = 30.0
const val FINAL_TEMPERATURE = 0.3 // 0.5 for board size = 30
const val ALPHA = 0.98
const val STEPS_PER_CHANGE = 600 // 200 for board size = 30

class BoardGenerator {

    fun generateAndPrint() {
        var temperature = INITIAL_TEMPERATURE
        var solutionFound = false
        var current = MemberType().also {
            it.initSolution()
            it.computeEnergy()
        }
        var working = current.clone()
        var best = MemberType(solution = intArrayOf(), energy = 100F)

        while (temperature > FINAL_TEMPERATURE) {
            println("Current temperature is: $temperature")

            repeat(STEPS_PER_CHANGE) {
                var useNew = false
                working.tweakSolution()
                working.computeEnergy()

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
}

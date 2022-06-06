package com.vito.sanel

import com.vito.sanel.models.MemberType
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.exp
import kotlin.random.Random

fun main() {
    val start = LocalDateTime.now()
    println("Starting at: $start")
    Runner().generateBoard()
    println("Finished in: " + Duration.between(start, LocalDateTime.now()).seconds + " sec")
}

class Runner {

    fun generateBoard() {
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

package com.vito.sanel

import com.vito.sanel.models.Board
import kotlin.random.Random

fun Board.tweakSolution() {
    val x = Random.nextInt(0, solutionSize)
    var y = Random.nextInt(0, solutionSize)
    while (x == y) {
        y = Random.nextInt(0, solutionSize)
    }
    solution[x] = solution[y].also { solution[y] = solution[x] }
}

fun Board.initDiagonalSolution() {
    (0 until solutionSize).forEach { solution[it] = it }
}

fun Board.initSolution() {
    initDiagonalSolution()
    repeat(solutionSize) { tweakSolution() }
}

fun Board.computeAndSetEnergy() {
    this.energy = computeEnergy()
}

fun Board.computeEnergy() = (0 until solutionSize).sumOf { x ->
    (0..3).sumOf { dIdx -> // NW then SE etc
        computeConflictsOnDiagonal(x, dIdx)
    }
}.toFloat()

val dx = intArrayOf(-1, 1, -1, 1)
val dy = intArrayOf(-1, 1, 1, -1)
private fun Board.computeConflictsOnDiagonal(x: Int, dIdx: Int): Int {
    var conflictCount = 0
    var posX = x
    var posY = solution[x]
    while (true) {
        posX += dx[dIdx] // move by abscissa
        posY += dy[dIdx] // move by ordinatus
        val borderLimitReached = (posX < 0) || (posX >= solutionSize) || (posY < 0) || (posY >= solutionSize)
        if (borderLimitReached) return conflictCount
        if (cellContainQueen(posX, posY)) conflictCount++
    }
}

fun Board.stringView() = getBoard().run {
    this.indices.joinToString("") { this[it].contentToString() + "\n" }
}

fun Board.printPrettySolution(temperature: Double, acceptedCount: Int) {
    println(
        "\nThe Board with solution energy $energy, temperature $temperature, acceptedByTolerance $acceptedCount:\n${
            stringView()
                .replace("true", "Q")
                .replace("false", "x")
        }",
    )
}

private fun Board.getBoard() =
    Array(solutionSize) { BooleanArray(solutionSize) }.also { bd ->
        (0 until solutionSize).forEach {
            bd[it][this.solution[it]] = true
        }
    }

package com.vito.sanel

import com.vito.sanel.models.Board
import kotlin.random.Random

fun Board.initSolution() {
    initDiagonalSolution()
    repeat(solutionSize) { tweakSolution() }
}

fun Board.initDiagonalSolution() {
    (0 until solutionSize).forEach { solution[it] = it }
}

fun Board.tweakSolution() {
    val x = Random.nextInt(0, solutionSize)
    var y = Random.nextInt(0, solutionSize)
    while (x == y) {
        y = Random.nextInt(0, solutionSize)
    }
    solution[x] = solution[y].also { solution[y] = solution[x] }
}

fun Board.computeAndSetEnergy() {
    this.energy = computeEnergy()
}

fun Board.computeEnergy() = (0 until solutionSize).sumOf { x ->
    (0..3).sumOf { dIdx -> // Check only four ways as diagonals: NW then SE etc
        computeConflictsOnDiagonal(
            board = this,
            x,
            diagonalIndex = dIdx,
        )
    }
}.toFloat()

private val diagonalX = intArrayOf(-1, 1, -1, 1)
private val diagonalY = intArrayOf(-1, 1, 1, -1)
private fun computeConflictsOnDiagonal(board: Board, x: Int, diagonalIndex: Int): Int {
    var conflictCount = 0
    var posX = x
    var posY = board.solution[x]
    while (true) {
        posX += diagonalX[diagonalIndex] // move by abscissa
        posY += diagonalY[diagonalIndex] // move by ordinatus
        val borderLimitReached = (posX < 0) || (posX >= board.solutionSize) ||
            (posY < 0) || (posY >= board.solutionSize)
        if (borderLimitReached) return conflictCount
        if (board.cellContainQueen(posX, posY)) conflictCount++
    }
}

private fun Board.getBoard() = // get matrix represent boolean type
    Array(solutionSize) { BooleanArray(solutionSize) }.also { bd ->
        (0 until solutionSize).forEach {
            bd[it][this.solution[it]] = true
        }
    }

fun Board.stringView() = getBoard().run {
    this.indices.joinToString("") { this[it].contentToString() + "\n" }
}

fun Board.printPrettySolution(
    temperature: Double,
    acceptedCount: Int,
) {
    val prefix = if (energy == 0F) { "have just" } else { "not" }
    println(
        "The solution $prefix found!\n" +
            "The Board with solution energy $energy, temperature $temperature, acceptedByTolerance $acceptedCount:\n" +
            stringView().replace("true", "Q").replace("false", "x"),
    )
}

package com.vito.sanel

import com.vito.sanel.models.Board
import kotlin.random.Random

private const val MAX_DIAGONAL_COUNT = 3

/**
 * Extensions for class Board consist three blocks:
 *
 * 1 - Init block
 * 1.1) initDiagonalSolution - set all queen positions to main diagonal, from 0 till board size
 * 1.2) tweakSolution - do some minor random changes in queen positions on board
 * 1.3) initSolution - generate random solution according array size, using initDiagonalSolution & tweakSolution
 * functions
 *
 * 2 - Compute Energy block
 * 2.1) cellContainQueen - check and return true if current board contains queen
 * params: @x abscissa, @y ordinary
 * 2.2) computeConflictsOnDiagonal - calculate conflicts only at four diagonals, no need to check vertical & horizontal
 * 2.3) computeEnergy - calculate using computeConflictsOnDiagonal: check only four ways NW then SE etc.
 * 2.4) computeAndSetEnergy - Set computed energy
 *
 * 3 - Print Solution block
 * 3.1) getBoardMatrixView - create matrix based on current board as array
 * 3.2) stringMatrixView - make pretty view using separate by rows
 * 3.3) printPrettySolution - print final pretty view via change "true" values to "Q" symbol, x else
 * params: @action temperature, acceptedCount
 */

fun Board.initSolution() {
    initDiagonalSolution()
    repeat(solutionSize) { tweakSolution() }
}

fun Board.initDiagonalSolution() {
    (0 until solutionSize).forEach { idx -> solution[idx] = idx }
}

fun Board.tweakSolution() {
    val x = Random.nextInt(0, solutionSize)
    var y = Random.nextInt(0, solutionSize)
    while (x == y) {
        y = Random.nextInt(0, solutionSize)
    }
    solution[x] = solution[y].also { solution[y] = solution[x] }
}

fun Board.computeAndSetEnergy() { this.energy = computeEnergy() }

private fun Board.computeEnergy() = (0 until solutionSize).sumOf { x ->
    (0..MAX_DIAGONAL_COUNT).sumOf { dIdx -> // Check only four ways as diagonals: NW then SE etc
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
        if (cellContainQueen(board, posX, posY)) conflictCount++
    }
}

// Check and return true if current board contains queen at indexes x, y
private fun cellContainQueen(board: Board, x: Int, y: Int): Boolean = board.solution[x] == y

fun Board.printPrettySolution(
    temperature: Double,
    acceptedCount: Int,
) {
    val prefix = if (energy == 0F) { "have just" } else { "not" }
    println(
        "The solution $prefix found!\n" +
            "The Board with solution energy $energy, temperature $temperature, acceptedByTolerance $acceptedCount:\n" +
            stringMatrixView().replace("true", "Q").replace("false", "x"),
    )
}

fun Board.stringMatrixView() = getBoardMatrixView(this).run {
    this.indices.joinToString("") { this[it].contentToString() + "\n" }
}

private fun getBoardMatrixView(board: Board): Array<BooleanArray> =
    Array(board.solutionSize) { BooleanArray(board.solutionSize) }.also { bd ->
        (0 until board.solutionSize).forEach {
            bd[it][board.solution[it]] = true
        }
    }

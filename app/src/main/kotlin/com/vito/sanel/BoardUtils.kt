package com.vito.sanel

import com.vito.sanel.models.QueenBoard
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

fun QueenBoard.initSolution() {
    initDiagonalSolution()
    repeat(solutionSize) { tweakSolution() }
}

fun QueenBoard.initDiagonalSolution() {
    (0 until solutionSize).forEach { idx -> solutionXtoY[idx] = idx }
}

fun QueenBoard.tweakSolution() {
    val x = Random.nextInt(0, solutionSize)
    var y = Random.nextInt(0, solutionSize)
    while (x == y) {
        y = Random.nextInt(0, solutionSize)
    }
    solutionXtoY[x] = solutionXtoY[y].also { solutionXtoY[y] = solutionXtoY[x] }
}

fun QueenBoard.computeAndSetEnergy() { this.energy = this.conflictQueensOnDiagonals }


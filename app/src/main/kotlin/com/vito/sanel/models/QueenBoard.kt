package com.vito.sanel.models

import kotlin.random.Random

data class QueenBoard(
    val solutionXtoY: IntArray,
) {

    val solutionSize get() = solutionXtoY.size

    private fun isQueen(x: Int, y: Int) = solutionXtoY[x] == y

    fun randomSwapQueens(count: Int = 1): QueenBoard {
        val newSolutionXtoY = solutionXtoY.copyOf()
        repeat(count) {
            val x = Random.nextInt(solutionSize)
            var y = Random.nextInt(solutionSize)
            while (x == y) {
                y = Random.nextInt(0, solutionSize)
            }
            newSolutionXtoY[x] = newSolutionXtoY[y].also { newSolutionXtoY[y] = newSolutionXtoY[x] }
        }
        return QueenBoard(newSolutionXtoY)
    }

    override fun toString() = buildString {
        appendLine("Current board conflictQueensOnDiagonals: $threatingQueenCount")
        for (y in 0 until solutionSize) {
            for (x in 0 until solutionSize) {
                append(if (isQueen(x, y)) "Q" else "x")
                append(" ")
            }
            appendLine()
        }
    }

    val threatingQueenCount by lazy {
        (0 until solutionSize).sumOf { computeConflictQueensOnDiagonals(x = it) }
    }

    private fun computeConflictQueensOnDiagonals(x: Int): Int =
        computeConflictQueensOnDiagonal(x, xInc = -1, yInc = -1) +
            computeConflictQueensOnDiagonal(x, xInc = 1, yInc = 1) +
            computeConflictQueensOnDiagonal(x, xInc = -1, yInc = 1) +
            computeConflictQueensOnDiagonal(x, xInc = 1, yInc = -1)

    private fun computeConflictQueensOnDiagonal(x: Int, xInc: Int, yInc: Int): Int {
        var conflictCount = 0
        var posX = x + xInc
        var posY = solutionXtoY[x] + yInc
        while (posX in 0 until solutionSize && posY in 0 until solutionSize) {
            if (isQueen(posX, posY)) {
                conflictCount++
            }
            posX += xInc
            posY += yInc
        }
        return conflictCount
    }

    companion object {
        fun randomInit(size: Int) =
            QueenBoard(IntArray(size) { it })
                .randomSwapQueens(count = size)
    }
}

package com.vito.sanel.models

data class QueenBoard(
    val solutionXtoY: IntArray,
    var energy: Int = -1,
) {

    val solutionSize get() = solutionXtoY.size

    private fun isQueen(x: Int, y: Int) = solutionXtoY[x] == y

    fun clone() = QueenBoard(solutionXtoY = solutionXtoY.copyOf(), energy = energy)

    override fun toString() = buildString {
        for (y in 0 until solutionSize) {
            for (x in 0 until solutionSize) {
                append(if (isQueen(x, y)) "Q" else "x")
                append(" ")
            }
            appendLine()
        }
    }

    val conflictQueensOnDiagonals by lazy {
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
            if (isQueen(posX, posY))
                conflictCount++
            posX += xInc
            posY += yInc
        }
        return conflictCount
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is QueenBoard) return false

        if (!solutionXtoY.contentEquals(other.solutionXtoY)) return false

        return energy == other.energy
    }

    override fun hashCode(): Int {
        var result = solutionXtoY.contentHashCode()
        result = 31 * result + energy.hashCode()

        return result
    }
}

package com.vito.sanel.models

data class Board(
    val solution: IntArray,
    var energy: Float = -1F,
) {

    val solutionSize get() = solution.size

    fun clone() = Board(solution = solution.copyOf(), energy = energy)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Board) return false

        if (!solution.contentEquals(other.solution)) return false

        return energy == other.energy
    }

    override fun hashCode(): Int {
        var result = solution.contentHashCode()
        result = 31 * result + energy.hashCode()

        return result
    }
}

package com.vito.sanel.models

const val DEFAULT_MAX_BOARD_LENGTH = 30

data class MemberType(
    val solution: IntArray = IntArray(DEFAULT_MAX_BOARD_LENGTH + 1),
    var energy: Float = -1F
) {

    fun solutionLastIndex() = solution.size - 1

    fun solutionSize() = solution.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MemberType) return false

        if (!solution.contentEquals(other.solution)) return false
        if (energy != other.energy) return false

        return true
    }

    override fun hashCode(): Int {
        var result = solution.contentHashCode()
        result = 31 * result + energy.hashCode()
        return result
    }
}

package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class BrianLukeTest {

    @Test
    fun `should be different after one tweakSolution call`() {
        QueenBoard(solutionXtoY = IntArray(DEFAULT_MAX_BOARD_SIZE)).run {
            this.initDiagonalSolution()
            val initStateSolution = this.solutionXtoY.copyOf()
            this.tweakSolution()
            assertEquals(initStateSolution.size, this.solutionXtoY.size)
            assertFalse(initStateSolution.contentEquals(this.solutionXtoY))
        }
    }

    @Test
    fun `should be different after few tweakSolution call`() {
        QueenBoard(solutionXtoY = IntArray(DEFAULT_MAX_BOARD_SIZE)).run {
            this.initDiagonalSolution()
            val initStateSolution = this.solutionXtoY.copyOf()
            (0..5).forEach { _ -> this.tweakSolution() }
            assertEquals(initStateSolution.size, this.solutionXtoY.size)
            assertFalse(initStateSolution.contentEquals(this.solutionXtoY))
        }
    }

    @Test
    fun `computeEnergy should be zero size one because of the edge`() {
        QueenBoard(solutionXtoY = intArrayOf(0)).run {
            this.computeAndSetEnergy().also {
                assertEquals(0, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy should be zero size two`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 1)).run {
            this.computeAndSetEnergy().also { assertEquals(2, this.energy) }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version0`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 1, 2)).run {
            this.computeAndSetEnergy().also {
                assertNotEquals(0, this.energy)
                assertEquals(6, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version1`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 2, 1)).run {
            this.computeAndSetEnergy().also { assertEquals(2, this.energy) }
        }
    }

    @Test
    fun `computeEnergy should be zero`() {
        QueenBoard(solutionXtoY = intArrayOf(4, 6, 0, 3, 1, 7, 5, 2)).run {
            this.computeAndSetEnergy().also {
                assertEquals(0, this.energy)
            }
        }
    }
}

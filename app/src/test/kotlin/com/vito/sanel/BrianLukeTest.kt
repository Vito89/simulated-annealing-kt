package com.vito.sanel

import com.vito.sanel.models.QueenBoard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BrianLukeTest {

    @Test
    fun `board solution should be different after one tweakSolution call`() {
        QueenBoard.randomInit(size = DEFAULT_MAX_BOARD_SIZE).run {
            val newStateSolution = this.randomSwapQueens()
            assertEquals(this.solutionSize, newStateSolution.solutionSize)
            assertFalse(this.solutionXtoY.contentEquals(newStateSolution.solutionXtoY))
        }
    }

    @Test
    fun `board solution should be different after few randomSwapQueens call`() {
        QueenBoard.randomInit(size = DEFAULT_MAX_BOARD_SIZE).run {
            var newStateSolution = this.randomSwapQueens()
            repeat(5) {
                newStateSolution = this.randomSwapQueens()
            }
            assertEquals(this.solutionSize, newStateSolution.solutionSize)
            assertFalse(this.solutionXtoY.contentEquals(newStateSolution.solutionXtoY))
        }
    }

    @Test
    fun `board solution energy should be zero size one because of the edge`() {
        QueenBoard.randomInit(size = 0).run {
            assertEquals(0, this.conflictQueensOnDiagonals)
        }
    }

    @Test
    fun `board solution energy should be more than zero size`() {
        QueenBoard.randomInit(size = DEFAULT_MAX_BOARD_SIZE).run {
            assertTrue(this.conflictQueensOnDiagonals > 0)
        }
    }

    @Test
    fun `board solution energy shouldn't be zero cause board size is two`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 1)).run {
            assertEquals(2, this.conflictQueensOnDiagonals)
        }
    }

    @Test
    fun `board solution energy shouldn't be zero size three ver0`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 1, 2)).run {
            assertNotEquals(0, this.conflictQueensOnDiagonals)
            assertEquals(6, this.conflictQueensOnDiagonals)
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three ver1`() {
        QueenBoard(solutionXtoY = intArrayOf(0, 2, 1)).run {
            assertEquals(2, this.conflictQueensOnDiagonals)
        }
    }

    @Test
    fun `computeEnergy should be zero according custom data`() {
        QueenBoard(solutionXtoY = intArrayOf(4, 6, 0, 3, 1, 7, 5, 2)).run {
            assertEquals(0, this.conflictQueensOnDiagonals)
        }
    }
}

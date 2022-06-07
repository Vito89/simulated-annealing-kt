package com.vito.sanel

import com.vito.sanel.models.Board
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BrianLukeTest {

    @Test
    fun `should be different after one tweakSolution call`() {
        Board().run {
            this.initDiagonalSolution()
            val initStateSolution = this.solution.copyOf()
            this.tweakSolution()
            assertEquals(initStateSolution.size, this.solution.size)
            assertFalse(initStateSolution.contentEquals(this.solution))
        }
    }

    @Test
    fun `should be different after few tweakSolution call (low possible false test)`() {
        Board().run {
            this.initDiagonalSolution()
            val initStateSolution = this.solution.copyOf()
            (0..5).forEach { _ -> this.tweakSolution() }
            assertEquals(initStateSolution.size, this.solution.size)
            assertFalse(initStateSolution.contentEquals(this.solution))
        }
    }

    @Test
    fun `computeEnergy should be zero size one because of the edge`() {
        Board(solution = intArrayOf(0)).run {
            this.computeAndSetEnergy().also {
                assertEquals(0F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy should be zero size two`() {
        Board(solution = intArrayOf(0, 1)).run {
            this.computeAndSetEnergy().also { assertEquals(2F, this.energy) }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version0`() {
        Board(solution = intArrayOf(0, 1, 2)).run {
            this.computeAndSetEnergy().also {
                assertNotEquals(0F, this.energy)
                assertEquals(6F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version1`() {
        Board(solution = intArrayOf(0, 2, 1)).run {
            this.computeAndSetEnergy().also { assertEquals(2F, this.energy) }
        }
    }

    @Test
    fun `computeEnergy should be zero`() {
        Board(solution = intArrayOf(4, 6, 0, 3, 1, 7, 5, 2)).run {
            this.computeAndSetEnergy().also {
                assertEquals(0F, this.energy)
            }
        }
    }

    @Test
    fun `should be equals stringView size two`() {
        Board(solution = intArrayOf(0, 1)).run {
            this.stringView().also {
                assertTrue(it.isNotBlank())
                assertEquals("[true, false]\n[false, true]\n", it)
                print(it)
            }
            print(this.printPrettySolution())
        }
    }
}

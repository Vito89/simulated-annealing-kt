package com.vito.sanel

import com.vito.sanel.models.MemberType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BrianLukeTest {

    @Test
    fun `should be different after one tweakSolution call`() {
        MemberType().run {
            this.preInitSolution()
            val initStateSolution = this.solution.copyOf()
            this.tweakSolution()
            assertEquals(initStateSolution.size, this.solution.size)
            assertFalse(initStateSolution.contentEquals(this.solution))
        }
    }

    @Test
    fun `should be different after few tweakSolution call (low possible false test)`() {
        MemberType().run {
            this.preInitSolution()
            val initStateSolution = this.solution.copyOf()
            (0..5).forEach { _ -> this.tweakSolution() }
            assertEquals(initStateSolution.size, this.solution.size)
            assertFalse(initStateSolution.contentEquals(this.solution))
        }
    }

    @Test
    fun `computeEnergy should be zero size one because of the edge`() {
        MemberType(solution = intArrayOf(0)).run {
            this.computeEnergy().also {
                assertEquals(0F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy should be zero size two`() {
        MemberType(solution = intArrayOf(0, 1)).run {
            this.computeEnergy().also {
                assertNotEquals(0F, this.energy)
                assertEquals(2F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version0`() {
        MemberType(solution = intArrayOf(0, 1, 2)).run {
            this.computeEnergy().also {
                assertNotEquals(0F, this.energy)
                assertEquals(6F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy shouldn't be zero size three version1`() {
        MemberType(solution = intArrayOf(0, 2, 1)).run {
            this.computeEnergy().also {
                assertNotEquals(0F, this.energy)
                assertEquals(2F, this.energy)
            }
        }
    }

    @Test
    fun `computeEnergy should be zero`() {
        MemberType(solution = intArrayOf(4, 6, 0, 3, 1, 7, 5, 2)).run {
            this.computeEnergy().also {
                assertEquals(0F, this.energy)
            }
        }
    }

    @Test
    fun `should be equals stringView size two`() {
        MemberType(solution = intArrayOf(0, 1)).run {
            this.stringView().also {
                assertTrue(it.length > 10)
                assertEquals("[true, false]\n[false, true]\n", it)
                print(it)
            }
            print(this.emitPrettySolution())
        }
    }
}

package com.vito.sanel

import com.vito.sanel.models.MemberType
import kotlin.random.Random

fun MemberType.tweakSolution() {
    val x = Random.nextInt(0, solutionSize)
    var y = Random.nextInt(0, solutionSize)
    while (x == y) {
        y = Random.nextInt(0, solutionSize)
    }
    solution[x] = solution[y].also { solution[y] = solution[x] }
}

fun MemberType.initDiagonalSolution() {
    (0 until solutionSize).forEach { solution[it] = it }
}

fun MemberType.initSolution() {
    initDiagonalSolution()
    repeat(solutionSize) { tweakSolution() }
}

fun MemberType.computeEnergy() {
    var conflictCount = 0
    val dx = intArrayOf(-1, 1, -1, 1)
    val dy = intArrayOf(-1, 1, 1, -1)

    val board = getBoard()
    (0 until solutionSize).forEach { x -> // for all solution line (numbers)
        (0..3).forEach { jj -> // NW then SE etc
            var tmpX = x
            var tmpY = solution[x]
            while (true) {
                tmpX += dx[jj]
                tmpY += dy[jj]
                if ((tmpX < 0) || (tmpX >= solutionSize) || (tmpY < 0) || (tmpY >= solutionSize)) break
                if (board[tmpX][tmpY]) conflictCount++
            }
        }
    }
    this.energy = conflictCount.toFloat()
}

fun MemberType.stringView() = getBoard().run {
    this.indices.joinToString("") { this[it].contentToString() + "\n" }
}

fun MemberType.printPrettySolution() {
    println(
        "\nThe Board with solution energy $energy:\n${
            stringView()
                .replace("true", "Q")
                .replace("false", "x")
        }"
    )
}

private fun MemberType.getBoard() =
    Array(solutionSize) { BooleanArray(solutionSize) }.also { bd ->
        (0 until solutionSize).forEach {
            bd[it][this.solution[it]] = true
        }
    }

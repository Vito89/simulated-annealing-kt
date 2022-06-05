package com.vito.sanel

import com.vito.sanel.models.MemberType
import kotlin.random.Random

fun MemberType.tweakSolution() {
    val x = Random.nextInt(0, solutionLastIndex())
    var y = Random.nextInt(0, solutionLastIndex())
    while (x == y) {
        y = Random.nextInt(0, solutionLastIndex())
    }
    this.solution[x] = this.solution[y].also { this.solution[y] = this.solution[x] }
}

fun MemberType.preInitSolution() {
    (0..solutionLastIndex()).forEach { this.solution[it] = it }
}

fun MemberType.initSolution() {
    this.preInitSolution().also { (0..solutionLastIndex()).forEach { _ -> this.tweakSolution() } }
}

fun MemberType.computeEnergy() {
    var conflictCount = 0
    val dx = intArrayOf(-1, 1, -1, 1)
    val dy = intArrayOf(-1, 1, 1, -1)

    val board = getBoard()
    (0..solutionLastIndex()).forEach { x -> // for all solution line (numbers)
        (0..3).forEach { jj -> // NW then SE etc
            var tmpX = x
            var tmpY = this.solution[x]
            while (true) {
                tmpX += dx[jj]
                tmpY += dy[jj]
                if ((tmpX < 0) || (tmpX >= solutionSize()) || (tmpY < 0) || (tmpY >= solutionSize())) break
                if (board[tmpX][tmpY]) conflictCount++
            }
        }
    }
    this.energy = conflictCount.toFloat()
}

fun MemberType.emitPrettySolution() {
    println(
        "\nThe Board:\n${
            stringView()
                .replace("true", "Q")
                .replace("false", "x")
        }"
    )
}

fun MemberType.stringView() = getBoard().run {
    this.indices.joinToString("") { this[it].contentToString() + "\n" }
}

private fun MemberType.getBoard() =
    Array(solutionLastIndex() + 1) { BooleanArray(solutionLastIndex() + 1) }.also { bd ->
        (0..solutionLastIndex()).forEach {
            bd[it][this.solution[it]] = true
        }
    }

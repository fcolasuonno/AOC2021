package day15

import isDebug
import java.io.File
import java.util.*

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/main/kotlin/$dir/$name").readLines()
    val parsed = parse(input)
    part1(parsed)
    part2(parsed)
}

fun parse(input: List<String>) =
    input.joinToString("").let { IntArray(it.length) { i -> it[i].digitToInt() } to input.size }

fun part1(input: Pair<IntArray, Int>) {
    val res = aStar(input.first, input.second)
    println("Part 1 = $res")
}

private fun aStar(cost: IntArray, size: Int): Int {
    val frontier = PriorityQueue(compareBy<Pair<Int, Int>> { it.first }).apply {
        add(0 to 0)
    }
    val costSoFar = IntArray(size * size) { if (it == 0) 0 else Int.MAX_VALUE }
    val end = size * size - 1
    while (frontier.peek()?.second != end) {
        val curr = frontier.remove().second
        val currCost = costSoFar[curr]
        val x = curr % size
        val y = curr / size
        listOf((x - 1) to y, (x + 1) to y, x to (y - 1), x to (y + 1))
            .filter { it.first in 0 until size && it.second in 0 until size }
            .forEach { (newX, newY) ->
                val next = newX + newY * size
                val newCost = currCost + cost[next]
                if (newCost < costSoFar[next]) {
                    costSoFar[next] = newCost
                    frontier.add(newCost + end - newX - newY to next)
                }
            }
    }
    return costSoFar[end]
}

fun part2(input: Pair<IntArray, Int>) {
    val (cost, size) = input
    val newInput = IntArray(size * size * 25) { i ->
        val origX = i % (5 * size) % size
        val multX = i % (5 * size) / size
        val origY = i / (5 * size) % size
        val multY = i / (5 * size) / size
        (cost[origX + origY * size] + multX + multY - 1) % 9 + 1
    }
    val res = aStar(newInput, size * 5)
    println("Part 2 = $res")
}
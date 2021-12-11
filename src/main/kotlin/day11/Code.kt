package day11

import isDebug
import neighbours
import java.io.File

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/main/kotlin/$dir/$name").readLines()
    val parsed = parse(input)
    part1(parsed)
    part2(parsed)
}

fun parse(input: List<String>) = input.flatMapIndexed { row, s ->
    s.mapIndexed { col, c -> Pair(col, row) to c.digitToInt() }
}.toMap().let { initialMap ->
    generateSequence(initialMap) { map ->
        val toFlash = mutableSetOf<Pair<Int, Int>>()
        map.mapValues { (coord, value) ->
            (value + 1).also { if (it > 9) toFlash.add(coord) }
        }.toMutableMap().apply {
            val flashed = mutableSetOf<Pair<Int, Int>>()
            while (toFlash.isNotEmpty()) {
                val flashing = toFlash.first()
                toFlash.remove(flashing)
                flashed.add(flashing)
                set(flashing, 0)
                flashing.neighbours.filter { it in keys && it !in flashed }.forEach { coord ->
                    set(coord, (getValue(coord) + 1).also { if (it > 9) toFlash.add(coord) })
                }
            }
        }
    }
}

fun part1(input: Sequence<Map<Pair<Int, Int>, Int>>) {
    val res = input.map { it.count { it.value == 0 } }.take(101).sum()
    println("Part 1 = $res")
}

private fun Map<Pair<Int, Int>, Int>.print() =
    (0..9).joinToString("\n") { row -> (0..9).joinToString("") { col -> getValue(Pair(col, row)).toString() } }

fun part2(input: Sequence<Map<Pair<Int, Int>, Int>>) {
    val res = input.indexOfFirst { it.all { (_, value) -> value == 0 } }
    println("Part 2 = $res")
}
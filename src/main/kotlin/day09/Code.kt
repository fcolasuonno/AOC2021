package day09

import isDebug
import java.io.File

private val Pair<Int, Int>.adjacent
    get() = listOf(Pair(first - 1, second), Pair(first + 1, second), Pair(first, second - 1), Pair(first, second + 1))

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
}.toMap()

fun part1(input: Map<Pair<Int, Int>, Int>) {
    val res = input.filter { (loc, height) ->
        loc.adjacent.all {
            input.getOrDefault(it, 10) > height
        }
    }.values.sumOf { it + 1 }
    println("Part 1 = $res")
}

fun part2(input: Map<Pair<Int, Int>, Int>) {
    val res =
        input.filter { (loc, height) -> loc.adjacent.all { input.getOrDefault(it, 10) > height } }.map { lowPoint ->
            generateSequence(setOf(lowPoint.key)) { frontier ->
                frontier.flatMap {
                    it.adjacent.filter { candidate ->
                        candidate !in frontier && input.getOrDefault(candidate, 9) < 9
                    }
                }.takeIf { it.isNotEmpty() }?.let { frontier + it }
            }.last().size
        }.sorted().takeLast(3).reduce { acc, i -> acc * i }
    println("Part 2 = $res")
}
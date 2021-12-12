package day12

import isDebug
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

data class Cave(val name: String) {
    companion object {
        val START = Cave("start")
        val END = Cave("end")
    }

    val isSmall = name.all { it.isLowerCase() }
}

fun parse(input: List<String>) = input.flatMap {
    it.split("-").let { (from, to) -> listOf(Cave(from) to Cave(to), Cave(to) to Cave(from)) }
}.filterNot { it.second == Cave.START || it.first == Cave.END }
    .groupBy(Pair<Cave, Cave>::first, Pair<Cave, Cave>::second)

fun part1(input: Map<Cave, List<Cave>>) {
    val res = generateSequence(0 to listOf(listOf(Cave.START))) { (existing, start) ->
        start.takeIf { it.isNotEmpty() }?.flatMap { before ->
            input.getValue(before.last())
                .filter { !it.isSmall || it !in before }
                .map { before + it }
        }?.partition { it.last() == Cave.END }?.let { existing + it.first.size to it.second }
    }.last().first
    println("Part 1 = $res")
}

fun part2(input: Map<Cave, List<Cave>>) {
    val res = generateSequence(0 to listOf(listOf(Cave.START))) { (existing, start) ->
        start.takeIf { it.isNotEmpty() }?.flatMap { before ->
            input.getValue(before.last())
                .filter { cave ->
                    !cave.isSmall || cave !in before || before.filter { it.isSmall }
                        .let { it.distinct().size == it.size }
                }
                .map { before + it }
        }?.partition { it.last() == Cave.END }?.let { existing + it.first.size to it.second }
    }.last().first
    println("Part 2 = $res")
}
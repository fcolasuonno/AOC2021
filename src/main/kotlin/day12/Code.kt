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
        val (found, new) = start.flatMap { before ->
            input.getValue(before.last())
                .filter { !it.isSmall || it !in before }
                .map { before + it }
        }.partition { it.last() == Cave.END }
        new.takeIf { it.isNotEmpty() }?.let { (existing + found.size) to it }
    }.last().first
    println("Part 1 = $res")
}

fun part2(input: Map<Cave, List<Cave>>) {
    val res = generateSequence(0 to listOf(listOf(Cave.START))) { (existing, start) ->
        val (found, new) = start.filter { it.last() != Cave.END }
            .flatMap { before ->
                input.getValue(before.last()).map { before + it }
            }.filter {
                it.filter { it.isSmall }.groupingBy { it }.eachCount()
                    .let { it.count { it.value == 2 } <= 1 && it.none { it.value > 2 } }
            }.partition { it.last() == Cave.END }
        new.takeIf { it.isNotEmpty() }?.let { (existing + found.size) to it }
    }.last().first
    println("Part 2 = $res")
}
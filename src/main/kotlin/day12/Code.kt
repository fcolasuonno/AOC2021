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

fun part1(c: Cave, input: Map<Cave, List<Cave>>, seen: Set<Cave>): Int =
    input.getValue(c).filter { !it.isSmall || it !in seen }.sumOf {
        if (it == Cave.END) 1 else part1(it, input, if (it.isSmall) (seen + it) else seen)
    }

fun part1(input: Map<Cave, List<Cave>>) {
    val res = part1(Cave.START, input, emptySet())
    println("Part 1 = $res")
}

fun part2(c: Cave, input: Map<Cave, List<Cave>>, seen: Set<Cave>, hasDouble: Boolean): Int =
    input.getValue(c).filter { !it.isSmall || it !in seen || !hasDouble }.sumOf {
        if (it == Cave.END) 1 else part2(
            it,
            input,
            if (it.isSmall) (seen + it) else seen,
            hasDouble || (it.isSmall && it in seen)
        )
    }

fun part2(input: Map<Cave, List<Cave>>) {
    val res = part2(Cave.START, input, emptySet(), false)
    println("Part 2 = $res")
}
package day07

import isDebug
import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/main/kotlin/$dir/$name").readLines()
    val parsed = parse(input)
    part1(parsed)
    part2(parsed)
}

fun parse(input: List<String>) = input.single().split(',').map {
    it.toInt()
}.requireNoNulls()

fun part1(input: List<Int>) {
    val range = requireNotNull(input.minOrNull())..requireNotNull(input.maxOrNull())
    val res = range.minOfOrNull { finalPosition -> input.sumOf { abs(it - finalPosition) } }
    println("Part 1 = $res")
}

fun part2(input: List<Int>) {
    val range = requireNotNull(input.minOrNull())..requireNotNull(input.maxOrNull())
    val res = range.minOf { finalPosition ->
        input.sumOf { abs(it - finalPosition).let { n -> n * (n + 1) / 2 } }
    }
    println("Part 2 = $res")
}
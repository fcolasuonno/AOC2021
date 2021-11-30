package day0N

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

private val lineStructure = """(\d+)-(\d+) (.): (.+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (low, _, _, _) = it.toList()
        low.toInt()
    }
}.requireNoNulls()

fun part1(input: List<Int>) {
    val res = input
    println("Part 1 = $res")
}

fun part2(input: List<Int>) {
    val res = input.size
    println("Part 2 = $res")
}
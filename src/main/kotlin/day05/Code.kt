package day05

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

val format = """([0-9]+),([0-9]+) -> ([0-9]+),([0-9]+)""".toRegex()

data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    fun points() = if (x1 == x2)
        (minOf(y1, y2)..maxOf(y1, y2)).map { x1 to it }
    else
        (minOf(x1, x2)..maxOf(x1, x2)).map { it to (y1 + (y2 - y1) * (it - x1) / (x2 - x1)) }
}

fun parse(input: List<String>) = input.map {
    requireNotNull(format.matchEntire(it)).destructured.let { (x1, y1, x2, y2) ->
        Line(
            x1.toInt(),
            y1.toInt(),
            x2.toInt(),
            y2.toInt()
        )
    }
}.requireNoNulls()

fun part1(input: List<Line>) {
    val res = input.filter { it.x1 == it.x2 || it.y1 == it.y2 }
        .flatMap { it.points() }.groupingBy { it }.eachCount().filterValues { it >= 2 }.count()
    println("Part 1 = $res")
}

fun part2(input: List<Line>) {
    val res = input.flatMap { it.points() }.groupingBy { it }.eachCount().filterValues { it >= 2 }.count()
    println("Part 2 = $res")
}
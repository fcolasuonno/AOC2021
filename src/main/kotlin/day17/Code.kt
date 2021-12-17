package day17

import isDebug
import java.io.File
import kotlin.math.abs
import kotlin.math.sqrt

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/main/kotlin/$dir/$name").readLines()
    val parsed = parse(input)
    part1(parsed)
    part2(parsed)
}

private val format = """target area: x=([-0-9]+)\.\.([-0-9]+), y=([-0-9]+)\.\.([-0-9]+)""".toRegex()

fun parse(input: List<String>) = format.matchEntire(input.single())?.destructured!!.toList().map(String::toInt)
    .let { (x1, x2, y1, y2) -> x1..x2 to y1..y2 }

fun part1(input: Pair<IntRange, IntRange>) {
    val res = (abs(input.second.minOrNull()!!) - 1).let { it * (it + 1) / 2 }
    println("Part 1 = $res")
}

fun part2(input: Pair<IntRange, IntRange>) {
    val (xRange, yRange) = input
    val res = ((sqrt(2 * xRange.first.toFloat()).toInt() - 1)..xRange.last).sumOf { x ->
        (yRange.first..-yRange.first).count { y ->
            (1..1000).any { step ->
                x.drag(minOf(x, step)) in xRange && y.drag(step) in yRange
            }
        }
    }
    println("Part 2 = $res")
}

fun Int.drag(step: Int) = times(step) - step * (step - 1) / 2
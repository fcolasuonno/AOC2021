package day06

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

fun parse(input: List<String>) = input.single().split(',').map {
    it.toInt()
}.requireNoNulls()

fun part1(input: List<Int>) {
    val res = generateSequence(input) { daysLeft ->
        daysLeft.map { if (it > 0) it - 1 else 6 } + List(daysLeft.count { it == 0 }) { 8 }
    }.drop(1).take(80).last().size
    println("Part 1 = $res")
}

fun part2(input: List<Int>) {
    val res = generateSequence(input.groupingBy { it }.eachCount()
        .let { count -> (0..8).map { count.getOrDefault(it, 0).toLong() } }) { currentCount ->
        (0..8).map {
            when (it) {
                6 -> currentCount[0] + currentCount[7]
                8 -> currentCount[0]
                else -> currentCount[it + 1]
            }
        }
    }.drop(1).take(256).last().sum()
    println("Part 2 = $res")
}
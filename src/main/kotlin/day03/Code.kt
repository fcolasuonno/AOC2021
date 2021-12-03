package day03

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

fun parse(input: List<String>) = input.map { line ->
    line.map { if (it == '0') -1 else 1 }
}.requireNoNulls()

fun List<Int>.toDec() = map { if (it > 0) 1 else 0 }.fold(0) { acc, i -> acc * 2 + i }

fun <T : Any> generateSequenceIndexed(seed: T, nextFunction: (Int, T) -> T?) =
    generateSequence(Pair(0, seed)) { (index, current) ->
        nextFunction(index, current)?.let {
            Pair(
                index + 1,
                it
            )
        }
    }.map { it.second }

fun <T : Any> Sequence<List<T>>.firstSingle() = first { it.size == 1 }.single()

fun part1(input: List<List<Int>>) {
    val sum = input.reduce { acc, ints -> acc.zip(ints) { a, b -> a + b } }
    val gamma = sum.toDec()
    val epsilon = sum.map { -it }.toDec()
    val res = epsilon * gamma
    println("Part 1 = $res")
}

fun part2(input: List<List<Int>>) {
    val oxygen = generateSequenceIndexed(input) { index, list ->
        val bitFilter = if (list.sumOf { it[index] } < 0) -1 else 1
        list.filter { it[index] == bitFilter }
    }.firstSingle().toDec()
    val scrubber = generateSequenceIndexed(input) { index, list ->
        val bitFilter = if (list.sumOf { it[index] } >= 0) -1 else 1
        list.filter { it[index] == bitFilter }
    }.firstSingle().toDec()
    val res = oxygen * scrubber
    println("Part 2 = $res")
}
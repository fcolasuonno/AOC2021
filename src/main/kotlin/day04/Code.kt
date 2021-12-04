package day04

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

fun parse(input: List<String>) =
    input.first().split(',').map { it.toInt() } to input.drop(2).joinToString("\n").split("\n\n")
        .flatMapIndexed { card, s ->
            s.split('\n').flatMapIndexed { row, nums ->
                nums.split("""\s+""".toRegex()).filter { it.isNotEmpty() }
                    .mapIndexed { col, num -> Triple(card, row, col) to num.toInt() }
            }
        }

fun part1(input: Pair<List<Int>, List<Pair<Triple<Int, Int, Int>, Int>>>) {
    val (extraction, numbers) = input
    val lookup = numbers.groupBy({ it.second }) { it.first }
    val outcome = extraction.runningFold(listOf<Triple<Int, Int, Int>>()) { acc, num ->
        acc + lookup[num].orEmpty()
    }
    val res = outcome.mapIndexed { index, calledNumbers ->
        extraction.take(index) to (
                calledNumbers.groupingBy { (card, row, _) -> card to row }.eachCount().filter { it.value == 5 } +
                        calledNumbers.groupingBy { (card, _, col) -> card to col }.eachCount().filter { it.value == 5 })
            .keys.map { it.first }
    }.first {
        it.second.isNotEmpty()
    }.let { (extracted, winner) ->
        numbers.filter { (pos, num) -> pos.first == winner.single() && num !in extracted }
            .sumOf { it.second } * extracted.last()
    }
    println("Part 1 = $res")
}

fun part2(input: Pair<List<Int>, List<Pair<Triple<Int, Int, Int>, Int>>>) {
    val (extraction, numbers) = input
    val lookup = numbers.groupBy({ it.second }) { it.first }
    val outcome = extraction.runningFold(listOf<Triple<Int, Int, Int>>()) { acc, num ->
        acc + lookup[num].orEmpty()
    }
    val res = outcome.mapIndexed { index, calledNumbers ->
        extraction.take(index) to (
                calledNumbers.groupingBy { (card, row, _) -> card to row }.eachCount().filter { it.value == 5 } +
                        calledNumbers.groupingBy { (card, _, col) -> card to col }.eachCount().filter { it.value == 5 })
            .keys.map { it.first }.distinct()
    }.zipWithNext().first { (_, res) ->
        res.second.size == numbers.distinctBy { it.first.first }.size
    }.let { (prev, res) ->
        val loser = (res.second - prev.second).single()
        numbers.filter { (pos, num) -> pos.first == loser && num !in res.first }.sumOf { it.second } * res.first.last()
    }
    println("Part 2 = $res")
}
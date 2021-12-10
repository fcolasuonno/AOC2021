package day10

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

object Chunk {
    val delimiters = mapOf(
        '(' to ')',
        '[' to ']',
        '{' to '}',
        '<' to '>'
    )
}

fun parse(input: List<String>) = input.map {
    it.toList()
}.requireNoNulls()

fun part1(input: List<List<Char>>) {
    val points = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )
    val res = input.mapNotNull {
        ArrayDeque<Char>().run {
            it.firstOrNull { c ->
                if (c in Chunk.delimiters) !add(c)
                else Chunk.delimiters[removeLast()] != c
            }
        }
    }.sumOf { points.getValue(it) }
    println("Part 1 = $res")
}

fun part2(input: List<List<Char>>) {
    val points = mapOf(
        '(' to 1,
        '[' to 2,
        '{' to 3,
        '<' to 4
    )
    val res = input.mapNotNull { line ->
        line.fold(ArrayDeque<Char>() as ArrayDeque<Char>?) { deque, c ->
            deque?.takeIf {
                if (c in Chunk.delimiters) it.add(c)
                else Chunk.delimiters[it.removeLast()] == c
            }
        }?.foldRight(0L) { c, acc -> acc * 5 + points.getValue(c) }
    }.sorted().let { it[it.size / 2] }
    println("Part 2 = $res")
}
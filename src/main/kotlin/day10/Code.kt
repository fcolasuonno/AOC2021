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

data class Chunk(val startDelimiter: Char, val start: Int) {
    val completion: String
        get() = if (end == null) (children.lastOrNull()?.completion.orEmpty() + delimiters[startDelimiter]) else ""

    val corruptedChar: Char?
        get() = endDelimiter?.takeIf { delimiters[startDelimiter] != it }
            ?: children.firstNotNullOfOrNull { it.corruptedChar }

    val incomplete
        get() = corruptedChar == null && end == null

    private var endDelimiter: Char? = null
    private var end: Int? = null
    private val children = mutableListOf<Chunk>()

    companion object {
        fun parse(line: List<Char>) =
            generateSequence(listOf(Chunk(line[0], 0).parse(line))) { chunks ->
                chunks.last().end?.inc()?.takeIf { it < line.size }?.let { next ->
                    chunks + Chunk(line[next], next).parse(line)
                }
            }.last()

        val delimiters = mapOf(
            '(' to ')',
            '[' to ']',
            '{' to '}',
            '<' to '>'
        )
    }

    private fun parse(line: List<Char>, from: Int = start + 1): Chunk {
        when (val next = line.getOrNull(from)) {
            in delimiters.keys -> {
                val chunk = Chunk(next!!, from).parse(line)
                children.add(chunk)
                chunk.end?.let { parse(line, it + 1) }
            }
            in delimiters.values -> {
                end = from
                endDelimiter = next
            }
        }
        return this
    }
}

fun parse(input: List<String>) = input.map {
    Chunk.parse(it.toList())
}.requireNoNulls()

fun part1(input: List<List<Chunk>>) {
    val points = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )
    val res = input.mapNotNull {
        it.firstNotNullOfOrNull { chunk -> chunk.corruptedChar }
    }.sumOf { points.getValue(it) }
    println("Part 1 = $res")
}

fun part2(input: List<List<Chunk>>) {
    val points = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )
    val res = input.mapNotNull {
        it.last().takeIf { chunk -> chunk.incomplete }?.completion?.fold(0L) { acc, c -> acc * 5 + points.getValue(c) }
    }.sorted().let { it[it.size / 2] }
    println("Part 2 = $res")
}
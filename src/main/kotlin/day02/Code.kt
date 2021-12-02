package day02

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

enum class Direction(val horizontal: Int, val depth: Int) {
    FORWARD(1, 0),
    DOWN(0, 1),
    UP(0, -1),
}

fun parse(input: List<String>) = input.map {
    it.split(' ').let { (movement, amount) ->
        requireNotNull(
            when (movement) {
                "forward" -> Direction.FORWARD
                "down" -> Direction.DOWN
                "up" -> Direction.UP
                else -> null
            }
        ) to amount.toInt()
    }
}.requireNoNulls()

fun part1(input: List<Pair<Direction, Int>>) {
    val res =
        input.fold(Pair(0, 0)) { (horizontal, depth), (dir, amount) ->
            Pair(
                (horizontal + dir.horizontal * amount),
                (depth + dir.depth * amount).coerceAtLeast(0)
            )
        }.let { (x, y) ->
            x * y
        }
    println("Part 1 = $res")
}

fun part2(input: List<Pair<Direction, Int>>) {
    val res = input.fold(Triple(0, 0, 0)) { (aim, horizontal, depth), (dir, amount) ->
        Triple(
            aim + when (dir) {
                Direction.FORWARD -> 0
                Direction.DOWN -> amount
                Direction.UP -> -amount
            },
            horizontal + if (dir == Direction.FORWARD) amount else 0,
            (depth + if (dir == Direction.FORWARD) aim * amount else 0).coerceAtLeast(0)
        )
    }.let { (_, horizontal, depth) -> horizontal * depth }
    println("Part 2 = $res")
}
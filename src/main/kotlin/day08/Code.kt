package day08

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

fun parse(input: List<String>) = input.map {
    it.split(" | ").let { (digits, output) ->
        digits.split(" ").map(String::toSet) to
                output.split(" ").map(String::toSet)
    }
}.requireNoNulls()

fun part1(input: List<Pair<List<Set<Char>>, List<Set<Char>>>>) {
    val res = input.sumOf { it.second.count { it.size in setOf(2, 3, 4, 7) } }
    println("Part 1 = $res")
}

fun part2(input: List<Pair<List<Set<Char>>, List<Set<Char>>>>) {
    val res = input.sumOf { (input, output) ->
        val remaining = input.toMutableSet()
        val mapping = mutableMapOf(
            1 to input.first { it.size == 2 }.also { remaining.remove(it) },
            7 to input.first { it.size == 3 }.also { remaining.remove(it) },
            4 to input.first { it.size == 4 }.also { remaining.remove(it) },
            8 to input.first { it.size == 7 }.also { remaining.remove(it) },
        )
        mapping[6] = remaining.first { it.size == 6 && (mapping[7]!! - it).isNotEmpty() }.also { remaining.remove(it) }
        mapping[0] = remaining.first { it.size == 6 && (mapping[4]!! - it).isNotEmpty() }.also { remaining.remove(it) }
        mapping[9] = remaining.first { it.size == 6 }.also { remaining.remove(it) }
        mapping[5] = remaining.first { (mapping[6]!! - it).size == 1 }.also { remaining.remove(it) }
        mapping[3] = remaining.first { (mapping[1]!! - it).isEmpty() }.also { remaining.remove(it) }
        mapping[2] = remaining.first()
        mapping.map { it.value to it.key }.toMap().let { invertedMap ->
            output.map { invertedMap[it] }
        }.joinToString("").toInt()
    }
    println("Part 2 = $res")
}
package day14

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

fun parse(input: List<String>) = input.first().windowed(2, partialWindows = true) to input.drop(2).associate {
    it.split(" -> ").let { (key, generated) -> key to listOf(key[0] + generated, generated + key[1]) }
}

fun part1(input: Pair<List<String>, Map<String, List<String>>>) {
    val (template, rules) = input
    val res = generateSequence(template) { pairs ->
        pairs.flatMap { rules[it] ?: listOf(it) }
    }.take(1 + 10).last().groupingBy { it.first() }.eachCount().values.sorted().let { it.last() - it.first() }
    println("Part 1 = $res")
}

fun part2(input: Pair<List<String>, Map<String, List<String>>>) {
    val (template, rules) = input
    val res = generateSequence(template.map { it to 1L }.frequency()) { frequency ->
        frequency.flatMap { (k, v) -> rules[k]?.map { it to v } ?: listOf(k to v) }.frequency()
    }.take(1 + 40).last().map { it.key.first() to it.value }.frequency().values.sorted().let { it.last() - it.first() }
    println("Part 1 = $res")
}

private fun <K> List<Pair<K, Long>>.frequency(): Map<K, Long> = fold(mutableMapOf()) { acc, item ->
    acc.apply {
        put(item.first, getOrDefault(item.first, 0L) + item.second)
    }
}

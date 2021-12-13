package day13

import Coord
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

fun parse(input: List<String>) = input.filter { ',' in it }.map {
    it.split(",").let { (x, y) -> Coord(x.toInt(), y.toInt()) }
}.toSet() to
        input.filter { it.startsWith("fold along") }.map {
            it.substringAfter("fold along ")
                .split("=").let { (dir, pos) -> dir to pos.toInt() }
        }

fun part1(input: Pair<Set<Coord>, List<Pair<String, Int>>>) {
    val (map, fold) = input
    val res = doFold(map, fold.first()).size
    println("Part 1 = \n$res")
}

fun part2(input: Pair<Set<Coord>, List<Pair<String, Int>>>) {
    val (map, fold) = input
    val res = fold.fold(map, ::doFold).let { set ->
        (0..set.maxOf { it.second }).joinToString("\n") { y ->
            (0..set.maxOf { it.first }).joinToString("") { x ->
                if (Coord(x, y) in set) "##" else "  "
            }
        }
    }
    println("Part 2 = \n$res")
}

private fun doFold(acc: Set<Coord>, fold: Pair<String, Int>) =
    if (fold.first == "y") acc.filter { it.second > fold.second }
        .let { toFold -> (acc - toFold) + toFold.map { (x, y) -> Coord(x, 2 * fold.second - y) } }
    else acc.filter { it.first > fold.second }
        .let { toFold -> (acc - toFold) + toFold.map { (x, y) -> Coord(2 * fold.second - x, y) } }

package day22

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
    val (on, cuboid) = it.split(' ')
    val (x, y, z) = cuboid.split(',').map { it.drop(2).split("..").map { it.toInt() } }
    Cuboid(on == "on", x[0]..x[1], y[0]..y[1], z[0]..z[1])
}

private fun IntRange.intersect1(range: IntRange) = if (first <= range.last && last >= range.first)
    maxOf(first, range.first)..minOf(last, range.last) else IntRange.EMPTY

data class Cuboid(val on: Boolean, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {
    fun intersect(cuboid: Cuboid, invert: Boolean) = Cuboid(
        if (invert) on != cuboid.on else on == cuboid.on,
        xRange.intersect1(cuboid.xRange),
        yRange.intersect1(cuboid.yRange),
        zRange.intersect1(cuboid.zRange)
    )

    val size = xRange.count().toLong() * yRange.count() * zRange.count()
}

data class Space(val cuboids: MutableList<Cuboid> = mutableListOf()) {
    operator fun plus(cuboid: Cuboid): Space = this.apply {
        val common = cuboids.map { it.intersect(cuboid, invert = cuboid.on) }.filter { it.size > 0 }
        if (cuboid.on) {
            cuboids.add(cuboid)
        }
        common.forEach {
            cuboids.add(it)
        }
    }

    val sum
        get() = cuboids.fold(0L) { acc, cuboid ->
            if (cuboid.on) { acc + cuboid.size } else { acc - cuboid.size }
        }

}

fun part1(input: List<Cuboid>) {
    val sum = input.filter {
        listOf(
            it.xRange.first,
            it.xRange.last,
            it.yRange.first,
            it.yRange.last,
            it.zRange.first,
            it.zRange.last
        ).all { it in -50..50 }
    }.fold(Space()) { acc, cuboid -> acc + cuboid }.sum
    println("Part 1 = $sum")
}

fun part2(input: List<Cuboid>) {
    println("Part 2 = ${input.fold(Space()) { acc, cuboid -> acc + cuboid }.sum}")
}



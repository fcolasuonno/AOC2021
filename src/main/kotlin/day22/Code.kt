package day22

import isDebug
import java.io.File
import java.util.*

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
    val (on, cube) = it.split(' ')
    val (x, y, z) = cube.split(',').map { it.drop(2).split("..").map { it.toInt() } }
    Cuboid(
        on = (on == "on"), cube = Cube(
            x[0]..x[1], y[0]..y[1], z[0]..z[1]
        )
    )
}

data class Cube(val xRange: IntRange, val yRange: IntRange, val zRange: IntRange) {

}

data class Cuboid(val on: Boolean, val cube: Cube)

data class Rect(val set: TreeMap<Int, Line> = TreeMap(compareBy { it })) {
    fun add(xRange: IntRange, yRange: IntRange) {
        if (set.isNotEmpty()) {
            val floor = set.floorEntry(xRange.first)
            val ceil = set.ceilingEntry(xRange.last + 1)
            set.subMap(xRange.first, true, xRange.last + 1, true).keys.forEach {
                set.remove(it)
            }
            if (floor == null) {
                set[xRange.first] = Line().apply { add(yRange) }
            } else {
                set[xRange.first] = floor.value + yRange
            }
            if (ceil == null) {
                set[xRange.last + 1] = Line()
            } else {
                set[xRange.last + 1] = ceil.value
            }
        } else {
            set[xRange.first] = Line().apply { add(yRange) }
            set[xRange.last + 1] = Line()
        }
    }

    fun remove(xRange: IntRange, yRange: IntRange) {
        if (set.isNotEmpty()) {
            val floor = set.floorEntry(xRange.first)
            val ceil = set.ceilingEntry(xRange.last + 1)
            set.subMap(xRange.first, true, xRange.last + 1, true).keys.forEach {
                set.remove(it)
            }
            if (floor != null) {
                set[xRange.first] = floor.value - yRange
            }
            if (ceil != null) {
                set[xRange.last + 1] = ceil.value
            }
        }
    }

}

data class Line(val set: TreeMap<Int, Boolean> = TreeMap(compareBy { it })) {
    fun add(xRange: IntRange) {
        if (set.isNotEmpty()) {
            val floor = set.floorEntry(xRange.first)
            val ceil = set.ceilingEntry(xRange.last + 1)
            set.subMap(xRange.first, true, xRange.last + 1, true).keys.forEach {
                set.remove(it)
            }
            if (floor == null || floor.value == false) {
                set[xRange.first] = true
            }
            if (ceil == null || ceil.value == true) {
                set[xRange.last + 1] = false
            }
        } else {
            set[xRange.first] = true
            set[xRange.last + 1] = false
        }
    }

    fun remove(xRange: IntRange) {
        if (set.isNotEmpty()) {
            val floor = set.floorEntry(xRange.first)
            val ceil = set.ceilingEntry(xRange.last + 1)
            set.subMap(xRange.first, true, xRange.last + 1, true).keys.forEach {
                set.remove(it)
            }
            if (floor != null && floor.value == true) {
                set[xRange.first] = false
            }
            if (ceil != null && ceil.value == false) {
                set[xRange.last + 1] = true
            }
        } else {
            set[xRange.first] = false
            set[xRange.last + 1] = true
        }
    }

    operator fun plus(yRange: IntRange): Line {
        val l = Line()
        l.set.putAll(set)
        l.add(yRange)
        return l
    }

    operator fun minus(yRange: IntRange): Line {
        val l = Line()
        l.set.putAll(set)
        l.remove(yRange)
        return l
    }

}

fun part1(input: List<Cuboid>) {

    val line = Rect()
    input.forEach {
        System.err.println("" + it.on + ":" + it.cube.xRange + "," + it.cube.yRange)
        if (it.on) {
            line.add(it.cube.xRange, it.cube.yRange)
        } else {
            line.remove(it.cube.xRange, it.cube.yRange)
        }
        System.err.println(line)
    }
}

fun part2(input: List<Cuboid>) {
    println("Part 2 = ${input.size}")
}


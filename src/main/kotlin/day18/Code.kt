package day18

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

sealed class SFNumber {
    companion object {
        fun parse(string: String, offset: Int) = (if (string[offset] == '[') {
            SFPair.parsePair(string, offset)
        } else SFRegular(string[offset].digitToInt()))
    }

    abstract val length: Int
    abstract fun explode(level: Int, l: Int = 0, r: Int = 0): Triple<SFNumber, Int, Int>
    abstract fun split(): SFNumber
    abstract fun addRight(r: Int): SFNumber
    abstract fun addLeft(l: Int): SFNumber

    fun reduce() = generateSequence(this) { prev ->
        prev.explode(0).first.takeIf { it != prev } ?: prev.split().takeIf { it != prev }
    }.last()

    data class SFRegular(val value: Int) : SFNumber() {
        companion object {
            val ZERO = SFRegular(0)
        }

        override val length: Int = 1
        override fun explode(level: Int, l: Int, r: Int) = Triple(SFRegular(value + l + r), 0, 0)

        override fun split() = if (value > 9) SFPair(SFRegular(value / 2), SFRegular((value + 1) / 2)) else this
        override fun addRight(r: Int) = SFRegular(value + r)
        override fun addLeft(l: Int) = SFRegular(value + l)

        override fun toString() = value.toString()
    }

    data class SFPair(val left: SFNumber, val right: SFNumber) : SFNumber() {
        companion object {
            fun parsePair(string: String, offset: Int): SFPair =
                parse(string, offset + 1).let { left ->
                    SFPair(left, parse(string, offset + left.length + 2))
                }
        }

        override val length = left.length + right.length + 3
        override fun explode(level: Int, l: Int, r: Int) =
            if (level == 4) {
                Triple(SFRegular.ZERO, (left as SFRegular).value, (right as SFRegular).value)
            } else (left.explode(level + 1, l, r).takeIf { it.first != left }?.let { (red, l, r) ->
                Triple(SFPair(red, right.addLeft(r)), l, 0)
            } ?: right.explode(level + 1, l, r).takeIf { it.first != right }?.let { (red, l, r) ->
                Triple(SFPair(left.addRight(l), red), 0, r)
            } ?: Triple(this, l, r))

        override fun split() =
            left.split().takeIf { it != left }?.let { SFPair(it, right) } ?: right.split().takeIf { it != right }
                ?.let { SFPair(left, it) } ?: this

        override fun addRight(r: Int) = SFPair(left, right.addRight(r))
        override fun addLeft(l: Int) = SFPair(left.addLeft(l), right)

        override fun toString() = "[$left,$right]"
    }
}

fun parse(input: List<String>) = input.map {
    SFNumber.parse(it, 0).reduce()
}.requireNoNulls()

fun part1(input: List<SFNumber>) {
    val res = input.reduce { acc, sfNumber -> SFNumber.SFPair(acc, sfNumber).reduce() }.magnitude()
    println("Part 1 = $res")
}

private fun SFNumber.magnitude(): Int = when (this) {
    is SFNumber.SFRegular -> value
    is SFNumber.SFPair -> 3 * left.magnitude() + 2 * right.magnitude()
}

fun part2(input: List<SFNumber>) {
    val res = input.flatMap { first ->
        input.filter { it != first }.map { second -> SFNumber.SFPair(first, second).reduce() }
    }.maxOf { it.magnitude() }
    println("Part 2 = $res")
}
package day16

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

sealed class Packet {
    abstract val end: Int
    abstract val version: Int
    abstract val type: Int
    abstract val value: Long

    companion object {
        fun parse(input: String, offset: Int): Packet {
            val version = input.next(offset, 3)
            return when (val type = input.next(offset + 3, 3)) {
                4 -> Literal.parsePacket(version, type, input, offset + 6)
                else -> Operator.parsePacket(version, type, input, offset + 6)
            }
        }

        private fun String.next(offset: Int, size: Int) = substring(offset, offset + size).toInt(2)
    }

    data class Literal(
        override val version: Int,
        override val type: Int,
        override val end: Int,
        override val value: Long
    ) : Packet() {

        companion object {
            fun parsePacket(version: Int, type: Int, input: String, offset: Int) =
                generateSequence(offset) { it + 5 }.map { input.next(it, 5) }.takeWhile {
                    it.and(0b10000) != 0
                }.map { it.and(0b1111) }.toList().let { it + input.next(offset + it.size * 5, 5) }.let {
                    Literal(version, type, offset + (it.size * 5), it.fold(0L) { acc, i -> acc * 16 + i })
                }
        }
    }

    data class Operator(
        override val version: Int,
        override val type: Int,
        override val end: Int,
        val subPackets: List<Packet>
    ) : Packet() {

        companion object {
            fun parsePacket(version: Int, type: Int, input: String, offset: Int) =
                if (input.next(offset, 1) == 0) {
                    val length = input.next(offset + 1, 15)
                    val subList = input.substring(offset + 16, offset + 16 + length)
                    val subPackets = generateSequence(parse(subList, 0)) { prev ->
                        prev.end.takeIf { it in subList.indices }?.let { parse(subList, it) }
                    }.toList()
                    Operator(version, type, offset + 16 + length, subPackets)
                } else {
                    val length = input.next(offset + 1, 11)
                    val subPackets = generateSequence(parse(input, offset + 12)) { prev ->
                        prev.end.takeIf { it in input.indices }?.let { parse(input, it) }
                    }.take(length).toList()
                    Operator(version, type, subPackets.last().end, subPackets)
                }
        }

        override val value: Long
            get() = when (type) {
                0 -> subPackets.sumOf { it.value }
                1 -> subPackets.fold(1) { acc, packet -> acc * packet.value }
                2 -> subPackets.minOf { it.value }
                3 -> subPackets.maxOf { it.value }
                5 -> if (subPackets[0].value > subPackets[1].value) 1 else 0
                6 -> if (subPackets[0].value < subPackets[1].value) 1 else 0
                7 -> if (subPackets[0].value == subPackets[1].value) 1 else 0
                else -> throw IllegalArgumentException()
            }
    }
}

fun parse(input: List<String>) = input.single().map { it.digitToInt(16).toString(2).padStart(4, '0') }.joinToString("")
    .let { stream ->
        generateSequence(Packet.parse(stream, 0)) { prev ->
            align(prev).takeIf { it + 7 in stream.indices }?.let { Packet.parse(stream, it) }
        }
    }.toList()

private fun align(prev: Packet) = prev.end.let {
    if (it % 4 != 0) {
        it + 4 - (it % 4)
    } else it
}

fun sum(input: List<Packet>): Long = input.sumOf { it.version + if (it is Packet.Operator) sum(it.subPackets) else 0 }

fun part1(input: List<Packet>) {
    val res = sum(input)
    println("Part 1 = $res")
}

fun part2(input: List<Packet>) {
    val res = input.map { it.value }
    println("Part 2 = $res")
}
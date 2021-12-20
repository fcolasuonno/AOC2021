package day20

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

data class BorderInfo(val borderValue: Boolean, val minX: Int, val minY: Int, val maxX: Int, val maxY: Int) {
    fun update(b: Boolean) = BorderInfo(
        if (b) !borderValue else borderValue,
        if (borderValue) minX - 5 else minX + 2,
        if (borderValue) minY - 5 else minY + 2,
        if (borderValue) maxX + 5 else maxX - 2,
        if (borderValue) maxY + 5 else maxY - 2,
    )
}

fun parse(input: List<String>): Sequence<Pair<Set<Pair<Int, Int>>, BorderInfo>> {
    val enhancer =
        input.first().foldIndexed(mutableSetOf<Int>()) { index, acc, c -> acc.apply { if (c == '#') add(index) } }
    val image = input.drop(2)
        .flatMapIndexed { col, s -> s.mapIndexedNotNull { row, c -> if (c == '#') (row to col) else null } }.toSet()
    val borderInfo = BorderInfo(false, -5, -5, image.maxOf { it.first } + 5, image.maxOf { it.second } + 5)
    return generateSequence(image to borderInfo) { (prev, border) ->
        mutableSetOf<Pair<Int, Int>>().apply {
            (border.minX..border.maxX).forEach { row ->
                (border.minY..border.maxY).forEach { col ->
                    val index = listOf(
                        (row - 1) to (col - 1),
                        (row) to (col - 1),
                        (row + 1) to (col - 1),
                        (row - 1) to (col),
                        (row) to (col),
                        (row + 1) to (col),
                        (row - 1) to (col + 1),
                        (row) to (col + 1),
                        (row + 1) to (col + 1)
                    ).fold(0) { acc, neighbours ->
                        val value =
                            if (neighbours in prev ||
                                (border.borderValue &&
                                        (row == border.minX || row == border.maxX || col == border.minY || col == border.maxY))
                            ) 1 else 0
                        acc * 2 + value
                    }
                    if (index in enhancer &&
                        !(border.borderValue &&
                                (row <= (border.minX) || row >= (border.maxX) || col <= (border.minY) || col >= (border.maxY)))
                    ) add(row to col)
                }
            }
        } to border.update(0 in enhancer)
    }
}

fun part1(input: Sequence<Pair<Set<Pair<Int, Int>>, BorderInfo>>) {
    val res = input.take(1 + 2).last().first.size
    println("Part 1 = $res")
}

fun part2(input: Sequence<Pair<Set<Pair<Int, Int>>, BorderInfo>>) {
    val res = input.take(1 + 50).last().first.size
    println("Part 2 = $res")
}
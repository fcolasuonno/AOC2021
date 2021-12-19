package day19

import isDebug
import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/main/kotlin/$dir/$name").readLines()
    val parsed = parse(input)
    part1(parsed)
    part2(parsed)
}

fun parse(input: List<String>) =
    input.indices.filter { input[it].startsWith("--- scanner ") }.plus(input.indices.last + 1)
        .zipWithNext().map { scannerList ->
            ((scannerList.first + 1) until scannerList.second).mapNotNull {
                input[it].takeIf(String::isNotEmpty)?.split(",")?.map(String::toInt)
                    ?.let { (x, y, z) -> Triple(x, y, z) }
            }.toSet()
        }.let { scanned ->
            val known = scanned.first().toMutableSet()
            val scanners = mutableSetOf(Triple(0, 0, 0))
            val rest = scanned.drop(1).toMutableList()
            while (rest.isNotEmpty()) {
                val first = rest.first().also { rest.removeAt(0) }
                val distances = known.let { beacons ->
                    beacons.map { b -> b to beacons.map { other -> b.distance(other) }.toSet() }
                }
                val otherDistanceMap = first.let { beacons ->
                    beacons.map { b -> b to beacons.map { other -> b.distance(other) }.toSet() }
                }
                val mapping = selects.mapNotNull { select ->
                    transforms.firstNotNullOfOrNull { transform ->
                        distances.mapNotNull { (beacon, distanceList) ->
                            otherDistanceMap.mapNotNull { (candidate, candidateDistanceList) ->
                                candidateDistanceList.intersect(distanceList).size.takeIf { it > 10 }
                                    ?.let { matches -> candidate to matches }
                            }.maxByOrNull { (_, matches) -> matches }?.let { (candidate, _) -> beacon to candidate }
                        }.map {
                            transform(select(it.first), it.second)
                        }.groupingBy { it }.eachCount().entries.singleOrNull { it.value > 10 }?.let {
                            it.key to transform
                        }
                    }
                }
                if (mapping.size != 3) {
                    rest.add(first)
                    continue
                }
                scanners.add(Triple(mapping[0].first, mapping[1].first, mapping[2].first))
                known.addAll(first.map {
                    Triple(
                        -mapping[0].second(-mapping[0].first, it),
                        -mapping[1].second(-mapping[1].first, it),
                        -mapping[2].second(-mapping[2].first, it),
                    )
                })
            }
            known to scanners
        }

fun Triple<Int, Int, Int>.distance(other: Triple<Int, Int, Int>) =
    abs(other.first - first) + abs(other.second - second) + abs(other.third - third)

val transforms = listOf<(Int, Triple<Int, Int, Int>) -> Int>(
    { a, b -> a - b.first },
    { a, b -> a + b.first },
    { a, b -> a - b.second },
    { a, b -> a + b.second },
    { a, b -> a - b.third },
    { a, b -> a + b.third },
)
val selects = listOf<(Triple<Int, Int, Int>) -> Int>(
    Triple<Int, Int, Int>::first,
    Triple<Int, Int, Int>::second,
    Triple<Int, Int, Int>::third
)

fun part1(input: Pair<Set<Triple<Int, Int, Int>>, Set<Triple<Int, Int, Int>>>) {
    val res = input.first.size
    println("Part 1 = $res")
}

fun part2(input: Pair<Set<Triple<Int, Int, Int>>, Set<Triple<Int, Int, Int>>>) {
    val res = input.second.let { scanners -> scanners.flatMap { a -> scanners.map { b -> a.distance(b) } }.maxOrNull() }
    println("Part 2 = $res")
}
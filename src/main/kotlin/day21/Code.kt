package day21

import isDebug
import java.io.File
import kotlin.math.min

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
    it.last().digitToInt()
}.let { (p1, p2) -> p1 to p2 }

data class Game1(val p1Pos: Int, val p2Pos: Int, val p1Score: Int, val p2Score: Int, val turn: Int, val dice: Int)

fun part1(input: Pair<Int, Int>) {
    val res = generateSequence(Game1(input.first, input.second, 0, 0, 0, 1)) { prev ->
        prev.takeIf {
            it.p1Score < 1000 && it.p2Score < 1000
        }?.let {
            val diceSum = prev.dice + (prev.dice) % 100 + 1 + (prev.dice + 1) % 100 + 1
            if (prev.turn % 2 == 0) {
                val p1Pos = prev.p1Pos.advanceDirac(diceSum)
                Game1(p1Pos, prev.p2Pos, prev.p1Score + p1Pos, prev.p2Score, prev.turn + 1, (prev.dice + 2) % 100 + 1)
            } else {
                val p2Pos = prev.p2Pos.advanceDirac(diceSum)
                Game1(prev.p1Pos, p2Pos, prev.p1Score, prev.p2Score + p2Pos, prev.turn + 1, (prev.dice + 2) % 100 + 1)
            }
        }
    }.last().let { it.turn * 3 * min(it.p1Score, it.p2Score) }
    println("Part 1 = $res")
}

data class Score(val p1: Int, val p2: Int)
data class Position(val p1: Int, val p2: Int)

typealias UniverseCount = Long
typealias PossiblePositions = Map<Position, UniverseCount>

data class Game2(val pos: Map<Score, PossiblePositions>, val turn: Int)

fun part2(input: Pair<Int, Int>) {
    val combinations =
        (1..3).flatMap { p1 ->
            (1..3).flatMap { p2 ->
                (1..3).map { p3 -> p1 + p2 + p3 }
            }
        }.groupingBy { it }.eachCount()
    val res = generateSequence(
        Game2(mapOf(Score(0, 0) to mapOf(Position(input.first, input.second) to 1L)), 0)
    ) { prev ->
        val (won, unknown) = prev.pos.entries.partition { it.key.p1 >= 21 || it.key.p2 >= 21 }
        unknown.takeIf { it.isNotEmpty() }?.let { uncertain ->
            Game2(
                (won.flatMap { (scores, pos) -> pos.map { (positions, count) -> scores to (positions to count) } } +
                        uncertain.flatMap { (points, pos) ->
                            pos.flatMap { (positions, universes) ->
                                combinations.map { (sumOfDices, numOccurrences) ->
                                    if (prev.turn % 2 == 0) {
                                        val endPos = positions.p1.advanceDirac(sumOfDices)
                                        points.copy(p1 = points.p1 + endPos) to
                                                (positions.copy(p1 = endPos) to universes * numOccurrences)
                                    } else {
                                        val endPos = positions.p2.advanceDirac(sumOfDices)
                                        points.copy(p2 = points.p2 + endPos) to
                                                (positions.copy(p2 = endPos) to universes * numOccurrences)
                                    }
                                }
                            }
                        }).fold(mutableMapOf()) { scores, outcomes ->
                    scores.apply {
                        set(outcomes.first, getOrDefault(outcomes.first, mutableMapOf()).toMutableMap().apply {
                            set(outcomes.second.first, getOrDefault(outcomes.second.first, 0) + outcomes.second.second)
                        })
                    }
                }, prev.turn + 1
            )
        }
    }.last().pos.entries.partition { it.key.p1 >= 21 }
        .let { (p1Wins, p2Wins) ->
            maxOf(
                p1Wins.sumOf { (_, outcomes) -> outcomes.values.sum() },
                p2Wins.sumOf { (_, outcomes) -> outcomes.values.sum() }
            )
        }
    println("Part 2 = $res")
}

private fun Int.advanceDirac(sumOfDices: Int) = plus(sumOfDices - 1) % 10 + 1

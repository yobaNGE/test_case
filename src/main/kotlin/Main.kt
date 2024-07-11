package org.chiches

import kotlin.random.Random

data class Cannon(var isOperational: Boolean = true)

class Team(val name: String, val cannons: MutableList<Cannon>, val strategy: (Team, Team) -> Unit)

fun drunkCommanderStrategy(attackingTeam: Team, defendingTeam: Team) {
    attackingTeam.cannons.filter { it.isOperational }.forEach {
        val targetIndex = Random.nextInt(defendingTeam.cannons.size)
        if (Random.nextDouble() < 0.1) defendingTeam.cannons[targetIndex].isOperational = false
    }
}

fun twoPlatoonsStrategy(attackingTeam: Team, defendingTeam: Team) {
    val groupSize = attackingTeam.cannons.size / 2
    val groups = attackingTeam.cannons.chunked(groupSize)
    groups.forEach { group ->
        if (defendingTeam.cannons.any { it.isOperational }) {
            val target = defendingTeam.cannons.indexOfFirst { it.isOperational }
            group.filter { it.isOperational }.forEach {
                if (Random.nextDouble() < 0.1) defendingTeam.cannons[target].isOperational = false
            }
        }
    }
}

fun threePlatoonsStrategy(attackingTeam: Team, defendingTeam: Team) {
    val groupSize = attackingTeam.cannons.size / 3
    val groups = attackingTeam.cannons.chunked(groupSize)
    groups.forEach { group ->
        if (defendingTeam.cannons.any { it.isOperational }) {
            val target = defendingTeam.cannons.indexOfFirst { it.isOperational }
            group.filter { it.isOperational }.forEach {
                if (Random.nextDouble() < 0.1) defendingTeam.cannons[target].isOperational = false
            }
        }
    }
}

fun fourPlatoonsStrategy(attackingTeam: Team, defendingTeam: Team) {
    val groupSize = attackingTeam.cannons.size / 4
    val groups = attackingTeam.cannons.chunked(groupSize)
    groups.forEach { group ->
        if (defendingTeam.cannons.any { it.isOperational }) {
            val target = defendingTeam.cannons.indexOfFirst { it.isOperational }
            group.filter { it.isOperational }.forEach {
                if (Random.nextDouble() < 0.1) defendingTeam.cannons[target].isOperational = false
            }
        }
    }
}

fun battle(team1: Team, team2: Team): String {
    while (team1.cannons.any { it.isOperational } && team2.cannons.any { it.isOperational }) {
        team1.strategy(team1, team2)
        team2.strategy(team2, team1)
    }
    return when {
        team1.cannons.any { it.isOperational } -> team1.name
        team2.cannons.any { it.isOperational } -> team2.name
        else -> "Draw"
    }
}

fun simulateBattles(
    team1Strategy: (Team, Team) -> Unit,
    team2Strategy: (Team, Team) -> Unit,
    rounds: Int = 100000
): Map<String, Int> {
    val results = mutableMapOf("Team1" to 0, "Team2" to 0, "Draw" to 0)
    repeat(rounds) {
        val team1 = Team("Team1", MutableList(12) { Cannon() }, team1Strategy)
        val team2 = Team("Team2", MutableList(12) { Cannon() }, team2Strategy)
        val result = battle(team1, team2)
        results[result] = results[result]!! + 1
    }
    return results
}

fun main() {
    val strategies = listOf(
        ::drunkCommanderStrategy,
        ::twoPlatoonsStrategy,
        ::threePlatoonsStrategy,
        ::fourPlatoonsStrategy
    )

    val strategyNames = listOf(
        "Drunk Commander",
        "Two Platoons",
        "Three Platoons",
        "Four Platoons"
    )

    var minDifference = Double.MAX_VALUE
    var optimalCombination: Pair<String, String>? = null
    var maxDifference = Double.MIN_VALUE
    var maxCombination: Pair<String, String>? = null


    for (i in strategies.indices) {
        for (j in strategies.indices) {
            val results = simulateBattles(strategies[i], strategies[j])
            println("Strategy ${strategyNames[i]} vs ${strategyNames[j]}")
            println("Team1 wins: ${results["Team1"]}")
            println("Team2 wins: ${results["Team2"]}")
            println("Draws: ${results["Draw"]}\n")

            val totalBattles = results["Team1"]!! + results["Team2"]!! + results["Draw"]!!
            val team1WinRate = results["Team1"]!!.toDouble() / totalBattles
            val team2WinRate = results["Team2"]!!.toDouble() / totalBattles
            val drawRate = results["Draw"]!!.toDouble() / totalBattles

            val difference = kotlin.math.abs(team1WinRate - team2WinRate)

            if (difference < minDifference) {
                minDifference = difference
                optimalCombination = strategyNames[i] to strategyNames[j]
            }
            if (difference > maxDifference) {
                maxDifference = difference
                maxCombination = strategyNames[i] to strategyNames[j]
            }
        }
    }

    println("Optimal combination of strategies with minimal difference in effectiveness:")
    println("Team1 Strategy: ${optimalCombination?.first}")
    println("Team2 Strategy: ${optimalCombination?.second}")
    println("Difference in effectiveness: $minDifference")
    println("Combination of strategies with maximal difference in effectiveness:")
    println("Team1 Strategy: ${maxCombination?.first}")
    println("Team2 Strategy: ${maxCombination?.second}")
    println("Difference in effectiveness: $maxDifference")
}

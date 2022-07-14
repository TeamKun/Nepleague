package com.github.bun133.nepleague

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

class NepleagueSession(
    val answer: String,
    val question: List<Component>,
    val joinedTeam: List<Team>
) {
    private val inputs = mutableMapOf<Team, Array<NepChar>>()

    private fun getMapEntry(t: Team): Array<NepChar> {
        val e = inputs[t]
        return if (e != null) {
            e
        } else {
            inputs[t] = Array(answer.length) { NepChar() }
            inputs[t]!!
        }
    }

    fun getInput(team: Team, index: Int): NepChar? {
        return if (checkIndex(index)) {
            null
        } else {
            getMapEntry(team)[index]
        }
    }

    fun getInput(team: Team): MutableList<NepChar> {
        return getMapEntry(team).toMutableList()
    }

    fun setInput(team: Team, index: Int, input: NepChar): Boolean {
        if (checkIndex(index)) {
            return false
        }
        val e = inputs[team]
        if (e == null) {
            inputs[team] = Array(answer.length) { NepChar() }
            inputs[team]!![index] = input
        } else {
            e[index] = input
        }

        if (checkIfFinished(team)) {
            // TODO Finished Input at This Team
        }

        return true
    }

    private fun checkIfFinished(team: Team): Boolean {
        val e = inputs[team]
        return e?.all { it.isSet() } ?: false
    }

    private fun checkIndex(index: Int) = index !in answer.indices

    fun players(): List<Player> {
        return joinedTeam.map { it.entries.mapNotNull { t -> Bukkit.getOnlinePlayers().find { p -> p.name == t } } }
            .flatten()
    }

    fun sendQuestion() {
        question.forEach {
            Bukkit.broadcast(it)
        }
    }
}
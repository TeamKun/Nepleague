package net.kunlab.nepleague

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

    fun getInput(team: Team, index: Int) = getMapEntry(team)[index]
    fun getInput(team: Team): MutableList<NepChar> {
        return getMapEntry(team).toMutableList()
    }

    fun setInput(team: Team, index: Int, input: NepChar) {
        val e = inputs[team]
        if (e == null) {
            inputs[team] = Array(answer.length) { NepChar() }
            inputs[team]!![index] = input
        } else {
            e[index] = input
        }
    }

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
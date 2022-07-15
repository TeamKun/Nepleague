package com.github.bun133.nepleague

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.bukkitfly.server.plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

class NepleagueSession(
    val plugin: NepleaguePlugin,
    val answer: String,
    val question: String,
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
        if (checkIndex(index) || !joinedTeam.contains(team)) {
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

    fun remoteInput(p: Player, index: Int) = RemoteInput(plugin, p) { player, string ->
        if (string.length == 1) {
            val team = Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(player.name)
            if (team == null) {
                player.sendMessage(text("参加しているチームが見つかりませんでした", NamedTextColor.RED))
                return@RemoteInput false
            } else {
                val b = setInput(team, index, NepChar(string[0]))
                if (b) {
                    player.sendMessage(text("入力しました", NamedTextColor.GREEN))
                } else {
                    player.sendMessage(text("入力できませんでした", NamedTextColor.RED))
                }
                return@RemoteInput b
            }
        } else {
            return@RemoteInput false
        }
    }

    private fun checkIfFinished(team: Team): Boolean {
        val e = inputs[team]
        return e?.all { it.isSet() } ?: false
    }

    private fun checkIndex(index: Int) = index !in answer.toCharArray().indices

    fun players(): List<Player> {
        return joinedTeam.map { it.entries.mapNotNull { t -> Bukkit.getOnlinePlayers().find { p -> p.name == t } } }
            .flatten()
    }

    fun sendQuestion() {
        Bukkit.broadcast(text("お題:${question}"))
    }
}
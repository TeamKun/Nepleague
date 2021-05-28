package net.kunlab.nepleague

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class TeamManager(val plugin: Nepleague) : BukkitRunnable() {
    var teams = mutableListOf<Team>()
    fun addTeam(loc: Location, internalName: String) {
        addTeam(loc, internalName, "チーム$internalName")
    }

    fun addTeam(loc: Location, internalName: String, displayName: String) {
        teams.add(Team(loc, internalName, displayName, plugin))
    }

    fun checkAlreadyExists(internalName: String): Boolean {
        return teams.filter { it.internalName == internalName }.isNotEmpty()
    }

    // Showing Titles
    override fun run() {
        if (plugin.isGoingOn && plugin.resultMode == Nepleague.ResultMode.Title) {
            Bukkit.getOnlinePlayers().forEach {
                TitleProvider.getProvider(it, plugin.configManager.rayDistance, plugin.configManager.maxDistance)
                    ?.showTo(it)
            }
        }
    }
}

class Team(var loc: Location, val internalName: String, var displayName: String, plugin: Nepleague) {
    var answers = mutableMapOf<Int, Pair<Player, Char>>()
    var titleProvider: TitleProvider = TitleProvider(this, plugin = plugin)

    fun reset() {
        answers = mutableMapOf()
        titleProvider.isOpened = false
        titleProvider.isChated = false
    }

    fun getPlayers(): List<Player> {
        return answers.map { it.value.first }
    }

    fun set(index: Int, p: Player, s: Char) {
        answers[index] = Pair(p, s)
    }

    fun isCorrect(): Boolean {
        if (!titleProvider.plugin.isFinished) {
            return false
        } else {
            return titleProvider.plugin.currentString == getString(titleProvider.plugin.currentString.length)
        }
    }

    fun getString(size: Int): String {
//        println("Size:$size")
//        println("Answers:${answers}")
        val s = StringBuilder(size + 1)
        for (i in 0 until size) {
            val d = answers[i + 1]?.second
//            println("d:$d")
            if (d != null) {
                s.append(d)
//                s.setCharAt(i,d)
            } else {
                s.append('□')
//                s.setCharAt(i,'□')
            }
        }
        if (titleProvider.isOpened) {
//            println("Opened")
            val ss = s.toString()
//            println("ss:$ss")
            return ss
        } else {
            for (i in 0 until size) {
                if (s[i] != '□') {
                    s.setCharAt(i, '■')
                }
            }
            val ss = s.toString()
//            println("ss:$ss")
            return ss
        }
    }

    fun getPlayerAnswer(p: Player): Pair<Player, Char>? {
        return answers.filterValues { it.first === p }[0]
    }

    fun getAnswerIndex(ans: Pair<Player, Char>): Int? {
        val anss = answers.map { entry -> Pair(entry.key, entry.value) }.filter { it.second === ans }
        return if (anss.isNotEmpty()) {
            anss[0].first
        } else {
            null
        }
    }
}
package net.kunlab.nepleague

import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
        if (plugin.isGoingOn /*&& plugin.resultMode == Nepleague.ResultMode.Title*/) {
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
    var waiters = mutableListOf<InputWaiter>()

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
            return titleProvider.plugin.currentString == getStringRaw(titleProvider.plugin.currentString.length)
        }
    }

    fun getString(size: Int, p: Player): String {
        val s = StringBuilder(size + 1)
        for (i in 0 until size) {
            val d = answers[i + 1]?.second
            if (d != null) {
                if (titleProvider.isOpened) {
                    // ChatColorも追加
                    if (titleProvider.plugin.currentString.getOrNull(i) != null && titleProvider.plugin.currentString[i] == d) {
                        // 正解文字
                        s.append("" + ChatColor.RED + d + ChatColor.RESET)
                    } else {
                        // 不正解文字
                        s.append("" + ChatColor.BLUE + d + ChatColor.RESET)
                    }
                } else {
                    if (waiters.any { it.player == p && !it.isAlready && it.index == i + 1 }) {
                        // このプレイヤーが入力中のInputWaiterあり
                        s.append("" + ChatColor.YELLOW + d + ChatColor.RESET)
                    } else {
                        // なし
                        s.append(d)
                    }
                }
            } else {
                if (waiters.any { it.player == p && !it.isAlready && it.index == i + 1 }) {
                    // このプレイヤーが入力中のInputWaiterあり
                    if (titleProvider.isOpened) {
                        s.append("" + ChatColor.BLUE + '□' + ChatColor.RESET)
                    } else {
                        s.append("" + ChatColor.YELLOW + '□' + ChatColor.RESET)
                    }
                } else {
                    // なし
                    if (titleProvider.isOpened) {
                        s.append("" + ChatColor.BLUE + '_' + ChatColor.RESET)
                    } else {
                        s.append('□')
                    }
                }
            }
        }
        if (titleProvider.isOpened) {
            return s.toString()
        } else {
            for (i in 0 until size) {
                if (isMatchValid(s[i])) {
                    s.setCharAt(i, '■')
                } else {
//                    s.setCharAt(i, '_')
                }
            }

            s.toString()
                .indexedFilter { it == '_' || it == '■' || isMatchValid(it) }
                .forEach {
                    val pp = answers[it.first + 1]?.first
                    if (p == pp) {
                        // この文字は見ているプレイヤーが入力した
                        s.setCharAt(it.first, answers[it.first + 1]!!.second)
                    }
                }

            return s.toString()
        }
    }

    fun getStringRaw(size: Int): String {
        val s = StringBuilder(size + 1)
        for (i in 0 until size) {
            val d = answers[i + 1]?.second
            if (d != null) {
                if (titleProvider.isOpened) {
                    // ChatColorも追加
                    if (titleProvider.plugin.currentString.getOrNull(i) != null && titleProvider.plugin.currentString[i] == d) {
                        // 正解文字
                        s.append(d)
                    } else {
                        // 不正解文字
                        s.append(d)
                    }
                } else {
                    s.append(d)
                }
            } else {
                if (titleProvider.isOpened) {
                    s.append('_')
                } else {
                    s.append('□')
                }
            }
        }
        if (titleProvider.isOpened) {
            return s.toString()
        } else {
            for (i in 0 until size) {
                if (s[i] != '□') {
                    s.setCharAt(i, '■')
                } else {
                    s.setCharAt(i, '□')
                }
            }
            return s.toString()
        }
    }

    fun getStringWithPlayer(size: Int): MutableList<Pair<String, Player?>> {
        val list = mutableListOf<Pair<String, Player?>>()
        for (i in 0 until size) list.add(Pair("", null))

        for (i in 0 until size) {
            val d = answers[i + 1]?.second
            if (d != null) {
                if (titleProvider.isOpened) {
                    // ChatColorも追加
                    if (titleProvider.plugin.currentString.getOrNull(i) != null && titleProvider.plugin.currentString[i] == d) {
                        // 正解文字
                        list[i] = Pair("" + ChatColor.RED + d + ChatColor.RESET, answers[i + 1]?.first)
                    } else {
                        // 不正解文字
                        list[i] = Pair("" + ChatColor.BLUE + d + ChatColor.RESET, answers[i + 1]?.first)
                    }
                } else {
                    list[i] = Pair("" + d, answers[i + 1]?.first)
                }
            } else {
                if (titleProvider.isOpened) {
                    list[i] = Pair("" + ChatColor.BLUE + '_' + ChatColor.RESET, null)
                } else {
                    list[i] = Pair("" + '□', null)
                }
            }
        }
        if (titleProvider.isOpened) {
            return list
        } else {
            for (i in 0 until size) {
                if (list[i].first != "□") {
                    list[i] = Pair("■", answers[i + 1]?.first)
                } else {
                    list[i] = Pair("□", answers[i + 1]?.first)
                }
            }
            return list
        }
    }

    fun getPlayerAnswer(p: Player): Map<Int, Pair<Player, Char>> {
        return answers.filterValues { it.first === p }
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
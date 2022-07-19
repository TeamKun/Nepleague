package com.github.bun133.nepleague

import com.github.bun133.bukkitfly.component.text
import com.github.bun133.tinked.RunnableTask
import com.github.bun133.tinked.WaitTask
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team

class NepleagueSession(
    val plugin: NepleaguePlugin,
    val answer: String,
    val joinedTeam: List<Team>,
    private val checker: StringChecker
) {
    private val inputs = mutableMapOf<Team, Array<NepChar>>()

    // <editor-fold desc="getinputs">
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

    // </editor-fold>
    fun setInput(team: Team, index: Int, input: NepChar): Boolean {
        if (isDestroyed) return true
        if (checkIndex(index) || !joinedTeam.contains(team)) {
            return false    // invalid index or not joined team
        }
        if (input.char != null && !checker.check(input.char!!)) {
            return false    // invalid input
        }
        val e = inputs[team]
        if (e == null) {
            inputs[team] = Array(answer.length) { NepChar() }
            inputs[team]!![index] = input
        } else {
            e[index] = input
        }

        broadCastState(SessionState.WAITING_INPUT, team, index) // 再描画

        if (checkIfFinished()) {
            onFinished()
        }

        return true
    }

    fun remoteInput(p: Player, index: Int) = RemoteInput(plugin, p) { player, string ->
        if (string.length == 1) {
            val team = Bukkit.getScoreboardManager().mainScoreboard.getEntryTeam(player.name)
            if (team == null) {
                player.sendMessage(text("参加しているチームが見つかりませんでした", NamedTextColor.RED))
                return@RemoteInput true
            } else {
                val session = plugin.session()
                if (session != null && !session.isFinished) {
                    val b = session.setInput(team, index, NepChar(string[0]))
                    if (b) {
                        player.sendMessage(text("入力しました", NamedTextColor.GREEN))
                    } else {
                        player.sendMessage(text("問題の指示に従った回答ではありません (ひらがななどの指定がありませんか?)", NamedTextColor.RED))
                    }
                    return@RemoteInput true
                } else {
                    player.sendMessage(text("今は回答時間ではありません", NamedTextColor.RED))
                    return@RemoteInput true
                }
            }
        } else {
            player.sendMessage(text("1文字で入力してください!(このまま入力できます)", NamedTextColor.RED))
            return@RemoteInput false
        }
    }

    private fun checkIfFinished(): Boolean {
        val e = joinedTeam.map { inputs[it] }
        return if (e.any { it == null }) {
            false
        } else {
            e.all { t -> t?.all { it.isSet() } ?: false }
        }
    }

    private fun checkIndex(index: Int) = index !in answer.toCharArray().indices

    fun players(): List<Player> {
        return joinedTeam.map { it.entries.mapNotNull { t -> Bukkit.getOnlinePlayers().find { p -> p.name == t } } }
            .flatten()
    }

    // <editor-fold desc="drawers">
    private val drawers = mutableMapOf<Team, MutableMap<Int, NepleagueDrawer>>()

    private fun getDrawers(team: Team) = answer.toCharArray().indices.map { getDrawer(team, it) }
    private fun getDrawer(team: Team, index: Int): NepleagueDrawer? {
        val e = drawers[team]
        val map = if (e != null) {
            e
        } else {
            val mp = mutableMapOf<Int, NepleagueDrawer>()
            drawers[team] = mp
            drawers[team]!!
        }

        val drawer = map[index]
        if (drawer != null) {
            return drawer
        }

        val newDrawer = NepleagueDrawer.get(this, team, index)
        return if (newDrawer != null) {
            map[index] = newDrawer
            newDrawer
        } else {
            null
        }
    }
    // </editor-fold>

    private fun broadCastState(state: SessionState) {
        joinedTeam.map { getDrawers(it) }.flatten().filterNotNull().forEach { it.draw(state) }
    }

    private fun broadCastState(state: SessionState, team: Team, index: Int) {
        getDrawer(team, index)?.draw(state)
    }

    fun start() {
//        Bukkit.broadcast(text("お題:${question}"))
        broadCastState(SessionState.WAITING_INPUT)
    }

    private var isFinished = false
    private fun onFinished() {
        isFinished = true
        Bukkit.broadcast(text("すべてのチームが回答を入力したので、結果発表を行います", NamedTextColor.GREEN))
        WaitTask<Unit>(plugin.config.answerAnnounceDelay.value().toLong(), plugin).apply(RunnableTask {
            // Delayed task
            Bukkit.broadcast(text("正解: $answer", NamedTextColor.GREEN))
            Bukkit.broadcast(text(""))
            val result = judgeInput(answer, inputs.toList())
            broadCastResults(result)
            broadCastState(SessionState.OPEN_ANSWER)
        }).run(Unit)
    }

    private fun broadCastResults(r: List<Pair<Team, Pair<List<NepleagueResult>?, NepleagueResult>>>) {
        fun broadCastResult(r: Pair<Team, Pair<List<NepleagueResult>?, NepleagueResult>>) {
            val color = r.second.second.toColor()
            val wrongCount = r.second.first?.count { it == NepleagueResult.Wrong } ?: 0

            Bukkit.broadcast(text("[${r.second.second.displayString}] チーム${r.first.name} ${wrongCount}ミス", color))
        }

        val sorted = r.sortedBy { it.second.second.ordinal }
        sorted.forEach {
            broadCastResult(it)
        }
    }

    init {
        broadCastState(SessionState.BEFORE_START)
    }

    private var isDestroyed = false
    fun destroy() {
        if (isDestroyed) {
            return
        }
        isDestroyed = true
        broadCastState(SessionState.BEFORE_START)   // まっさらに戻す
    }
}

enum class SessionState {
    BEFORE_START,   // 開始前
    WAITING_INPUT,  // 入力中
    OPEN_ANSWER,    // 答え合わせの時間
}
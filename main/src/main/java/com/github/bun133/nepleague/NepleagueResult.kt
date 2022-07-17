package com.github.bun133.nepleague

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.scoreboard.Team

enum class NepleagueResult(val displayString: String) {
    Correct("正解"),
    Wrong("不正解"),
    NoInput("未入力"),
    Ignored("") // 空文字列のまま行きます(仕様上このEnumがResultになることはないはずなので)
}

fun NepleagueResult.toColor(): NamedTextColor {
    return when (this) {
        NepleagueResult.Correct -> NamedTextColor.DARK_RED
        NepleagueResult.Wrong -> NamedTextColor.BLUE
        NepleagueResult.NoInput -> NamedTextColor.GRAY
        NepleagueResult.Ignored -> NamedTextColor.LIGHT_PURPLE
    }
}

fun judgeInput(answer: String, input: NepChar?, index: Int): NepleagueResult {
    if (input == null) {
        return NepleagueResult.NoInput
    }
    val char = input.char
    return if (char != null) {
        val ans = answer.getOrNull(index) ?: return NepleagueResult.Ignored // 答えよりも入力が多い場合は無視する
        if (char == ans) {
            NepleagueResult.Correct
        } else {
            NepleagueResult.Wrong
        }
    } else {
        NepleagueResult.NoInput
    }
}

fun judgeInput(answer: String, input: Array<NepChar>): List<NepleagueResult>? {
    return if (answer.length != input.size) {
        null
    } else {
        input.mapIndexed { index, nepChar ->
            judgeInput(answer, nepChar, index)
        }
    }
}

fun judgeInput(
    answer: String,
    all: List<Pair<Team, Array<NepChar>>>
): List<Pair<Team, Pair<List<NepleagueResult>?, NepleagueResult>>> {
    fun mapResult(result: List<NepleagueResult>?): NepleagueResult {
        if (result == null) {
            // おかしいぽ
            throw IllegalArgumentException("result is null")
        } else {
            return if (result.any { it == NepleagueResult.Wrong }) {
                NepleagueResult.Wrong
            } else if (result.any { it == NepleagueResult.NoInput }) {
                NepleagueResult.NoInput
            } else {
                NepleagueResult.Correct
            }
        }
    }

    val judges = all.map { (team, input) ->
        val result = judgeInput(answer, input)
        team to (result to mapResult(result))
    }

    return judges
}
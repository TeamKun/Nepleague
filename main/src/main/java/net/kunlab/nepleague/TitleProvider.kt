package net.kunlab.nepleague

import com.github.bun133.flylib2.utils.ComponentUtils
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.time.Duration

class TitleProvider(val team: Team, val plugin: Nepleague) {
    init {
        providers.add(this)
    }

    var isOpened = false
    var isChated = false

    companion object {
        val providers = mutableListOf<TitleProvider>()
        fun getProvider(player: Player, plugin: Nepleague): TitleProvider? {
            return getProvider(player, plugin.configManager.rayDistance, plugin.configManager.maxDistance)
        }

        fun getProvider(player: Player, rayDistance: Double, maxDistance: Double): TitleProvider? {
//            if (providers.map { it.team }.any { it.getPlayers().contains(player) }) {
            // playerがどこかのチームに所属している場合
//                val ps = providers.filter { it.team.getPlayers().contains(player) }
//                if (ps.size == 1) {
//                    return ps[0]
//                } else {
//                    println("ERROR:In getProvider,Player(${ComponentUtils.toText(player.displayName())}) must have answered at two position!")
//                    player.sendMessage("ERROR:In getProvider,You must answered at two position!")
//                    player.sendMessage("" + ChatColor.RED + "2つ以上の場所で回答しないでください!")
//                }
//            } else {
            // playerがどこかのチームに所属していない場合
            val block = player.rayTraceBlocks(rayDistance)
            if (block != null && block.hitBlock != null) {
                val distance = providers.map { Pair(it, it.team.loc.distance(block.hitBlock!!.location)) }
                    .filter { it.second <= maxDistance }.minByOrNull { it.second }
                if (distance != null) {
                    return distance.first
                } else {
                    // ブロックにhitしたが範囲内にTeamがいないとき(非表示)
                    return null
                }
            } else {
                // どのブロックにもhitしないとき(非表示)
                return null
            }
//            }
//            return null
        }
    }

    fun showTo(p: Player) {
        if (team.getPlayers().contains(p) && !isOpened) {
            //チーム内
            val s = team.getString(plugin.currentString.length, p)
            val an = team.getPlayerAnswer(p)
            if (an.isEmpty()) {
                // チーム内だけど何も入力済みじゃない
                p.showTitle(
                    Title.title(
                        ComponentUtils.fromText(s),
                        ComponentUtils.fromText("" + getAnsColor(team) + team.displayName),
                        Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                    )
                )
            } else {
                // TODO たぶんというか確実にバグる(ChatColorが2文字分持ってってしまう)
                var last = 0
                an.forEach { (t, u) ->
                    s.indexedFilter { it == '_' || it == '■' || isMatchValid(it) }
                        .forEach {
                            s.replaceRange(it.first, it.first, u.second.toString())
                        }
                }

                p.showTitle(
                    Title.title(
                        ComponentUtils.fromText(s),
                        ComponentUtils.fromText("" + getAnsColor(team) + team.displayName),
                        Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                    )
                )
            }
        } else {
            // チーム外

            p.showTitle(
                Title.title(
                    ComponentUtils.fromText(team.getString(plugin.currentString.length, p)),
                    ComponentUtils.fromText("" + getAnsColor(team) + team.displayName),
                    Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                )
            )
        }
    }

    fun getAnsColor(team: Team): ChatColor {
        return when {
            !team.titleProvider.isOpened -> {
                ChatColor.WHITE
            }
            team.isCorrect() -> {
                ChatColor.RED
            }
            else -> ChatColor.BLUE
        }
    }
}

fun String.filter(f: (Char) -> Boolean): List<Char> {
    return this.toCharArray().filter(f)
}

fun String.indexed(): List<Pair<Int, Char>> {
    return this.toCharArray().mapIndexed { index, c -> Pair(index, c) }
}

fun String.indexedFilter(f: (Char) -> Boolean): List<Pair<Int, Char>> {
    return this.indexed().filter { f(it.second) }
}
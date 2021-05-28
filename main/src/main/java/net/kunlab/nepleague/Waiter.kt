package net.kunlab.nepleague

import com.github.bun133.flylib2.utils.ComponentUtils
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable

class InputWaiter(val team: Team, val index: Int, val player: Player, val plugin: Nepleague) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        player.sendMessage("1文字ひらがなを入力してください")
    }

    var isAlready = false

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        if (e.player === player && !isAlready && !plugin.isFinished) {
            val s = ComponentUtils.toText(e.message())
            if (s.length == 1 && s.matches(Regex("^[\\u3040-\\u309F]+\$"))) {
                team.set(index, player, s[0])
                isAlready = true
                player.sendMessage("「${s[0]}」を入力しました")
            } else {
                player.sendMessage("1文字ひらがなを入力してください")
            }
            e.isCancelled = true
        }
    }
}

/**
 * 回答発表系
 */
class RightClickWaiter(private val plugin: Nepleague) : Listener, BukkitRunnable() {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        this.runTaskTimer(plugin, 0, 1)
    }

    val players = mutableListOf<Player>()

    // 一回クリックしたつもりでも何回も発火するので
    val waiter = mutableMapOf<Player, Int>()

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (waiter.containsKey(e.player) && waiter[e.player]!! > 0) {
            return
        }
        if (players.contains(e.player)) {
            if (!plugin.isFinished) {
                e.player.sendMessage("回答を締め切っていませんよ...")
                e.player.sendMessage("/nep finishですよ...")
                return
            }
            when (plugin.resultMode) {
                Nepleague.ResultMode.Chat -> {
                    Bukkit.broadcastMessage("結果発表!")
                    Bukkit.broadcastMessage("模範解答:${plugin.currentString}")
                    plugin.teamManager.teams
                        // 重複表示回避処理
                        .filter { !it.titleProvider.isChated }
                        .forEach { team ->
                            team.titleProvider.isOpened = true
                            team.titleProvider.isChated = true

                            val comps = team.getString(plugin.currentString.length).mapIndexed { index, c ->
                                Pair(team.answers[index + 1]?.first, c)
                            }.map {
                                if (it.first != null) {
                                    ComponentUtils.fromText("" + it.second)
                                        .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(it.first!!.displayName()))
                                } else {
                                    ComponentUtils.fromText("" + it.second)
                                        .hoverEvent(
                                            net.kyori.adventure.text.event.HoverEvent.showText(
                                                ComponentUtils.fromText(
                                                    "[無回答]"
                                                )
                                            )
                                        )
                                }
                            }

//                            val comps = team.answers.map {
//                                ComponentUtils.fromText("" + it.value.second)
//                                    .hoverEvent(net.kyori.adventure.text.event.HoverEvent.showText(it.value.first.displayName()))
//                            }

                            var comp = if (team.isCorrect()) {
                                ComponentUtils.fromText("" + ChatColor.BLUE + "${team.displayName}:" + ChatColor.RESET)
                            } else {
                                ComponentUtils.fromText("" + ChatColor.RED + "${team.displayName}:" + ChatColor.RESET)
                            }

                            comps.forEach {
                                comp = comp.append(it)
                            }

                            Bukkit.getOnlinePlayers().forEach { player ->
                                player.sendMessage(comp)
                            }
//                        使えません残念でした！！！！
//                        Bukkit.broadcast(comp)
                        }
                }
                Nepleague.ResultMode.Title -> {
                    // TODO 演出
                    TitleProvider.getProvider(e.player, plugin)?.isOpened = true
                }
            }

            waiter[e.player] = 10
        }
    }

    override fun run() {
        waiter.forEach { (t, u) ->
            if (u > 0) {
                waiter[t] = u - 1
            }
        }
    }
}
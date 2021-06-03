package net.kunlab.nepleague

import com.github.bun133.flylib2.utils.ComponentUtils
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
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
        if (!(e.action == Action.RIGHT_CLICK_AIR || e.action == Action.RIGHT_CLICK_BLOCK)) {
            return
        }

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
                    // Nothing
                }
                Nepleague.ResultMode.Title -> {
                    // TODO 演出
                    val provider = TitleProvider.getProvider(e.player, plugin)
                    if (provider != null) {
                        provider.isOpened = true
                        if(provider.team.isCorrect()){
                            Sounds.Correct.sound()
                        }else{
                            Sounds.Wrong.sound()
                        }
                    }
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
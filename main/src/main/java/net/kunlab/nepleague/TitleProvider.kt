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
        if (team.getPlayers().contains(p)) {
            //チーム内
//            println("In Team")
            val s = team.getString(plugin.currentString.length)
            val an = team.getPlayerAnswer(p)
            if (an == null) {
//                println("Ans null")
                p.showTitle(
                    Title.title(
                        ComponentUtils.fromText(s),
                        ComponentUtils.fromText(team.displayName),
                        Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                    )
                )
            } else {
                val index = team.getAnswerIndex(an)
//                println("Ans:${an.second} index:$index")
                if (index != null) {
                    val sb = StringBuilder(s)
                    sb.setCharAt(index, an.second)
                    p.showTitle(
                        Title.title(
                            ComponentUtils.fromText(sb.toString()),
                            ComponentUtils.fromText(team.displayName),
                            Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                        )
                    )
                } else {
                    println("ERROR:In TitleProvider.showTo(player(${ComponentUtils.toText(p.displayName())}))")
                }
            }
        } else {
//            println("Not in Team")
            p.showTitle(
                Title.title(
                    ComponentUtils.fromText(team.getString(plugin.currentString.length)),
                    ComponentUtils.fromText(team.displayName),
                    Title.Times.of(Duration.ofSeconds(0), Duration.ofSeconds(1), Duration.ofSeconds(0))
                )
            )
        }
    }
}
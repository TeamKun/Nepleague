package com.github.bun133.nepleague

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.TextComponent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class RemoteInput(plugin: NepleaguePlugin, private val player: Player, val f: (Player, String) -> Boolean) : Listener {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private var isResolved = false

    @EventHandler
    fun onChat(e: AsyncChatEvent) {
        if (isResolved) return
        if (e.player == player) {
            val comp = e.originalMessage() as? TextComponent ?: return
            val text = comp.content()
            val b = f(e.player, text)
            if (b) {
                e.isCancelled = true
                isResolved = true
            }
        }
    }
}
package net.kunlab.nepleague

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun error(comp: String, color: ChatColor = ChatColor.RED) {
    Bukkit.broadcastMessage("" + color + comp)
}

fun log(comp: String, color: ChatColor = ChatColor.WHITE) {
    Bukkit.broadcastMessage("" + color + comp)
}

fun warn(comp: String, color: ChatColor = ChatColor.YELLOW) {
    Bukkit.broadcastMessage("" + color + comp)
}

fun info(comp: String, color: ChatColor = ChatColor.AQUA) {
    Bukkit.broadcastMessage("" + color + comp)
}

fun error(comp: String, p: Player, color: ChatColor = ChatColor.RED) {
    p.sendMessage("" + color + comp)
}

fun log(comp: String, p: Player, color: ChatColor = ChatColor.WHITE) {
    p.sendMessage("" + color + comp)
}

fun warn(comp: String, p: Player, color: ChatColor = ChatColor.YELLOW) {
    p.sendMessage("" + color + comp)
}

fun info(comp: String, p: Player, color: ChatColor = ChatColor.AQUA) {
    p.sendMessage("" + color + comp)
}

fun error(comp: String, c: CommandSender, color: ChatColor = ChatColor.RED) {
    c.sendMessage("" + color + comp)
}

fun log(comp: String, c: CommandSender, color: ChatColor = ChatColor.WHITE) {
    c.sendMessage("" + color + comp)
}

fun warn(comp: String, c: CommandSender, color: ChatColor = ChatColor.YELLOW) {
    c.sendMessage("" + color + comp)
}

fun info(comp: String, c: CommandSender, color: ChatColor = ChatColor.AQUA) {
    c.sendMessage("" + color + comp)
}
package com.github.bun133.nepleague

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.DoubleValue
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.StringValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue
import net.kunmc.lab.configlib.value.map.Team2LocationMapValue
import org.bukkit.plugin.Plugin
import java.awt.Color
import java.awt.Font
import java.io.File

class NepleagueConfig(plugin: NepleaguePlugin) : BaseConfig(plugin) {
    val answerAnnounceDelay = IntegerValue(20 * 3, 0, Integer.MAX_VALUE)
    val team = TeamSetValue()

    val displays = Team2LocationMapValue()

    val drawerConfig = DrawerConfig(plugin)
}

class DrawerConfig(val plugin: Plugin) : BaseConfig(plugin) {
    val fontSize = IntegerValue(10, 1, 100)
    private val fontColor = IntegerValue(0x000000, 0, 0xFFFFFF)
    fun fontColor() = Color(fontColor.value())

    private val fontFileName = StringValue("MPLUSRounded1c-Light.ttf")
    fun font(): Font {
        // TODO Performance issue
        return Font.createFont(
            Font.TRUETYPE_FONT,
            plugin.getResource(fontFileName.value())
        ).deriveFont(fontSize.value().toFloat())
    }

    private val defaultBackGroundColor = IntegerValue(0xFFFFFF, 0, 0xFFFFFF)
    fun defaultBackGroundColor() = Color(defaultBackGroundColor.value())

    private val correctAnswerColor = IntegerValue(0xFF0000, 0, 0xFFFFFF)
    fun correctAnswerColor() = Color(correctAnswerColor.value())

    private val wrongAnswerColor = IntegerValue(0x0000FF, 0, 0xFFFFFF)
    fun wrongAnswerColor() = Color(wrongAnswerColor.value())
}
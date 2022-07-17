package com.github.bun133.nepleague

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.LocationValue
import net.kunmc.lab.configlib.value.StringValue
import net.kunmc.lab.configlib.value.TeamValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue
import net.kunmc.lab.configlib.value.map.Team2LocationMapValue
import org.bukkit.plugin.Plugin
import java.awt.Color
import java.awt.Font

class NepleagueConfig(plugin: NepleaguePlugin) : BaseConfig(plugin) {
    val answerAnnounceDelay = IntegerValue(20 * 3, 0, Integer.MAX_VALUE)
    val team = TeamSetValue()

    val displays: MutableList<DisplayConfig> = mutableListOf()

    val drawerConfig = DrawerConfig(plugin)
}

class DrawerConfig(
    @Transient
    val plugin: Plugin
) : BaseConfig(plugin) {
    val fontSize = IntegerValue(200, 1, 10000)
    private val fontColor = IntegerValue(0xFFFFFF, 0, 0xFFFFFF)
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

class DisplayConfig() {
    val teamValue: TeamValue = TeamValue()
    val displayLocations: MutableList<DisplayEntry> = mutableListOf()
}

class DisplayEntry() {
    val displayLocation = LocationValue()
    val index = IntegerValue(0, 0, Integer.MAX_VALUE)
}
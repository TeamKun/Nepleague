package com.github.bun133.nepleague

import net.kunmc.lab.configlib.BaseConfig
import net.kunmc.lab.configlib.value.IntegerValue
import net.kunmc.lab.configlib.value.LocationValue
import net.kunmc.lab.configlib.value.StringValue
import net.kunmc.lab.configlib.value.TeamValue
import net.kunmc.lab.configlib.value.collection.TeamSetValue
import org.bukkit.plugin.Plugin
import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

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
    val fontSize : IntegerValue = IntegerValue(200, 1, 10000)
        .apply {
            this.onModify { value ->
                cachedFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    plugin.getResource(fontFileName.value())
                ).deriveFont(value.toFloat())
            }
        }

    // メインの文字色
    private val fontColor = IntegerValue(0x000000, 0, 0xFFFFFF)
    fun fontColor() = Color(fontColor.value())

    // 正誤発表時の文字色
    private val fontColorFlipped = IntegerValue(0xFFFFFF, 0, 0xFFFFFF)
    fun flippedFontColor() = Color(fontColorFlipped.value())

    private val fontFileName = StringValue("MPLUSRounded1c-Light.ttf")
        .apply {
            this.onModify { value ->
                cachedFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    plugin.getResource(value)
                ).deriveFont(fontSize.value().toFloat())
            }
        }

    @Transient
    private var cachedFont: Font? = null
    fun font(): Font {
        if (cachedFont == null) {
            cachedFont = Font.createFont(
                Font.TRUETYPE_FONT,
                plugin.getResource(fontFileName.value())
            ).deriveFont(fontSize.value().toFloat())
        }
        return cachedFont!!
    }

    private val defaultBackGroundColor = IntegerValue(0xFFFFFF, 0, 0xFFFFFF)
    fun defaultBackGroundColor() = Color(defaultBackGroundColor.value())

    private val whileInputImage = StringValue("inputting.png")

    @Transient
    private var cachedWhileInputImage: BufferedImage? = null
    fun whileInputImage(): BufferedImage {
        if (cachedWhileInputImage == null) {
            cachedWhileInputImage = ImageIO.read(plugin.getResource(whileInputImage.value()))
        }
        return cachedWhileInputImage!!
    }

    private val inputtedImage = StringValue("inputted.png")

    @Transient
    private var cachedInputtedImage: BufferedImage? = null
    fun inputtedImage(): BufferedImage {
        if (cachedInputtedImage == null) {
            cachedInputtedImage = ImageIO.read(plugin.getResource(inputtedImage.value()))
        }
        return cachedInputtedImage!!
    }
}

class DisplayConfig() {
    val teamValue: TeamValue = TeamValue()
    val displayLocations: MutableList<DisplayEntry> = mutableListOf()
}

class DisplayEntry() {
    val displayLocation = LocationValue()
    val index = IntegerValue(0, 0, Integer.MAX_VALUE)
}
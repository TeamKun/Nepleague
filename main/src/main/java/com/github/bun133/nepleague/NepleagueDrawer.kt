package com.github.bun133.nepleague

import com.github.bun133.bukkitfly.component.toAWTColor
import com.github.bun133.nepleague.map.MapDisplay
import org.bukkit.scoreboard.Team
import java.awt.Color
import java.awt.Graphics2D

class NepleagueDrawer(
    private val session: NepleagueSession,
    private val team: Team,
    private val index: Int,
    private val display: MapDisplay
) {
    companion object {
        fun get(session: NepleagueSession, team: Team, index: Int): NepleagueDrawer? {
            val displays = session.plugin.displayProvider.getDisplays(team)
            val display = displays[index]
            if (display != null) {
                return NepleagueDrawer(session, team, index, display)
            }
            return null
        }
    }


    private val conf = session.plugin.config.drawerConfig
    fun draw(state: SessionState) {
        println("Drawing:${state}")
        when (state) {
            SessionState.BEFORE_START -> {
                // 全部背景色にする
                drawBackGround()
            }
            SessionState.WAITING_INPUT -> {
                // TODO 入力してるかしてないかで見た目を変える
                // 全部背景色にする
                drawBackGround()
                flushWithCharInfo { graphics2D, nepChar ->
                    println("flushWithCharInfo")
                    if (nepChar != null && nepChar.isSet()) {
                        // 入力済み
                        drawNepChar(graphics2D, NepChar('済'), conf.fontColor())
                    } else {
                        // 未入力
                        drawNepChar(graphics2D, NepChar('未'), conf.fontColor())
                    }
                }
            }
            SessionState.OPEN_ANSWER -> {
                flushWithResultInfo { graphics2D, nepChar, nepleagueResult ->
                    drawBackGround(nepleagueResult.toColor().toAWTColor())
                    if (nepChar != null && index == this@NepleagueDrawer.index) {
                        drawNepChar(graphics2D, nepChar)
                    } else {
                        // DO Nothing
                        // 未入力のままここまできちゃったんだね...
                    }
                }
            }
        }
    }

    private fun drawBackGround(color: Color = conf.defaultBackGroundColor()) {
        display.flush {
            it.color = color
            val w = display.pixelWidth()
            val h = display.pixelHeight()
            it.fillRect(0, 0, w, h)
        }
    }

    private fun drawNepChar(g: Graphics2D, nepChar: NepChar, color: Color = conf.flippedFontColor()) {
        if (nepChar.isSet()) {
            g.font = conf.font()
            g.color = color
            val metrics = g.fontMetrics
            val s = nepChar.char!!.toString()
            val x = (display.pixelWidth() - metrics.stringWidth(s)) / 2
            val y = (display.pixelHeight() - metrics.height) / 2 + metrics.ascent
            g.drawString(s, x, y)
        } else {
            // DO NOTHING
        }
    }

    /**
     * @param f (Graphics2D,NepChar
     */
    private fun flushWithCharInfo(f: (Graphics2D, NepChar?) -> Unit) {
        display.flush { g ->
            f(g, session.getInput(team, index))
        }
    }

    private fun flushWithResultInfo(f: (Graphics2D, NepChar?, NepleagueResult) -> Unit) {
        flushWithCharInfo { g, nepChar ->
            f(g, nepChar, judgeInput(session.answer, nepChar, index))
        }
    }
}
package com.github.bun133.nepleague

import com.github.bun133.nepleague.map.MapDisplay
import org.bukkit.scoreboard.Team
import java.awt.Graphics2D

class NepleagueDrawer(private val session: NepleagueSession, private val team: Team, private val display: MapDisplay) {
    private constructor(session: NepleagueSession, team: Team) : this(
        session,
        team,
        session.plugin.displayProvider.getDisplay(team)!!
    )

    companion object {
        fun get(session: NepleagueSession, team: Team): NepleagueDrawer? {
            val display = session.plugin.displayProvider.getDisplay(team)
            return if (display != null) {
                NepleagueDrawer(session, team, display)
            } else {
                null
            }
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
                // TODO もっとしゃれおつに
                // 全部背景色にする
                drawBackGround()
            }
            SessionState.OPEN_ANSWER -> {
                drawBackGround()
                flushAllWithCharInfo { graphics2D: Graphics2D, nepChar: NepChar?, index: Int, pair: Pair<Int, Int> ->
                    if (nepChar != null) {
                        val color = conf.fontColor()
                        val font = conf.font()
                        graphics2D.color = color
                        graphics2D.font = font

                        drawNepChar(graphics2D, pair, nepChar)
                        // TODO 背景の色を正誤で変える
                    } else {
                        // DO Nothing
                        // 未入力のままここまできちゃったんだね...
                    }
                }
            }
        }
    }

    private fun drawBackGround() {
        display.flush {
            it.color = conf.defaultBackGroundColor()
            val w = display.pixelWidth()
            val h = display.pixelHeight()
            it.fillRect(0, 0, w, h)
        }
    }

    private fun drawNepChar(g: Graphics2D, left_up: Pair<Int, Int>, nepChar: NepChar) {
        val centerX = left_up.first + 128 / 2

        if (nepChar.isSet()) {
            // TODO NotDrawing
            g.font = conf.font()
            val metrics = g.fontMetrics
            val s = nepChar.char!!.toString()
            val x = centerX - metrics.stringWidth(s) / 2
            val y = (128 - metrics.height) / 2 + metrics.ascent
            g.color = conf.fontColor()
            g.drawString(s, x, y)
        } else {
            // DO NOTHING
        }
    }

    /**
     * @param f (Graphics2D,NepChar,Index,Pair(left_up_x,left_up_y))
     */
    private fun flushAllWithCharInfo(f: (Graphics2D, NepChar?, Int, Pair<Int, Int>) -> Unit) {
        flushAll { g, index, pair ->
            f(g, session.getInput(team, index), index, pair)
        }
    }

    /**
     * @param f (Graphics2D,Index,Pair(left_up_x,left_up_y))
     */
    private fun flushAll(f: (Graphics2D, Int, Pair<Int, Int>) -> Unit) {
        display.flush {
            for (x in 0 until display.pixelWidth() / 128) {
                f(it, x, Pair(x * 128, 0))
            }
        }
    }
}
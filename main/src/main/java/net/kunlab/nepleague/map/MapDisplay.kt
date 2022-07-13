package net.kunlab.nepleague.map

import net.kunlab.nepleague.util.BoxedArray
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class MapDisplay(private val maps: BoxedArray<MapSingleDisplay>) : Display {
    private val buf = BufferedImage(128 * maps.width, 128 * maps.height, BufferedImage.TYPE_INT_ARGB)
    override fun flush(f: (Graphics2D) -> Unit) {
        val g = buf.graphics as Graphics2D
        f(g)
        g.dispose()
        syncToChildren()
    }

    private fun syncToChildren() {
        maps.toList().forEach { (x, y, t) ->
            val b = buf.getSubimage(x * 128, y * 128, 128, 128)
            t.mapRenderer?.buf = b  // おいKotlinこれはいいのか
        }
    }
}
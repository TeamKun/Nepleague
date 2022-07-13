package net.kunlab.nepleague.map

import java.awt.Graphics2D

interface Display {
    fun flush(f: (Graphics2D) -> Unit)
}
package congard.agh.evolution.gui.simulator

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.Pane
import java.awt.DisplayMode
import java.awt.GraphicsEnvironment

abstract class CanvasPane : Pane() {
    private val frameMs = GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.displayMode.refreshRate.let {
        when (it) {
            DisplayMode.REFRESH_RATE_UNKNOWN -> 17 // 60 FPS
            else -> 1000 / it
        }
    }

    private var prevFrameMs = 0L

    private val canvas = Canvas()

    init {
        children.add(canvas)
    }

    fun draw() {
        // limit FPS to prevent freezing
        if (System.currentTimeMillis() - prevFrameMs < frameMs)
            return

        prevFrameMs = System.currentTimeMillis()

        val g = canvas.graphicsContext2D
        g.clearRect(0.0, 0.0, canvas.width, canvas.height)

        onDraw(g)
    }

    fun invalidate() {
        prevFrameMs = 0
        draw()
    }

    protected abstract fun onDraw(g: GraphicsContext)

    protected fun availableWidth() =
        canvas.width

    protected fun availableHeight() =
        canvas.height

    override fun layoutChildren() {
        val top = snappedTopInset()
        val right = snappedRightInset()
        val bottom = snappedBottomInset()
        val left = snappedLeftInset()

        val w = width - left - right
        val h = height - top - bottom

        canvas.layoutX = left
        canvas.layoutY = top

        canvas.width = w
        canvas.height = h

        draw()
    }
}
package congard.agh.evolution.gui.simulator.renderer

import congard.agh.evolution.gui.math.Rect
import congard.agh.evolution.gui.math.Vec2
import javafx.scene.canvas.GraphicsContext

fun GraphicsContext.fillRect(rect: Rect) {
    fillRect(rect.x().toDouble(), rect.y().toDouble(), rect.width().toDouble(), rect.height().toDouble())
}

fun GraphicsContext.strokeRect(rect: Rect) {
    strokeRect(rect.x().toDouble(), rect.y().toDouble(), rect.width().toDouble(), rect.height().toDouble())
}

fun GraphicsContext.fillText(text: String, pos: Vec2) {
    fillText(text, pos.x.toDouble(), pos.y.toDouble())
}

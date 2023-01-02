package congard.agh.evolution.gui.simulator.renderer

import congard.agh.evolution.gui.math.Rect
import congard.agh.evolution.simulation.world.WorldAbstractElement
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class CellRenderer : ElementRenderer {
    private val fill = Color.gray(0.3, 0.5)

    override fun render(g: GraphicsContext, rect: Rect, element: WorldAbstractElement?) {
        g.fill = fill
        g.fillRect(rect)
    }
}

package congard.agh.evolution.gui.simulator.renderer

import congard.agh.evolution.gui.math.Rect
import congard.agh.evolution.simulation.world.WorldAbstractElement
import javafx.scene.canvas.GraphicsContext

interface ElementRenderer {
    fun render(g: GraphicsContext, rect: Rect, element: WorldAbstractElement? = null)
}
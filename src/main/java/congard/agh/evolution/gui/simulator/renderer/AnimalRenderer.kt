package congard.agh.evolution.gui.simulator.renderer

import congard.agh.evolution.gui.math.Rect
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.world.Animal
import congard.agh.evolution.simulation.world.WorldAbstractElement
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class AnimalRenderer(
    private val engine: SimulationEngine
) : ElementRenderer {
    private val fill = Color.web("#234567")
    private val textFill = Color.web("#2A2E32")

    override fun render(g: GraphicsContext, rect: Rect, element: WorldAbstractElement?) {
        val animal = element as Animal

        g.fill = fill
        g.fillRect(rect)

        if (engine.isPaused || !engine.isStarted) {
            g.fill = textFill
            g.fillText(
                "Animal #${animal.id}\n" +
                "${animal.orientation}\n" +
                "${animal.health} HP",
                rect.center()
            )
        }
    }
}

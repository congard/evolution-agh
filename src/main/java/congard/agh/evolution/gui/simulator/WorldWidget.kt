package congard.agh.evolution.gui.simulator

import congard.agh.evolution.gui.math.Rect
import congard.agh.evolution.gui.math.Vec2
import congard.agh.evolution.gui.simulator.renderer.AnimalRenderer
import congard.agh.evolution.gui.simulator.renderer.CellRenderer
import congard.agh.evolution.gui.simulator.renderer.PlantRenderer
import congard.agh.evolution.gui.simulator.renderer.strokeRect
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.world.WorldCell
import congard.agh.evolution.simulation.world.WorldElementType
import javafx.geometry.VPos
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment

class WorldWidget(
    private val engine: SimulationEngine
) : CanvasPane() {

    private val translate = Vec2(0f, 0f) // in world coordinates
    private var prevMousePos = Vec2()
    private var scale = 100f

    private val cellRenderer = CellRenderer()
    private val plantRenderer = PlantRenderer()
    private val animalRenderer = AnimalRenderer(engine)

    private val strokeColor = Color.web("#1E1F22")

    init {
        setOnScroll { event ->
            val step = 1.1f.let { if (event.deltaY > 0) it else 1 / it }

            // origin = origin' * step
            // origin' = origin / step
            // delta = origin' - origin

            val origin = Vec2(event.x, event.y) / scale
            translate += origin / step - origin

            scale *= step

            draw()
        }

        setOnMousePressed { event ->
            prevMousePos = Vec2(event.x, event.y)
        }

        setOnMouseDragged { event ->
            val mousePos = Vec2(event.x, event.y)
            translate += (mousePos - prevMousePos) / scale
            prevMousePos = mousePos
            draw()
        }

        engine.addOnEventListener {
            if (it == Event.PAUSED) invalidate()
        }
    }

    /**
     * Returns cell at screen-space (x, y)
     */
    fun cellAt(x: Number, y: Number): WorldCell? {
        val pos = Vec2(x, y) / scale - translate
        val mapSize = engine.world.params.mapSize

        if (!Rect(Vec2(0f, 0f), Vec2(mapSize.x, mapSize.y)).contains(pos))
            return null

        return engine.world.getCell(pos.x.toInt(), pos.y.toInt())
    }

    /**
     * Transforms world space to screen space
     */
    private fun transform(x: Float, y: Float) =
        (Vec2(x, y) + translate) * scale

    override fun onDraw(g: GraphicsContext) {
        g.stroke = strokeColor
        g.lineWidth = 0.15

        g.font = Font.font(0.1 * scale)
        g.textAlign = TextAlignment.CENTER
        g.textBaseline = VPos.CENTER

        engine.world.forEachCell { cell ->
            val rect = Rect(transform(cell.x.toFloat(), cell.y.toFloat()), scale)

            val plants = cell.get(WorldElementType.PLANT)
            val animals = cell.get(WorldElementType.ANIMAL)

            if (plants.isEmpty() && animals.isEmpty())
                cellRenderer.render(g, rect)

            if (animals.isEmpty())
                for (plant in plants)
                    plantRenderer.render(g, rect, plant)

            for (animal in animals)
                animalRenderer.render(g, rect, animal)

            g.strokeRect(rect)
        }
    }
}

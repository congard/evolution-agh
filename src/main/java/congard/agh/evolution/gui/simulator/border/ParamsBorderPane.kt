package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.gui.start.ParamField
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.params.Param
import congard.agh.evolution.simulation.params.Params
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tooltip
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.util.Duration
import java.util.*
import kotlin.reflect.KMutableProperty

class ParamsBorderPane(engine: SimulationEngine)
    : BorderMenuPane("Params"), OnEventListener
{
    private val paramFields = LinkedList<ParamField>()

    private val btnSave = Button("@").apply {
        addToNav(this)
        tooltip = Tooltip("Apply params").apply { showDelay = Duration(250.0) }
        onAction = EventHandler { applyParams() }
    }

    init {
        engine.addOnEventListener(this)

        val grid = GridPane().apply { hgap = 16.0 }

        add(ScrollPane().apply { content = grid })

        fun addToGrid(title: String, node: Node) {
            val label = Label(title)

            GridPane.setHgrow(label, Priority.ALWAYS)
            GridPane.setHgrow(node, Priority.ALWAYS)

            val row = grid.rowCount

            grid.add(label, 0, row)
            grid.add(node, 1, row)
        }

        fun param(prop: KMutableProperty<*>) =
            prop.annotations.find { it is Param } as Param

        fun addSpacer() =
            grid.add(Pane().apply { minHeight = 12.0 }, 0, grid.rowCount)

        val params = engine.params

        // show immutable

        fun showImmutable(predicate: (param: Param) -> Boolean) = filterProperties(predicate).forEach { prop ->
            addToGrid(param(prop).name, Label(prop.getter.call(params).toString()))
        }

        // show immutable visible
        showImmutable { !it.mutable && !it.hidden }

        // show immutable hidden
        addSpacer()
        showImmutable { !it.mutable && it.hidden }

        // show mutable

        fun showMutable(predicate: (param: Param) -> Boolean) = filterProperties(predicate).forEach { prop ->
            val paramField = ParamField(prop, params)
            addToGrid(paramField.name(), paramField.node)
            paramFields.add(paramField)
        }

        // show mutable visible
        addSpacer()
        showMutable { it.mutable && !it.hidden }

        // show mutable hidden
        addSpacer()
        showMutable { it.mutable && it.hidden }
    }

    private fun filterProperties(predicate: (param: Param) -> Boolean) = Params.getProperties().filter { prop ->
        val annotations = prop.annotations

        if (annotations.isEmpty())
            return@filter false

        val param = prop.annotations.find { it is Param } as Param

        return@filter predicate(param)
    }

    private fun applyParams() =
        paramFields.forEach { it.readValue() }

    override fun onEvent(event: Event?) {
        when (event!!) {
            Event.STARTED, Event.RESUMED -> btnSave.isDisable = true
            Event.STOP, Event.PAUSED -> btnSave.isDisable = false
            else -> {}
        }
    }
}

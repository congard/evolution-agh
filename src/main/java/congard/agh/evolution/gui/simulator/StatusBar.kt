package congard.agh.evolution.gui.simulator

import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import java.text.SimpleDateFormat
import java.util.*

class StatusBar(
    private val engine: SimulationEngine
) : BorderPane(), OnEventListener {
    private val left = HBox().apply { styleClass.add("status-bar-box") }
    private val right = HBox().apply { styleClass.add("status-bar-box") }

    private val labelMessage = Label()
    private val labelStatus = Label("Stopped")
    private val labelDay = Label()

    init {
        id = "status-bar"

        setLeft(left)
        setRight(right)

        left.children.add(labelMessage)
        right.children.addAll(labelDay, labelStatus)

        displayMessage("Initialized")

        engine.addOnEventListener(this)
    }

    fun displayMessage(msg: String) {
        labelMessage.text = "$msg (${SimpleDateFormat("HH:mm:ss").format(Date(System.currentTimeMillis()))})"
    }

    override fun onEvent(event: Event?) {
        Platform.runLater {
            when (event!!) {
                Event.STARTED -> { displayMessage("Started"); labelStatus.text = "Running" }
                Event.STOP -> { displayMessage("Stopped"); labelStatus.text = "Stopped" }
                Event.PAUSED -> { displayMessage("Paused"); labelStatus.text = "Paused" }
                Event.RESUMED -> { displayMessage("Resumed"); labelStatus.text = "Running" }
                Event.TICK -> labelDay.text = "Day ${engine.world.day}"
            }
        }
    }
}
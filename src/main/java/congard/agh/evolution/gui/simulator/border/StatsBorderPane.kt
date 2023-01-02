package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import javafx.application.Platform
import javafx.collections.ObservableList
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.StackedAreaChart
import javafx.scene.chart.XYChart
import javafx.scene.control.Label

class StatsBorderPane(private val engine: SimulationEngine)
    : BorderMenuPane("Statistics"), OnEventListener
{
    private val labelAliveAnimalsCount = Label()
    private val labelPlantsCount = Label()
    private val labelFreeFieldsCount = Label()
    private val labelAvgEnergy = Label()
    private val labelAvgLifespan = Label()

    private val aliveAnimalsSeries = XYChart.Series<Number, Number>().apply { name = "Alive animals" }
    private val plantsSeries = XYChart.Series<Number, Number>().apply { name = "Plants" }

    private val chart = StackedAreaChart(
        NumberAxis().apply { label = "Day"; isAutoRanging = false },
        NumberAxis().apply { label = "Number of" }
    ).apply {
        data.addAll(aliveAnimalsSeries, plantsSeries)
        createSymbols = false
    }

    init {
        engine.addOnEventListener(this)
        addAll(labelAliveAnimalsCount, labelPlantsCount, labelFreeFieldsCount, labelAvgEnergy, labelAvgLifespan, chart)
        updateLabels()
    }

    private fun updateLabels() {
        val world = engine.world
        labelAliveAnimalsCount.text = "Alive animals count: ${world.aliveAnimalsCount}"
        labelPlantsCount.text = "Plants count: ${world.plantsCount}"
        labelFreeFieldsCount.text = "Free fields count: ${world.freeFieldsCount}"
        labelAvgEnergy.text = "Average energy: ${String.format("%.2f", world.avgEnergy)}"
        labelAvgLifespan.text = "Average lifespan: ${String.format("%.2f", world.calcAvgLifespan())} days"
    }

    override fun onOpen() {
        updateLabels()
    }

    override fun onEvent(event: Event?) {
        if (event!! != Event.TICK)
            return

        Platform.runLater {
            if (isOpen()) updateLabels()

            val world = engine.world

            fun update(data: ObservableList<XYChart.Data<Number, Number>>, value: Number) = data.apply {
                val chartLength = engine.world.params.statsChartLength

                if (size > chartLength) {
                    remove(0, size - chartLength)
                    (chart.xAxis as NumberAxis).lowerBound = get(0).xValue.toDouble()
                }

                add(XYChart.Data(world.day, value))
                (chart.xAxis as NumberAxis).upperBound = world.day.toDouble()
            }

            update(aliveAnimalsSeries.data, world.aliveAnimalsCount)
            update(plantsSeries.data, world.plantsCount)
        }
    }
}
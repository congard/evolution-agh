package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.gui.NoSelectionModel
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.world.Genotype
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority

class GenotypeStatsBorderPane(
    private val engine: SimulationEngine
) : BorderMenuPane("Genotype statistics"), OnEventListener {

    private class GenotypeDetails(
        val genotypePane: GenotypePane,
        var count: Int = 0
    ) {
        private val label = Label()

        val box = HBox().apply {
            HBox.setHgrow(genotypePane, Priority.ALWAYS)
            children.addAll(genotypePane, label)
            spacing = 6.0
        }

        fun updateLabel() {
            label.text = "$count"
        }
    }

    private val genotypes = HashMap<Genotype, GenotypeDetails>()

    private val list = ListView<HBox>().apply {
        selectionModel = NoSelectionModel()
    }

    init {
        engine.addOnEventListener(this)
        add(list)
    }

    private fun update() {
        if (!isOpen())
            return

        genotypes.forEach { entry: Map.Entry<Genotype, GenotypeDetails> ->
            entry.value.count = 0
        }

        engine.world.forEachAlive { animal ->
            val genotype = animal.genotype

            if (genotypes.containsKey(genotype)) {
                genotypes[genotype]!!.count += 1
            } else {
                genotypes[genotype] = GenotypeDetails(GenotypePane(genotype), 1)
            }
        }

        // remove nobody's genotypes
        genotypes.entries.removeIf { it.value.count == 0 }

        // sort values by count, decreasing
        val details = genotypes.values.sortedWith { a, b -> b.count.compareTo(a.count) }

        // update labels
        details.forEach { it.updateLabel() }

        // display
        list.items.clear()
        list.items.addAll(details.map { it.box })
    }

    override fun onOpen() {
        Platform.runLater { update() }
    }

    override fun onEvent(event: Event?) {
        if (event!! == Event.TICK && isOpen()) {
            Platform.runLater { update() }
        }
    }
}

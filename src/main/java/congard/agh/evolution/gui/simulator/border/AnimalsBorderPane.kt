package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.gui.NoSelectionModel
import congard.agh.evolution.gui.Resources
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.world.Animal
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.util.function.Consumer

class AnimalsBorderPane(
    private val engine: SimulationEngine
) : BorderMenuPane("Animals"), OnEventListener {

    private inner class AnimalPane(val animal: Animal) : TitledPane() {
        var isPinned = false
            private set

        private val genotypePane = GenotypePane(animal.genotype).apply {
            isHighlightActive = true
        }

        private val labelStatus = Label()
        private val labelAge = Label()
        private val labelBirthday = Label()
        private val labelHealth = Label()
        private val labelEatenPlants = Label()
        private val labelChildrenCount = Label()

        init {
            alignment = Pos.CENTER

            // header
            graphic = HBox().apply {
                minWidthProperty().bind(this@AnimalPane.widthProperty())

                alignment = Pos.CENTER
                padding = Insets(0.0, 10.0, 0.0, 35.0)

                children.add(Label("Animal #${animal.id}"))

                children.add(HBox().apply {
                    maxWidth = Double.MAX_VALUE
                    HBox.setHgrow(this, Priority.ALWAYS)
                })

                children.add(Button().apply {
                    styleClass.add("btn-control")

                    val icPin = Resources.icPinEmpty().apply { fill = Color.WHITE }
                    graphic = icPin

                    onAction = EventHandler {
                        isPinned = !isPinned

                        if (isPinned) icPin.fill = Color.web("#3592C4")
                        else icPin.fill = Color.WHITE

                        // update order (sort)
                        this@AnimalsBorderPane.update()
                    }
                })
            }

            content = VBox().apply {
                children.add(HBox().apply {
                    HBox.setHgrow(genotypePane, Priority.ALWAYS)
                    children.addAll(Label("Genotype: "), genotypePane)
                })

                children.addAll(
                    labelStatus, labelAge, labelBirthday,
                    labelHealth, labelEatenPlants, labelChildrenCount
                )
            }

            isExpanded = false

            expandedProperty().addListener { _, _, expanded ->
                if (expanded) {
                    update()
                }
            }

            update()
        }

        fun update() {
            if (!isExpanded)
                return

            genotypePane.draw()

            labelStatus.text = "Status: ${if (animal.isAlive) "alive" else "dead (day ${animal.deathDay})"}"
            labelAge.text = "Age: ${animal.age}"
            labelBirthday.text = "Birthday: day ${animal.birthDay}"
            labelHealth.text = "Health: ${animal.health}"
            labelEatenPlants.text = "Eaten plants count: ${animal.eatenPlantsCount}"
            labelChildrenCount.text = "Children count: ${animal.childrenCount}"
        }
    }

    private val panes = HashMap<Animal, AnimalPane>()

    private val list = ListView<AnimalPane>().apply {
        selectionModel = NoSelectionModel()
    }

    init {
        engine.addOnEventListener(this)
        add(list)
    }

    fun show(animalId: Long) {
        update()

        for (i in 0 until list.items.size) {
            val pane = list.items[i] as AnimalPane

            if (pane.animal.id == animalId) {
                Thread {
                    // you can find description in CellsBorderPane#show

                    var h: Double

                    do {
                        synchronized(list) {
                            h = list.prefHeight
                        }
                    } while (h == -1.0)

                    Platform.runLater {
                        pane.isExpanded = true
                        list.scrollTo(pane)
                    }
                }.start()

                break
            }
        }
    }

    private fun update() {
        val world = engine.world

        // update existing panes & create new if necessary
        Consumer<Animal> { animal ->
            if (panes.containsKey(animal)) {
                panes[animal]!!.update()
            } else {
                panes[animal] = AnimalPane(animal).also {
                    list.items.add(it)
                }
            }
        }.also {
            world.forEachAlive(it)
            world.forEachDead(it)
        }

        // sort panes
        FXCollections.sort(list.items, Comparator
            .comparing<Node, Boolean> { (it as AnimalPane).isPinned }
            .thenComparing { it -> (it as AnimalPane).animal.isAlive }
            .thenComparingInt { (it as AnimalPane).animal.age }
            .reversed())
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

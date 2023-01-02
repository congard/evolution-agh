package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.gui.NoSelectionModel
import congard.agh.evolution.gui.Resources
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.OnEventListener
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.world.Animal
import congard.agh.evolution.simulation.world.WorldCell
import congard.agh.evolution.simulation.world.WorldElementType
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class CellsBorderPane(
    private val engine: SimulationEngine,
    private val animalsBorderPane: AnimalsBorderPane
) : BorderMenuPane("Cells"), OnEventListener {

    private inner class CellPane(val cell: WorldCell) : TitledPane() {
        private var isAnimalsVisible = false

        private val labelElementsCount = Label()
        private val labelPlant = Label()
        private val labelNumberOfDeaths = Label()

        private val listAnimals = ListView<Animal>().apply {
            prefHeight = 2.0

            items.addListener(ListChangeListener {
                // Each row in a ListView should be 24px tall. Also, we have to
                // add an extra 2px to account for the borders of the ListView.
                // https://stackoverflow.com/a/17456527/9200394
                prefHeight = items.size * 24.0 + 2
            })

            setCellFactory {
                object : ListCell<Animal>() {
                    override fun updateItem(animal: Animal?, empty: Boolean) {
                        super.updateItem(item, empty)

                        if (animal == null) return

                        text = "#${animal.id}"

                        setOnMouseClicked { mouseClickedEvent ->
                            if (mouseClickedEvent.button == MouseButton.PRIMARY && mouseClickedEvent.clickCount == 2) {
                                animalsBorderPane.open()
                                animalsBorderPane.show(animal.id)
                            }
                        }
                    }
                }
            }
        }

        init {
            text = "Cell ${cell.pos}"

            content = VBox().apply {
                children.add(Label("Position: ${cell.pos}"))
                children.addAll(labelElementsCount, labelPlant, labelNumberOfDeaths)

                children.add(HBox().apply {
                    alignment = Pos.CENTER_LEFT

                    children.add(Label("Animals: "))

                    children.add(Button().apply {
                        styleClass.add("btn-control")

                        val icEye = Resources.icEye().apply { fill = Color.WHITE }
                        graphic = icEye

                        onAction = EventHandler {
                            isAnimalsVisible = !isAnimalsVisible

                            if (isAnimalsVisible) {
                                icEye.fill = Color.web("#3592C4")
                                (content as VBox).children.add(listAnimals)
                            } else {
                                icEye.fill = Color.WHITE
                                (content as VBox).children.remove(listAnimals)
                            }

                            update()
                        }
                    })
                })
            }

            isExpanded = false

            expandedProperty().addListener{ _, _, expanded ->
                if (expanded) {
                    update()
                }
            }

            update()
        }

        fun update() {
            if (!isExpanded)
                return

            labelElementsCount.text = "Elements count: " +
                    "${cell.get(WorldElementType.PLANT).size + cell.get(WorldElementType.ANIMAL).size}"
            labelPlant.text = "Contains plant: ${if (cell.get(WorldElementType.PLANT).isEmpty()) "no" else "yes"}"
            labelNumberOfDeaths.text = "Number of deaths: ${cell.numberOfDeaths}"

            if (isAnimalsVisible) {
                // this list shouldn't be large, so n^2 is fairly enough
                val animals = cell.get(WorldElementType.ANIMAL)

                // add missing
                animals.forEach {
                    val animal = it as Animal

                    if (!listAnimals.items.contains(animal)) {
                        listAnimals.items.add(animal)
                    }
                }

                // remove non-existent
                val iterator = listAnimals.items.listIterator()

                while (iterator.hasNext()) {
                    val animal = iterator.next()

                    if (!animals.contains(animal)) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    private val list = ListView<CellPane>().apply {
        selectionModel = NoSelectionModel()
        engine.world.forEachCell { cell -> items.add(CellPane(cell)) }
    }

    init {
        engine.addOnEventListener(this)
        add(list)
    }

    fun show(cell: WorldCell) {
        Thread {
            // a little hack: if CellsBorderPane is opened for the first time,
            // it may be not fully initialized. Therefore, some variables have
            // default values. To scroll we should wait until `list` is fully
            // initialized

            var h: Double

            do {
                synchronized(list) {
                    h = list.prefHeight
                }
            } while (h == -1.0)

            Platform.runLater {
                for (item in list.items) {
                    if (item.cell == cell) {
                        item.isExpanded = true
                        list.scrollTo(item)
                        break
                    }
                }
            }
        }.start()
    }

    private fun update() {
        list.items.forEach { cellPane -> cellPane.update() }
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

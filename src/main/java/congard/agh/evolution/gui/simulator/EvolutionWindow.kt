package congard.agh.evolution.gui.simulator

import congard.agh.evolution.gui.Resources
import congard.agh.evolution.gui.simulator.border.*
import congard.agh.evolution.simulation.Event
import congard.agh.evolution.simulation.SimulationEngine
import congard.agh.evolution.simulation.StatsRecorder
import congard.agh.evolution.simulation.params.Params
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.util.LinkedList

class EvolutionWindow(params: Params) : Stage() {
    private val engine = SimulationEngine(params).apply {
        setErrorHandler { e ->
            Platform.runLater {
                statusBar.displayMessage("Exception: $e")
                
                val alert = Alert(Alert.AlertType.ERROR)
                alert.dialogPane.stylesheets.add("styles.css") // TODO: create styles for dialogs
                alert.title = "Error"
                alert.headerText = "Error"
                alert.contentText = "Message: ${if (e.message != null) e.message else "<empty>"}\n$e"
                alert.showAndWait()
            }
        }

        addOnEventListener { event ->
            Platform.runLater {
                when (event!!) {
                    Event.STARTED -> btnChangeState.graphic = Resources.icPause()
                    Event.STOP -> {}
                    Event.PAUSED -> btnChangeState.graphic = Resources.icPlay()
                    Event.RESUMED -> btnChangeState.graphic = Resources.icPause()
                    Event.TICK -> worldWidget.draw()
                }
            }
        }
    }

    private val btnChangeState = Button().apply {
        styleClass.add("btn-control")
        onAction = EventHandler { changeState() }
        graphic = Resources.icPlay()
    }

    private val statusBar: StatusBar = StatusBar(engine)

    private val worldWidget: WorldWidget = WorldWidget(engine).apply {
        setOnContextMenuRequested { event ->
            ContextMenu().also {
                val cell = cellAt(event.x, event.y) ?: return@also

                it.items.add(MenuItem("Cell ${cell.pos}").apply {
                    styleClass.add("context-menu-title")
                    isDisable = true
                })

                it.items.add(MenuItem("Show...").apply {
                    onAction = EventHandler {
                        borderPanes.cells.open()
                        borderPanes.cells.show(cell)
                    }
                })

                it.show(scene.window, event.screenX, event.screenY)
            }
        }
    }

    private val contentPane = SplitPane().apply {
        items.add(worldWidget)
    }

    private val borderPanes = object {
        private inner class PaneInfo(
            val type: BorderMenu.Type,
            val pane: BorderMenuPane,
            val name: String
        ) {}

        val stats = StatsBorderPane(engine)
        val animals = AnimalsBorderPane(engine)
        val cells = CellsBorderPane(engine, animals)
        val genotypeStats = GenotypeStatsBorderPane(engine)
        val paramsBorderPane = ParamsBorderPane(engine)

        val panes = LinkedList<PaneInfo>().apply {
            add(PaneInfo(BorderMenu.Type.Left, stats, "Statistics"))
            add(PaneInfo(BorderMenu.Type.Left, cells, "Cells"))

            add(PaneInfo(BorderMenu.Type.Right, genotypeStats, "Genotype"))
            add(PaneInfo(BorderMenu.Type.Right, animals, "Animals"))
            add(PaneInfo(BorderMenu.Type.Right, paramsBorderPane, "Params"))
        }

        fun getLeft() =
            panes.filter { it.type == BorderMenu.Type.Left }

        fun getRight() =
            panes.filter { it.type == BorderMenu.Type.Right }

        fun addAll(borderMenu: BorderMenu, panes: List<PaneInfo>) =
            panes.forEach { pane -> borderMenu.add(pane.name, pane.pane) }

        fun indexer(indexer: (PaneInfo) -> Unit) = indexer
    }

    init {
        title = "Evolution"
        width = 1000.0
        height = 700.0

        val mainPane = BorderPane()
        mainPane.styleClass.add("pane")

        val topBox = VBox().apply {
            val menuBar = MenuBar().apply {
                val file = Menu("File").apply {
                    items.add(MenuItem().apply { // Record to CSV
                        fun updateText() {
                            text = when (engine.statsRecorder) {
                                null -> "Record to CSV..."
                                else -> "Stop recording"
                            }
                        }

                        updateText()

                        onAction = EventHandler {
                            if (engine.statsRecorder == null) {
                                val fileChooser = FileChooser()
                                fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("CSV", "*.csv"))
                                fileChooser.initialFileName = "stats_${System.currentTimeMillis()}.csv"
                                val file = fileChooser.showSaveDialog(this@EvolutionWindow)

                                if (file != null) {
                                    engine.statsRecorder = StatsRecorder(file, engine.world)
                                    statusBar.displayMessage("Recording to CSV was started")
                                }
                            } else {
                                engine.statsRecorder.close()
                                engine.statsRecorder = null
                                statusBar.displayMessage("Recording to CSV was stopped")
                            }

                            updateText()
                        }
                    })

                    items.add(SeparatorMenuItem())

                    items.add(MenuItem("Exit").apply {
                        onAction = EventHandler { this@EvolutionWindow.close() }
                    })
                }

                val panes = Menu("Panes").apply {
                    val indexer = borderPanes.indexer { info ->
                        items.add(MenuItem(info.name).apply {
                            onAction = EventHandler { info.pane.open() }
                        })
                    }

                    borderPanes.getLeft().forEach(indexer)
                    items.add(SeparatorMenuItem())
                    borderPanes.getRight().forEach(indexer)
                }

                menus.add(file)
                menus.add(panes)
            }

            val controls = HBox()
            controls.id = "controls"
            controls.children.add(btnChangeState)

            children.addAll(menuBar, controls)
        }

        mainPane.top = topBox
        mainPane.bottom = statusBar
        mainPane.center = contentPane
        mainPane.left = BorderMenu(BorderMenu.Type.Left, contentPane).apply {
            borderPanes.addAll(this, borderPanes.getLeft())
        }
        mainPane.right = BorderMenu(BorderMenu.Type.Right, contentPane).apply {
            borderPanes.addAll(this, borderPanes.getRight())
        }

        val scene = Scene(mainPane)
        scene.stylesheets.add("styles.css")
        setScene(scene)

        show()

        scene.window.addEventFilter(WindowEvent.WINDOW_HIDING) { onClose() }
    }

    private fun onClose() {
        if (engine.isStarted) {
            engine.stop()
        }
    }

    private fun changeState() {
        if (!engine.isStarted) {
            engine.start()
        } else if (engine.isPaused) {
            engine.resume()
        } else {
            engine.pause()
        }
    }
}

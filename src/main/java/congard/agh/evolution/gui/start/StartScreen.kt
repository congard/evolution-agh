package congard.agh.evolution.gui.start

import congard.agh.evolution.gui.simulator.EvolutionWindow
import congard.agh.evolution.simulation.params.Param
import congard.agh.evolution.simulation.params.Params
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class StartScreen : Stage() {
    private var params = Params()
    private val paramFields = LinkedList<ParamField>()

    private enum class FileChooserType {
        Open, Save
    }

    private fun showFileChooserDialog(type: FileChooserType): File? {
        val fileChooser = FileChooser()
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("JSON Files", "*.json"))
        fileChooser.initialFileName = "params.json"

        return when (type) {
            FileChooserType.Open -> fileChooser.showOpenDialog(this)
            FileChooserType.Save -> fileChooser.showSaveDialog(this)
        }
    }

    private fun showErrorDialog(header: String, content: String = "", ex: Exception? = null) {
        val alert = Alert(AlertType.ERROR)
        alert.dialogPane.stylesheets.add("styles.css") // TODO: create styles for dialogs
        alert.title = "Error"
        alert.headerText = header
        alert.contentText = content + if (ex != null) "\nMessage: ${ex.message}" else ""
        alert.showAndWait()
        ex?.printStackTrace()
    }

    private fun loadParams(file: File) {
        try {
            params = Params.deserialize(JSONObject(file.readText()))
            paramFields.forEach { it.update() }
        } catch (e: FileNotFoundException) {
            showErrorDialog("File not found", ex = e)
        } catch (e: JSONException) {
            showErrorDialog("File is corrupted",
                "Parse error: $file is not a valid params file", e)
        } catch (e: Exception) {
            showErrorDialog("Exception", ex = e)
        }
    }

    private fun saveParams(file: File) {
        try {
            file.writeText(JSONObject(params).toString())
        } catch (e: IOException) {
            showErrorDialog("IOException",
                "File $file cannot be written", e)
        } catch (e: Exception) {
            showErrorDialog("Exception", ex = e)
        }
    }

    private fun start() {
        paramFields.forEach {
            try {
                it.readValue()
            } catch (e: Exception) {
                showErrorDialog("Invalid data", "${it.name()}:", e)
                return
            }
        }

        EvolutionWindow(params)
    }

    private fun showAboutDialog() = Stage().apply {
        title = "About Evolution"
        width = 450.0
        height = 350.0

        val content = VBox().apply {
            styleClass.add("about-container")

            children.add(Label("Evolution"))

            children.add(TextFlow(
                Text("This project is developed by congard within the subject " +
                    "of Object Orientated Programming at the AGH University of Science and Technology\n\n" +
                    "Distributed under the BSD 4-clause license\n\n" +
                    "Homepage: "), Link("https://github.com/congard/evolution-agh"),
                Text("\nMy GitHub:"), Link("https://github.com/congard")
            ).apply { styleClass.add("text-flow") })
        }

        val scene = Scene(content)
        scene.stylesheets.add("styles.css")
        setScene(scene)

        show()
    }

    init {
        title = "Evolution - start"

        val mainBox = VBox()

        val contentBox = VBox()
        contentBox.id = "startScreen"

        val menuBar = MenuBar().apply {
            val menuFile = Menu("File").apply {
                items.add(MenuItem("Load params from file...").apply {
                    onAction = EventHandler {
                        loadParams(showFileChooserDialog(FileChooserType.Open) ?: return@EventHandler)
                    }
                })

                items.add(MenuItem("Save params").apply {
                    onAction = EventHandler {
                        saveParams(showFileChooserDialog(FileChooserType.Save) ?: return@EventHandler)
                    }
                })

                items.add(SeparatorMenuItem())

                items.add(MenuItem("Exit").apply { onAction = EventHandler { close() } })
            }

            val menuHelp = Menu("Help").apply {
                items.add(MenuItem("About").apply {
                    onAction = EventHandler { showAboutDialog() }
                })
            }

            menus.addAll(menuFile, menuHelp)
        }

        VBox.setVgrow(contentBox, Priority.ALWAYS)

        mainBox.children.addAll(menuBar, contentBox)

        val title = Label("Evolution")
        title.font = Font(24.0)
        contentBox.children.add(title)

        val gridPane = GridPane()
        gridPane.vgap = 8.0
        gridPane.hgap = 16.0

        contentBox.children.add(ScrollPane().apply {
            content = gridPane
            isFitToWidth = true
        })

        for (prop in Params.getProperties()) {
            val annotations = prop.annotations

            if (annotations.isEmpty())
                continue

            val param = prop.annotations.find { it is Param } as Param

            if (param.hidden)
                continue

            val label = Label(param.name)
            val field = ParamField(prop, params).also { paramFields.add(it) }

            GridPane.setHgrow(label, Priority.ALWAYS)
            GridPane.setHgrow(field.node, Priority.ALWAYS)

            val row = gridPane.rowCount

            gridPane.add(label, 0, row, 1, 1)
            gridPane.add(field.node, 1, row, 3, 1)
        }

        val btnStart = Button("Start").apply { onAction = EventHandler { start() } }
        contentBox.children.add(btnStart)

        val scene = Scene(mainBox)
        scene.stylesheets.add("styles.css")
        setScene(scene)

        width = 512.0
        height = 760.0
        show()
    }
}
package congard.agh.evolution.gui.simulator.border

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.util.Duration

open class BorderMenuPane(title: String) : VBox() {
    var menuEntry: BorderMenu.MenuEntry? = null

    private val nav = HBox()
    private val content: Pane

    init {
        styleClass.add("border-menu-pane")

        val header = BorderPane().apply {
            styleClass.add("border-menu-pane-header")
            left = Label(title).also { BorderPane.setAlignment(it, Pos.CENTER) }
            right = nav
        }

        nav.children.add(Button("–").apply {
            onAction = EventHandler { menuEntry?.close() }
            tooltip = Tooltip("Hide").apply { showDelay = Duration(250.0) }
        })

        content = VBox().apply { styleClass.add("border-menu-pane-content") }

        children.addAll(header, content)
    }

    fun add(node: Node) = content.children.add(node)

    fun addAll(vararg nodes: Node) = content.children.addAll(nodes)

    fun addToNav(button: Button) = nav.children.add(0, button)

    fun open() = menuEntry?.open()

    fun close() = menuEntry?.close()

    fun isOpen() = menuEntry?.isOpen ?: false

    open fun onOpen() {}
    open fun onClose() {}
}

package congard.agh.evolution.gui.simulator.border

import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.layout.VBox
import java.util.LinkedList

class BorderMenu(
    private val type: Type,
    private val hPane: SplitPane
) : VBox() {
    enum class Type(val rotateDeg: Double) {
        Left(-90.0),
        Right(90.0)
    }

    inner class MenuEntry(text: String, private val pane: BorderMenuPane) {
        private val classDefault = "menu-entry"
        private val classOpen = "menu-entry-open"

        private val button = Button(text).apply {
            styleClass.add(classDefault)
            rotate = type.rotateDeg
            onAction = EventHandler {
                if (isOpen) close()
                else open()
            }
        }

        private var prevDividerPos = when (type) {
            Type.Left -> 0.35
            Type.Right -> 0.65
        }

        var isOpen = false
            private set

        init {
            if (hPane.items.size == 0)
                throw IllegalStateException("hPane must contain at least 1 child (content)")

            pane.menuEntry = this
        }

        private fun getIndex() = when (type) {
            Type.Left -> 0
            Type.Right -> {
                if (hPane.items[0] is BorderMenuPane) 2  // left menu is opened
                else 1  // left menu is closed
            }
        }

        private fun getDividerIndex() = when (type) {
            Type.Left -> 0
            Type.Right -> getIndex() - 1
        }

        // Read more about why I wrap the button with Group here:
        // https://stackoverflow.com/questions/23062241/rotated-objects-inside-a-vbox-or-hbox
        fun createControl() = Group(button)

        fun open() {
            if (isOpen)
                return

            // close other entries if opened
            entries.forEach { it.close() }

            pane.onOpen()

            hPane.items.add(getIndex(), pane)
            hPane.setDividerPosition(getDividerIndex(), prevDividerPos)

            button.styleClass.add(classOpen)

            isOpen = true
        }

        fun close() {
            if (!isOpen)
                return

            pane.onClose()

            prevDividerPos = hPane.dividerPositions[getDividerIndex()]
            hPane.items.removeAt(getIndex())

            button.styleClass.remove(classOpen)

            isOpen = false
        }
    }

    private val entries = LinkedList<MenuEntry>()

    init {
        styleClass.add("border-menu")
    }

    fun add(text: String, pane: BorderMenuPane) =
        entries.add(MenuEntry(text, pane).also { children.add(it.createControl()) })
}

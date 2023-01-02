package congard.agh.evolution.gui.start

import congard.agh.evolution.gui.App
import javafx.event.EventHandler
import javafx.scene.control.Hyperlink

class Link(text: String) : Hyperlink(text) {
    init {
        onAction = EventHandler {
            App.instance!!.hostServices.showDocument(text)
        }
    }
}

package congard.agh.evolution.gui

import congard.agh.evolution.gui.start.StartScreen
import javafx.application.Application
import javafx.stage.Stage

class App : Application() {
    init {
        if (instance != null)
            throw IllegalStateException("Only one instance of App can exist at a time")
        instance = this
    }

    override fun start(primaryStage: Stage) {
        StartScreen()
    }

    companion object {
        var instance: App? = null
            private set
    }
}

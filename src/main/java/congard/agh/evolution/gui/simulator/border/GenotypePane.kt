package congard.agh.evolution.gui.simulator.border

import congard.agh.evolution.gui.simulator.CanvasPane
import congard.agh.evolution.simulation.world.Gene
import congard.agh.evolution.simulation.world.Genotype
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class GenotypePane(
    private val genotype: Genotype
) : CanvasPane() {
    private val highlightColor = Color.web("#F3AD6A", 0.75)

    var isHighlightActive = false

    private fun getColor(gene: Gene) = Color.web(when (gene) {
        Gene.G0 -> "#D0EDE9"
        Gene.G1 -> "#A7DAD8"
        Gene.G2 -> "#84C3C9"
        Gene.G3 -> "#67AAB7"
        Gene.G4 -> "#4E90A5"
        Gene.G5 -> "#3B738E"
        Gene.G6 -> "#2A5673"
        Gene.G7 -> "#034D6F"
    }, 0.5)

    override fun onDraw(g: GraphicsContext) {
        val w = availableWidth()
        val h = availableHeight()

        val geneWidth = w / genotype.size()

        g.stroke = highlightColor

        for (i in 0 until genotype.size()) {
            g.fill = getColor(genotype[i])
            g.fillRect(geneWidth * i, 0.0, geneWidth, h)

            if (isHighlightActive && i == genotype.activeGeneIndex) {
                g.strokeRect(geneWidth * i, 0.0, geneWidth, h)
            }
        }
    }
}

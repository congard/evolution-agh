package congard.agh.evolution.gui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.MultipleSelectionModel

// Based on https://stackoverflow.com/questions/20621752/javafx-make-listview-not-selectable-via-mouse
class NoSelectionModel<T> : MultipleSelectionModel<T>() {
    override fun clearAndSelect(index: Int) {}

    override fun select(index: Int) {}

    override fun clearSelection(index: Int) {}

    override fun clearSelection() {}

    override fun isSelected(index: Int) = false

    override fun isEmpty() = true

    override fun selectPrevious() {}

    override fun selectNext() {}

    override fun selectFirst() {}

    override fun selectLast() {}

    override fun getSelectedIndices(): ObservableList<Int> =
        FXCollections.emptyObservableList()

    override fun getSelectedItems(): ObservableList<T> =
        FXCollections.emptyObservableList()

    override fun selectIndices(index: Int, vararg indices: Int) {}

    override fun selectAll() {}

    override fun select(obj: T) {}
}
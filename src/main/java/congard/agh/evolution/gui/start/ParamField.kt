package congard.agh.evolution.gui.start

import congard.agh.evolution.simulation.math.Point2d
import congard.agh.evolution.simulation.params.*
import javafx.scene.Node
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import kotlin.reflect.KMutableProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * Creates JavaFx input for property prop
 * Supported types are:
 *     `Int`, `Float`, `Vector2d`, Enums
 */
class ParamField(
    private val prop: KMutableProperty<*>,
    private val params: Params
) {
    // reads & parses value, then writes it to params
    private val valReader: () -> Unit

    // JavaFx widget
    val node: Node

    init {
        when (prop.returnType.jvmErasure) {
            Int::class -> {
                node = TextField(prop.getter.call(params).toString())
                valReader = { prop.setter.call(params, node.text.toInt()) }
            }
            Float::class -> {
                node = TextField(prop.getter.call(params).toString())
                valReader = { prop.setter.call(params, node.text.toFloat()) }
            }
            Point2d::class -> {
                node = HBox()
                val default = prop.getter.call(params) as Point2d
                val widthField = TextField(default.x.toString())
                val heightField = TextField(default.y.toString())
                node.children.addAll(widthField, heightField)
                valReader = { prop.setter.call(params,
                    Point2d(
                        widthField.text.toInt(),
                        heightField.text.toInt()
                    )
                ) }
            }
            else -> {
                if (prop.returnType.jvmErasure.java.isEnum) {
                    node = ComboBox<Enum<*>>()

                    prop.returnType.jvmErasure.java.fields.forEach {
                        node.items.add(java.lang.Enum.valueOf(it.type as Class<out Enum<*>?>, it.name))
                    }

                    node.selectionModel.select(prop.getter.call(params) as Enum<*>)

                    valReader = { prop.setter.call(params, node.value) }
                } else {
                    // never should happen
                    throw RuntimeException("Unknown type: " + prop.returnType.toString())
                }
            }
        }
    }

    /**
     * Updates field based on property type
     */
    fun update() {
        when (prop.returnType.jvmErasure) {
            Int::class, Float::class -> (node as TextField).text = prop.getter.call(params).toString()
            Point2d::class -> {
                node as HBox
                val default = prop.getter.call(params) as Point2d
                (node.children[0] as TextField).text = default.x.toString()
                (node.children[1] as TextField).text = default.y.toString()
            }
            else -> {
                if (prop.returnType.jvmErasure.java.isEnum) {
                    (node as ComboBox<Enum<*>>).selectionModel.select(prop.getter.call(params) as Enum<*>)
                } else {
                    // never should happen
                    throw RuntimeException("Unknown type: " + prop.returnType.toString())
                }
            }
        }
    }

    fun readValue() = valReader()

    fun name() = (prop.annotations.find { it is Param } as Param).name
}
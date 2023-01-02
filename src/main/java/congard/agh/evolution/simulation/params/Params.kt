package congard.agh.evolution.simulation.params

import congard.agh.evolution.simulation.math.Point2d
import org.json.JSONObject
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

// I use Kotlin here because I want to keep this class readable;
// It's easier to auto-serialize/deserialize to/from JSON Kotlin
// class rather than Java, since Kotlin automatically generates
// getters & setters
// ...no i żeby było ciekawiej :)

class Params {
    @Param(name = "Map size")
    var mapSize = Point2d(16, 16)

    @Param(name = "Map type")
    var mapType = MapType.ROUND

    @Param(name = "Start plant count")
    var startPlantCount = 10

    @Param(name = "Plant nutritional value")
    var plantNutritionalValue = 5

    @Param(name = "Plants per day")
    var plantsPerDay = 5

    @Param(name = "Plant grow type")
    var plantGrowType = PlantGrowType.FORESTED_EQUATORS

    @Param(name = "Animals start count")
    var animalsStartCount = 10

    @Param(name = "Animals start energy")
    var animalsStartEnergy = 20

    @Param(name = "Well-fed threshold")
    var wellFedThreshold = 5

    @Param(name = "Child energy cost")
    var childEnergyCost = 3

    @Param(name = "The minimal number of mutations")
    var minMutationCount = 0

    @Param(name = "The maximum number of mutations")
    var maxMutationCount = 2

    @Param(name = "Mutation type")
    var mutationType = MutationType.RANDOM

    @Param(name = "Genome length")
    var genomeLength = 32

    @Param(name = "Behaviour type")
    var behaviourType = BehaviourType.PREDESTINATION

    @Param(name = "Day length (ms)", mutable = true)
    var dayLength = 100

    // hidden params

    @Param(name = "Animal life cost per day", hidden = true)
    var animalLifeCostPerDay = 1

    @Param(name = "Stats chart length (in days)", hidden = true, mutable = true)
    var statsChartLength = 500

    companion object {
        @JvmStatic
        fun deserialize(obj: JSONObject): Params {
            val params = Params()

            Params::class.memberProperties.forEach { property ->
                val key = property.name

                if (!obj.has(key))
                    return@forEach

                if (property is KMutableProperty<*>) {
                    val test: Any? = when (property.returnType.jvmErasure) {
                        Int::class -> obj.getInt(key)
                        Float::class -> obj.getFloat(key)
                        BehaviourType::class -> BehaviourType.valueOf(obj.getString(key))
                        MapType::class -> MapType.valueOf(obj.getString(key))
                        MutationType::class -> MutationType.valueOf(obj.getString(key))
                        PlantGrowType::class -> PlantGrowType.valueOf(obj.getString(key))
                        Point2d::class -> Point2d.deserialize(obj.getJSONObject(key))
                        else -> null // should never happen
                    }

                    if (test != null) {
                        property.setter.call(params, test)
                    }
                }
            }

            return params
        }

        fun getProperties() =
            Params::class.declaredMemberProperties.map { it as (KMutableProperty<*>) }
    }
}

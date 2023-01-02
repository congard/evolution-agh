package congard.agh.evolution.gui.math

class Vec2(
    var x: Float = 0f,
    var y: Float = 0f
) {
    constructor(v: Vec2) : this(v.x, v.y)

    constructor(x: Number, y: Number) : this(x.toFloat(), y.toFloat())

    operator fun unaryPlus() =
        Vec2(x, y)

    operator fun unaryMinus() =
        Vec2(-x, -y)

    operator fun plusAssign(v: Vec2) {
        x += v.x
        y += v.y
    }

    operator fun minusAssign(v: Vec2) {
        x -= v.x
        y -= v.y
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
    }

    operator fun divAssign(scalar: Float) =
        timesAssign(1 / scalar)

    operator fun plus(v: Vec2) =
        Vec2(x + v.x, y + v.y)

    operator fun minus(v: Vec2) =
        Vec2(x - v.x, y - v.y)

    operator fun times(scalar: Float) =
        Vec2(x * scalar, y * scalar)

    operator fun div(scalar: Float) =
        times(1 / scalar)

    operator fun get(index: Int): Float {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    operator fun set(index: Int, value: Float) {
        when (index) {
            0 -> x = value
            1 -> y = value
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    override fun toString(): String {
        return "Vec2($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec2

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }
}

fun dot(v1: Vec2, v2: Vec2) =
    v1.x * v2.x + v1.y * v2.y

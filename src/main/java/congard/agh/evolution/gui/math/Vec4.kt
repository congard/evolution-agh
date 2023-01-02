package congard.agh.evolution.gui.math

class Vec4(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 0f
) {
    constructor(v: Vec4) : this(v.x, v.y, v.z, v.w)

    operator fun unaryPlus() =
        Vec4(x, y, z, w)

    operator fun unaryMinus() =
        Vec4(-x, -y, -z, -w)

    operator fun plusAssign(v: Vec4) {
        x += v.x
        y += v.y
        z += v.z
        w += v.w
    }

    operator fun minusAssign(v: Vec4) {
        x -= v.x
        y -= v.y
        z -= v.z
        w -= v.w
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
        z *= scalar
        w *= scalar
    }

    operator fun divAssign(scalar: Float) =
        timesAssign(1 / scalar)

    operator fun plus(v: Vec4) =
        Vec4(x + v.x, y + v.y, z + v.z, w + v.w)

    operator fun minus(v: Vec4) =
        Vec4(x - v.x, y - v.y, z - v.z, w - v.w)

    operator fun times(scalar: Float) =
        Vec4(x * scalar, y * scalar, z * scalar, w * scalar)

    operator fun div(scalar: Float) =
        times(1 / scalar)

    operator fun get(index: Int): Float {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    operator fun set(index: Int, value: Float) {
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> w = value
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    override fun toString(): String {
        return "Vec4($x, $y, $z, $w)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec4

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }
}

fun dot(v1: Vec4, v2: Vec4) =
    v1.x * v2.x + v1.y * v2.y + v1.z * v2.z + v1.w * v2.w

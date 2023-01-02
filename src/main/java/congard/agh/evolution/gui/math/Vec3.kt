package congard.agh.evolution.gui.math

class Vec3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    constructor(v: Vec3) : this(v.x, v.y, v.z)

    constructor(v: Vec2) : this(v.x, v.y, 0f)

    operator fun unaryPlus() =
        Vec3(x, y, z)

    operator fun unaryMinus() =
        Vec3(-x, -y, -z)

    operator fun plusAssign(v: Vec3) {
        x += v.x
        y += v.y
        z += v.z
    }

    operator fun minusAssign(v: Vec3) {
        x -= v.x
        y -= v.y
        z -= v.z
    }

    operator fun timesAssign(scalar: Float) {
        x *= scalar
        y *= scalar
        z *= scalar
    }

    operator fun divAssign(scalar: Float) =
        timesAssign(1 / scalar)

    operator fun plus(v: Vec3) =
        Vec3(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: Vec3) =
        Vec3(x - v.x, y - v.y, z - v.z)

    operator fun times(scalar: Float) =
        Vec3(x * scalar, y * scalar, z * scalar)

    operator fun div(scalar: Float) =
        times(1 / scalar)

    operator fun get(index: Int): Float {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    operator fun set(index: Int, value: Float) {
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> throw IndexOutOfBoundsException("Invalid index: $index")
        }
    }

    override fun toString(): String {
        return "Vec3($x, $y, $z)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Vec3

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}

fun dot(v1: Vec3, v2: Vec3) =
    v1.x * v2.x + v1.y * v2.y + v1.z * v2.z

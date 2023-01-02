package congard.agh.evolution.gui.math

class Mat4() {
    private val cols = Array(4) { index -> Vec4().also { it[index] = 1.0f } }

    constructor(m: Mat4) : this() {
        for (i in 0..3) {
            cols[i] = Vec4(m[i])
        }
    }

    constructor(vararg cols: Vec4) : this() {
        assert(cols.size == 4)

        for (i in 0..3) {
            this.cols[i] = cols[i]
        }
    }

    constructor(col1: Vec4, col2: Vec4, col3: Vec4, col4: Vec4) : this() {
        cols[0] = col1
        cols[1] = col2
        cols[2] = col3
        cols[3] = col4
    }

    operator fun get(index: Int) =
        cols[index]

    operator fun set(index: Int, value: Vec4) {
        cols[index] = value
    }

    operator fun timesAssign(scalar: Float) =
        cols.forEach { it *= scalar }

    operator fun divAssign(scalar: Float) =
        timesAssign(1 / scalar)

    operator fun times(scalar: Float) =
        Mat4(*cols.map { it * scalar }.toTypedArray())

    operator fun div(scalar: Float) =
        times(1 / scalar)

    operator fun times(v: Vec4): Vec4 {
        val mul0 = this[0] * v[0]
        val mul1 = this[1] * v[1]
        val mul2 = this[2] * v[2]
        val mul3 = this[3] * v[3]
        return mul0 + mul1 + mul2 + mul3
    }

    operator fun times(m: Mat4): Mat4 {
        val srcA0 = this[0]
        val srcA1 = this[1]
        val srcA2 = this[2]
        val srcA3 = this[3]

        val srcB0 = m[0]
        val srcB1 = m[1]
        val srcB2 = m[2]
        val srcB3 = m[3]

        val result = Mat4()
        result[0] = srcA0 * srcB0[0] + srcA1 * srcB0[1] + srcA2 * srcB0[2] + srcA3 * srcB0[3]
        result[1] = srcA0 * srcB1[0] + srcA1 * srcB1[1] + srcA2 * srcB1[2] + srcA3 * srcB1[3]
        result[2] = srcA0 * srcB2[0] + srcA1 * srcB2[1] + srcA2 * srcB2[2] + srcA3 * srcB2[3]
        result[3] = srcA0 * srcB3[0] + srcA1 * srcB3[1] + srcA2 * srcB3[2] + srcA3 * srcB3[3]

        return result
    }

    override fun toString(): String {
        return "Mat4(${cols[0]}, ${cols[1]}, ${cols[2]}, ${cols[3]})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Mat4

        if (!cols.contentEquals(other.cols)) return false

        return true
    }

    override fun hashCode(): Int {
        return cols.contentHashCode()
    }
}

fun scale(m: Mat4, x: Float, y: Float, z: Float): Mat4 {
    val result = Mat4()
    result[0] = m[0] * x
    result[1] = m[1] * y
    result[2] = m[2] * z
    result[3] = m[3]
    return result
}

fun translate(m: Mat4, dx: Float, dy: Float, dz: Float): Mat4 {
    val result = Mat4(m)
    result[3] = m[0] * dx + m[1] * dy + m[2] * dz + m[3]
    return result
}

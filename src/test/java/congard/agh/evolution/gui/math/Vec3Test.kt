package congard.agh.evolution.gui.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class Vec3Test {

    @Test
    operator fun unaryPlus() {
        val v = Vec3(1f, 2f, 3f)
        assertEquals(v, +v)
    }

    @Test
    operator fun unaryMinus() {
        val expected = Vec3(-1f, -2f, -3f)
        assertEquals(expected, -Vec3(1f, 2f, 3f))
    }

    @Test
    fun plusAssign() {
        val actual = Vec3(1f, 1f, 1f)
        val delta = Vec3(0f, 1f, 2f)
        val expected = Vec3(1f, 2f, 3f)

        actual += delta

        assertEquals(expected, actual)
    }

    @Test
    fun minusAssign() {
        val actual = Vec3(2f, 3f, 4f)
        val delta = Vec3(1f, 1f, 1f)
        val expected = Vec3(1f, 2f, 3f)

        actual -= delta

        assertEquals(expected, actual)
    }

    @Test
    fun timesAssign() {
        val actual = Vec3(0.5f, 1f, 1.5f)
        val scalar = 2f
        val expected = Vec3(1f, 2f, 3f)

        actual *= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun divAssign() {
        val actual = Vec3(0.5f, 1f, 1.5f)
        val scalar = 0.5f
        val expected = Vec3(1f, 2f, 3f)

        actual /= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun plus() {
        val actual = Vec3(0f, 2f, 0f) + Vec3(1f, 0f, 3f)
        val expected = Vec3(1f, 2f, 3f)
        assertEquals(expected, actual)
    }

    @Test
    fun minus() {
        val actual = Vec3(5f, 4f, 3f) - Vec3(4f, 2f, 0f)
        val expected = Vec3(1f, 2f, 3f)
        assertEquals(expected, actual)
    }

    @Test
    fun times() {
        val actual = Vec3(0.5f, 1f, 1.5f) * 2f
        val expected = Vec3(1f, 2f, 3f)
        assertEquals(expected, actual)
    }

    @Test
    fun div() {
        val actual = Vec3(0.5f, 1f, 1.5f) / 0.5f
        val expected = Vec3(1f, 2f, 3f)
        assertEquals(expected, actual)
    }

    @Test
    fun get() {
        val v = Vec3(1f, 2f, 3f)
        assertEquals(1f, v[0])
        assertEquals(2f, v[1])
        assertEquals(3f, v[2])
    }

    @Test
    fun set() {
        val actual = Vec3()
        actual[0] = 1f
        actual[1] = 2f
        actual[2] = 3f
        val expected = Vec3(1f, 2f, 3f)
        assertEquals(expected, actual)
    }

    @Test
    fun testDot() {
        val v1 = Vec3(1f, 2f, 3f)
        val v2 = Vec3(-3f, 0f, 1f)
        val actual = dot(v1, v2)
        val expected = 0f
        kotlin.test.assertEquals(expected, actual)
    }
}
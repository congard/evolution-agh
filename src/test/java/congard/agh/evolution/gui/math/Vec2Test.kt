package congard.agh.evolution.gui.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class Vec2Test {

    @Test
    operator fun unaryPlus() {
        val v = Vec2(1f, 2f)
        assertEquals(v, +v)
    }

    @Test
    operator fun unaryMinus() {
        val expected = Vec2(-1f, -2f)
        assertEquals(expected, -Vec2(1f, 2f))
    }

    @Test
    fun plusAssign() {
        val actual = Vec2(1f, 1f)
        val delta = Vec2(0f, 1f)
        val expected = Vec2(1f, 2f)

        actual += delta

        assertEquals(expected, actual)
    }

    @Test
    fun minusAssign() {
        val actual = Vec2(2f, 3f)
        val delta = Vec2(1f, 1f)
        val expected = Vec2(1f, 2f)

        actual -= delta

        assertEquals(expected, actual)
    }

    @Test
    fun timesAssign() {
        val actual = Vec2(0.5f, 1f)
        val scalar = 2f
        val expected = Vec2(1f, 2f)

        actual *= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun divAssign() {
        val actual = Vec2(0.5f, 1f)
        val scalar = 0.5f
        val expected = Vec2(1f, 2f)

        actual /= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun plus() {
        val actual = Vec2(0f, 2f) + Vec2(1f, 0f)
        val expected = Vec2(1f, 2f)
        assertEquals(expected, actual)
    }

    @Test
    fun minus() {
        val actual = Vec2(5f, 4f) - Vec2(4f, 2f)
        val expected = Vec2(1f, 2f)
        assertEquals(expected, actual)
    }

    @Test
    fun times() {
        val actual = Vec2(0.5f, 1f) * 2f
        val expected = Vec2(1f, 2f)
        assertEquals(expected, actual)
    }

    @Test
    fun div() {
        val actual = Vec2(0.5f, 1f) / 0.5f
        val expected = Vec2(1f, 2f)
        assertEquals(expected, actual)
    }

    @Test
    fun get() {
        val v = Vec2(1f, 2f)
        assertEquals(1f, v[0])
        assertEquals(2f, v[1])
    }

    @Test
    fun set() {
        val actual = Vec2()
        actual[0] = 1f
        actual[1] = 2f
        val expected = Vec2(1f, 2f)
        assertEquals(expected, actual)
    }

    @Test
    fun testDot() {
        val v1 = Vec2(1f, 3f)
        val v2 = Vec2(-3f, 1f)
        val actual = dot(v1, v2)
        val expected = 0f
        kotlin.test.assertEquals(expected, actual)
    }
}
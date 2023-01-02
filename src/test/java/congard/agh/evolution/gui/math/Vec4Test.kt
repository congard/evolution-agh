package congard.agh.evolution.gui.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Vec4Test {
    @Test
    fun unaryPlus() {
        val v = Vec4(1f, 2f, 3f, 4f)
        assertEquals(v, +v)
    }

    @Test
    fun unaryMinus() {
        val expected = Vec4(-1f, -2f, -3f, -4f)
        assertEquals(expected, -Vec4(1f, 2f, 3f, 4f))
    }

    @Test
    fun plusAssign() {
        val actual = Vec4(1f, 1f, 1f, 1f)
        val delta = Vec4(0f, 1f, 2f, 3f)
        val expected = Vec4(1f, 2f, 3f, 4f)

        actual += delta

        assertEquals(expected, actual)
    }

    @Test
    fun minusAssign() {
        val actual = Vec4(2f, 3f, 4f, 5f)
        val delta = Vec4(1f, 1f, 1f, 1f)
        val expected = Vec4(1f, 2f, 3f, 4f)

        actual -= delta

        assertEquals(expected, actual)
    }

    @Test
    fun timesAssign() {
        val actual = Vec4(0.5f, 1f, 1.5f, 2f)
        val scalar = 2f
        val expected = Vec4(1f, 2f, 3f, 4f)

        actual *= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun divAssign() {
        val actual = Vec4(0.5f, 1f, 1.5f, 2f)
        val scalar = 0.5f
        val expected = Vec4(1f, 2f, 3f, 4f)

        actual /= scalar

        assertEquals(expected, actual)
    }

    @Test
    fun plus() {
        val actual = Vec4(0f, 2f, 0f, 4f) + Vec4(1f, 0f, 3f, 0f)
        val expected = Vec4(1f, 2f, 3f, 4f)
        assertEquals(expected, actual)
    }

    @Test
    fun minus() {
        val actual = Vec4(5f, 4f, 3f, 2f) - Vec4(4f, 2f, 0f, -2f)
        val expected = Vec4(1f, 2f, 3f, 4f)
        assertEquals(expected, actual)
    }

    @Test
    fun times() {
        val actual = Vec4(0.5f, 1f, 1.5f, 2f) * 2f
        val expected = Vec4(1f, 2f, 3f, 4f)
        assertEquals(expected, actual)
    }

    @Test
    fun div() {
        val actual = Vec4(0.5f, 1f, 1.5f, 2f) / 0.5f
        val expected = Vec4(1f, 2f, 3f, 4f)
        assertEquals(expected, actual)
    }

    @Test
    fun get() {
        val v = Vec4(1f, 2f, 3f, 4f)
        assertEquals(1f, v[0])
        assertEquals(2f, v[1])
        assertEquals(3f, v[2])
        assertEquals(4f, v[3])
    }

    @Test
    fun set() {
        val actual = Vec4()
        actual[0] = 1f
        actual[1] = 2f
        actual[2] = 3f
        actual[3] = 4f
        val expected = Vec4(1f, 2f, 3f, 4f)
        assertEquals(expected, actual)
    }

    @Test
    fun testDot() {
        val v1 = Vec4(1f, 2f, 3f, 4f)
        val v2 = Vec4(-3f, -2f, 1f, 1f)
        val actual = dot(v1, v2)
        val expected = 0f
        assertEquals(expected, actual)
    }
}

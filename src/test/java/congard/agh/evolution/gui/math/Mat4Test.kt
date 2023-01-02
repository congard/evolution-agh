package congard.agh.evolution.gui.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class Mat4Test {
    @Test
    fun get() {
        val m = Mat4()
        assertEquals(Vec4(1f, 0f, 0f, 0f), m[0])
        assertEquals(Vec4(0f, 1f, 0f, 0f), m[1])
        assertEquals(Vec4(0f, 0f, 1f, 0f), m[2])
        assertEquals(Vec4(0f, 0f, 0f, 1f), m[3])
    }

    @Test
    fun set() {
        val m = Mat4()
        m[0] = Vec4(1f, 2f, 3f, 4f)
        m[1] = Vec4(2f, 4f, 6f, 8f)
        m[2] = Vec4(3f, 6f, 9f, 12f)
        m[3] = Vec4(4f, 8f, 12f, 16f)
        assertEquals(Vec4(1f, 2f, 3f, 4f), m[0])
        assertEquals(Vec4(2f, 4f, 6f, 8f), m[1])
        assertEquals(Vec4(3f, 6f, 9f, 12f), m[2])
        assertEquals(Vec4(4f, 8f, 12f, 16f), m[3])
    }

    @Test
    fun timesAssign() {
        val m = Mat4()
        m *= 2f
        assertEquals(Vec4(2f, 0f, 0f, 0f), m[0])
        assertEquals(Vec4(0f, 2f, 0f, 0f), m[1])
        assertEquals(Vec4(0f, 0f, 2f, 0f), m[2])
        assertEquals(Vec4(0f, 0f, 0f, 2f), m[3])
    }

    @Test
    fun divAssign() {
        val m = Mat4()
        m /= 0.5f
        assertEquals(Vec4(2f, 0f, 0f, 0f), m[0])
        assertEquals(Vec4(0f, 2f, 0f, 0f), m[1])
        assertEquals(Vec4(0f, 0f, 2f, 0f), m[2])
        assertEquals(Vec4(0f, 0f, 0f, 2f), m[3])
    }

    @Test
    fun times() {
        val m = Mat4() * 2f
        assertEquals(Vec4(2f, 0f, 0f, 0f), m[0])
        assertEquals(Vec4(0f, 2f, 0f, 0f), m[1])
        assertEquals(Vec4(0f, 0f, 2f, 0f), m[2])
        assertEquals(Vec4(0f, 0f, 0f, 2f), m[3])
    }

    @Test
    fun div() {
        val m = Mat4() / 0.5f
        assertEquals(Vec4(2f, 0f, 0f, 0f), m[0])
        assertEquals(Vec4(0f, 2f, 0f, 0f), m[1])
        assertEquals(Vec4(0f, 0f, 2f, 0f), m[2])
        assertEquals(Vec4(0f, 0f, 0f, 2f), m[3])
    }

    @Test
    fun testTimesMxV() {
        val m = Mat4()
        val v = Vec4(1f, 0f, 0f, 1f)
        val actual = m * v
        assertEquals(v, actual)

        val m1 = Mat4(
            Vec4(-0.5f, 0f, 0f, 0f),
            Vec4(0f, -0.5f, 0f, 0f),
            Vec4(0f, 0f, 0f, 0f),
            Vec4(-0.5f, -2f, 3f, 1f)
        )
        val v1 = Vec4(1f, 0f, 0f, 1f)
        val actual1 = m1 * v1
        val expected1 = Vec4(-1f, -2f, 3f, 1f)
        assertEquals(expected1, actual1)
    }

    @Test
    fun testTimesMxM() {
        val translation = translate(Mat4(), 1f, 0f, 0f)
        val scaling = scale(Mat4(), 0.5f, 0.5f, 0.5f)
        val actual = translation * scaling
        val expected = Mat4(
            Vec4(0.5f, 0f, 0f, 0f),
            Vec4(0f, 0.5f, 0f, 0f),
            Vec4(0.0f, 0f, 0.5f, 0f),
            Vec4(1f, 0f, 0f, 1f)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun testScale() {
        val m = scale(Mat4(), 1f, 2f, 3f)
        val v = Vec4(1f, 1f, 1f, 1f)
        val actual = m * v
        val expected = Vec4(1f, 2f, 3f, 1f)
        assertEquals(expected, actual)
    }

    @Test
    fun testTranslate() {
        val m = translate(Mat4(), 1f, 2f, 3f)
        val v = Vec4(0f, 0f, 0f, 1f)
        val actual = m * v
        val expected = Vec4(1f, 2f, 3f, 1f)
        assertEquals(expected, actual)
    }
}
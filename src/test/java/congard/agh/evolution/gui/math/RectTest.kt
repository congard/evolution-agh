package congard.agh.evolution.gui.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class RectTest {

    @Test
    fun x() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(1f, r.x())
    }

    @Test
    fun y() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(2f, r.y())
    }

    @Test
    fun width() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(2f, r.width())
    }

    @Test
    fun height() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(2f, r.height())
    }

    @Test
    fun size() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(Vec2(2f, 2f), r.size())
    }

    @Test
    fun center() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))
        assertEquals(Vec2(2f, 3f), r.center())
    }

    @Test
    fun contains() {
        val r = Rect(Vec2(1f, 2f), Vec2(3f, 4f))

        assertTrue(r.contains(Vec2(1f, 2f)))
        assertTrue(r.contains(Vec2(3f, 4f)))
        assertTrue(r.contains(Vec2(2f, 3f)))

        assertFalse(r.contains(Vec2(0f, 1f)))
        assertFalse(r.contains(Vec2(3f, 5f)))
    }
}
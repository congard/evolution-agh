package congard.agh.evolution.gui.math

class Rect(
    val upperLeft: Vec2,
    val lowerRight: Vec2
) {
    constructor(upperLeft: Vec2, width: Float, height: Float)
            : this(upperLeft, upperLeft + Vec2(width, height))

    constructor(upperLeft: Vec2, size: Float) : this(upperLeft, size, size)

    fun x() = upperLeft.x
    fun y() = upperLeft.y

    fun width() = lowerRight.x - upperLeft.x
    fun height() = lowerRight.y - upperLeft.y

    fun size() = lowerRight - upperLeft

    fun center() = upperLeft + size() / 2f

    operator fun contains(v: Vec2): Boolean {
        val x1 = upperLeft.x
        val x2 = lowerRight.x
        val y1 = upperLeft.y
        val y2 = lowerRight.y
        return v.x in x1..x2 && v.y in y1..y2
    }
}

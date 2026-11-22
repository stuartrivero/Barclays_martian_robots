package com.barclays.martianrobots.domain

// Declared in clockwise order, so turning is a step through the entries.
enum class Orientation(val dx: Int, val dy: Int) {
    N(0, 1),
    E(1, 0),
    S(0, -1),
    W(-1, 0);

    fun right(): Orientation = entries[(ordinal + 1) % entries.size]

    fun left(): Orientation = entries[(ordinal + entries.size - 1) % entries.size]
}

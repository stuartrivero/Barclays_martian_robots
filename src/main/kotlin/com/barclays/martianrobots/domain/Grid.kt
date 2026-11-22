package com.barclays.martianrobots.domain

data class Grid(
    val upperRight: Position,
    val scents: Set<Position> = emptySet(),
    val occupied: Set<Position> = emptySet(),
) {
    init {
        require(upperRight.x in 0..MAX_COORDINATE && upperRight.y in 0..MAX_COORDINATE) {
            "Grid corner must be between 0 and $MAX_COORDINATE, was ${upperRight.x} ${upperRight.y}"
        }
    }

    fun contains(position: Position): Boolean =
        position.x in 0..upperRight.x && position.y in 0..upperRight.y

    fun isScented(position: Position): Boolean = position in scents

    fun isOccupied(position: Position): Boolean = position in occupied

    fun withScent(position: Position): Grid = copy(scents = scents + position)

    fun withOccupied(position: Position): Grid = copy(occupied = occupied + position)

    fun withoutOccupied(position: Position): Grid = copy(occupied = occupied - position)

    companion object {
        const val MAX_COORDINATE = 50
    }
}

package com.barclays.martianrobots.domain

data class RobotState(val position: Position, val orientation: Orientation) {
    fun positionAhead(): Position = position.step(orientation)
}

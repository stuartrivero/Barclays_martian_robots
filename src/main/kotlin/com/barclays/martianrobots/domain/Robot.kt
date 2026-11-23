package com.barclays.martianrobots.domain

data class RobotResult(val finalState: RobotState, val lost: Boolean)

data class RobotRun(val result: RobotResult, val grid: Grid)

data class Robot(val startState: RobotState, val commands: List<Command>) {

    fun run(grid: Grid): RobotRun {
        var state = startState
        for (command in commands) {
            val next = command.apply(state)
            when {
                !grid.contains(next.position) -> {
                    // Scent marks the point a robot fell from, not the direction it took,
                    // so it protects that point from moves off the grid in every direction.
                    if (grid.isScented(state.position)) continue
                    return RobotRun(RobotResult(state, lost = true), grid.withScent(state.position))
                }
                grid.isOccupied(next.position) -> continue
                else -> state = next
            }
        }
        return RobotRun(RobotResult(state, lost = false), grid.withOccupied(state.position))
    }
}

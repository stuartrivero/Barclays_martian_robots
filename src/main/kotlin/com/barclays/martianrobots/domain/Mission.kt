package com.barclays.martianrobots.domain

data class Mission(val grid: Grid, val robots: List<Robot>) {

    private val startingRobotPositions = robots.map { it.startState.position }

    init {
        startingRobotPositions.forEach { start ->
            require(grid.contains(start)) { "starting position ${start.x} ${start.y} is outside the grid" }
        }
        require(startingRobotPositions.toSet().size == startingRobotPositions.size) { "two robots share a starting position" }
    }

    // Every robot is placed at its start before the first one moves, so each robot
    // is blocked by the robots still waiting at their starts and by earlier robots
    // that weren't lost. A robot frees its start as soon as it begins to run.
    fun run(): List<RobotResult> {
        val gridWithRobotsPlaced = startingRobotPositions.fold(grid, Grid::withOccupied)
        return robots
            .fold(emptyList<RobotResult>() to gridWithRobotsPlaced) { (results, current), robot ->
                val run = robot.run(current.withoutOccupied(robot.startState.position))
                (results + run.result) to run.grid
            }
            .first
    }
}

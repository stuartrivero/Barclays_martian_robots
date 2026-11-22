package com.barclays.martianrobots.domain

class InvalidStartException(val robotIndex: Int, message: String) : IllegalArgumentException(message)

data class Mission(val grid: Grid, val robots: List<Robot>) {

    // Robots run one after another, each on the grid left by the ones before it,
    // because scent and occupied positions only matter across robots.
    fun run(): List<RobotResult> =
        robots.withIndex()
            .fold(emptyList<RobotResult>() to grid) { (results, current), (index, robot) ->
                checkStart(current, index, robot.start.position)
                val run = robot.run(current)
                (results + run.result) to run.grid
            }
            .first

    private fun checkStart(grid: Grid, index: Int, start: Position) {
        if (!grid.contains(start)) {
            throw InvalidStartException(index, "Starting position ${start.x} ${start.y} is outside the grid")
        }
        if (grid.isOccupied(start)) {
            throw InvalidStartException(index, "Starting position ${start.x} ${start.y} is already occupied")
        }
    }
}

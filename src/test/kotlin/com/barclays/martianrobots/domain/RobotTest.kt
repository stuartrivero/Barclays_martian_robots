package com.barclays.martianrobots.domain

import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Orientation.S
import com.barclays.martianrobots.domain.Orientation.W
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RobotTest {

    private val grid = Grid(upperRight = Position(5, 3))

    private fun robot(x: Int, y: Int, orientation: Orientation, letters: String) =
        Robot(RobotState(Position(x, y), orientation), letters.map { Commands.forLetter(it)!! })

    @Test
    fun `robot from the brief ends where the sample output says`() {
        val run = robot(1, 1, E, "RFRFRFRF").run(grid)

        run.result shouldBe RobotResult(RobotState(Position(1, 1), E), lost = false)
    }

    @Test
    fun `robot is lost off each of the four edges and reports its last position on the grid`() {
        robot(2, 3, N, "F").run(grid).result shouldBe RobotResult(RobotState(Position(2, 3), N), lost = true)
        robot(5, 1, E, "F").run(grid).result shouldBe RobotResult(RobotState(Position(5, 1), E), lost = true)
        robot(2, 0, S, "F").run(grid).result shouldBe RobotResult(RobotState(Position(2, 0), S), lost = true)
        robot(0, 1, W, "F").run(grid).result shouldBe RobotResult(RobotState(Position(0, 1), W), lost = true)
    }

    @Test
    fun `lost robot runs none of its remaining instructions`() {
        robot(2, 3, N, "FRFF").run(grid).result shouldBe RobotResult(RobotState(Position(2, 3), N), lost = true)
    }

    @Test
    fun `lost robot leaves scent at its last position and does not occupy it`() {
        val after = robot(2, 3, N, "F").run(grid).grid

        after.isScented(Position(2, 3)) shouldBe true
        after.isOccupied(Position(2, 3)) shouldBe false
    }

    @Test
    fun `robot ignores move off grid from scented point and carries on`() {
        val scented = grid.withScent(Position(2, 3))

        val run = robot(2, 3, N, "FRF").run(scented)

        run.result shouldBe RobotResult(RobotState(Position(3, 3), E), lost = false)
    }

    @Test
    fun `scent at a corner protects both of its edges`() {
        val scented = grid.withScent(Position(5, 3))

        robot(5, 3, N, "F").run(scented).result.lost shouldBe false
        robot(5, 3, E, "F").run(scented).result.lost shouldBe false
    }

    @Test
    fun `scent only protects its own point, so a robot can still be lost elsewhere`() {
        val scented = grid.withScent(Position(2, 3))

        val run = robot(2, 3, N, "FRFLF").run(scented)

        run.result shouldBe RobotResult(RobotState(Position(3, 3), N), lost = true)
        run.grid.isScented(Position(3, 3)) shouldBe true
    }

    @Test
    fun `robot that finishes on the grid occupies its final position`() {
        val after = robot(1, 1, N, "F").run(grid).grid

        after.isOccupied(Position(1, 2)) shouldBe true
        after.isScented(Position(1, 2)) shouldBe false
    }

    @Test
    fun `robot ignores move into a position occupied by an earlier robot and carries on`() {
        val occupied = grid.withOccupied(Position(1, 2))

        val run = robot(1, 1, N, "FRF").run(occupied)

        run.result shouldBe RobotResult(RobotState(Position(2, 1), E), lost = false)
    }

    @Test
    fun `robot can turn while next to an occupied position`() {
        val occupied = grid.withOccupied(Position(1, 2))

        robot(1, 1, N, "LR").run(occupied).result shouldBe RobotResult(RobotState(Position(1, 1), N), lost = false)
    }
}

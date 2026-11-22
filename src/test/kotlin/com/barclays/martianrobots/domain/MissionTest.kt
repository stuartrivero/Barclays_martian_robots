package com.barclays.martianrobots.domain

import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Orientation.S
import com.barclays.martianrobots.domain.Orientation.W
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class MissionTest {

    private val grid = Grid(upperRight = Position(5, 3))

    private fun robot(x: Int, y: Int, orientation: Orientation, letters: String) =
        Robot(RobotState(Position(x, y), orientation), letters.map { Commands.forLetter(it)!! })

    @Test
    fun `sample from the brief, where the third robot is saved by the second robot's scent`() {
        val results = Mission(
            grid,
            listOf(
                robot(1, 1, E, "RFRFRFRF"),
                robot(3, 2, N, "FRRFLLFFRRFLL"),
                robot(0, 3, W, "LLFFFLFLFL"),
            ),
        ).run()

        results shouldBe listOf(
            RobotResult(RobotState(Position(1, 1), E), lost = false),
            RobotResult(RobotState(Position(3, 3), N), lost = true),
            RobotResult(RobotState(Position(2, 3), S), lost = false),
        )
    }

    @Test
    fun `later robot ignores move into a position held by an earlier robot`() {
        val results = Mission(grid, listOf(robot(1, 2, N, "R"), robot(1, 1, N, "FRF"))).run()

        results[1] shouldBe RobotResult(RobotState(Position(2, 1), E), lost = false)
    }

    @Test
    fun `lost robot does not block a later robot`() {
        val results = Mission(grid, listOf(robot(2, 3, N, "F"), robot(2, 2, N, "F"))).run()

        results[1] shouldBe RobotResult(RobotState(Position(2, 3), N), lost = false)
    }

    @Test
    fun `no robots gives no results`() {
        Mission(grid, emptyList()).run() shouldBe emptyList()
    }

    @Test
    fun `rejects a robot starting on a position held by an earlier robot`() {
        val error = shouldThrow<InvalidStartException> {
            Mission(grid, listOf(robot(1, 1, N, "R"), robot(0, 0, E, "F"), robot(1, 1, S, "F"))).run()
        }

        error.robotIndex shouldBe 2
    }

    @Test
    fun `cannot be created with a robot starting outside the grid`() {
        shouldThrow<IllegalArgumentException> {
            Mission(grid, listOf(robot(1, 1, N, "R"), robot(6, 0, E, "F")))
        }
    }
}

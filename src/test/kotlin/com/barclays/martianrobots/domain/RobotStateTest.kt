package com.barclays.martianrobots.domain

import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Orientation.S
import com.barclays.martianrobots.domain.Orientation.W
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class RobotStateTest {

    @Test
    fun `position ahead is one step in the direction the robot faces`() {
        val from = Position(2, 2)

        RobotState(from, N).positionAhead() shouldBe Position(2, 3)
        RobotState(from, E).positionAhead() shouldBe Position(3, 2)
        RobotState(from, S).positionAhead() shouldBe Position(2, 1)
        RobotState(from, W).positionAhead() shouldBe Position(1, 2)
    }

    @Test
    fun `position ahead can be off the grid, which the grid decides`() {
        RobotState(Position(0, 0), S).positionAhead() shouldBe Position(0, -1)
        RobotState(Position(0, 0), W).positionAhead() shouldBe Position(-1, 0)
    }
}

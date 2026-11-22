package com.barclays.martianrobots.domain

import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Orientation.W
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CommandTest {

    private val start = RobotState(Position(1, 1), N)

    @Test
    fun `left turns the robot without moving it`() {
        Left.apply(start) shouldBe RobotState(Position(1, 1), W)
    }

    @Test
    fun `right turns the robot without moving it`() {
        Right.apply(start) shouldBe RobotState(Position(1, 1), E)
    }

    @Test
    fun `forward moves one step in the current orientation without turning`() {
        Forward.apply(start) shouldBe RobotState(Position(1, 2), N)
    }

    @Test
    fun `commands are looked up by their letter`() {
        Commands.forLetter('L') shouldBe Left
        Commands.forLetter('R') shouldBe Right
        Commands.forLetter('F') shouldBe Forward
    }

    @Test
    fun `an unknown letter has no command`() {
        Commands.forLetter('X') shouldBe null
        Commands.forLetter('l') shouldBe null
    }
}

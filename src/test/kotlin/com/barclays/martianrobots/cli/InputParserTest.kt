package com.barclays.martianrobots.cli

import com.barclays.martianrobots.domain.Forward
import com.barclays.martianrobots.domain.Grid
import com.barclays.martianrobots.domain.Left
import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Position
import com.barclays.martianrobots.domain.Right
import com.barclays.martianrobots.domain.Robot
import com.barclays.martianrobots.domain.RobotState
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class InputParserTest {

    private fun parse(vararg lines: String) = InputParser.parse(lines.toList())

    private fun parseError(vararg lines: String) =
        shouldThrow<InvalidInputException> { parse(*lines) }

    @Test
    fun `parses the grid and each robot with the line number of its position line`() {
        val input = parse("5 3", "1 1 E", "RFL", "", "3 2 N", "F")

        input.grid shouldBe Grid(upperRight = Position(5, 3))
        input.robots shouldBe listOf(
            Robot(RobotState(Position(1, 1), E), listOf(Right, Forward, Left)),
            Robot(RobotState(Position(3, 2), N), listOf(Forward)),
        )
        input.robotLineNumbers shouldBe listOf(2, 5)
    }

    @Test
    fun `ignores extra spaces and blank lines between robots`() {
        val input = parse("  5   3 ", "", "", " 1  1   E ", "  F  ", "   ")

        input.grid shouldBe Grid(upperRight = Position(5, 3))
        input.robots shouldBe listOf(Robot(RobotState(Position(1, 1), E), listOf(Forward)))
    }

    @Test
    fun `a grid with no robots is valid`() {
        parse("5 3").robots shouldBe emptyList()
    }

    @Test
    fun `rejects empty input`() {
        parseError().message shouldBe "Line 1: missing grid line"
    }

    @Test
    fun `rejects a malformed grid line`() {
        parseError("5").message shouldBe "Line 1: expected grid as 'x y', got '5'"
        parseError("5 three").message shouldBe "Line 1: expected grid as 'x y', got '5 three'"
        parseError("5 3 1").message shouldBe "Line 1: expected grid as 'x y', got '5 3 1'"
    }

    @Test
    fun `rejects grid coordinates outside 0 to 50`() {
        parseError("51 3").message shouldBe "Line 1: coordinate 51 is outside 0-50"
        parseError("5 -1").message shouldBe "Line 1: coordinate -1 is outside 0-50"
    }

    @Test
    fun `rejects a malformed position line`() {
        parseError("5 3", "1 1", "F").message shouldBe "Line 2: expected position as 'x y orientation', got '1 1'"
        parseError("5 3", "1 x E", "F").message shouldBe "Line 2: expected position as 'x y orientation', got '1 x E'"
    }

    @Test
    fun `rejects position coordinates outside 0 to 50`() {
        parseError("5 3", "1 51 E", "F").message shouldBe "Line 2: coordinate 51 is outside 0-50"
    }

    @Test
    fun `rejects a starting position outside the grid`() {
        parseError("5 3", "1 1 E", "F", "6 1 N", "F").message shouldBe
            "Line 4: starting position 6 1 is outside the grid"
        parseError("5 3", "1 4 N", "F").message shouldBe "Line 2: starting position 1 4 is outside the grid"
    }

    @Test
    fun `rejects an unknown orientation`() {
        parseError("5 3", "1 1 X", "F").message shouldBe "Line 2: unknown orientation 'X'"
        parseError("5 3", "1 1 n", "F").message shouldBe "Line 2: unknown orientation 'n'"
    }

    @Test
    fun `rejects a position line not followed straight away by an instruction line`() {
        parseError("5 3", "1 1 E").message shouldBe "Line 2: position has no instruction line after it"
        parseError("5 3", "1 1 E", "", "F").message shouldBe "Line 2: position has no instruction line after it"
    }

    @Test
    fun `rejects an unknown command letter`() {
        parseError("5 3", "1 1 E", "FXF").message shouldBe "Line 3: unknown command 'X'"
        parseError("5 3", "1 1 E", "F F").message shouldBe "Line 3: unknown command ' '"
    }

    @Test
    fun `accepts instructions of 99 characters and rejects 100`() {
        parse("5 3", "1 1 E", "L".repeat(99)).robots.single().commands.size shouldBe 99
        parseError("5 3", "1 1 E", "L".repeat(100)).message shouldBe
            "Line 3: instructions must be under 100 characters, got 100"
    }
}

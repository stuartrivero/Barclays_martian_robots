package com.barclays.martianrobots.cli

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.io.StringReader
import java.io.StringWriter
import org.junit.jupiter.api.Test

class MainTest {

    private fun runOn(input: String): String {
        val output = StringWriter()
        runRobots(StringReader(input), output)
        return output.toString()
    }

    @Test
    fun `sample input from the brief produces the sample output`() {
        val input = """
            5 3
            1 1 E
            RFRFRFRF

            3 2 N
            FRRFLLFFRRFLL

            0 3 W
            LLFFFLFLFL
        """.trimIndent()

        runOn(input) shouldBe """
            1 1 E
            3 3 N LOST
            2 3 S

        """.trimIndent()
    }

    @Test
    fun `a starting position already occupied is reported with its line number`() {
        val error = shouldThrow<InvalidInputException> {
            runOn("5 3\n1 1 N\nR\n\n1 1 E\nF\n")
        }

        error.message shouldBe "Line 5: starting position 1 1 is already occupied"
    }

    @Test
    fun `a starting position outside the grid is reported with its line number`() {
        val error = shouldThrow<InvalidInputException> {
            runOn("5 3\n6 1 N\nR\n")
        }

        error.message shouldBe "Line 2: starting position 6 1 is outside the grid"
    }

    @Test
    fun `nothing is written when a later robot is invalid`() {
        val output = StringWriter()

        shouldThrow<InvalidInputException> {
            runRobots(StringReader("5 3\n1 1 E\nF\n\n2 2 N\nFXF\n"), output)
        }

        output.toString() shouldBe ""
    }
}

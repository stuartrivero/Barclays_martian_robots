package com.barclays.martianrobots.cli

import com.barclays.martianrobots.domain.Command
import com.barclays.martianrobots.domain.Commands
import com.barclays.martianrobots.domain.Grid
import com.barclays.martianrobots.domain.Orientation
import com.barclays.martianrobots.domain.Position
import com.barclays.martianrobots.domain.Robot
import com.barclays.martianrobots.domain.RobotState

class InvalidInputException(lineNumber: Int, problem: String) : IllegalArgumentException("Line $lineNumber: $problem")

data class ParsedInput(val grid: Grid, val robots: List<Robot>)

object InputParser {

    private const val MAX_INSTRUCTION_LENGTH = 99

    fun parse(lines: List<String>): ParsedInput {
        val gridLine = lines.firstOrNull() ?: throw InvalidInputException(1, "missing grid line")
        val grid = Grid(upperRight = parseGrid(gridLine))

        val robots = mutableListOf<Robot>()
        val startLineNumbers = mutableMapOf<Position, Int>()
        var index = 1
        while (index < lines.size) {
            if (lines[index].isBlank()) {
                index++
                continue
            }
            val positionLineNumber = index + 1
            val start = parseStart(lines[index], positionLineNumber, grid)
            startLineNumbers[start.position]?.let { takenBy ->
                throw InvalidInputException(
                    positionLineNumber,
                    "starting position ${start.position.x} ${start.position.y} is already taken by the robot on line $takenBy",
                )
            }
            startLineNumbers[start.position] = positionLineNumber
            val instructions = lines.getOrNull(index + 1)?.trim()
            if (instructions.isNullOrEmpty()) {
                throw InvalidInputException(positionLineNumber, "position has no instruction line after it")
            }
            robots += Robot(start, parseCommands(instructions, positionLineNumber + 1))
            index += 2
        }
        return ParsedInput(grid, robots)
    }

    private fun parseGrid(line: String, lineNumber: Int = 1): Position {
        val tokens = line.tokens()
        val x = tokens.getOrNull(0)?.toIntOrNull()
        val y = tokens.getOrNull(1)?.toIntOrNull()
        if (tokens.size != 2 || x == null || y == null) {
            throw InvalidInputException(lineNumber, "expected grid as 'x y', got '${line.trim()}'")
        }
        return Position(coordinate(x, lineNumber), coordinate(y, lineNumber))
    }

    private fun parseStart(line: String, lineNumber: Int, grid: Grid): RobotState {
        val tokens = line.tokens()
        val x = tokens.getOrNull(0)?.toIntOrNull()
        val y = tokens.getOrNull(1)?.toIntOrNull()
        if (tokens.size != 3 || x == null || y == null) {
            throw InvalidInputException(lineNumber, "expected position as 'x y orientation', got '${line.trim()}'")
        }
        val orientation = Orientation.entries.find { it.name == tokens[2] }
            ?: throw InvalidInputException(lineNumber, "unknown orientation '${tokens[2]}'")
        val position = Position(coordinate(x, lineNumber), coordinate(y, lineNumber))
        if (!grid.contains(position)) {
            throw InvalidInputException(lineNumber, "starting position $x $y is outside the grid")
        }
        return RobotState(position, orientation)
    }

    private fun parseCommands(instructions: String, lineNumber: Int): List<Command> {
        if (instructions.length > MAX_INSTRUCTION_LENGTH) {
            throw InvalidInputException(
                lineNumber,
                "instructions must be under ${MAX_INSTRUCTION_LENGTH + 1} characters, got ${instructions.length}",
            )
        }
        return instructions.map { letter ->
            Commands.forLetter(letter) ?: throw InvalidInputException(lineNumber, "unknown command '$letter'")
        }
    }

    private fun coordinate(value: Int, lineNumber: Int): Int {
        if (value !in 0..Grid.MAX_COORDINATE) {
            throw InvalidInputException(lineNumber, "coordinate $value is outside 0-${Grid.MAX_COORDINATE}")
        }
        return value
    }

    private fun String.tokens(): List<String> = trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
}

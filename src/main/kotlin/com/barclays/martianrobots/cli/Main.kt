package com.barclays.martianrobots.cli

import com.barclays.martianrobots.domain.InvalidStartException
import com.barclays.martianrobots.domain.Mission
import com.barclays.martianrobots.domain.RobotResult
import java.io.File
import java.io.IOException
import java.io.Reader
import java.io.Writer
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size > 1) fail("Usage: martian-robots [input-file]  (reads stdin if no file is given)")
    try {
        val input = if (args.isEmpty()) System.`in`.reader() else File(args[0]).reader()
        input.use { runRobots(it, System.out.writer()) }
    } catch (e: InvalidInputException) {
        fail(e.message)
    } catch (e: IOException) {
        fail("Cannot read input: ${e.message}")
    }
}

// Every robot runs before anything is written, so invalid input never produces partial output.
fun runRobots(input: Reader, output: Writer) {
    val parsed = InputParser.parse(input.readLines())
    val results = try {
        Mission(parsed.grid, parsed.robots).run()
    } catch (e: InvalidStartException) {
        throw InvalidInputException(parsed.robotLineNumbers[e.robotIndex], e.message.orEmpty())
    }
    results.forEach { output.write(format(it) + "\n") }
    output.flush()
}

private fun format(result: RobotResult): String {
    val (position, orientation) = result.finalState
    val line = "${position.x} ${position.y} $orientation"
    return if (result.lost) "$line LOST" else line
}

private fun fail(message: String?): Nothing {
    System.err.println(message)
    exitProcess(1)
}

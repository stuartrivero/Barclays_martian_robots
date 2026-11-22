package com.barclays.martianrobots.domain

// A command only says where the robot wants to be next. Robot decides whether
// the grid allows it, so a new command gets the lost, scent and collision rules for free.
interface Command {
    val letter: Char
    fun apply(state: RobotState): RobotState
}

object Left : Command {
    override val letter = 'L'
    override fun apply(state: RobotState) = state.copy(orientation = state.orientation.left())
}

object Right : Command {
    override val letter = 'R'
    override fun apply(state: RobotState) = state.copy(orientation = state.orientation.right())
}

object Forward : Command {
    override val letter = 'F'
    override fun apply(state: RobotState) = state.copy(position = state.positionAhead())
}

object Commands {
    private val byLetter: Map<Char, Command> = listOf(Left, Right, Forward).associateBy { it.letter }

    fun forLetter(letter: Char): Command? = byLetter[letter]
}

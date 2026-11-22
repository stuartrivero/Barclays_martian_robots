# Martian Robots

A small Kotlin command-line program that simulates robots moving around a bounded grid on Mars.
Robots that drive off the edge are lost, and leave a "scent" that stops later robots falling off at the same point.

<!-- TODO: one-line summary of the approach once built, e.g. "Pure domain model + thin CLI; ~N lines of code." -->

## Requirements

- JDK 17 or newer on your `PATH` (or `JAVA_HOME` set) to launch Gradle. If JDK 25 isn't installed, Gradle downloads it for building and testing.
- Java 25 or newer to run the built app outside Gradle.
- Internet access on the first build, so the Gradle wrapper can download Gradle, the project dependencies and JDK 25 if needed.
- Nothing else. The Gradle wrapper is included, so you don't need Gradle installed.

## Build and run

```bash
./gradlew build
```

Run against the sample input from the brief:

```bash
./gradlew run -q --args="sample-input.txt"
```

Or pipe input on stdin:

```bash
./gradlew run -q < sample-input.txt
```

Expected output:

```
1 1 E
3 3 N LOST
2 3 S
```

<!-- TODO: confirm the exact run commands once the CLI exists (file arg vs stdin vs both). -->

## Running the tests

```bash
./gradlew test
```

The tests cover:

- **Each command on its own**: turning left and right through all four orientations, and moving forward in each direction.
- **Lost robots**: driving off each of the four edges, and reporting the last position on the grid with `LOST`.
- **Scent**: a later robot ignores a move off the grid from a scented point, carries on with the rest of its instructions, and can still be lost at a *different*, unscented point.
- **Collisions**: a move into a position held by an earlier robot is ignored, a lost robot doesn't block anything, and a robot starting on an occupied position is rejected.
- **Parsing**: valid input, and each kind of invalid input (see [Error handling](#error-handling)).
- **End to end**: the sample input from the brief produces the sample output.

<!-- TODO: update to match the real test classes. -->

## Design

Hexagonal in spirit: a pure domain core, with parsing and console I/O as a thin adapter around it.

```
src/main/kotlin/com/barclays/martianrobots/
  domain/
    Orientation.kt   N/E/S/W, with left/right turns and the (dx, dy) step for each
    Position.kt      x and y on the grid
    RobotState.kt    a position plus an orientation
    Grid.kt          bounds, scented positions and positions occupied by earlier robots
    Command.kt       the Command interface, and Left / Right / Forward
    Robot.kt         runs one robot's commands against a grid
    Mission.kt       runs robots one after another, carrying the grid between them
  cli/
    InputParser.kt   text -> grid and robot instructions, with validation
    Main.kt          entry point: reads input, prints results
```

- **Domain and I/O are separate.** The `domain` package is plain Kotlin with no I/O and depends on nothing else in the project. Everything that deals with text and the console lives in `cli`, so the core is easy to test directly with plain values.
- **No formal ports.** I didn't add port interfaces (such as an `InstructionSource` or `ResultSink`), because there is only one adapter in each direction and they would each have exactly one implementation. If a second adapter turns up, for example an HTTP API or a different input format, that's the point to introduce them.
- **Streams are passed in.** The program's entry point takes an input `Reader` and an output `Writer`, and `main` passes it stdin and stdout. The end-to-end tests run the sample input from the brief in memory, without touching files or the console.
- **Commands only say where the robot wants to go.** Each command implements a small `Command` interface that turns one `RobotState` into the next, with no knowledge of the grid. `Robot` then applies the grid rules in one place: moving off the grid, scent, and positions occupied by other robots. Commands are looked up by their letter in a single registry. So adding a command (for example `B` for back) means writing one class and registering it, and it follows the lost, scent and collision rules automatically.
- **The mission carries state between robots.** Scent and occupied positions only matter across robots, so `Mission` runs the robots in order and passes the grid from one to the next. The scent rule can be tested directly in the domain, without going through the CLI.
- **Immutable throughout.** `Position`, `RobotState` and `Grid` are data classes, and nothing is changed in place. Running a robot returns its result and a new `Grid` with any new scent and the robot's final position added, and `Mission` folds over the robots with it. Every step is a pure function from input to output, so each rule can be tested on its own, and the order of side effects never becomes part of the behaviour.

## Assumptions and decisions

<!-- TODO: confirm or edit each of these as you implement. -->

1. **Scent belongs to a grid point, whatever the direction.** The brief says a scent is left "at the last grid position" and that a move off the world "from a grid point from which a robot has been previously lost" is ignored. So a robot on a scented point is protected from falling off in *any* direction, not only the direction the earlier robot took. For example, a corner point protects both of its edges.
2. **A lost robot stops.** It runs none of its remaining instructions and reports its last position on the grid followed by `LOST`.
3. **Ignored moves don't end the robot's run.** The robot just carries on with the next instruction.
4. **Grid size.** The input gives the upper-right corner, and the lower-left corner is `0 0`. Both coordinates must be between 0 and 50. A grid of `0 0` (a single point) is allowed.
5. **Starting positions** must be inside the grid. A robot that starts outside it is rejected as invalid input, not reported as `LOST`, because it was never on the grid to be lost from.
6. **Every robot has exactly two lines:** a position line, then an instruction line with at least one command. A position line with no instruction line after it is invalid input.
7. **Whitespace is ignored.** Extra spaces between tokens, spaces at the start or end of a line, and blank lines are all ignored. So a blank line never counts as one of a robot's two lines.
8. **Robots can't move into an occupied position.** Robots run one after another, and each robot that isn't lost stays at its final position. A later robot's move into one of those positions is ignored, and the robot carries on with its next instruction, just like a move blocked by scent. Lost robots have left the grid, so they leave scent but don't occupy a position. A robot whose starting position is already occupied is rejected as invalid input.

## Error handling

The brief gives limits (coordinates up to 50, instructions under 100 characters, the letters `L`, `R` and `F`), so input outside those limits is rejected instead of guessed at:

| Input | Behaviour |
|---|---|
| Malformed grid or position line | Error naming the line number, exit code 1 |
| Coordinate outside 0–50 | Error, exit code 1 |
| Unknown orientation or command letter | Error naming the character and line, exit code 1 |
| Instruction of 100 characters or more | Error, exit code 1 |
| Position line with no instruction line after it | Error naming the position line, exit code 1 |
| Starting position outside the grid | Error naming the line number, exit code 1 |
| Starting position already occupied by an earlier robot | Error naming the line number, exit code 1 |

**All or nothing.** The whole input is parsed and every robot is run before anything is printed. If any of the errors above occurs, the program prints only the error, to stderr, and exits with code 1. It never prints results for some robots and then fails. An occupied starting position can only be detected while the robots run, so `Mission` reports which robot failed, and the CLI turns that into a line number.

## What I'd do with more time

<!-- TODO: fill in honestly at the end. Some ideas: -->

- Property-based tests, for example: a robot that is never lost always stays on the grid, and four `R`s always bring a robot back to its starting orientation.
- A `--trace` option that prints every step, to help debug long instruction strings.
- Package the app as a single runnable jar or native binary, so reviewers don't need Gradle to run it.

## Use of AI

<!-- TODO: write this yourself, honestly. For example: -->

I used Claude Code as a pair programmer: to discuss the design, draft the scaffolding and docs, and suggest edge cases for tests. I made the design decisions above, reviewed every line, and can explain and extend any of it. `AGENTS.md` holds the instructions I gave the agent.

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
- **Parsing**: valid input, and each kind of invalid input (see [Error handling](#error-handling)).
- **End to end**: the sample input from the brief produces the sample output.

<!-- TODO: update to match the real test classes. -->

## Design

<!-- TODO: adjust to match the code as it ends up. Keep this section short. -->

```
src/main/kotlin/
  Orientation.kt   N/E/S/W, with left/right turns and the (dx, dy) step for each
  Position.kt      x, y and orientation
  Grid.kt          bounds plus the set of scented points
  Command.kt       the Command interface, and Left / Right / Forward
  Robot.kt         runs a robot's commands against a grid
  InputParser.kt   text -> grid and robot instructions, with validation
  Main.kt          CLI: reads input, prints results
```

- **Domain and I/O are separate.** The simulation is plain Kotlin with no I/O. Parsing and printing sit around the edge, so the core is easy to test directly.
- **Adding commands.** Each command implements a small `Command` interface. Commands are looked up by their letter in a single registry. Adding a new command (for example `B` for back) means writing one class and registering it. The robot, grid and parser stay as they are.
- **Scent belongs to the grid.** Scent is shared by all robots, so the `Grid` owns it and tracks it across robots run one after another. Robots ask the grid before taking a step that would lose them.
- **Mostly immutable.** <!-- TODO: say whether Position/Robot state is immutable (copy-on-move) and why. -->

## Assumptions and decisions

<!-- TODO: confirm or edit each of these as you implement. -->

1. **Scent belongs to a grid point, whatever the direction.** The brief says a scent is left "at the last grid position" and that a move off the world "from a grid point from which a robot has been previously lost" is ignored. So a robot on a scented point is protected from falling off in *any* direction, not only the direction the earlier robot took. For example, a corner point protects both of its edges.
2. **A lost robot stops.** It runs none of its remaining instructions and reports its last position on the grid followed by `LOST`.
3. **Ignored moves don't end the robot's run.** The robot just carries on with the next instruction.
4. **Grid size.** The input gives the upper-right corner, and the lower-left corner is `0 0`. Both coordinates must be between 0 and 50. A grid of `0 0` (a single point) is allowed.
5. **Starting positions** must be inside the grid. <!-- TODO: decide — reject the input, or report the robot as LOST straight away? -->
6. **Empty instruction lines** are allowed. The robot's final position is its starting position.
7. **Whitespace.** Extra spaces between tokens are allowed, and blank lines between robots are skipped. <!-- TODO: confirm. -->

## Error handling

The brief gives limits (coordinates up to 50, instructions under 100 characters, the letters `L`, `R` and `F`), so input outside those limits is rejected instead of guessed at:

| Input | Behaviour |
|---|---|
| Malformed grid or position line | Error naming the line number, exit code 1 |
| Coordinate outside 0–50 | Error, exit code 1 |
| Unknown orientation or command letter | Error naming the character and line, exit code 1 |
| Instruction of 100 characters or more | Error, exit code 1 |
| Position line with no instruction line after it | Error, exit code 1 |

<!-- TODO: decide whether the whole input is validated before any output (all-or-nothing), or robots are processed until the first bad one. All-or-nothing is the simpler choice to explain. -->

## What I'd do with more time

<!-- TODO: fill in honestly at the end. Some ideas: -->

- Property-based tests, for example: a robot that is never lost always stays on the grid, and four `R`s always bring a robot back to its starting orientation.
- A `--trace` option that prints every step, to help debug long instruction strings.
- Package the app as a single runnable jar or native binary, so reviewers don't need Gradle to run it.

## Use of AI

<!-- TODO: write this yourself, honestly. For example: -->

I used Claude Code as a pair programmer: to discuss the design, draft the scaffolding and docs, and suggest edge cases for tests. I made the design decisions above, reviewed every line, and can explain and extend any of it. `AGENTS.md` holds the instructions I gave the agent.

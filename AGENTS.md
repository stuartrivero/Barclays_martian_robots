# AGENTS.md

Instructions for AI coding agents working in this repository.

## Context

This is a timeboxed tech test, the "Martian Robots" problem. The reviewers are judging judgement, clarity and tests, not
size. **Keep it small.** Don't add frameworks, dependency injection, databases, config files or extra modules. If
something isn't required by the brief, ask before adding it.

## Stack

- Kotlin on the JVM, built with Gradle (Kotlin DSL) and the Gradle wrapper.
- JDK toolchain: 25. It is resolved through the foojay resolver plugin in `settings.gradle.kts`.
- Tests: JUnit 6 with Kotest assertions (`shouldBe`, `shouldThrow` and so on). Don't add other assertion libraries.
- The only libraries are the Kotlin standard library and test dependencies.

## Commands

```bash
./gradlew build        # compile + test
./gradlew test         # tests only
./gradlew run -q --args="sample-input.txt"
```

Run `./gradlew build` before you call any task done. It must pass.

## Code conventions

- Code lives in two packages under `com.barclays.martianrobots`:
    - `domain`: grid, position, robot state, orientation, commands, robot and mission. No I/O, and no imports from
      `cli`.
    - `cli`: `InputParser` and `Main`, the only code that handles text and the console. It depends on `domain`, never
      the other way round.
- Don't add port interfaces or more packages unless a second adapter is actually needed. Ask first.
- The entry point takes an input `Reader` and an output `Writer`. `main` passes stdin and stdout, and end-to-end tests
  pass in-memory streams.
- New robot commands implement the `Command` interface and are registered by their letter. Don't add `when (char)`
  branches anywhere else.
- Prefer immutable data classes and pure functions. Use clear names rather than comments, and only comment on *why*
  something is done, for example the scent rule.
- Throw a clear error with the line number for invalid input. Never silently guess.
- Don't add dependencies without asking.

## Tests

- Every behaviour change comes with a test. Focus on the edge cases: lost robots on each edge, scent ignoring a move,
  scent at corners, moves into positions occupied by other robots (finished or still at their start), and invalid input.
- Name tests after the behaviour, for example `` `robot ignores move off grid from scented point` ``.
- Keep the end-to-end sample test (brief input → brief output) passing.

## Docs

- When behaviour or an assumption changes, update the matching section of `README.md`: Assumptions and decisions, or
  Error handling.
- Don't create extra documentation files.

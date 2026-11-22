# AGENTS.md

Instructions for AI coding agents working in this repository.

## Context

This is a timeboxed tech test, the "Martian Robots" problem. The reviewers are judging judgement, clarity and tests, not size. **Keep it small.** Don't add frameworks, dependency injection, databases, config files or extra modules. If something isn't required by the brief, ask before adding it.

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

- Keep the domain (grid, robot, orientation, commands) free of I/O. Only `InputParser` and `Main` handle text and the console.
- New robot commands implement the `Command` interface and are registered by their letter. Don't add `when (char)` branches anywhere else.
- Prefer immutable data classes and pure functions. Use clear names rather than comments, and only comment on *why* something is done, for example the scent rule.
- Throw a clear error with the line number for invalid input. Never silently guess.
- Don't add dependencies without asking.

## Tests

- Every behaviour change comes with a test. Focus on the edge cases: lost robots on each edge, scent ignoring a move, scent at corners, and invalid input.
- Name tests after the behaviour, for example `` `robot ignores move off grid from scented point` ``.
- Keep the end-to-end sample test (brief input → brief output) passing.

## Docs

- When behaviour or an assumption changes, update the matching section of `README.md`: Assumptions and decisions, or Error handling.
- Don't create extra documentation files.

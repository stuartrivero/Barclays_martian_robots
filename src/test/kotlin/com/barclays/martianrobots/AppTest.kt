package com.barclays.martianrobots

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AppTest {
    @Test
    fun `greets Mars by default`() {
        greeting() shouldBe "Hello, Mars!"
    }

    @Test
    fun `greets the given name`() {
        greeting("Robot") shouldBe "Hello, Robot!"
    }
}

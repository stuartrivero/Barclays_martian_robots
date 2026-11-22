package com.barclays.martianrobots

import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {
    @Test
    fun `greets Mars by default`() {
        assertEquals("Hello, Mars!", greeting())
    }

    @Test
    fun `greets the given name`() {
        assertEquals("Hello, Robot!", greeting("Robot"))
    }
}

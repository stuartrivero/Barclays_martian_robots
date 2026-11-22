package com.barclays.martianrobots.domain

import com.barclays.martianrobots.domain.Orientation.E
import com.barclays.martianrobots.domain.Orientation.N
import com.barclays.martianrobots.domain.Orientation.S
import com.barclays.martianrobots.domain.Orientation.W
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class OrientationTest {

    @Test
    fun `turning left goes anticlockwise through all four orientations`() {
        N.left() shouldBe W
        W.left() shouldBe S
        S.left() shouldBe E
        E.left() shouldBe N
    }

    @Test
    fun `turning right goes clockwise through all four orientations`() {
        N.right() shouldBe E
        E.right() shouldBe S
        S.right() shouldBe W
        W.right() shouldBe N
    }

    @Test
    fun `four turns in the same direction return to the start`() {
        Orientation.entries.forEach { start ->
            start.right().right().right().right() shouldBe start
            start.left().left().left().left() shouldBe start
        }
    }

    @Test
    fun `north increases y and east increases x`() {
        (N.dx to N.dy) shouldBe (0 to 1)
        (E.dx to E.dy) shouldBe (1 to 0)
        (S.dx to S.dy) shouldBe (0 to -1)
        (W.dx to W.dy) shouldBe (-1 to 0)
    }
}

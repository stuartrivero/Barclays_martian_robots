package com.barclays.martianrobots.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GridTest {

    private val grid = Grid(upperRight = Position(5, 3))

    @Test
    fun `contains every point from 0 0 to the upper right corner inclusive`() {
        grid.contains(Position(0, 0)) shouldBe true
        grid.contains(Position(5, 3)) shouldBe true
        grid.contains(Position(5, 0)) shouldBe true
        grid.contains(Position(0, 3)) shouldBe true
    }

    @Test
    fun `does not contain points past any edge`() {
        grid.contains(Position(-1, 0)) shouldBe false
        grid.contains(Position(0, -1)) shouldBe false
        grid.contains(Position(6, 3)) shouldBe false
        grid.contains(Position(5, 4)) shouldBe false
    }

    @Test
    fun `a 0 0 grid is a single point`() {
        val single = Grid(upperRight = Position(0, 0))

        single.contains(Position(0, 0)) shouldBe true
        single.contains(Position(1, 0)) shouldBe false
    }

    @Test
    fun `adding scent returns a new grid and leaves the original unchanged`() {
        val scented = grid.withScent(Position(3, 3))

        scented.isScented(Position(3, 3)) shouldBe true
        grid.isScented(Position(3, 3)) shouldBe false
    }

    @Test
    fun `adding an occupied position returns a new grid and leaves the original unchanged`() {
        val occupied = grid.withOccupied(Position(1, 1))

        occupied.isOccupied(Position(1, 1)) shouldBe true
        grid.isOccupied(Position(1, 1)) shouldBe false
    }

    @Test
    fun `coordinates up to 50 are allowed`() {
        Grid(upperRight = Position(Grid.MAX_COORDINATE, Grid.MAX_COORDINATE))
            .contains(Position(50, 50)) shouldBe true
    }

    @Test
    fun `rejects an upper right corner outside 0 to 50`() {
        shouldThrow<IllegalArgumentException> { Grid(upperRight = Position(51, 3)) }
        shouldThrow<IllegalArgumentException> { Grid(upperRight = Position(5, -1)) }
    }
}

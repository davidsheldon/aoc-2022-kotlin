package aoc2022

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class UtilsKtTest {
    @Test
    fun `check takeWhilePlusOne`() {

        assertEquals((1..5).asSequence().takeWhilePlusOne { it < 4 }.toList(), listOf(1,2,3,4))

    }
}
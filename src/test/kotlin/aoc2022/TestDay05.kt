package aoc2022

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TestDay05 {
    @Test
    fun `test rotateStrings`() {
        val stack = Day05.Stacks(listOf("Abc", "D"))
        println(stack)
        assertThat(parseToStack(stack.toString().split('\n'))).isEqualTo(stack)
    }


}
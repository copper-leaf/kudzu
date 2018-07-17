package com.eden.monadik

import org.junit.jupiter.api.Test
import strikt.api.expect

class TestWord {

    @Test
    fun testWordParser() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = WordParser("kotlin")

        input = "kotlin"
        output = underTest.test(input)
        expected = """
            (WordNode: kotlin)
        """
        expect(output).parsedCorrectly(expected)

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

}

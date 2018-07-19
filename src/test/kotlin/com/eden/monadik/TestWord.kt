package com.eden.monadik

import com.eden.monadik.parser.WordParser
import org.junit.jupiter.api.Test
import strikt.api.expect

class TestWord {

    @Test
    fun testWordParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
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

    @Test
    fun testNamedWordNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = WordParser("kotlin", name = "word")

        input = "kotlin"
        output = underTest.test(input)
        expected = """
            (WordNode:word: kotlin)
        """
        expect(output).parsedCorrectly(expected)

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

}

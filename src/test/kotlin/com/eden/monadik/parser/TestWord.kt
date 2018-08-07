package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.ParserContext
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.parsedIncorrectly
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo

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
            (WordNode: 'kotlin')
        """
        expect(output)
                .parsedCorrectly(expected)
                .node()
                .map { this!!.text }
                .isEqualTo("kotlin")

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testTokenParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = TokenParser()

        input = "kotlin"
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("kotlin")

        input = "java"
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("java")

        input = "12345"
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("12345")

        input = "="
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
            (WordNode:word: 'kotlin')
        """
        expect(output)
                .parsedCorrectly(expected)
                .node()
                .map { this!!.text }
                .isEqualTo("kotlin")

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

}

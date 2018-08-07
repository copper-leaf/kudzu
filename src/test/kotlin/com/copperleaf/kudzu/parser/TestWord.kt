package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("java")
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.text }
                .isEqualTo("12345")
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "="
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
        expect(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }

}

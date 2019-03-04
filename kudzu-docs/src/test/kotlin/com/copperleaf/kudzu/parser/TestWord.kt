package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.checkParsingWhenEmpty
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
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
        expectThat(output)
                .parsedCorrectly(expected)
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("kotlin")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
    }

    @Test
    fun testTokenParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = TokenParser()

        input = "kotlin"
        output = underTest.test(input)
        expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("kotlin")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("java")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "12345"
        output = underTest.test(input)
        expectThat(output)
                .parsedCorrectly()
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("12345")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "="
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        input = ""
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
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
        expectThat(output)
                .parsedCorrectly(expected)
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("kotlin")
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "java"
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()

        underTest.checkParsingWhenEmpty()
    }

}

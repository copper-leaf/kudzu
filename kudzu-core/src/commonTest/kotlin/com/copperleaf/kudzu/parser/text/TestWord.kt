package com.copperleaf.kudzu.parser.text

import com.copperleaf.kudzu.checkParsingWhenEmpty
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.get
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestWord {

    @Test
    fun testWordParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = WordParser("kotlin")

        input = "kotlin"
        output = underTest.test(input)
        expected = """
            (TextNode: 'kotlin')
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
        var output: ParserResult<Node>?
        val underTest = AnyTokenParser()

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
    fun testNamedTextNode() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = WordParser("kotlin")

        input = "kotlin"
        output = underTest.test(input)
        expected = """
            (TextNode: 'kotlin')
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

package com.copperleaf.kudzu.parser.maybe

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import kotlin.test.Test

class TestMaybe {

    @Test
    fun testMaybeOfCharParsers() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = MaybeParser(
            DigitParser()
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (empty)
            )
        """
        expectThat(output)
            .parsedCorrectly(expected, true)
            .node()
            .isNonTerminal()
            .withChildren(0)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    @Test
    fun testNamedMaybeNode() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = MaybeParser(
            DigitParser()
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (empty)
            )
        """
        expectThat(output).parsedCorrectly(expected, true)
    }
}

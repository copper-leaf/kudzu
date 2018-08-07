package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isTrue

class TestMaybe {

    @Test
    fun testMaybeOfCharParsers() {
        var input: String
        var output: Pair<Node, ParserContext>?
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
        expect(output).parsedCorrectly(expected)
                .node()
                .isNonTerminal()
                .withChildren(1)
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (empty)
            )
        """
        expect(output)
                .parsedCorrectly(expected, true)
                .node()
                .isNonTerminal()
                .withChildren(0)
        expect(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testNamedMaybeNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = MaybeParser(
                DigitParser(name = "digit"),
                name = "maybe"
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (MaybeNode:maybe:
              (CharNode:digit: '1')
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode:maybe:
              (empty)
            )
        """
        expect(output).parsedCorrectly(expected, true)
    }

}

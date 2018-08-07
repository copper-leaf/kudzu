package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.ParserContext
import com.eden.monadik.isNonTerminal
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect

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
        expect(output as Pair<Node, ParserContext>?).parsedCorrectly(expected, true)
    }

}

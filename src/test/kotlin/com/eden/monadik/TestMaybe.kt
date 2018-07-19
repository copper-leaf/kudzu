package com.eden.monadik

import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.MaybeParser
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isNotNull
import strikt.assertions.isNull

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
        expect(output!!.first.children).isNotNull().hasSize(1)

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode: (empty))
        """
        expect(output).parsedCorrectly(expected, true)
        expect(output!!.first.children).isNull()
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
            (MaybeNode:maybe: (empty))
        """
        expect(output).parsedCorrectly(expected, true)
    }

}

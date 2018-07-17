package com.eden.monadik

import org.junit.jupiter.api.Test
import strikt.api.expect

class TestMaybe {

    @Test
    fun testMaybeOfCharParsers() {
        var input: String
        var output: Pair<Node, ParsingContext>?
        var expected: String
        val underTest = MaybeParser(
                DigitParser()
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (MaybeNode:
              (CharNode: 1)
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expected = """
            (MaybeNode: (empty))
        """
        expect(output).parsedCorrectly(expected, true)
    }

}

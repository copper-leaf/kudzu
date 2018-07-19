package com.eden.monadik

import com.eden.monadik.parser.ChoiceParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.LetterParser
import com.eden.monadik.parser.ManyParser
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isNotNull

class TestChoice {

    @Test
    fun testChoiceOfCharParsers() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ChoiceParser(
                DigitParser(),
                LetterParser()
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: '1')
            )
        """
        expect(output).parsedCorrectly(expected)
        expect(output!!.first.children).isNotNull().hasSize(1)

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: 'a')
            )
        """
        expect(output).parsedCorrectly(expected)
        expect(output!!.first.children).isNotNull().hasSize(1)

        input = " "
        output = underTest.test(input)
        expect(output).parsedIncorrectly()
    }

    @Test
    fun testManyMultiCharChoice() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ManyParser(
                ChoiceParser(
                        DigitParser(),
                        LetterParser()
                )
        )

        input = "1a2b3c4d"
        output = underTest.test(input)
        expected = """
            (ManyNode:
              (ChoiceNode:
                (CharNode: '1')
              )
              (ChoiceNode:
                (CharNode: 'a')
              )
              (ChoiceNode:
                (CharNode: '2')
              )
              (ChoiceNode:
                (CharNode: 'b')
              )
              (ChoiceNode:
                (CharNode: '3')
              )
              (ChoiceNode:
                (CharNode: 'c')
              )
              (ChoiceNode:
                (CharNode: '4')
              )
              (ChoiceNode:
                (CharNode: 'd')
              )
            )
        """
        expect(output).parsedCorrectly(expected)
    }

    @Test
    fun testNamedChoiceNode() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ChoiceParser(
                DigitParser(name = "digit"),
                LetterParser(name = "letter"),
                name = "choice"
        )

        input = "1"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:choice:
              (CharNode:digit: '1')
            )
        """
        expect(output).parsedCorrectly(expected)

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:choice:
              (CharNode:letter: 'a')
            )
        """
        expect(output).parsedCorrectly(expected)
    }

}

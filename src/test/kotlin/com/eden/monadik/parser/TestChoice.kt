package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.ParserContext
import com.eden.monadik.isNonTerminal
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.parsedIncorrectly
import com.eden.monadik.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect

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
        expect(output)
                .parsedCorrectly(expected)
                .node()
                    .isNonTerminal()
                    .withChildren(1)

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: 'a')
            )
        """
        expect(output)
                .parsedCorrectly(expected)
                .node()
                    .isNonTerminal()
                    .withChildren(1)

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

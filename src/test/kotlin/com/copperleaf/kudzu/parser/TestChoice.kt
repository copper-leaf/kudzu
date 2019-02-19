package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

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
        expectThat(output)
                .parsedCorrectly(expected)
                .node()
                    .isNonTerminal()
                    .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: 'a')
            )
        """
        expectThat(output)
                .parsedCorrectly(expected)
                .node()
                    .isNonTerminal()
                    .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
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
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:choice:
              (CharNode:letter: 'a')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testExactChoiceParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ExactChoiceParser(
            WordParser("aaaa", name = "W1"),
            WordParser("bbbb", name = "W2"),
            WordParser("aabb", name = "W3"),
            WordParser("bbaabb", name = "W4")
        )

        input = "aaaa"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (WordNode:W1: 'aaaa')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "bbbb"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (WordNode:W2: 'bbbb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "aabb"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (WordNode:W3: 'aabb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "bbaabb"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (WordNode:W4: 'bbaabb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }

}

package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.text.WordParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import kotlin.test.Test

class TestChoice {

    @Test
    fun testChoiceOfCharParsers() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = PredictiveChoiceParser(
            DigitParser(),
            LetterParser()
        )

        input = "1"
        output = underTest.test(input, logErrors = true)
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
        output = underTest.test(input, logErrors = true)
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
        output = underTest.test(input, logErrors = true)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }

    @Test
    fun testManyMultiCharChoice() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ManyParser(
            PredictiveChoiceParser(
                DigitParser(),
                LetterParser()
            )
        )

        input = "1a2b3c4d"
        output = underTest.test(input, logErrors = true)
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
        var output: ParserResult<Node>?
        var expected: String
        val underTest = PredictiveChoiceParser(
            DigitParser(),
            LetterParser(),
        )

        input = "1"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "a"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (CharNode: 'a')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testExactChoiceParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ExactChoiceParser(
            WordParser("aaaa"),
            WordParser("bbbb"),
            WordParser("aabb"),
            WordParser("bbaabb")
        )

        input = "aaaa"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (TextNode: 'aaaa')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "bbbb"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (TextNode: 'bbbb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "aabb"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (TextNode: 'aabb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = "bbaabb"
        output = underTest.test(input, logErrors = true)
        expected = """
            (ChoiceNode:
              (TextNode: 'bbaabb')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(1)
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()

        input = " "
        output = underTest.test(input, logErrors = true)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext(input, 0, false))).isFalse()
    }
}
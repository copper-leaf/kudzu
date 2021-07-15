package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.get
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isFalse
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.parser.text.ScanParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
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
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: '1')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "a"
        output = underTest.test(input)
        expected = """
            (ChoiceNode:
              (CharNode: 'a')
            )
        """
        expectThat(output).parsedCorrectly(expected)
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()
    }

    @Test
    fun testExactChoiceParser() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ExactChoiceParser(
            LiteralTokenParser("aaaa"),
            LiteralTokenParser("bbbb"),
            LiteralTokenParser("aabb"),
            LiteralTokenParser("bbaabb")
        )

        input = "aaaa"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "bbbb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "aabb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "bbaabb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    @Test
    fun testPredictiveChoiceWithAmbiguousLiteralTokenParsers() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = PredictiveChoiceParser(
            LiteralTokenParser("aaaa"),
            LiteralTokenParser("bbbb"),
            LiteralTokenParser("aabb"),
            LiteralTokenParser("bbaabb")
        )

        input = "aaaa"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "bbbb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "aabb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = "bbaabb"
        output = underTest.test(input)
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
        expectThat(underTest.predict(ParserContext.fromString(input))).isTrue()

        input = " "
        output = underTest.test(input)
        expectThat(output).parsedIncorrectly()
        expectThat(underTest.predict(ParserContext.fromString(input))).isFalse()
    }

    @Test
    fun testPredictiveChoiceWithSequenceParsersThatStartWithAmbiguousLiteralToken() {
        val makeTagParser: (String) -> Parser<*> = {
            val openTag = LiteralTokenParser("<$it>")
            val closeTag = LiteralTokenParser("</$it>")
            FlatMappedParser(
                SequenceParser(
                    openTag,
                    ScanParser(closeTag),
                    closeTag
                )
            ) { (nodeContext, _, scanned, _) ->
                TextNode(scanned.text, nodeContext)
            }
        }

        val underTest = PredictiveChoiceParser(
            makeTagParser("a"),
            makeTagParser("b"),
            makeTagParser("i"),
            makeTagParser("article"),
            makeTagParser("blockquote"),
        )

        expectThat(underTest.predict(ParserContext.fromString("<a></a>"))).isTrue()
        expectThat(underTest.predict(ParserContext.fromString("<b></b>"))).isTrue()
        expectThat(underTest.predict(ParserContext.fromString("<i></i>"))).isTrue()
        expectThat(underTest.predict(ParserContext.fromString("<article></article>"))).isTrue()
        expectThat(underTest.predict(ParserContext.fromString("<blockquote></blockquote>"))).isTrue()
        expectThat(underTest.predict(ParserContext.fromString("<bold></bold>"))).isFalse()
        expectThat(underTest.predict(ParserContext.fromString("<a></b>"))).isTrue()

        "<a><article>some text</article></a>".run {
            expectThat(underTest.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .get { text }
                .isEqualTo("<article>some text</article>")
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }

        "<a></article>".run {
            expectThat(underTest.test(this))
                .parsedIncorrectly()
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
    }
}

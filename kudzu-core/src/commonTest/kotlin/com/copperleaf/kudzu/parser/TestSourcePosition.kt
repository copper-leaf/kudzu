package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.hasSize
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.chars.WhitespaceCharParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.WordParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import kotlin.test.Test

class TestSourcePosition {

    val testText = """
        |one two
        |
        |one
        |
        |one
        |two
        |two one
        |o ne
        |
        |t w o
        |
        |
    """.trimMargin().trim()

    @Test
    fun testSourcePositionIncludeWhitespace() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            WordParser("one"),
            WhitespaceCharParser(),
            WordParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (TextNode: 'one')
              (CharNode: '
            ')
              (TextNode: 'two')
            )
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNonTerminal()

        expectThat(output!!.first.context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(2:4))")
        expectThat((output.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expectThat((output.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(2:1))")
        expectThat((output.first as NonTerminalNode).children[2].context.toString())
            .isEqualTo("NodeContext(ParserContext(2:1) to ParserContext(2:4))")
    }

    @Test
    fun testSourcePositionSkipWhitespace() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            WordParser("one"),
            WordParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input, true)
        expected = """
            (SequenceNode:
              (TextNode: 'one')
              (TextNode: 'two')
            )
        """
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(2)

        expectThat(output!!.first.context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(2:4))")
        expectThat((output.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expectThat((output.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(2:4))")
    }

    @Test
    fun testSourcePositionFromResourcesFile() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ManyParser(
            PredictiveChoiceParser(
                WordParser("one"),
                WordParser("two")
            )
        )

        input = testText
        output = underTest.test(input, true)
        expected = """
            (ManyNode:
              (ChoiceNode:
                (TextNode: 'one')
              )
              (ChoiceNode:
                (TextNode: 'two')
              )
              (ChoiceNode:
                (TextNode: 'one')
              )
              (ChoiceNode:
                (TextNode: 'one')
              )
              (ChoiceNode:
                (TextNode: 'two')
              )
              (ChoiceNode:
                (TextNode: 'two')
              )
              (ChoiceNode:
                (TextNode: 'one')
              )
              (ChoiceNode:
                (TextNode: 'one')
              )
              (ChoiceNode:
                (TextNode: 'two')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)

        expectThat(output!!.first.context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(10:6))")
        expectThat((output.first as NonTerminalNode).children)
            .hasSize(9)
        expectThat((output.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expectThat((output.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(1:8))")
        expectThat((output.first as NonTerminalNode).children[2].context.toString())
            .isEqualTo("NodeContext(ParserContext(1:8) to ParserContext(3:4))")
        expectThat((output.first as NonTerminalNode).children[3].context.toString())
            .isEqualTo("NodeContext(ParserContext(3:4) to ParserContext(5:4))")
        expectThat((output.first as NonTerminalNode).children[4].context.toString())
            .isEqualTo("NodeContext(ParserContext(5:4) to ParserContext(6:4))")
        expectThat((output.first as NonTerminalNode).children[5].context.toString())
            .isEqualTo("NodeContext(ParserContext(6:4) to ParserContext(7:4))")
        expectThat((output.first as NonTerminalNode).children[6].context.toString())
            .isEqualTo("NodeContext(ParserContext(7:4) to ParserContext(7:8))")
        expectThat((output.first as NonTerminalNode).children[7].context.toString())
            .isEqualTo("NodeContext(ParserContext(7:8) to ParserContext(8:5))")
        expectThat((output.first as NonTerminalNode).children[8].context.toString())
            .isEqualTo("NodeContext(ParserContext(8:5) to ParserContext(10:6))")
    }
}

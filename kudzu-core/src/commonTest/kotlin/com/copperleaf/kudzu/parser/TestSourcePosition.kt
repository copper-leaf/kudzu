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
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.withChildren
import io.kotest.core.spec.style.StringSpec

class TestSourcePosition : StringSpec({

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

    "testSourcePositionIncludeWhitespace" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            LiteralTokenParser("one"),
            WhitespaceCharParser(),
            LiteralTokenParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input)
        expected = """
            (Sequence3Node:
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
            .isEqualTo("NodeContext(1:1 to 2:4)")
        expectThat((output!!.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(1:1 to 1:4)")
        expectThat((output!!.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(1:4 to 2:1)")
        expectThat((output!!.first as NonTerminalNode).children[2].context.toString())
            .isEqualTo("NodeContext(2:1 to 2:4)")
    }

    "testSourcePositionSkipWhitespace" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = SequenceParser(
            LiteralTokenParser("one"),
            LiteralTokenParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input, true)
        expected = """
            (Sequence2Node:
              (TextNode: 'one')
              (TextNode: 'two')
            )
        """
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(2)

        expectThat(output!!.first.context.toString())
            .isEqualTo("NodeContext(1:1 to 2:4)")
        expectThat((output!!.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(1:1 to 1:4)")
        expectThat((output!!.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(1:4 to 2:4)")
    }

    "testSourcePositionFromResourcesFile" {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = ManyParser(
            PredictiveChoiceParser(
                LiteralTokenParser("one"),
                LiteralTokenParser("two")
            )
        )

        input = testText
        output = underTest.test(input, true)
        expected = """
            (ManyNode:
              (Choice2Node.Option1:
                (TextNode: 'one')
              )
              (Choice2Node.Option2:
                (TextNode: 'two')
              )
              (Choice2Node.Option1:
                (TextNode: 'one')
              )
              (Choice2Node.Option1:
                (TextNode: 'one')
              )
              (Choice2Node.Option2:
                (TextNode: 'two')
              )
              (Choice2Node.Option2:
                (TextNode: 'two')
              )
              (Choice2Node.Option1:
                (TextNode: 'one')
              )
              (Choice2Node.Option1:
                (TextNode: 'one')
              )
              (Choice2Node.Option2:
                (TextNode: 'two')
              )
            )
        """
        expectThat(output).parsedCorrectly(expected)

        expectThat(output!!.first.context.toString())
            .isEqualTo("NodeContext(1:1 to 10:6)")
        expectThat((output!!.first as NonTerminalNode).children)
            .hasSize(9)
        expectThat((output!!.first as NonTerminalNode).children[0].context.toString())
            .isEqualTo("NodeContext(1:1 to 1:4)")
        expectThat((output!!.first as NonTerminalNode).children[1].context.toString())
            .isEqualTo("NodeContext(1:4 to 1:8)")
        expectThat((output!!.first as NonTerminalNode).children[2].context.toString())
            .isEqualTo("NodeContext(1:8 to 3:4)")
        expectThat((output!!.first as NonTerminalNode).children[3].context.toString())
            .isEqualTo("NodeContext(3:4 to 5:4)")
        expectThat((output!!.first as NonTerminalNode).children[4].context.toString())
            .isEqualTo("NodeContext(5:4 to 6:4)")
        expectThat((output!!.first as NonTerminalNode).children[5].context.toString())
            .isEqualTo("NodeContext(6:4 to 7:4)")
        expectThat((output!!.first as NonTerminalNode).children[6].context.toString())
            .isEqualTo("NodeContext(7:4 to 7:8)")
        expectThat((output!!.first as NonTerminalNode).children[7].context.toString())
            .isEqualTo("NodeContext(7:8 to 8:5)")
        expectThat((output!!.first as NonTerminalNode).children[8].context.toString())
            .isEqualTo("NodeContext(8:5 to 10:6)")
    }
})

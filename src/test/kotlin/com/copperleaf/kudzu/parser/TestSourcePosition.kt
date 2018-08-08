package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.isNonTerminal
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.hasSize
import strikt.assertions.isEqualTo



class TestSourcePosition {

    @Test
    fun testSourcePositionIncludeWhitespace() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                WordParser("one"),
                WhitespaceParser(),
                WordParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input)
        expected = """
            (SequenceNode:
              (WordNode: 'one')
              (CharNode: '
            ')
              (WordNode: 'two')
            )
        """
        expect(output)
                .parsedCorrectly(expected)
                .node()
                    .isNonTerminal()

        expect(output!!.first.context.toString())              .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(2:4))")
        expect((output.first as NonTerminalNode).children[0].context.toString()).isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expect((output.first as NonTerminalNode).children[1].context.toString()).isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(2:1))")
        expect((output.first as NonTerminalNode).children[2].context.toString()).isEqualTo("NodeContext(ParserContext(2:1) to ParserContext(2:4))")
    }

    @Test
    fun testSourcePositionSkipWhitespace() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = SequenceParser(
                WordParser("one"),
                WordParser("two")
        )

        input = "one\ntwo"
        output = underTest.test(input, true)
        expected = """
            (SequenceNode:
              (WordNode: 'one')
              (WordNode: 'two')
            )
        """
        expect(output).parsedCorrectly(expected)
                .node()
                .isNonTerminal()
                .withChildren(2)

        expect(output!!.first.context.toString())              .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(2:4))")
        expect((output.first as NonTerminalNode).children[0].context.toString()).isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expect((output.first as NonTerminalNode).children[1].context.toString()).isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(2:4))")
    }

    @Test
    fun testSourcePositionFromResourcesFile() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val underTest = ManyParser(
                ChoiceParser(
                        WordParser("one"),
                        WordParser("two")
                )
        )

        input = "" + this::class.java.getResourceAsStream("/test.txt").reader().readText()
        output = underTest.test(input, true)
        expected = """
            (ManyNode:
              (ChoiceNode:
                (WordNode: 'one')
              )
              (ChoiceNode:
                (WordNode: 'two')
              )
              (ChoiceNode:
                (WordNode: 'one')
              )
              (ChoiceNode:
                (WordNode: 'one')
              )
              (ChoiceNode:
                (WordNode: 'two')
              )
              (ChoiceNode:
                (WordNode: 'two')
              )
              (ChoiceNode:
                (WordNode: 'one')
              )
              (ChoiceNode:
                (WordNode: 'one')
              )
              (ChoiceNode:
                (WordNode: 'two')
              )
            )
        """
        expect(output).parsedCorrectly(expected)

        expect(output!!.first.context.toString())              .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(10:6))")
        expect((output.first as NonTerminalNode).children).hasSize(9)
        expect((output.first as NonTerminalNode).children[0].context.toString()).isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(1:4))")
        expect((output.first as NonTerminalNode).children[1].context.toString()).isEqualTo("NodeContext(ParserContext(1:4) to ParserContext(1:8))")
        expect((output.first as NonTerminalNode).children[2].context.toString()).isEqualTo("NodeContext(ParserContext(1:8) to ParserContext(3:4))")
        expect((output.first as NonTerminalNode).children[3].context.toString()).isEqualTo("NodeContext(ParserContext(3:4) to ParserContext(5:4))")
        expect((output.first as NonTerminalNode).children[4].context.toString()).isEqualTo("NodeContext(ParserContext(5:4) to ParserContext(6:4))")
        expect((output.first as NonTerminalNode).children[5].context.toString()).isEqualTo("NodeContext(ParserContext(6:4) to ParserContext(7:4))")
        expect((output.first as NonTerminalNode).children[6].context.toString()).isEqualTo("NodeContext(ParserContext(7:4) to ParserContext(7:8))")
        expect((output.first as NonTerminalNode).children[7].context.toString()).isEqualTo("NodeContext(ParserContext(7:8) to ParserContext(8:5))")
        expect((output.first as NonTerminalNode).children[8].context.toString()).isEqualTo("NodeContext(ParserContext(8:5) to ParserContext(10:6))")
    }

}
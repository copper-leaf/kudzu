package com.eden.monadik.parser

import com.eden.monadik.Node
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.ParserContext
import com.eden.monadik.isNonTerminal
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect
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
              (WordNode: one)
              (CharNode: '
            ')
              (WordNode: two)
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
              (WordNode: one)
              (WordNode: two)
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
                (WordNode: one)
              )
              (ChoiceNode:
                (WordNode: two)
              )
              (ChoiceNode:
                (WordNode: one)
              )
              (ChoiceNode:
                (WordNode: one)
              )
              (ChoiceNode:
                (WordNode: two)
              )
              (ChoiceNode:
                (WordNode: two)
              )
              (ChoiceNode:
                (WordNode: one)
              )
              (ChoiceNode:
                (WordNode: one)
              )
              (ChoiceNode:
                (WordNode: two)
              )
            )
        """
        expect(output).parsedCorrectly(expected)

        expect(output!!.first.context.toString())              .isEqualTo("NodeContext(ParserContext(1:1) to ParserContext(10:6))")
//        expect(output!!.first.children!![0].context.toString()).isEqualTo("NodeContext(ParserContext(0:0) to ParserContext(0:3))")
//        expect(output!!.first.children!![1].context.toString()).isEqualTo("NodeContext(ParserContext(0:3) to ParserContext(1:3))")
    }

}

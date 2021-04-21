package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.containsExactly
import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.chars.LetterParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.RequiredWhitespaceParser
import com.copperleaf.kudzu.test
import com.copperleaf.kudzu.visit
import kotlin.test.Test

@ExperimentalStdlibApi
class TestVisitor {

    data class VisitedNode(
        val name: String,
        val context: NodeContext,
    ) {
        override fun toString(): String {
            return "($name: ${context.startPosition} to ${context.endPosition})"
        }
    }

    @Test
    fun testVisitorOnEnter() {
        val underTest = SequenceParser(
            MaybeParser(
                ManyParser(LetterParser()),
            ),
            RequiredWhitespaceParser(),
            ManyParser(DigitParser())
        )

        val result = underTest.test(
            """
            |asdf
            | 123
            """.trimMargin().trim()
        )

        expectThat(result)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .also {
                val nodesInVisitOrder = buildList<VisitedNode> {
                    it.visit {
                        add(
                            VisitedNode(
                                it.astNodeName,
                                it.context,
                            )
                        )
                    }
                }

                expectThat(nodesInVisitOrder)
                    .map { it.toString() }
                    .also { println(it.joinToString(separator = "\n")) }
                    .containsExactly(
                        "(SequenceNode: 1:1 to 2:5)",
                            "(MaybeNode: 1:1 to 1:5)",
                                "(ManyNode: 1:1 to 1:5)",
                                    "(CharNode: 1:1 to 1:2)",
                                    "(CharNode: 1:2 to 1:3)",
                                    "(CharNode: 1:3 to 1:4)",
                                    "(CharNode: 1:4 to 1:5)",
                        "(TextNode: 1:5 to 2:2)",
                        "(ManyNode: 2:2 to 2:5)",
                            "(CharNode: 2:2 to 2:3)",
                            "(CharNode: 2:3 to 2:4)",
                            "(CharNode: 2:4 to 2:5)",
                    )

            }
    }

    @Test
    fun testVisitorOnExit() {
        val underTest = SequenceParser(
            MaybeParser(
                ManyParser(LetterParser()),
            ),
            RequiredWhitespaceParser(),
            ManyParser(DigitParser())
        )

        val result = underTest.test(
            """
            |asdf
            | 123
            """.trimMargin().trim()
        )

        expectThat(result)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .also {
                val nodesInVisitOrder = buildList<VisitedNode> {
                    it.visit(reversed = true) {
                        add(
                            VisitedNode(
                                it.astNodeName,
                                it.context,
                            )
                        )
                    }
                }

                expectThat(nodesInVisitOrder)
                    .map { it.toString() }
                    .also { println(it.joinToString(separator = "\n")) }
                    .containsExactly(
                                    "(CharNode: 1:1 to 1:2)",
                                    "(CharNode: 1:2 to 1:3)",
                                    "(CharNode: 1:3 to 1:4)",
                                    "(CharNode: 1:4 to 1:5)",
                                "(ManyNode: 1:1 to 1:5)",
                            "(MaybeNode: 1:1 to 1:5)",
                            "(TextNode: 1:5 to 2:2)",
                                "(CharNode: 2:2 to 2:3)",
                                "(CharNode: 2:3 to 2:4)",
                                "(CharNode: 2:4 to 2:5)",
                            "(ManyNode: 2:2 to 2:5)",
                        "(SequenceNode: 1:1 to 2:5)",
                    )

            }
    }
}

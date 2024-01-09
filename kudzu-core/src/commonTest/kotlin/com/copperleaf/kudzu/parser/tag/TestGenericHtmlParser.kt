package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.node.tag.TagNameNode
import com.copperleaf.kudzu.node.tag.TagNode
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.many.SeparatedByParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.predict.PredictionParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.parser.text.RequiredWhitespaceParser
import com.copperleaf.kudzu.parser.value.AnyLiteralParser
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec

@Suppress("UNCHECKED_CAST")
class TestGenericHtmlParser : StringSpec({

    val htmlAttr: Parser<ValueNode<Pair<String, Any>>> = MappedParser(
        SequenceParser(
            AnyTokenParser(),
            CharInParser('='),
            AnyLiteralParser()
        )
    ) { (_, key, _, value) ->
        key.text to value.value
    }

    "testHtmlAttr" {
        "one=\"two\"".run {
            expectThat(htmlAttr.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttr.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("one" to "two")
                }
        }
        "three=4".run {
            expectThat(htmlAttr.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttr.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("three" to 4)
                }
        }
        "five=6.7".run {
            expectThat(htmlAttr.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttr.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("five" to 6.7)
                }
        }
        "eight=true".run {
            expectThat(htmlAttr.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttr.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("eight" to true)
                }
        }
    }

    val htmlAttrList: Parser<ValueNode<Map<String, Any>>> = MappedParser(
        SeparatedByParser(
            htmlAttr,
            RequiredWhitespaceParser()
        )
    ) {
        it.nodeList.map { it.value }.toMap()
    }

    "testHtmlAttrList" {
        "one=\"two\" three=4".run {
            expectThat(htmlAttrList.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttrList.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(
                        mapOf(
                            "one" to "two",
                            "three" to 4
                        )
                    )
                }
        }

        "five=6.7 eight=true".run {
            expectThat(htmlAttrList.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttrList.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(
                        mapOf(
                            "five" to 6.7,
                            "eight" to true
                        )
                    )
                }
        }
        "one=\"two\" three=4 five=6.7 eight=true".run {
            expectThat(htmlAttrList.predict(ParserContext.fromString(this))).isTrue()
            expectThat(htmlAttrList.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(
                        mapOf(
                            "one" to "two",
                            "three" to 4,
                            "five" to 6.7,
                            "eight" to true
                        )
                    )
                }
        }
    }

    val openTag: Parser<TagNameNode<ValueNode<Map<String, Any>>>> = FlatMappedParser(
        SequenceParser(
            PredictionParser(
                SequenceParser(
                    CharInParser('<'),
                    AnyTokenParser(),
                )
            ),
            RequiredWhitespaceParser(),
            htmlAttrList,
            CharInParser('>'),
        )
    ) { (nodeContext, tagNameSequence, _, attrMap) ->
        val (_, _, tagName) = tagNameSequence
        val contentNode = ValueNode(attrMap.value, nodeContext)

        TagNameNode(tagName.text, contentNode, nodeContext)
    }

    "testHtmlTagOpen" {
        "<a one=\"two\" three=4 five=6.7 eight=true>".run {
            expectThat(openTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(openTag.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    tagName.isEqualTo("a")
                    wrapped.value.isEqualTo(
                        mapOf(
                            "one" to "two",
                            "three" to 4,
                            "five" to 6.7,
                            "eight" to true
                        )
                    )
                }
        }
        "<a one=\"two\" three=4 five=6.7 eight=true>".run {
            expectThat(openTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(openTag.test(this, logErrors = true))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    tagName.isEqualTo("a")
                    wrapped.value.isEqualTo(
                        mapOf(
                            "one" to "two",
                            "three" to 4,
                            "five" to 6.7,
                            "eight" to true
                        )
                    )
                }
        }
    }

    val closeTag: Parser<TagNameNode<Node>> = FlatMappedParser(
        SequenceParser(
            LiteralTokenParser("</"),
            AnyTokenParser(),
            CharInParser('>'),
        )
    ) {
        val (nodeContext, _, tagName, _) = it

        TagNameNode(tagName.text, it, nodeContext)
    }

    "testHtmlTagClose" {
        "</a>".run {
            expectThat(closeTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(closeTag.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    tagName.isEqualTo("a")
                }
        }
        "</blockquote>".run {
            expectThat(closeTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(closeTag.test(this, logErrors = true))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    tagName.isEqualTo("blockquote")
                }
        }
    }

    val tagParser = TagParser(
        listOf(
            TagBuilder(
                "html",
                openTag,
                closeTag
            )
        ),
        allowSameTagRecursion = true
    )

    "testHtmlTagParser" {
        (
                "before tag " +
                        "<a one=\"two\" three=4 five=6.7 eight=true>" +
                        "hello world" +
                        "</a> " +
                        "after tag"
                ).run {
                expectThat(tagParser.predict(ParserContext.fromString(this))).isTrue()
                expectThat(tagParser.test(this, logErrors = true))
                    .parsedCorrectly()
            }
        (
                "before tag " +
                        "<a one=\"two\" three=4 five=6.7 eight=true>" +
                        "goodbye" +
                        " <b one=2>cruel</b>" +
                        " world" +
                        "</a> " +
                        "after tag"
                ).run {
                expectThat(tagParser.predict(ParserContext.fromString(this))).isTrue()
                expectThat(tagParser.test(this))
                    .parsedCorrectly(
                        """
                    |(ManyNode:
                    |  (TextNode: 'before tag ')
                    |  (TagNode:
                    |    (TagNameNode:
                    |      (ValueNode: '{one=two, three=4, five=6.7, eight=true}')
                    |    )
                    |    (ManyNode:
                    |      (TextNode: 'goodbye ')
                    |      (TagNode:
                    |        (TagNameNode:
                    |          (ValueNode: '{one=2}')
                    |        )
                    |        (ManyNode:
                    |          (TextNode: 'cruel')
                    |        )
                    |        (TagNameNode:
                    |          (Sequence3Node:
                    |            (TextNode: '</')
                    |            (TextNode: 'b')
                    |            (CharNode: '>')
                    |          )
                    |        )
                    |      )
                    |      (TextNode: ' world')
                    |    )
                    |    (TagNameNode:
                    |      (Sequence3Node:
                    |        (TextNode: '</')
                    |        (TextNode: 'a')
                    |        (CharNode: '>')
                    |      )
                    |    )
                    |  )
                    |  (TextNode: ' after tag')
                    |)
                    """.trimMargin()
                    )
                    .node()
                    .isNotNull()
                    .apply {
                        (this.nodeList[0] as? TextNode)
                            .isNotNull()
                            .text
                            .isEqualTo("before tag ")

                        (this.nodeList[1] as? TagNode<*, *, *>)
                            .isNotNull()
                            .also { tagNode ->
                                (tagNode.opening as? TagNameNode<ValueNode<Map<String, Any>>>)
                                    .isNotNull()
                                    .apply {
                                        tagName.isEqualTo("a")
                                        wrapped.value.isEqualTo(
                                            mapOf(
                                                "one" to "two",
                                                "three" to 4,
                                                "five" to 6.7,
                                                "eight" to true
                                            )
                                        )
                                    }
                                (tagNode.content as? ManyNode<Node>)
                                    .isNotNull()
                                    .nodeList
                                    .also { contentNodes ->
                                        (contentNodes[0] as? TextNode)
                                            .isNotNull()
                                            .text
                                            .isEqualTo("goodbye ")
                                        (contentNodes[1] as? TagNode<*, *, *>)
                                            .isNotNull()
                                            .also { innerTag ->
                                                (innerTag.opening as? TagNameNode<ValueNode<Map<String, Any>>>)
                                                    .isNotNull()
                                                    .apply {
                                                        tagName.isEqualTo("b")
                                                        wrapped.value.isEqualTo(mapOf("one" to 2))
                                                    }
                                            }
                                        (contentNodes[2] as? TextNode)
                                            .isNotNull()
                                            .text
                                            .isEqualTo(" world")
                                    }
                            }

                        (this.nodeList[2] as? TextNode)
                            .isNotNull()
                            .text
                            .isEqualTo(" after tag")
                    }
            }
    }

    "testMismatchedOpenAndCloseTags" {
        (
                "before tag " +
                        "<a one=\"two\" three=4 five=6.7 eight=true>" +
                        "hello world" +
                        "</b> " +
                        "after tag"
                ).run {
                expectThat(tagParser.predict(ParserContext.fromString(this))).isTrue()
                expectThat(
                    tagParser.test(
                        this,
                        logErrors = false,
                        expectedErrorMessage = """
                    |Parse error at 1:12 (SimpleTagParser)
                    |
                    |Mismatched closing tag: Expected tag name to be 'a', got 'b'
                    |
                    |1|before tag <a one="two" three=4 five=6.7 eight=true>hello world</b> after tag
                    |>>>>>>>>>>>>>^
                    """.trimMargin()
                    )
                ).parsedIncorrectly()
            }
    }
})

package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.tag.TagNameNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

class TestMarkdownWithSpecificHtmlTagsParser {

    private fun <T : Node> Parser<T>.asTagNameParser(name: String): Parser<TagNameNode<T>> {
        return FlatMappedParser(this) {
            TagNameNode(name, it, it.context)
        }
    }

    @Test
    fun testFullMiniMarkdownTagParser() {
        val underTest = TagParser(
            listOf(
                TagBuilder(
                    "bold",
                    LiteralTokenParser("**").asTagNameParser("**"),
                    LiteralTokenParser("**").asTagNameParser("**"),
                ),
                TagBuilder(
                    "italic",
                    LiteralTokenParser("_").asTagNameParser("_"),
                    LiteralTokenParser("_").asTagNameParser("_"),
                ),
                TagBuilder(
                    "inline code",
                    LiteralTokenParser("`").asTagNameParser("`"),
                    LiteralTokenParser("`").asTagNameParser("`"),
                ),
                TagBuilder(
                    "strikethrough",
                    LiteralTokenParser("~").asTagNameParser("~"),
                    LiteralTokenParser("~").asTagNameParser("~"),
                ),
                TagBuilder(
                    "anchor",
                    LiteralTokenParser("<a>").asTagNameParser("a"),
                    LiteralTokenParser("</a>").asTagNameParser("a"),
                ),
                TagBuilder(
                    "bold",
                    LiteralTokenParser("<b>").asTagNameParser("b"),
                    LiteralTokenParser("</b>").asTagNameParser("b"),
                ),
                TagBuilder(
                    "italics",
                    LiteralTokenParser("<i>").asTagNameParser("i"),
                    LiteralTokenParser("</i>").asTagNameParser("i"),
                ),
                TagBuilder(
                    "article",
                    LiteralTokenParser("<article>").asTagNameParser("article"),
                    LiteralTokenParser("</article>").asTagNameParser("article"),
                ),
                TagBuilder(
                    "blockquote",
                    LiteralTokenParser("<blockquote>").asTagNameParser("blockquote"),
                    LiteralTokenParser("</blockquote>").asTagNameParser("blockquote"),
                )
            )
        )

        "**one**".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "_one_".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "~one~".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "`one`".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "<a>one</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "<a> one</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "<a>one </a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "<a>one two, three, blah</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        "<a>one two <a>three</a></a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
        ("one<a>two **three** _fo`u`r_</a> ~five~").run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true))
                .parsedCorrectly(
                    """
                    |(ManyNode:
                    |  (TextNode: 'one')
                    |  (TagNode:
                    |    (TagNameNode:
                    |      (TextNode: '<a>')
                    |    )
                    |    (ManyNode:
                    |      (TextNode: 'two ')
                    |      (TagNode:
                    |        (TagNameNode:
                    |          (TextNode: '**')
                    |        )
                    |        (ManyNode:
                    |          (TextNode: 'three')
                    |        )
                    |        (TagNameNode:
                    |          (TextNode: '**')
                    |        )
                    |      )
                    |      (TextNode: ' ')
                    |      (TagNode:
                    |        (TagNameNode:
                    |          (TextNode: '_')
                    |        )
                    |        (ManyNode:
                    |          (TextNode: 'fo')
                    |          (TagNode:
                    |            (TagNameNode:
                    |              (TextNode: '`')
                    |            )
                    |            (ManyNode:
                    |              (TextNode: 'u')
                    |            )
                    |            (TagNameNode:
                    |              (TextNode: '`')
                    |            )
                    |          )
                    |          (TextNode: 'r')
                    |        )
                    |        (TagNameNode:
                    |          (TextNode: '_')
                    |        )
                    |      )
                    |    )
                    |    (TagNameNode:
                    |      (TextNode: '</a>')
                    |    )
                    |  )
                    |  (TextNode: ' ')
                    |  (TagNode:
                    |    (TagNameNode:
                    |      (TextNode: '~')
                    |    )
                    |    (ManyNode:
                    |      (TextNode: 'five')
                    |    )
                    |    (TagNameNode:
                    |      (TextNode: '~')
                    |    )
                    |  )
                    |)
                """.trimMargin()
                )
                .node()
        }
    }
}

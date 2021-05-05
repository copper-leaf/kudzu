package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

@Suppress("UNCHECKED_CAST")
@ExperimentalStdlibApi
class TestMarkdownWithSpecificHtmlTagsParser {

    @Test
    fun testFullMiniMarkdownTagParser() {
        val underTest = TagParser(
            listOf(
                TagBuilder(
                    "bold",
                    LiteralTokenParser("**"),
                    LiteralTokenParser("**"),
                ),
                TagBuilder(
                    "italic",
                    LiteralTokenParser("_"),
                    LiteralTokenParser("_"),
                ),
                TagBuilder(
                    "inline code",
                    LiteralTokenParser("`"),
                    LiteralTokenParser("`"),
                ),
                TagBuilder(
                    "strikethrough",
                    LiteralTokenParser("~"),
                    LiteralTokenParser("~"),
                ),
                TagBuilder(
                    "anchor",
                    LiteralTokenParser("<a>"),
                    LiteralTokenParser("</a>"),
                ),
                TagBuilder(
                    "bold",
                    LiteralTokenParser("<b>"),
                    LiteralTokenParser("</b>"),
                ),
                TagBuilder(
                    "italics",
                    LiteralTokenParser("<i>"),
                    LiteralTokenParser("</i>"),
                ),
                TagBuilder(
                    "article",
                    LiteralTokenParser("<article>"),
                    LiteralTokenParser("</article>"),
                ),
                TagBuilder(
                    "blockquote",
                    LiteralTokenParser("<blockquote>"),
                    LiteralTokenParser("</blockquote>"),
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
                    """|
                    |(ManyNode:
                    |  (TextNode: 'one')
                    |  (TagNode:
                    |    (TextNode: '<a>')
                    |    (ManyNode:
                    |      (TextNode: 'two ')
                    |      (TagNode:
                    |        (TextNode: '**')
                    |        (ManyNode:
                    |          (TextNode: 'three')
                    |        )
                    |      )
                    |      (TextNode: ' ')
                    |      (TagNode:
                    |        (TextNode: '_')
                    |        (ManyNode:
                    |          (TextNode: 'fo')
                    |          (TagNode:
                    |            (TextNode: '`')
                    |            (ManyNode:
                    |              (TextNode: 'u')
                    |            )
                    |          )
                    |          (TextNode: 'r')
                    |        )
                    |      )
                    |    )
                    |  )
                    |  (TextNode: ' ')
                    |  (TagNode:
                    |    (TextNode: '~')
                    |    (ManyNode:
                    |      (TextNode: 'five')
                    |    )
                    |  )
                    |)
                """.trimMargin()
                )
                .node()
        }
    }
}

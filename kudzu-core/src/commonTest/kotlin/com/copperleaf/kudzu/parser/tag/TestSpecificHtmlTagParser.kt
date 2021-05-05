package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

@Suppress("UNCHECKED_CAST")
@ExperimentalStdlibApi
class TestSpecificHtmlTagParser {

    val tags = listOf(
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

    @Test
    fun testTagParserPredict() {
        val underTest = TagParser(
            tags
        )

        "<a>one</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
        "<b>one</b>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
        "<i>one</i>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
        "<article>one</article>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
        "<blockquote>one</blockquote>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
        "this is text before the quote<blockquote>one</blockquote>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
    }

    @Test
    fun testTagParserParse() {
        val underTest = TagParser(
            tags
        )

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
        ("one<a>two<b>three</b>four</a>five").run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
        }
    }
}

package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.tag.TagNameNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.test
import io.kotest.core.spec.style.StringSpec

class TestSpecificHtmlTagParser : StringSpec({

    val tags = listOf(
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

    "testTagParserPredict" {
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

    "testTagParserParse" {
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
})

fun <T : Node> Parser<T>.asTagNameParser(name: String): Parser<TagNameNode<T>> {
    return FlatMappedParser(this) {
        TagNameNode(name, it, it.context)
    }
}

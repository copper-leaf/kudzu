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
import kotlin.test.Test

@ExperimentalStdlibApi
class TestMiniMarkdownParser {

    private fun <T : Node> Parser<T>.asTagNameParser(name: String): Parser<TagNameNode<T>> {
        return FlatMappedParser(this) {
            TagNameNode(name, it, it.context)
        }
    }

    @Test
    fun testSimpleMarkdownTagParser() {
        val underTest = TagParser(
            listOf(
                TagBuilder(
                    "**",
                    LiteralTokenParser("**").asTagNameParser("**"),
                    LiteralTokenParser("**").asTagNameParser("**"),
                )
            )
        )

        "**one**".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this, logErrors = true)).parsedCorrectly()
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
    }
}

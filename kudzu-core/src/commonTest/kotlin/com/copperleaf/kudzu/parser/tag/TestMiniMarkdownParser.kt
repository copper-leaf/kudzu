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
class TestMiniMarkdownParser {

    @Test
    fun testSimgpleMarkdownTagParser() {
        val underTest = TagParser(
            listOf(
                TagBuilder(
                    "**",
                    LiteralTokenParser("**"),
                    LiteralTokenParser("**"),
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

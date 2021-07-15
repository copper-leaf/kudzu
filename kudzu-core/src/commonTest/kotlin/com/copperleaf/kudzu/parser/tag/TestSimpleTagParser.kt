package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.node.maybe.MaybeNode
import com.copperleaf.kudzu.node.tag.TagNameNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parsedIncorrectly
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.many.SeparatedByParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.mapped.MappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.parser.text.LiteralTokenParser
import com.copperleaf.kudzu.parser.text.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.text.RequiredWhitespaceParser
import com.copperleaf.kudzu.parser.text.ScanParser
import com.copperleaf.kudzu.parser.value.AnyLiteralParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

@Suppress("UNCHECKED_CAST")
@ExperimentalStdlibApi
class TestSimpleTagParser {

    private fun <T : Node> Parser<T>.asTagNameParser(name: String): Parser<TagNameNode<T>> {
        return FlatMappedParser(this) {
            TagNameNode(name, it, it.context)
        }
    }

    @Test
    fun testTrivialTag() {
        val underTest = SimpleTagParser(
            "anchor",
            LiteralTokenParser("<a>").asTagNameParser("a"),
            AnyTokenParser(),
            LiteralTokenParser("</a>").asTagNameParser("a")
        )

        "<a>one</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    opening.text.isEqualTo("<a>")
                    content.text.isEqualTo("one")
                }
        }

        "<a>two</a>".run {
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
            expectThat(underTest.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    opening.text.isEqualTo("<a>")
                    content.text.isEqualTo("two")
                }
        }

        "<a>two".run {
            expectThat(underTest.test(this))
                .parsedIncorrectly()
            expectThat(underTest.predict(ParserContext.fromString(this))).isTrue()
        }
    }

// Tag parser
// ---------------------------------------------------------------------------------------------------------------------

    private val keyValueParser: Parser<ValueNode<Pair<String, Any>>> = MappedParser(
        SequenceParser(
            AnyTokenParser(),
            CharInParser('='),
            AnyLiteralParser()
        )
    ) {
        val (key, _, value) = it.children
        key.text to (value as ValueNode<Any>).value
    }

    private val paramsListParser: Parser<ValueNode<Map<String, Any>>> = MappedParser(
        SequenceParser(
            RequiredWhitespaceParser(),
            SeparatedByParser(
                term = keyValueParser,
                separator = RequiredWhitespaceParser()
            ),
            OptionalWhitespaceParser()
        )
    ) {
        val (_, manyPairsNode, _) = it.children

        val manyPairs: List<Pair<String, Any>> = (manyPairsNode as ManyNode<ValueNode<Pair<String, Any>>>)
            .nodeList
            .map { it.value }

        manyPairs.toMap()
    }

    private val openingTag: Parser<ValueNode<Map<String, Any>>> = MappedParser(
        SequenceParser(
            LiteralTokenParser("<a"),
            MaybeParser(
                paramsListParser
            ),
            CharInParser('>'),
        )
    ) { seq ->
        val (_, maybeNode, _) = seq.children

        (maybeNode as MaybeNode<ValueNode<Map<String, Any>>>).node?.value ?: emptyMap()
    }

    private val nonTrivialTagParser = SimpleTagParser(
        "anchor",
        openingTag.asTagNameParser("a"),
        ScanParser(LiteralTokenParser("</a>")),
        LiteralTokenParser("</a>").asTagNameParser("a")
    )

    @Test
    fun testKeyValueParser() {
        "one=\"two\"".run {
            expectThat(keyValueParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(keyValueParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("one" to "two")
                }
        }
        "three=4".run {
            expectThat(keyValueParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(keyValueParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo("three" to 4)
                }
        }
    }

    @Test
    fun testParamsListParser() {
        " one=\"two\"".run {
            expectThat(paramsListParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(paramsListParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(mapOf("one" to "two"))
                }
        }
        " three=4".run {
            expectThat(paramsListParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(paramsListParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(mapOf("three" to 4))
                }
        }

        " one=\"two\" three=4".run {
            expectThat(paramsListParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(paramsListParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(mapOf("one" to "two", "three" to 4))
                }
        }
    }

    @Test
    fun testOpeningTag() {
        "<a>".run {
            expectThat(openingTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(openingTag.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(emptyMap<String, Any>())
                }
        }

        "<a one=\"two\">".run {
            expectThat(openingTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(openingTag.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(mapOf("one" to "two"))
                }
        }

        "<a one=\"two\" three=4>".run {
            expectThat(openingTag.predict(ParserContext.fromString(this))).isTrue()
            expectThat(openingTag.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    value.isEqualTo(mapOf<String, Any>("one" to "two", "three" to 4))
                }
        }
    }

    @Test
    fun testFullSimpleTag() {
        "<a> </a>".run {
            expectThat(nonTrivialTagParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(nonTrivialTagParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    opening.wrapped.value.isEqualTo(emptyMap<String, Any>())
                    content.text.isEqualTo(" ")
                }
        }

        "<a></a>".run {
            expectThat(nonTrivialTagParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(nonTrivialTagParser.test(this))
                .parsedIncorrectly()
        }

        "<a one=\"two\" three=4>This is the text that will run all the way to the end of the tag</a>".run {
            expectThat(nonTrivialTagParser.predict(ParserContext.fromString(this))).isTrue()
            expectThat(nonTrivialTagParser.test(this))
                .parsedCorrectly()
                .node()
                .isNotNull()
                .apply {
                    opening.wrapped.value.isEqualTo(mapOf<String, Any>("one" to "two", "three" to 4))
                    content.text.isEqualTo("This is the text that will run all the way to the end of the tag")
                }
        }
    }
}

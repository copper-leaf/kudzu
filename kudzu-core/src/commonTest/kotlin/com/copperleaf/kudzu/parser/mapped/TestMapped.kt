package com.copperleaf.kudzu.parser.mapped

import com.copperleaf.kudzu.expectThat
import com.copperleaf.kudzu.isA
import com.copperleaf.kudzu.isEqualTo
import com.copperleaf.kudzu.isNotNull
import com.copperleaf.kudzu.isTrue
import com.copperleaf.kudzu.node
import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.ChoiceNode
import com.copperleaf.kudzu.node.mapped.ValueNode
import com.copperleaf.kudzu.node.text.TextNode
import com.copperleaf.kudzu.parsedCorrectly
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.parser.chars.CharInParser
import com.copperleaf.kudzu.parser.chars.DigitParser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser
import com.copperleaf.kudzu.parser.text.AnyTokenParser
import com.copperleaf.kudzu.parser.text.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.text.WordParser
import com.copperleaf.kudzu.test
import kotlin.test.Test

@OptIn(ExperimentalStdlibApi::class)
class TestMapped {

    @Test
    fun testMappedParser1() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = MappedParser(
            DigitParser(),
        ) { it.text.toInt() }

        input = "3"
        output = underTest.test(input)
        expected = """
            (ValueNode: '3')
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNotNull()
            .also {
                it.text.isEqualTo("3")
                it.isA<ValueNode<Int>>().value.isEqualTo(3)
            }
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    // asdf, 123, (Type1-A)
    data class SomeComplexModel(
        val node1Value: String,
        val node2Value: Int,
        val node3Value: SomeComplexModelType,
    )

    // A
    // B
    enum class SomeComplexModelEnum {
        A, B
    }

    // (Type1-A)
    // (Type1-B)
    // (Type2-A)
    // (Type2-B)
    sealed class SomeComplexModelType {
        data class Type1(
            val enumValue: SomeComplexModelEnum
        ) : SomeComplexModelType()

        data class Type2(
            val enumValue: SomeComplexModelEnum
        ) : SomeComplexModelType()
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testMappedParser2() {
        val nodeValue1Parser = MappedParser(
            AnyTokenParser()
        ) { tokenNode ->
            tokenNode.text.toUpperCase()
        }
        val nodeValue2Parser = MappedParser(
            ManyParser(DigitParser())
        ) { manyNode ->
            manyNode
                .children
                .joinToString(separator = "") { digitNode -> digitNode.text }
                .toInt()
        }
        val someComplexModelEnumParser = MappedParser(
            CharInParser('A', 'B')
        ) { charNode ->
            SomeComplexModelEnum.valueOf(charNode.text)
        }
        val someComplexModelTypeParser = MappedParser(
            SequenceParser(
                CharInParser('('),
                PredictiveChoiceParser(
                    WordParser("Type1"),
                    WordParser("Type2"),
                ),
                CharInParser('-'),
                someComplexModelEnumParser,
                CharInParser(')'),
            )
        ) { sequenceNode ->
            val (_, choiceNode, _, enumNode, _) = sequenceNode.children
            val enumValue = (enumNode as ValueNode<SomeComplexModelEnum>).value
            val choiceNodeType = (choiceNode as ChoiceNode).node.text

            when (choiceNodeType) {
                "Type1" -> SomeComplexModelType.Type1(enumValue)
                "Type2" -> SomeComplexModelType.Type2(enumValue)
                else -> error("invalid model type: $choiceNodeType") // should never get here
            }
        }
        val someComplexModelParser = MappedParser(
            SequenceParser(
                nodeValue1Parser,
                CharInParser(','),
                OptionalWhitespaceParser(),
                nodeValue2Parser,
                CharInParser(','),
                OptionalWhitespaceParser(),
                someComplexModelTypeParser
            )
        ) { sequenceNode ->
            val (nodeValue1, nodeValue2, someComplexModel) = sequenceNode.children.let {
                listOf(
                    it[0],
                    it[3],
                    it[6]
                )
            }

            SomeComplexModel(
                nodeValue1.text,
                (nodeValue2 as ValueNode<Int>).value,
                (someComplexModel as ValueNode<SomeComplexModelType>).value,
            )
        }

        val underTest = someComplexModelParser
        val input = "asdf, 123, (Type1-A)"
        val output = underTest.test(input)
        val expected = """
            (ValueNode: 'SomeComplexModel(node1Value=ASDF, node2Value=123, node3Value=Type1(enumValue=A))')
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNotNull()
            .also {
                it.text.isEqualTo("SomeComplexModel(node1Value=ASDF, node2Value=123, node3Value=Type1(enumValue=A))")
                val model = it.isA<ValueNode<SomeComplexModel>>().value

                expectThat(model.node1Value).isEqualTo("ASDF")
                expectThat(model.node2Value).isEqualTo(123)
                expectThat(model.node3Value).isEqualTo(SomeComplexModelType.Type1(SomeComplexModelEnum.A))
            }
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testFlatMappedParser1() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = FlatMappedParser(
            DigitParser(),
        ) {
            val intValue = it.text.toInt()

            ValueNode<Int>(intValue, it.context)
        }

        input = "3"
        output = underTest.test(input)
        expected = """
            (ValueNode: '3')
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNotNull()
            .also {
                it.text.isEqualTo("3")
                it.isA<ValueNode<Int>>().value.isEqualTo(3)
            }
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }

    @Test
    fun testFlatMappedParser2() {
        var input: String
        var output: ParserResult<Node>?
        var expected: String
        val underTest = FlatMappedParser(
            DigitParser(),
        ) {
            TextNode("This is the replacement text", it.context)
        }

        input = "3"
        output = underTest.test(input)
        expected = """
            (TextNode: 'This is the replacement text')
        """
        expectThat(output)
            .parsedCorrectly(expected)
            .node()
            .isNotNull()
            .also {
                it.text.isEqualTo("This is the replacement text")
            }
        expectThat(underTest.predict(ParserContext(input, 0, false))).isTrue()
    }
}

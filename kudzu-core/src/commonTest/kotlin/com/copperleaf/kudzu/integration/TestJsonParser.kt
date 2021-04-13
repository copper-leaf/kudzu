package com.copperleaf.kudzu.integration

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.Visitor
import com.copperleaf.kudzu.VisitorContext
import com.copperleaf.kudzu.VisitorException
import com.copperleaf.kudzu.child
import com.copperleaf.kudzu.find
import com.copperleaf.kudzu.has
import com.copperleaf.kudzu.hasChild
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.ChoiceNode
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.DigitParser
import com.copperleaf.kudzu.parser.LazyParser
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.WordParser
import com.copperleaf.kudzu.visit
import kotlin.test.Test
import kotlin.test.BeforeTest

/* Tests parsing and evaluating a json file recursively based on the schema at https://www.json.org/. Parsers for
 * strings and numbers have been simplified as this is just a proof of concept.
 */
class TestJsonParser {

    val mockJson = """
            |{
            |  "_string": "two",
            |  "_number": 123456,
            |  "_false": false,
            |  "_true": true,
            |  "_null": null,
            |  "_object": {
            |    "key_one": "value_one",
            |    "key_two": "value_two",
            |    "key_three": "value_three"
            |  },
            |  "_array": [
            |    "element_one",
            |    "element_two",
            |    "element_three"
            |  ]
            |}
        """.trimMargin().trim()

    @BeforeTest
    fun setup() {
        initJsonGrammar()
    }

    @Test
    fun testJsonParserPredictions() {
        expectThat(lazyObjectParser.predict(ParserContext("{}", 0, false))).isTrue()
        expectThat(lazyObjectParser.predict(ParserContext("[]", 0, false))).isFalse()

        expectThat(lazyArrayParser.predict(ParserContext("[]", 0, false))).isTrue()
        expectThat(lazyArrayParser.predict(ParserContext("{}", 0, false))).isFalse()

        expectThat(jsonParser.predict(ParserContext("{}", 0, false))).isTrue()
        expectThat(jsonParser.predict(ParserContext("[]", 0, false))).isTrue()
        expectThat(jsonParser.predict(ParserContext("true", 0, false))).isFalse()

        expectThat(lazyValueParser.predict(ParserContext("{}", 0, false))).isTrue()
        expectThat(lazyValueParser.predict(ParserContext("[]", 0, false))).isTrue()
        expectThat(lazyValueParser.predict(ParserContext("true", 0, false))).isTrue()
        expectThat(lazyValueParser.predict(ParserContext("false", 0, false))).isTrue()
        expectThat(lazyValueParser.predict(ParserContext("null", 0, false))).isTrue()
        expectThat(lazyValueParser.predict(ParserContext("\"asdf\"", 0, false))).isTrue()
    }

    @Test
    fun testJsonParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = jsonParser

        input = mockJson
        output = underTest.test(input, true)
        expectThat(output).parsedCorrectly()
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testJsonVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = jsonParser

        input = mockJson
        output = underTest.test(input, true)
        expectThat(output).parsedCorrectly()

        val context = JsonContext()

        output!!.first.visit(context, JsonVisitor())

        expectThat(context.parsed)
            .isNotNull()
            .get { this as? Map<String, Any?> }
            .isNotNull()

        expectThat(context.parsed).get { this as Map<String, Any?> }.apply {
            this["_string"].isEqualTo("two")
            this["_number"].isEqualTo(123456.0)
            this["_false"].isEqualTo(false)
            this["_true"].isEqualTo(true)
            this["_null"].isEqualTo(null)
            this["_object"].get { this as Map<String, Any?> }.apply {
                this["key_one"].isEqualTo("value_one")
                this["key_two"].isEqualTo("value_two")
                this["key_three"].isEqualTo("value_three")
            }
            this["_array"].get { this as List<Any?> }.apply {
                this.containsExactlyInAnyOrder("element_one", "element_two", "element_three")
            }
        }
    }
}

private var lazyObjectParser = LazyParser()
private var lazyMembersParser = LazyParser()
private var lazyPairParser = LazyParser()

private var lazyArrayParser = LazyParser()
private var lazyElementsParser = LazyParser()

private var lazyValueParser = LazyParser()
private var lazyNumberParser = LazyParser()

private var jsonParser: Parser = ChoiceParser(
    lazyObjectParser,
    lazyArrayParser,
    name = "json"
)

private fun initJsonGrammar() {
    lazyValueParser.parser = ChoiceParser(
        getStringParser(),
        lazyNumberParser,
        lazyObjectParser,
        lazyArrayParser,
        WordParser("true", name = "boolean"),
        WordParser("false", name = "boolean"),
        WordParser("null", name = "null"),
        name = "value"
    )

    lazyNumberParser.parser = ManyParser(
        DigitParser(),
        name = "number"
    )

    lazyObjectParser.parser = SequenceParser(
        CharInParser('{'),
        OptionalWhitespaceParser(),
        lazyMembersParser,
        OptionalWhitespaceParser(),
        CharInParser('}'),
        name = "object"
    )

    lazyMembersParser.parser = SequenceParser(
        lazyPairParser,
        OptionalWhitespaceParser(),
        ManyParser(
            SequenceParser(
                CharInParser(','),
                OptionalWhitespaceParser(),
                lazyMembersParser
            ),
            name = "nextPair"
        ),
        name = "pair"
    )

    lazyPairParser.parser = SequenceParser(
        getStringParser(),
        OptionalWhitespaceParser(),
        CharInParser(':'),
        OptionalWhitespaceParser(),
        lazyValueParser
    )

    lazyArrayParser.parser = SequenceParser(
        CharInParser('['),
        OptionalWhitespaceParser(),
        lazyElementsParser,
        OptionalWhitespaceParser(),
        CharInParser(']'),
        name = "array"
    )

    lazyElementsParser.parser = SequenceParser(
        lazyValueParser,
        OptionalWhitespaceParser(),
        ManyParser(
            SequenceParser(
                CharInParser(','),
                OptionalWhitespaceParser(),
                lazyElementsParser
            ),
            name = "nextElement"
        ),
        name = "elements"
    )
}

private class JsonVisitor : Visitor<JsonContext>({ it is ChoiceNode }, "json") {

    override fun visit(context: JsonContext, node: Node) {
        context.parsed = getValue(node)
    }

    fun getValue(node: Node): Any? {
        return when (node.name) {
            "json" -> getJsonValue(node)
            "object" -> getObjectValue(node)
            "array" -> getArrayValue(node)
            "string" -> getStringValue(node)
            "number" -> getNumberValue(node)
            "boolean" -> getBooleanValue(node)
            "null" -> getNullValue(node)
            else -> throw VisitorException("unrecognized JSON node type: ${node.name}")
        }
    }

    private fun getJsonValue(node: Node): Any {
        if (node.has(null, "object")) {
            return getObjectValue(node.find(null, "object"))
        } else if (node.has(null, "array")) {
            return getObjectValue(node.find(null, "array"))
        } else {
            throw VisitorException("unrecognized JSON child node type: ${node.name}")
        }
    }

    private fun getObjectValue(node: Node): Map<String, Any?> {
        val pairs = HashMap<String, Any?>()

        var currentPair = node
        while (true) {
            if (!currentPair.has(null, "pair")) break
            currentPair = currentPair.find(null, "pair")

            val key = currentPair.child().find(null, "string").find(null, "argValue").text
            val value = getValue((currentPair.child().find(null, "value")).child())

            pairs[key] = value

            if (!currentPair.has(null, "nextPair")) break
            currentPair = currentPair.find(null, "nextPair")

            if (!currentPair.hasChild()) break
            currentPair = currentPair.child()
        }

        return pairs
    }

    private fun getArrayValue(node: Node): List<Any?> {
        val elements = ArrayList<Any?>()

        var currentPair = node
        while (true) {
            if (!currentPair.has(null, "elements")) break
            currentPair = currentPair.find(null, "elements")

            val value = getValue((currentPair.find(null, "value")).child())

            elements.add(value)

            if (!currentPair.has(null, "nextElement")) break
            currentPair = currentPair.find(null, "nextElement")

            if (!currentPair.hasChild()) break
            currentPair = currentPair.child()
        }

        return elements
    }

    private fun getStringValue(node: Node): String {
        return node.find(null, "argValue").text
    }

    private fun getNumberValue(node: Node): Double {
        return node.text.toDouble()
    }

    private fun getBooleanValue(node: Node): Boolean {
        return node.text.toBoolean()
    }

    @Suppress("UNUSED_PARAMETER")
    private fun getNullValue(node: Node): Any? {
        return null
    }
}

private class JsonContext : VisitorContext {

    var parsed: Any? = null
}

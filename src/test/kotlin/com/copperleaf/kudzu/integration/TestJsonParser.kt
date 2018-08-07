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
import com.copperleaf.kudzu.parsedCorrectly
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.get
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isNotNull
import strikt.assertions.isTrue

/* Tests parsing and evaluating a json file recursively based on the schema at https://www.json.org/. Parsers for
 * strings and numbers have been simplified as this is just a proof of concept.
 */
class TestJsonParser {

    @BeforeEach
    fun setup() {
        initJsonGrammar()
    }

    @Test
    fun testJsonParserPredictions() {
        expect(lazyObjectParser.predict(ParserContext("{}", 0, false))).isTrue()
        expect(lazyObjectParser.predict(ParserContext("[]", 0, false))).isFalse()

        expect(lazyArrayParser.predict(ParserContext("[]", 0, false))).isTrue()
        expect(lazyArrayParser.predict(ParserContext("{}", 0, false))).isFalse()

        expect(jsonParser.predict(ParserContext("{}", 0, false))).isTrue()
        expect(jsonParser.predict(ParserContext("[]", 0, false))).isTrue()
        expect(jsonParser.predict(ParserContext("true", 0, false))).isFalse()

        expect(lazyValueParser.predict(ParserContext("{}", 0, false))).isTrue()
        expect(lazyValueParser.predict(ParserContext("[]", 0, false))).isTrue()
        expect(lazyValueParser.predict(ParserContext("true", 0, false))).isTrue()
        expect(lazyValueParser.predict(ParserContext("false", 0, false))).isTrue()
        expect(lazyValueParser.predict(ParserContext("null", 0, false))).isTrue()
        expect(lazyValueParser.predict(ParserContext("\"asdf\"", 0, false))).isTrue()
    }

    @Test
    fun testJsonParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = jsonParser

        input = "" + this::class.java.getResourceAsStream("/mockjson.json").reader().readText()
        output = underTest.test(input, true)
        expect(output).parsedCorrectly()
    }

    @Test
    fun testJsonVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = jsonParser

        input = "" + this::class.java.getResourceAsStream("/mockjson.json").reader().readText()
        output = underTest.test(input, true)
        expect(output).parsedCorrectly()

        val context = JsonContext()

        output!!.first.visit(context, JsonVisitor())

        expect(context.parsed).isNotNull()
        expect(context.parsed)
                .map { this as? Map<String, Any?> }
                .isNotNull()

        expect(context.parsed).map { this as Map<String, Any?> }.evaluate {
            this["_string"].isEqualTo("two")
            this["_number"].isEqualTo(123456.0)
            this["_false"].isEqualTo(false)
            this["_true"].isEqualTo(true)
            this["_null"].isEqualTo(null)
            this["_object"].map { this as Map<String, Any?> }.evaluate {
                this["key_one"].isEqualTo("value_one")
                this["key_two"].isEqualTo("value_two")
                this["key_three"].isEqualTo("value_three")
            }
            this["_array"].map { this as List<Any?> }.evaluate {
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

private class JsonVisitor : Visitor<JsonContext>(ChoiceNode::class, "json") {

    override fun visit(context: JsonContext, node: Node) {
        context.parsed = context.getValue(node)
    }

}

private class JsonContext : VisitorContext {

    var parsed: Any? = null

    fun getValue(node: Node): Any? {
        return when (node.name) {
            "json"    -> getJsonValue(node)
            "object"  -> getObjectValue(node)
            "array"   -> getArrayValue(node)
            "string"  -> getStringValue(node)
            "number"  -> getNumberValue(node)
            "boolean" -> getBooleanValue(node)
            "null"    -> getNullValue(node)
            else      -> throw VisitorException("unrecognized JSON node type: ${node.name}")
        }
    }

    private fun getJsonValue(node: Node): Any {
        if (node.has(null, "object")) {
            return getObjectValue(node.find(null, "object"))
        }
        else if (node.has(null, "array")) {
            return getObjectValue(node.find(null, "array"))
        }
        else {
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

    private fun getNullValue(node: Node): Any? {
        return null
    }

}
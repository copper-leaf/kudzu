package com.copperleaf.kudzu.integration

import com.copperleaf.kudzu.*
import com.copperleaf.kudzu.parser.BetweenTimesParser
import com.copperleaf.kudzu.parser.CharInParser
import com.copperleaf.kudzu.parser.CharNotInParser
import com.copperleaf.kudzu.parser.ChoiceParser
import com.copperleaf.kudzu.parser.ManyNode
import com.copperleaf.kudzu.parser.ManyParser
import com.copperleaf.kudzu.parser.MaybeParser
import com.copperleaf.kudzu.parser.OptionalWhitespaceParser
import com.copperleaf.kudzu.parser.RequiredWhitespaceParser
import com.copperleaf.kudzu.parser.SequenceNode
import com.copperleaf.kudzu.parser.SequenceParser
import com.copperleaf.kudzu.parser.TokenParser
import com.copperleaf.kudzu.visitor.DfsTreeVisitor
import kotlin.test.Test

class TestCommandLineArgsParser {

    @Test
    fun testStringParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getStringParser()

        input = "\"asdf\"".trim()
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { findAnywhere<ManyNode>("argValue").text }
            .isEqualTo("asdf")

        input = """ "as\"df" """.trim()
        output = underTest.test(input)
        expectThat(output)
            .parsedCorrectly()
            .node()
            .isNotNull()
            .get { findAnywhere<ManyNode>("argValue").text }
            .isEqualTo("as\"df".trim())
    }

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testCommandLineArgsParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getParser()

        input = "--verbose -d asdf1 -d asdf2 -d asdf3 --destination=\"asdf4\"  --superDestination=\"as\\\"df5\""
        output = underTest.test(input)
        expectThat(output).parsedCorrectly()

        val context = ArgsContext()
        val visitor = ArgsVisitor()

        val iterator = DfsTreeVisitor(setOf(visitor))
        iterator.visit(context, output!!.first)

        expectThat(context.args)["verbose"]
            .isEqualTo("true")

        expectThat(context.args)["d"]
            .isNotNull()
            .get { this as? List<Any> }
            .isNotNull()
            .containsExactlyInAnyOrder("asdf1", "asdf2", "asdf3")

        expectThat(context.args)["destination"]
            .isEqualTo("asdf4")

        expectThat(context.args)["superDestination"]
            .isEqualTo("as\"df5")
    }
}

// Parser
// ----------------------------------------------------------------------------------------------------------------------

private lateinit var stringParser: Parser

internal fun getStringParser(): Parser {
    if (!::stringParser.isInitialized) {
        stringParser = SequenceParser(
            CharInParser('\"'),
            ManyParser(
                CharNotInParser('\"', escapeChar = '\\'),
                name = "argValue"
            ),
            CharInParser('\"'),
            name = "string"
        )
    }

    return stringParser
}

internal fun getArgNameSublist(): List<Parser> {
    return listOf(
        BetweenTimesParser(1, 2, CharInParser('-')),
        TokenParser(name = "argName"),
        ChoiceParser(
            CharInParser('='),
            RequiredWhitespaceParser(),
            name = "keyValueSeparator"
        )
    )
}

internal fun getArgValueSublist(): Parser {
    return MaybeParser(
        ChoiceParser(
            getStringParser(),
            TokenParser(name = "argValue")
        )
    )
}

internal fun getParser(): Parser {
    val argParserList = ArrayList<Parser>()
    argParserList.addAll(getArgNameSublist())
    argParserList.add(getArgValueSublist())
    argParserList.add(OptionalWhitespaceParser())

    val argParser = SequenceParser(
        *argParserList.toTypedArray(),
        name = "arg"
    )

    return ManyParser(argParser)
}

// Visitor
// ----------------------------------------------------------------------------------------------------------------------

class ArgsContext : VisitorContext {
    val args = HashMap<String, Any>()
}

class ArgsVisitor : Visitor<ArgsContext>({ it is SequenceNode }, "arg") {

    @Suppress("UNCHECKED_CAST")
    override fun visit(context: ArgsContext, node: Node) {
        val argName = node.findAnywhere(null, "argName").text
        val argValue: String = try {
            node.findAnywhere(null, "argValue").text
        } catch (e: VisitorException) {
            "true"
        }

        if (context.args.containsKey(argName)) {
            if (context.args[argName] is MutableList<*>) {
                (context.args[argName] as MutableList<Any>).add(argValue)
            } else {
                context.args[argName] = mutableListOf(context.args[argName], argValue)
            }
        } else {
            context.args[argName] = argValue
        }
    }
}

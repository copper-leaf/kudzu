package com.eden.monadik.integration

import com.eden.monadik.Node
import com.eden.monadik.Parser
import com.eden.monadik.ParserContext
import com.eden.monadik.Visitor
import com.eden.monadik.VisitorContext
import com.eden.monadik.VisitorException
import com.eden.monadik.findAnywhere
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.parser.BetweenTimesParser
import com.eden.monadik.parser.CharInParser
import com.eden.monadik.parser.CharNotInParser
import com.eden.monadik.parser.ChoiceParser
import com.eden.monadik.parser.ManyNode
import com.eden.monadik.parser.ManyParser
import com.eden.monadik.parser.MaybeParser
import com.eden.monadik.parser.OptionalWhitespaceParser
import com.eden.monadik.parser.RequiredWhitespaceParser
import com.eden.monadik.parser.SequenceNode
import com.eden.monadik.parser.SequenceParser
import com.eden.monadik.parser.TokenParser
import com.eden.monadik.visitor.DfsTreeVisitor
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.get
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class TestCommandLineArgsParser {

    @Test
    fun testStringParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getStringParser()

        input = "\"asdf\"".trim()
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.findAnywhere<ManyNode>("argValue").text }
                .isEqualTo("asdf")

        input = """ "as\"df" """.trim()
        output = underTest.test(input)
        expect(output)
                .parsedCorrectly()
                .node()
                .map { this!!.findAnywhere<ManyNode>("argValue").text }
                .isEqualTo("as\"df".trim())
    }

    @Test
    fun testCommandLineArgsParser() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val underTest = getParser()

        input = "--verbose -d asdf1 -d asdf2 -d asdf3 --destination=\"asdf4\"  --superDestination=\"as\\\"df5\""
        output = underTest.test(input)
        expect(output).parsedCorrectly()

        val context = ArgsContext()
        val visitor = ArgsVisitor()

        val iterator = DfsTreeVisitor(setOf(visitor))
        iterator.visit(context, output!!.first)

        expect(context.args)["verbose"]
                .isEqualTo("true")

        expect(context.args)["d"]
                .map { this as? List<Any> }
                .isNotNull()
                .containsExactlyInAnyOrder("asdf1", "asdf2", "asdf3")

        expect(context.args)["destination"]
                .isEqualTo("asdf4")

        expect(context.args)["superDestination"]
                .isEqualTo("as\"df5")
    }
}


// Parser
//----------------------------------------------------------------------------------------------------------------------

private fun getStringParser(): Parser {
    return SequenceParser(
            CharInParser('\"'),
            ManyParser(
                    CharNotInParser('\"', escapeChar = '\\'),
                    name = "argValue"
            ),
            CharInParser('\"')
    )
}

private fun getArgNameSublist(): List<Parser> {
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

private fun getArgValueSublist(): Parser {
    return MaybeParser(
            ChoiceParser(
                    getStringParser(),
                    TokenParser(name = "argValue")
            )
    )
}

private fun getParser(): Parser {
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
//----------------------------------------------------------------------------------------------------------------------

class ArgsContext : VisitorContext {
    val args = HashMap<String, Any>()
}

class ArgsVisitor : Visitor<ArgsContext>(SequenceNode::class, "arg") {
    override fun visit(context: ArgsContext, node: Node) {
        val argName = node.findAnywhere(null, "argName").text
        val argValue: String = try {
            node.findAnywhere(null, "argValue").text
        }
        catch (e: VisitorException) {
            "true"
        }

        if (context.args.containsKey(argName)) {
            if (context.args[argName] is MutableList<*>) {
                (context.args[argName] as MutableList<Any>).add(argValue)
            }
            else {
                context.args[argName] = mutableListOf(context.args[argName], argValue)
            }
        }
        else {
            context.args[argName] = argValue
        }
    }
}
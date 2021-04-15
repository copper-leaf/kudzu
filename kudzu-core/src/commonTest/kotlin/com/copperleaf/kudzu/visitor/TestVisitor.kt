package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.*
import com.copperleaf.kudzu.parser.*
import kotlin.test.Test

class TestVisitor {

    @Test
    fun testManyCharVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val parser = ManyParser(CharParser())

        input = "asdf"
        output = parser.test(input)
        expected = """
            (ManyNode:
              (CharNode: 'a')
              (CharNode: 's')
              (CharNode: 'd')
              (CharNode: 'f')
            )
        """
        expectThat(output).parsedCorrectly(expected)
            .node()
            .isNonTerminal()
            .withChildren(4)

        val charVisitor = StringAccumulatorVisitor({ it is CharNode })
        val context = StringAccumulatorContext()
        val dfsTreeVisitor = DfsTreeVisitor(setOf(charVisitor))

        dfsTreeVisitor.visit(context, output!!.first)

        expectThat(context.acc).isEqualTo("asdf")
    }

    @Test
    fun testManyLettersAndNumbersVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val parser = ManyParser(
            ChoiceParser(
                LetterParser(name = "letters"),
                DigitParser(name = "digits")
            )
        )

        input = "a1s2d3f4"
        output = parser.test(input)
        expectThat(output).parsedCorrectly()

        val stringAccumulatorContext = StringAndDigitAccumulatorContext()
        var dfsTreeVisitor = DfsTreeVisitor(setOf(StringAndDigitAccumulatorVisitor({ it is CharNode })))
        dfsTreeVisitor.visit(stringAccumulatorContext, output!!.first)
        expectThat(stringAccumulatorContext.stringAcc).isEqualTo("a1s2d3f4")

        val stringAndDigitAccumulatorContext = StringAndDigitAccumulatorContext()
        val lettersNodeVisitor = StringAndDigitAccumulatorVisitor({ it is CharNode }, "letters")
        val digitsNodeVisitor = StringAndDigitAccumulatorVisitor({ it is CharNode }, "digits")
        dfsTreeVisitor = DfsTreeVisitor(setOf(lettersNodeVisitor, digitsNodeVisitor))
        dfsTreeVisitor.visit(stringAndDigitAccumulatorContext, output.first)
        expectThat(stringAndDigitAccumulatorContext.stringAcc).isEqualTo("asdf")
        expectThat(stringAndDigitAccumulatorContext.digitAcc).isEqualTo(10)
    }

    @Test
    fun testManyLettersAndNumbersByParentNodeVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        val parser = ManyParser(
            ChoiceParser(
                ChoiceParser(
                    LetterParser(name = "letters"),
                    name = "letters"
                ),
                ChoiceParser(
                    DigitParser(name = "digits"),
                    name = "digits"
                )
            )
        )

        input = "a1s2d3f4"
        output = parser.test(input)
        expectThat(output).parsedCorrectly()
            .node()
            .isNonTerminal()
        output!!.first.linkTree()

        (output.first as NonTerminalNode).children.forEach {
            expectThat(it.parent).isSameInstanceAs(output.first)
        }

        val stringAccumulatorContext = StringAndDigitAccumulatorContext()
        var dfsTreeVisitor = DfsTreeVisitor(setOf(StringAndDigitAccumulatorVisitor({ it is CharNode })))
        dfsTreeVisitor.visit(stringAccumulatorContext, output.first)
        expectThat(stringAccumulatorContext.stringAcc).isEqualTo("a1s2d3f4")

        val stringAndDigitAccumulatorContext = StringAndDigitAccumulatorContext()
        val lettersNodeVisitor = StringAndDigitByParentAccumulatorVisitor({ it is CharNode }, "letters")
        val digitsNodeVisitor = StringAndDigitByParentAccumulatorVisitor({ it is CharNode }, "digits")
        dfsTreeVisitor = DfsTreeVisitor(setOf(lettersNodeVisitor, digitsNodeVisitor))
        dfsTreeVisitor.visit(stringAndDigitAccumulatorContext, output.first)
        expectThat(stringAndDigitAccumulatorContext.stringAcc).isEqualTo("asdf")
        expectThat(stringAndDigitAccumulatorContext.digitAcc).isEqualTo(10)
    }
}

class StringAccumulatorVisitor(nodeMatcher: (Node) -> Boolean, nodeName: String? = null) :
    Visitor<StringAccumulatorContext>(nodeMatcher, nodeName) {

    override fun visit(context: StringAccumulatorContext, node: Node) {
        context.acc += node.text
    }
}

class StringAccumulatorContext : VisitorContext {

    var acc: String = ""
}

class StringAndDigitAccumulatorVisitor(nodeMatcher: (Node) -> Boolean, nodeName: String? = null) :
    Visitor<StringAndDigitAccumulatorContext>(nodeMatcher, nodeName) {

    override fun visit(context: StringAndDigitAccumulatorContext, node: Node) {
        if (this.nodeName == "letters" && node.name == "letters") {
            context.stringAcc += node.text
        } else if (this.nodeName == "digits" && node.name == "digits") {
            context.digitAcc += node.text.toInt()
        } else if (this.nodeName == null) {
            context.stringAcc += node.text
        }
    }
}

class StringAndDigitByParentAccumulatorVisitor(nodeMatcher: (Node) -> Boolean, nodeName: String? = null) :
    Visitor<StringAndDigitAccumulatorContext>(nodeMatcher, nodeName) {

    override fun visit(context: StringAndDigitAccumulatorContext, node: Node) {
        if (this.nodeName == "letters" && node.parent?.name == "letters") {
            context.stringAcc += node.text
        } else if (this.nodeName == "digits" && node.parent?.name == "digits") {
            context.digitAcc += node.text.toInt()
        } else if (this.nodeName == null) {
            context.stringAcc += node.text
        }
    }
}

class StringAndDigitAccumulatorContext : VisitorContext {

    var stringAcc: String = ""
    var digitAcc: Int = 0
}

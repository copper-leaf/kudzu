package com.eden.monadik.visitor

import com.eden.monadik.Node
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.ParserContext
import com.eden.monadik.Visitor
import com.eden.monadik.VisitorContext
import com.eden.monadik.isNonTerminal
import com.eden.monadik.node
import com.eden.monadik.parsedCorrectly
import com.eden.monadik.parser.CharNode
import com.eden.monadik.parser.CharParser
import com.eden.monadik.parser.ChoiceParser
import com.eden.monadik.parser.DigitParser
import com.eden.monadik.parser.LetterParser
import com.eden.monadik.parser.ManyParser
import com.eden.monadik.withChildren
import org.junit.jupiter.api.Test
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isSameInstanceAs
import kotlin.reflect.KClass

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
        expect(output).parsedCorrectly(expected)
                .node()
                .isNonTerminal()
                .withChildren(4)

        val charVisitor = StringAccumulatorVisitor(CharNode::class)
        val context = StringAccumulatorContext()
        val dfsTreeVisitor = DfsTreeVisitor(setOf(charVisitor))

        dfsTreeVisitor.visit(context, output!!.first)

        expect(context.acc).isEqualTo("asdf")
    }

    @Test
    fun testManyLettersAndNumbersVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
        val parser = ManyParser(
                ChoiceParser(
                        LetterParser(name = "letters"),
                        DigitParser(name = "digits")
                )
        )

        input = "a1s2d3f4"
        output = parser.test(input)
        expect(output).parsedCorrectly()

        val stringAccumulatorContext = StringAndDigitAccumulatorContext()
        var dfsTreeVisitor = DfsTreeVisitor(setOf(StringAndDigitAccumulatorVisitor(CharNode::class)))
        dfsTreeVisitor.visit(stringAccumulatorContext, output!!.first)
        expect(stringAccumulatorContext.stringAcc).isEqualTo("a1s2d3f4")

        val stringAndDigitAccumulatorContext = StringAndDigitAccumulatorContext()
        val lettersNodeVisitor = StringAndDigitAccumulatorVisitor(CharNode::class, "letters")
        val digitsNodeVisitor = StringAndDigitAccumulatorVisitor(CharNode::class, "digits")
        dfsTreeVisitor = DfsTreeVisitor(setOf(lettersNodeVisitor, digitsNodeVisitor))
        dfsTreeVisitor.visit(stringAndDigitAccumulatorContext, output.first)
        expect(stringAndDigitAccumulatorContext.stringAcc).isEqualTo("asdf")
        expect(stringAndDigitAccumulatorContext.digitAcc).isEqualTo(10)
    }

    @Test
    fun testManyLettersAndNumbersByParentNodeVisitor() {
        var input: String
        var output: Pair<Node, ParserContext>?
        var expected: String
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
        expect(output).parsedCorrectly()
                .node()
                .isNonTerminal()
        output!!.first.linkTree()

        (output.first as NonTerminalNode).children.forEach {
            expect(it.parent).isSameInstanceAs(output.first)
        }

        val stringAccumulatorContext = StringAndDigitAccumulatorContext()
        var dfsTreeVisitor = DfsTreeVisitor(setOf(StringAndDigitAccumulatorVisitor(CharNode::class)))
        dfsTreeVisitor.visit(stringAccumulatorContext, output!!.first)
        expect(stringAccumulatorContext.stringAcc).isEqualTo("a1s2d3f4")

        val stringAndDigitAccumulatorContext = StringAndDigitAccumulatorContext()
        val lettersNodeVisitor = StringAndDigitByParentAccumulatorVisitor(CharNode::class, "letters")
        val digitsNodeVisitor = StringAndDigitByParentAccumulatorVisitor(CharNode::class, "digits")
        dfsTreeVisitor = DfsTreeVisitor(setOf(lettersNodeVisitor, digitsNodeVisitor))
        dfsTreeVisitor.visit(stringAndDigitAccumulatorContext, output.first)
        expect(stringAndDigitAccumulatorContext.stringAcc).isEqualTo("asdf")
        expect(stringAndDigitAccumulatorContext.digitAcc).isEqualTo(10)
    }
}

class StringAccumulatorVisitor(nodeClass: KClass<out Node>, nodeName: String? = null)
    : Visitor<StringAccumulatorContext>(nodeClass, nodeName) {

    override fun visit(context: StringAccumulatorContext, node: Node) {
        context.acc += node.text
    }

}

class StringAccumulatorContext : VisitorContext {

    var acc: String = ""

}

class StringAndDigitAccumulatorVisitor(nodeClass: KClass<out Node>, nodeName: String? = null)
    : Visitor<StringAndDigitAccumulatorContext>(nodeClass, nodeName) {

    override fun visit(context: StringAndDigitAccumulatorContext, node: Node) {
        if(this.nodeName == "letters" && node.name == "letters") {
            context.stringAcc += node.text
        }
        else if(this.nodeName == "digits" && node.name == "digits") {
            context.digitAcc += Integer.parseInt(node.text)
        }
        else if(this.nodeName == null) {
            context.stringAcc += node.text
        }
    }

}

class StringAndDigitByParentAccumulatorVisitor(nodeClass: KClass<out Node>, nodeName: String? = null)
    : Visitor<StringAndDigitAccumulatorContext>(nodeClass, nodeName) {

    override fun visit(context: StringAndDigitAccumulatorContext, node: Node) {
        if(this.nodeName == "letters" && node.parent?.name == "letters") {
            context.stringAcc += node.text
        }
        else if(this.nodeName == "digits" && node.parent?.name == "digits") {
            context.digitAcc += Integer.parseInt(node.text)
        }
        else if(this.nodeName == null) {
            context.stringAcc += node.text
        }
    }

}

class StringAndDigitAccumulatorContext : VisitorContext {

    var stringAcc: String = ""
    var digitAcc: Int = 0

}
package com.copperleaf.kudzu

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.visitor.Visitor
import com.copperleaf.kudzu.visitor.VisitorImpl

/**
 * Used by a Parser to verify that the input is not empty when it expects to be able to consume a character.
 */
@ExperimentalStdlibApi
fun Parser<*>.checkNotEmpty(input: ParserContext) {
    if (input.isEmpty()) throw ParserException("unexpected end of input", this, input)
}

/**
 * Visit a Node with a list of [Visitor.Callback] to introspect a parse tree.
 *
 * @see [Visitor]
 */
@ExperimentalStdlibApi
fun Node.visit(vararg callbacks: Visitor.Callback) {
    VisitorImpl(*callbacks).visit(this)
}

/**
 * Visit a Node with a simple callback function to introspect a parse tree. If [reversed] is false (the default
 * behavior), the [callback] will be called during the [Visitor.Callback.enter] callback. If [reversed] is true, it will
 * be called during the [Visitor.Callback.exit] callback.
 *
 * @see [Visitor]
 */
@ExperimentalStdlibApi
fun Node.visit(reversed: Boolean = false, callback: (Node) -> Unit) {
    val visitorCallback = if (reversed) {
        object : Visitor.Callback {
            override fun exit(node: Node) {
                callback(node)
            }
        }
    } else {
        object : Visitor.Callback {
            override fun enter(node: Node) {
                callback(node)
            }
        }
    }

    visit(visitorCallback)
}

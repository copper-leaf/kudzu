package com.copperleaf.kudzu

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.visitor.Visitor
import com.copperleaf.kudzu.visitor.VisitorImpl

@ExperimentalStdlibApi
fun Parser<*>.checkNotEmpty(input: ParserContext) {
    if (input.isEmpty()) throw ParserException("unexpected end of input", this, input)
}

fun Char.isLetter(): Boolean = KudzuPlatform.isLetter(this)
fun Char.isDigit(): Boolean = KudzuPlatform.isDigit(this)
fun Char.isLetterOrDigit(): Boolean = KudzuPlatform.isLetterOrDigit(this)

@ExperimentalStdlibApi
fun Node.visit(vararg callbacks: Visitor.Callback) {
    VisitorImpl(*callbacks).visit(this)
}

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

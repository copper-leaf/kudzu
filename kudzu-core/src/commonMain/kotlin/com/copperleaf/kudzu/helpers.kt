package com.copperleaf.kudzu

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserException
import com.copperleaf.kudzu.parser.ParserResult
import com.copperleaf.kudzu.visitor.Visitor
import com.copperleaf.kudzu.visitor.VisitorImpl

/**
 * Used by a Parser to verify that the input is not empty when it expects to be able to consume a character.
 */
public fun Parser<*>.checkNotEmpty(input: ParserContext, expected: (() -> String)? = null) {
    if (input.isEmpty()) {
        val expectedValue = expected?.invoke()
        val message = if (expectedValue != null) {
            "unexpected end of input. Expected '$expectedValue'"
        } else {
            "unexpected end of input"
        }
        throw ParserException(message, this, input)
    }
}

/**
 * Visit a Node with a list of [Visitor.Callback] to introspect a parse tree.
 *
 * @see [Visitor]
 */
public fun Node.visit(vararg callbacks: Visitor.Callback) {
    VisitorImpl(*callbacks).visit(this)
}

/**
 * Visit a Node with a simple callback function to introspect a parse tree. If [reversed] is false (the default
 * behavior), the [callback] will be called during the [Visitor.Callback.enter] callback. If [reversed] is true, it will
 * be called during the [Visitor.Callback.exit] callback.
 *
 * @see [Visitor]
 */
public fun Node.visit(reversed: Boolean = false, callback: (Node) -> Unit) {
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

public typealias RemapperFn = ParserContext.(Parser<*>, ParserException) -> ParserException

public suspend inline fun <
    BaseParser : Node,
    ChildParser : Node
    > DeepRecursiveScope<ParserContext, ParserResult<BaseParser>>.parseWithRemappedErrors(
    parser: Parser<ChildParser>,
    input: ParserContext,
    remapErrors: RemapperFn,
): ParserResult<ChildParser> {
    return try {
        parser.parse.callRecursive(input)
    } catch (e: ParserException) {
        throw remapErrors(input, parser, e)
    }
}

package com.copperleaf.kudzu.node

import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.util.printAst

/**
 * The abstract representation of a node parsed from the input. All nodes must be a subclass of either [TerminalNode]
 * or [NonTerminalNode].
 */
public sealed class Node(
    public val context: NodeContext
) {

    public open val astNodeName: String get() = this::class.simpleName!!
    public abstract val text: String

    override fun toString(): String {
        return printAst(0)
    }
}

/**
 * The abstract representation of a "leaf" or "terminal" parse node. Terminal nodes have no children, and have [text]
 * parsed directly from the input. TerminalNodes are produced by a [Parser] that does not delegate to any other parsers,
 * but only parses input itself.
 */
public abstract class TerminalNode(context: NodeContext) : Node(context)

/**
 * The abstract representation of an "inner" or "non-terminal" parse node. Non-terminal nodes are defined as containing
 * a list of "children" nodes, but that list may be empty. NonTerminalNodes are produced by a [Parser] that delegates to
 * another parser in some manner, but may handle some of the parsing logic itself. The [text] of a NonTerminalNode is
 * the text of all its children nodes joined together.
 */
public abstract class NonTerminalNode(context: NodeContext) : Node(context) {
    public abstract val children: List<Node>

    final override val text: String by lazy {
        children.joinToString(separator = "") { it.text }
    }
}

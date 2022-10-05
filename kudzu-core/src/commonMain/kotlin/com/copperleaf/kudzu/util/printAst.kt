package com.copperleaf.kudzu.util

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode
import com.copperleaf.kudzu.node.TerminalNode

public fun Node.printAst(currentIndent: Int): String {
    return when (this) {
        is TerminalNode -> printTerminalNodeAst(currentIndent)
        is NonTerminalNode -> printNonTerminalNodeAst(currentIndent)
    }
}

internal fun TerminalNode.printTerminalNodeAst(currentIndent: Int): String {
    return "${indent(currentIndent)}($astNodeName: '$text')"
}

internal fun NonTerminalNode.printNonTerminalNodeAst(currentIndent: Int): String {
    val childrenPrinted = if (children.isNotEmpty()) {
        children
            .map { it.printAst(currentIndent + 2) }
            .joinToString(separator = "\n")
    } else {
        "${indent(currentIndent + 2)}(empty)"
    }

    return "${indent(currentIndent)}($astNodeName:\n" +
        childrenPrinted +
        "\n" +
        "${indent(currentIndent)})"
}

internal fun indent(currentIndent: Int): String {
    return (0 until currentIndent).map { " " }.joinToString(separator = "")
}

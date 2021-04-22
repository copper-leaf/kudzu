package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode

@ExperimentalStdlibApi
internal class VisitorImpl(
    private val callbacks: List<Visitor.Callback>
) : Visitor {
    constructor(vararg callbacks: Visitor.Callback) : this(callbacks.toList())

    private fun enter(node: Node) {
        callbacks.forEach { it.enter(node) }
    }

    private fun exit(node: Node) {
        callbacks.forEach { it.exit(node) }
    }

    private fun onStart() {
        callbacks.forEach { it.onStart() }
    }

    private fun onFinish() {
        callbacks.forEach { it.onFinish() }
    }

    override fun visit(node: Node) {
        onStart()
        visitNode(node)
        onFinish()
    }

    private val visitNode = DeepRecursiveFunction<Node, Unit> { node ->
        enter(node)

        if (node is NonTerminalNode) {
            node.children.forEach { childNode ->
                callRecursive(childNode)
            }
        }

        exit(node)
    }
}

package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NonTerminalNode
import com.copperleaf.kudzu.Visitor
import com.copperleaf.kudzu.VisitorContext

class DfsTreeVisitor<T: VisitorContext>(private val visitors: Set<Visitor<T>>) {

    fun visit(context: T, node: Node) {
        node.linkTree()
        visitNode(context, node)
        visitors.forEach { it.onFinish(context) }
    }

    private fun visitNode(context: T, node: Node) {
        if (node is NonTerminalNode) {
            node.children.forEach { childNode ->
                visitNode(context, childNode)
            }
        }

        findNodeVisitors(node).forEach { visitor ->
            visitor.visit(context, node)
        }
    }

    private fun findNodeVisitors(node: Node): Set<Visitor<T>> {
        return visitors
                .filter { visitor -> (node::class == visitor.nodeClass || visitor.nodeClass.java.isAssignableFrom(node::class.java)) }
                .filter { visitor -> if (visitor.nodeName != null) node.name == visitor.nodeName else true }
                .toSet()
    }

}
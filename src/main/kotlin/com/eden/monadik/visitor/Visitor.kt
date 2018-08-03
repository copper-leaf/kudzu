package com.eden.monadik.visitor

import com.eden.monadik.Node
import com.eden.monadik.NonTerminalNode
import com.eden.monadik.Visitor
import com.eden.monadik.VisitorContext

class DfsTreeVisitor<T: VisitorContext>(private val visitors: Set<Visitor<T>>) : Visitor<T>(Node::class, null) {

    override fun visit(context: T, node: Node) {
        if (node is NonTerminalNode) {
            node.children.forEach { childNode ->
                visit(context, childNode)
            }
        }

        visitNode(context, node)
    }

    private fun findNodeVisitors(node: Node): Set<Visitor<T>> {
        return visitors
                .filter { visitor -> visitor.nodeClass == node::class }
                .filter { visitor -> if (visitor.nodeName != null) node.name == visitor.nodeName else true }
                .toSet()
    }

    private fun visitNode(context: T, node: Node) {
        findNodeVisitors(node).forEach { visitor ->
            visitor.visit(context, node)
        }
    }
}
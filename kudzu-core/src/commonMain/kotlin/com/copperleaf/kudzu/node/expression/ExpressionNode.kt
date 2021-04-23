package com.copperleaf.kudzu.node.expression

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A Node representing a generic non-terminal node in an Expression. Other expression nodes must extend this to support
 * simplifying the resulting expression AST.
 */
abstract class ExpressionNode(
    context: NodeContext
) : NonTerminalNode(context) {

    abstract fun simplify(): Node

    protected fun Node.simplifyChild(): Node {
        return if(this is ExpressionNode) {
            this.simplify()
        }
        else {
            this
        }
    }
}

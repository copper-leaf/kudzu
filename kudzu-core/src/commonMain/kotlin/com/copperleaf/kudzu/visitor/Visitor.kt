package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.node.Node

interface Visitor {

    fun visit(node: Node)

    interface Callback {
        fun enter(node: Node) { }
        fun exit(node: Node) { }

        fun onStart() { }
        fun onFinish() { }
    }
}

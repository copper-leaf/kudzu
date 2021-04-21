package com.copperleaf.kudzu.visitor

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NonTerminalNode

/**
 * A Visitor allows one to introspect the structure of a parse-tree and view all nodes that have been parsed. Each Node
 * will be visited in the following order:
 *
 * - [Visitor.Callback.enter] a Node
 * - If the Node is a [NonTerminalNode], recurse and visit each of its children nodes in order
 * - [Visitor.Callback.exit] a Node
 *
 * Tips/Recipes:
 * - A Visitor can be applied to any given node, anywhere in the parse tree; it does not necessarily have to start at the
 *   root node (though that is usually the case).
 * - It is up to the [Visitor.Callback] to filter unwanted nodes and track depth within the parse-tree. You can track
 *   depth by incrementing a counter in the [Visitor.Callback.enter] callback and decrementing it in the
 *   [Visitor.Callback.exit] callback.
 * - Nodes are only aware of their children, and thus from any given node you cannot traverse upward through the tree.
 * - The Visitor implementation makes no guarantee of the actual call-stack. The default implementation uses
 *   [DeepRecursiveFunction] to implement iterative recursion through the tree, so only a single Node will actually be
 *   on the call stack at a time.
 * - For the same reason as above, be wary of traversing more than a few nodes deep from within a callback. Parse trees
 *   can be very deep and general recursion should not be used within a callback; that's the purpose of the Visitor.
 * - Parse trees are immutable structures. Visitation is a read-only process.
 */
interface Visitor {

    /**
     * Visit this node and all its children nodes, recursively.
     */
    fun visit(node: Node)

    /**
     * An interface for receiving callback info about the visitation of a parse tree.
     */
    interface Callback {
        /**
         * Called when entering a Node in the parse-tree, before recursing to enter any of its children nodes.
         */
        fun enter(node: Node) { }

        /**
         * Called when exiting a Node in the parse-tree, after recursing and exiting all of its children nodes.
         */
        fun exit(node: Node) { }

        /**
         * Called before visitation has start, before any nodes in the parse-tree have been entered.
         */
        fun onStart() { }

        /**
         * Called after visitation has completed, after all nodes in the parse-tree have been both entered and exited.
         */
        fun onFinish() { }
    }
}

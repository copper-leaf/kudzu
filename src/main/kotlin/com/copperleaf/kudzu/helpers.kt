package com.copperleaf.kudzu

import com.copperleaf.kudzu.visitor.DfsTreeVisitor
import kotlin.reflect.KClass

/**
 * Finds the first matching node by class and name in the immediate children of this node. Throws
 * {@link VisitorException} if this node is a {@link TerminalNode} or if no child nodes match the query.
 */
@Throws(VisitorException::class)
fun Node.find(nodeClass: KClass<out Node>?, nodeName: String? = null): Node {
    val message = "cannot find child node of ${nodeClass?.java?.simpleName} $nodeName".trim()

    if(this is TerminalNode) throw VisitorException("$message: node is terminal")

    else if(this is NonTerminalNode) {
        for(node in children) {
            val matchesClass = if(nodeClass != null) node::class == nodeClass else true
            val matchesName = if(nodeName != null) node.name == nodeName else true

            if(matchesClass && matchesName) {
                return node
            }
        }
    }

    throw VisitorException("$message: no matching nodes found")
}

@Throws(VisitorException::class)
inline fun <reified T: Node> Node.find(nodeName: String? = null): T {
    val foundNode = find(T::class, nodeName)
    if(foundNode is T) {
        return foundNode
    }
    else {
        throw ClassCastException("Found a node, but it was not of type ${foundNode.javaClass.simpleName}")
    }
}

fun Node.has(nodeClass: KClass<out Node>?, nodeName: String? = null): Boolean {
    return try {
        find(nodeClass, nodeName)
        true
    }
    catch (e: VisitorException) {
        false
    }
}

fun Node.child(): Node {
    return find(null, null)
}

@JvmName("typedChild")
inline fun <reified T: Node> Node.child(): T {
    return find<T>(null)
}

fun Node.hasChild(): Boolean {
    return has(null, null)
}

/**
 * Finds the first matching node by class and name in any child of this node. Throws {@link VisitorException} if this
 * node is a {@link TerminalNode} or if no child nodes match the query.
 */
@Throws(VisitorException::class)
fun Node.findAnywhere(nodeClass: KClass<out Node>?, nodeName: String? = null): Node {
    val message = "cannot find child node of ${nodeClass?.java?.simpleName} $nodeName".trim()

    if(this is TerminalNode) throw VisitorException("$message: node is terminal")

    else if(this is NonTerminalNode) {
        for(node in children) {
            val matchesClass = if(nodeClass != null) node::class == nodeClass else true
            val matchesName = if(nodeName != null) node.name == nodeName else true

            if(matchesClass && matchesName) {
                return node
            }
            else if(node is NonTerminalNode) {
                try {
                    return node.findAnywhere(nodeClass, nodeName)
                }
                catch (e: VisitorException) { }
            }
        }
    }

    throw VisitorException("$message: no matching nodes found")
}

@Throws(VisitorException::class)
inline fun <reified T: Node> Node.findAnywhere(nodeName: String? = null): T {
    val foundNode = findAnywhere(T::class, nodeName)
    if(foundNode is T) {
        return foundNode
    }
    else {
        throw ClassCastException("Found a node, but it was not of type ${foundNode.javaClass.simpleName}")
    }
}

fun Node.hasAnywhere(nodeClass: KClass<out Node>?, nodeName: String? = null): Boolean {
    return try {
        findAnywhere(nodeClass, nodeName)
        true
    }
    catch (e: VisitorException) {
        false
    }
}


fun <T: VisitorContext> Node.visit(context: T, vararg visitors: Visitor<T>) : T {
    val iterator = DfsTreeVisitor(setOf(*visitors))
    iterator.visit(context, this)
    return context
}
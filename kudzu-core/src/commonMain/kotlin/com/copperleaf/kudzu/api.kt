package com.copperleaf.kudzu

// Exceptions
// ----------------------------------------------------------------------------------------------------------------------

class ParserException(
    message: String,
    val parser: Parser<*>,
    val input: ParserContext
) : Exception("Parse error: $message (${parser::class.simpleName}(${parser.name}) at ${input.position})")

class VisitorException(message: String) : Exception(message)

// Parsing Context
// ----------------------------------------------------------------------------------------------------------------------

data class ParserContext(
    private val input: String,
    private val sourcePosition: Int,
    private val skipWhitespace: Boolean,
    private val lineNumber: Int = 1,
    private val lineColumn: Int = 1
) {

    fun isEmpty(): Boolean {
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) {
                idx++
            }
            return (input.length - idx) == 0
        } else {
            return (input.length - sourcePosition) == 0
        }
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    val position: String
        get() {
            return "$lineNumber:$lineColumn"
        }

    fun next(): Char {
        val nextChar: Char
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) idx++
            nextChar = input[idx]
        } else {
            nextChar = input[sourcePosition]
        }

        return nextChar
    }

    fun remaining(): ParserContext {
        if (skipWhitespace) {
            var idx = sourcePosition
            var lineNumbersInc = lineNumber
            var lineColumnsInc = lineColumn
            while (idx < input.length && input[idx].isWhitespace()) {
                if (input[idx] == '\n') {
                    lineNumbersInc++
                    lineColumnsInc = 1
                } else {
                    lineColumnsInc++
                }
                idx++
            }

            return copy(sourcePosition = idx + 1, lineNumber = lineNumbersInc, lineColumn = lineColumnsInc + 1)
        } else {
            if (input[sourcePosition] == '\n') {
                return copy(sourcePosition = sourcePosition + 1, lineNumber = lineNumber + 1, lineColumn = 1)
            } else {
                return copy(sourcePosition = sourcePosition + 1, lineColumn = lineColumn + 1)
            }
        }
    }

    override fun toString(): String {
        return "ParserContext($position)"
    }

    infix fun isAfter(other: ParserContext): Boolean {
        return this.sourcePosition > other.sourcePosition
    }
}

data class NodeContext(val startContext: ParserContext, val endContext: ParserContext) {
    override fun toString(): String {
        return "NodeContext($startContext to $endContext)"
    }
}

interface VisitorContext

// Parser, Node, and Visitor base classes
// ----------------------------------------------------------------------------------------------------------------------

abstract class Node(val name: String, val context: NodeContext) {

    var parent: Node? = null

    abstract fun printAst(currentIndent: Int): String
    protected fun indent(currentIndent: Int): String {
        return (0 until currentIndent).map { " " }.joinToString(separator = "")
    }

    protected val nodeName: String
        get() {
            return if (name.isNotBlank()) ":$name" else ""
        }

    override fun toString(): String {
        return printAst(0)
    }

    fun linkTree() {
        if (this is NonTerminalNode) {
            this.children.forEach { childNode ->
                childNode.parent = this
                childNode.linkTree()
            }
        }
    }

    abstract val text: String
}

abstract class TerminalNode(name: String, context: NodeContext) : Node(name, context) {
    abstract override val text: String

    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(${this::class.simpleName}$nodeName: '$text')"
    }
}

abstract class NonTerminalNode(name: String, context: NodeContext) : Node(name, context) {
    abstract val children: List<Node>

    override val text: String
        get() = children.map { it.text }.joinToString(separator = "")

    override fun printAst(currentIndent: Int): String {
        val childrenPrinted = if (children.isNotEmpty()) {
            children
                .map { it.printAst(currentIndent + 2) }
                .joinToString(separator = "\n")
        } else {
            "${indent(currentIndent + 2)}(empty)"
        }

        return "${indent(currentIndent)}(${this::class.simpleName}$nodeName:\n" +
            childrenPrinted +
            "\n" +
            "${indent(currentIndent)})"
    }
}

abstract class Parser<NodeType : Node>(val name: String) {

    abstract fun predict(input: ParserContext): Boolean

    abstract fun parse(input: ParserContext): Pair<NodeType, ParserContext>
}

abstract class Visitor<T : VisitorContext>(val nodeMatcher: (Node) -> Boolean, val nodeName: String? = null) {

    abstract fun visit(context: T, node: Node)

    open fun onFinish(context: T) {
    }
}

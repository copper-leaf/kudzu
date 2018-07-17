package com.eden.monadik

// Exceptions
//----------------------------------------------------------------------------------------------------------------------

class ParserException(message: String, val parser: Parser, val input: ParsingContext) : Exception(message)

// Parsing Context
//----------------------------------------------------------------------------------------------------------------------

data class ParsingContext(private val input: String, private val sourcePosition: Int, private val skipWhitespace: Boolean) {

    fun isEmpty(): Boolean {
        return (input.length - sourcePosition) == 0
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    fun next(): Char {
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) idx++
            return input[idx]
        }
        else {
            return input[sourcePosition]
        }
    }

    fun remaining(): ParsingContext {
        if (skipWhitespace) {
            var idx = sourcePosition
            while (idx < input.length && input[idx].isWhitespace()) idx++
            return copy(sourcePosition = idx + 1)
        }
        else {
            return copy(sourcePosition = sourcePosition + 1)
        }
    }
}

// Parser and Node base classes
//----------------------------------------------------------------------------------------------------------------------

abstract class Node {
    abstract fun printAst(currentIndent: Int): String
    protected fun indent(currentIndent: Int): String {
        return (0 until currentIndent).map { " " }.joinToString(separator = "")
    }

    override fun toString(): String {
        return printAst(0)
    }
}

abstract class Parser {
    abstract fun parse(input: ParsingContext): Pair<Node, ParsingContext>

    fun test(input: ParsingContext): Pair<Node, ParsingContext>? {
        return try {
            parse(input)
        }
        catch (e: ParserException) {
            null
        }
    }

    fun test(input: String, skipWhitespace: Boolean = false): Pair<Node, ParsingContext>? {
        return test(ParsingContext(input, 0, skipWhitespace))
    }

}

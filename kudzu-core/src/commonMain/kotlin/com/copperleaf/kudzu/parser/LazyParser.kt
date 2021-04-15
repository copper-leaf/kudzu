package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext

/**
 * A shim to allow parsers to be mutually recursive. Unlike most parsers, the Lazy parser does not do anything on its
 * own or return its own node. At parse-time, it acts as as if it is its parser, and not like it wraps its parser.
 *
 * For simpler instantiation of parsers that recursively calls _itself_, you can initialize the LazyParser with a lambda
 * to evaluate and return a parser instead of assigning it after creation. The receiver of that lambda is the lazy
 * parser being initialized.
 *
 * Predicts true when:
 *   - its parser predicts true
 *
 * Parsing fails when:
 *   - its parser fails to parse
 */
class LazyParser<T : Node>(name: String = "") : Parser<T>(name) {

    constructor(name: String = "", creator: Parser<T>.() -> Parser<T>) : this(name) {
        this.parser = this.creator()
    }

    lateinit var parser: Parser<T>

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override fun parse(input: ParserContext): Pair<T, ParserContext> = parser.parse(input)
}

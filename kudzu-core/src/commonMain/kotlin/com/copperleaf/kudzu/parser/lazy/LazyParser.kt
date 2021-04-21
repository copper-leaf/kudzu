package com.copperleaf.kudzu.parser.lazy

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext

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
@ExperimentalStdlibApi
class LazyParser<T : Node> : Parser<T> {

    private lateinit var parser: Parser<T>

    @Suppress("UNCHECKED_CAST")
    infix fun <U : T> uses(parser: Parser<U>) {
        this.parser = parser as Parser<T>
    }

    override fun predict(input: ParserContext): Boolean = parser.predict(input)

    override val parse get() = parser.parse
}

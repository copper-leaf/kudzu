package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.node.Node

/**
 * A generic Parser that consumes some input and returns a Node as the parser result.
 *
 * Parsers are predictive; rather than fully parsing s sub-tree to determine success, parsing can be optimized in many
 * cases by checking only the next character. This is done with [Parser.predict], and should be used when determining
 * whether to continue parsing recursively with another parser, and is intentionally not a [DeepRecursiveFunction], to
 * signify that a full parser recursion should not be implemented/checked in [Parser.predict].
 *
 * Because parsers are recursive, parse trees can go very deep. For abnormal inputs this may, in rare cases, lead to a
 * StackOverflowError, which cannot be avoided with traditional recursive parser, but is not likely to be a real
 * significant issue in practical usage. But what is an issue in normal usage is trying to understand errors during
 * parsing or evaluation. In traditional recursive parsers, the normal call-stack implements recursion and cannot be
 * tail-optimized, so stack traces can be incredibly deep. Deep stacktraces provide no benefit to either the user or the
 * parser's author, and it only obscures the actual problem. Thus, parsing in Kudzu is implemented with Kotlin's
 * [DeepRecursiveFunction], which uses `suspend` functions to convert a recursive function into an iterative one, where
 * the recursion is managed on the heap rather than on the call-stack. This leads to very short stack traces which
 * surface only the actual error encountered, but omits all the intermediate frames that are not relevant to the actual
 * problem (either in syntax during parsing or logic during evaluation).
 *
 * Finally, Kudzu parsers _expect_ successful parsing. You do not need to manually check for success on each recursive
 * call; a parser either suceeded and returned a Node as its result, or it failed catastrophically and unrecoverably.
 * It is expected that a catastrophic failure in parsing is rare, because most of these kinds of parsing errors will be
 * caught by a failed [predict] call, rather than failing further down the parser-tree during [parse]. So for
 * implementing custom parsers, you should operate on the assumption that is a parser predicts true, it will parese
 * successfully as well, or else it will fail in a way that your parser cannot/should not try to handle. It is simply
 * failed syntax that should be bubbled back up to the user to correct.
 */
public interface Parser<NodeType : Node> {
    public fun predict(input: ParserContext): Boolean
    public val parse: ParseFunction<NodeType>
}

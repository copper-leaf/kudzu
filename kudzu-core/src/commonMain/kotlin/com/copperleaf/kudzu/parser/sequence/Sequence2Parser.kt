package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.sequence.Sequence2Node
import com.copperleaf.kudzu.parser.ParseFunction
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.runParser

/**
 * A version of [SequenceNParser] with fixed size that retains its generic type information for each parser in the
 * sequence. Do not create this parser directly, use the [SequenceParser] factory functions, which will select thr right
 * Seqeuence parser and type parameters for you.
 */
public class Sequence2Parser<T1 : Node, T2 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
) : Parser<Sequence2Node<T1, T2>> {
    override fun predict(input: ParserContext): Boolean {
        return p1.predict(input)
    }

    override val parse: ParseFunction<Sequence2Node<T1, T2>> = runParser { input ->
        val (n1, r1) = p1.parse.callRecursive(input)
        val (n2, r2) = p2.parse.callRecursive(r1)

        Sequence2Node(
            n1,
            n2,
            NodeContext(input, r2)
        ) to r2
    }
}

package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.NodeContext
import com.copperleaf.kudzu.node.sequence.Sequence8Node
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.ParserContext
import com.copperleaf.kudzu.parser.ParserResult

/**
 * A version of [SequenceNParser] with fixed size that retains its generic type information for each parser in the
 * sequence. Do not create this parser directly, use the [SequenceParser] factory functions, which will select thr right
 * Seqeuence parser and type parameters for you.
 */
@ExperimentalStdlibApi
class Sequence8Parser<T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node, T8 : Node>(
    private val p1: Parser<T1>,
    private val p2: Parser<T2>,
    private val p3: Parser<T3>,
    private val p4: Parser<T4>,
    private val p5: Parser<T5>,
    private val p6: Parser<T6>,
    private val p7: Parser<T7>,
    private val p8: Parser<T8>,
) : Parser<Sequence8Node<T1, T2, T3, T4, T5, T6, T7, T8>> {

    override fun predict(input: ParserContext): Boolean {
        return p1.predict(input)
    }

    override val parse =
        DeepRecursiveFunction<ParserContext, ParserResult<Sequence8Node<T1, T2, T3, T4, T5, T6, T7, T8>>> { input ->
            val (n1, r1) = p1.parse.callRecursive(input)
            val (n2, r2) = p2.parse.callRecursive(r1)
            val (n3, r3) = p3.parse.callRecursive(r2)
            val (n4, r4) = p4.parse.callRecursive(r3)
            val (n5, r5) = p5.parse.callRecursive(r4)
            val (n6, r6) = p6.parse.callRecursive(r5)
            val (n7, r7) = p7.parse.callRecursive(r6)
            val (n8, r8) = p8.parse.callRecursive(r7)

            Sequence8Node(
                n1,
                n2,
                n3,
                n4,
                n5,
                n6,
                n7,
                n8,
                NodeContext(input, r8)
            ) to r8
        }
}

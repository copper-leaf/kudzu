@file:Suppress("FunctionName", "NOTHING_TO_INLINE")
package com.copperleaf.kudzu.parser.sequence

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.sequence.Sequence2Node
import com.copperleaf.kudzu.node.sequence.Sequence3Node
import com.copperleaf.kudzu.node.sequence.Sequence4Node
import com.copperleaf.kudzu.node.sequence.Sequence5Node
import com.copperleaf.kudzu.node.sequence.Sequence6Node
import com.copperleaf.kudzu.node.sequence.Sequence7Node
import com.copperleaf.kudzu.node.sequence.Sequence8Node
import com.copperleaf.kudzu.node.sequence.Sequence9Node
import com.copperleaf.kudzu.node.sequence.SequenceNNode
import com.copperleaf.kudzu.parser.Parser

inline fun <T1 : Node, T2 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
): Parser<Sequence2Node<T1, T2>> {
    return Sequence2Parser(
        p1,
        p2,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
): Parser<Sequence3Node<T1, T2, T3>> {
    return Sequence3Parser(
        p1,
        p2,
        p3,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
): Parser<Sequence4Node<T1, T2, T3, T4>> {
    return Sequence4Parser(
        p1,
        p2,
        p3,
        p4,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
): Parser<Sequence5Node<T1, T2, T3, T4, T5>> {
    return Sequence5Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
): Parser<Sequence6Node<T1, T2, T3, T4, T5, T6>> {
    return Sequence6Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
): Parser<Sequence7Node<T1, T2, T3, T4, T5, T6, T7>> {
    return Sequence7Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
    )
}

inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node, T8 : Node> SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
    p8: Parser<T8>,
): Parser<Sequence8Node<T1, T2, T3, T4, T5, T6, T7, T8>> {
    return Sequence8Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
        p8,
    )
}

inline fun <
    T1 : Node,
    T2 : Node,
    T3 : Node,
    T4 : Node,
    T5 : Node,
    T6 : Node,
    T7 : Node,
    T8 : Node,
    T9 : Node
    > SequenceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
    p8: Parser<T8>,
    p9: Parser<T9>,
): Parser<Sequence9Node<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
    return Sequence9Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
        p8,
        p9,
    )
}

inline fun SequenceParser(
    vararg parsers: Parser<*>,
): Parser<SequenceNNode> {
    return SequenceNParser(
        *parsers
    )
}

inline fun SequenceParser(
    parsers: Collection<Parser<*>>,
): Parser<SequenceNNode> {
    return SequenceNParser(
        *parsers.toTypedArray()
    )
}

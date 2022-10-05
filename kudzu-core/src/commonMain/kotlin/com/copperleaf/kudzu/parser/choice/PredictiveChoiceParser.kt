@file:Suppress("FunctionName", "NOTHING_TO_INLINE")
package com.copperleaf.kudzu.parser.choice

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.choice.Choice2Node
import com.copperleaf.kudzu.node.choice.Choice3Node
import com.copperleaf.kudzu.node.choice.Choice4Node
import com.copperleaf.kudzu.node.choice.Choice5Node
import com.copperleaf.kudzu.node.choice.Choice6Node
import com.copperleaf.kudzu.node.choice.Choice7Node
import com.copperleaf.kudzu.node.choice.Choice8Node
import com.copperleaf.kudzu.node.choice.Choice9Node
import com.copperleaf.kudzu.node.choice.ChoiceNNode
import com.copperleaf.kudzu.parser.Parser

/* ktlint-disable max-line-length */
public inline fun <T1 : Node, T2 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
): Parser<Choice2Node<T1, T2>> {
    return Choice2Parser(
        p1,
        p2,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
): Parser<Choice3Node<T1, T2, T3>> {
    return Choice3Parser(
        p1,
        p2,
        p3,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
): Parser<Choice4Node<T1, T2, T3, T4>> {
    return Choice4Parser(
        p1,
        p2,
        p3,
        p4,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
): Parser<Choice5Node<T1, T2, T3, T4, T5>> {
    return Choice5Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
): Parser<Choice6Node<T1, T2, T3, T4, T5, T6>> {
    return Choice6Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
): Parser<Choice7Node<T1, T2, T3, T4, T5, T6, T7>> {
    return Choice7Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <T1 : Node, T2 : Node, T3 : Node, T4 : Node, T5 : Node, T6 : Node, T7 : Node, T8 : Node> PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
    p8: Parser<T8>,
): Parser<Choice8Node<T1, T2, T3, T4, T5, T6, T7, T8>> {
    return Choice8Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
        p8,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun <
    T1 : Node,
    T2 : Node,
    T3 : Node,
    T4 : Node,
    T5 : Node,
    T6 : Node,
    T7 : Node,
    T8 : Node,
    T9 : Node
    > PredictiveChoiceParser(
    p1: Parser<T1>,
    p2: Parser<T2>,
    p3: Parser<T3>,
    p4: Parser<T4>,
    p5: Parser<T5>,
    p6: Parser<T6>,
    p7: Parser<T7>,
    p8: Parser<T8>,
    p9: Parser<T9>,
): Parser<Choice9Node<T1, T2, T3, T4, T5, T6, T7, T8, T9>> {
    return Choice9Parser(
        p1,
        p2,
        p3,
        p4,
        p5,
        p6,
        p7,
        p8,
        p9,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun PredictiveChoiceParser(
    vararg parsers: Parser<*>,
): Parser<ChoiceNNode> {
    return ChoiceNParser(
        *parsers,
        strategy = ChoiceStrategy.Predictive
    )
}

public inline fun PredictiveChoiceParser(
    parsers: Collection<Parser<*>>,
): Parser<ChoiceNNode> {
    return ChoiceNParser(
        *parsers.toTypedArray(),
        strategy = ChoiceStrategy.Predictive
    )
}
/* ktlint-enable max-line-length */

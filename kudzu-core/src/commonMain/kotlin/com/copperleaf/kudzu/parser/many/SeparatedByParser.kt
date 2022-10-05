package com.copperleaf.kudzu.parser.many

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.wrapped.WrappedParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.maybe.MaybeParser
import com.copperleaf.kudzu.parser.sequence.SequenceParser

/**
 * Consumes a list of [parser], each time separated by [separator]. The resulting [ManyNode] contains the nodes parsed
 * by [parser], without the [separator] nodes ignored.
 *
 * Grammatically, it implements the following production rule:
 *
 * SeparatedByParser ::= parser (separator parser)*
 */

public class SeparatedByParser<T : Node>(
    term: Parser<T>,
    separator: Parser<*>,
) : WrappedParser<ManyNode<T>>({
    FlatMappedParser(
        SequenceParser(
            term,
            MaybeParser(
                ManyParser(
                    SequenceParser(
                        separator,
                        term
                    )
                )
            )
        )
    ) { (nodeContext, initialNode, maybeRemainingList) ->
        val initialNodeList: List<T> = listOf(initialNode)
        val remainingNodeList: List<T> = maybeRemainingList
            .node
            ?.nodeList
            ?.map { (_, _, itemNode) -> itemNode }
            ?: emptyList()

        val actualNodeList = initialNodeList + remainingNodeList

        ManyNode(
            actualNodeList,
            nodeContext
        )
    }
})

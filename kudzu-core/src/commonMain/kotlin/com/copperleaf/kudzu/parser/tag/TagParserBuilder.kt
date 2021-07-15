package com.copperleaf.kudzu.parser.tag

import com.copperleaf.kudzu.node.Node
import com.copperleaf.kudzu.node.many.ManyNode
import com.copperleaf.kudzu.node.tag.TagNode
import com.copperleaf.kudzu.parser.Parser
import com.copperleaf.kudzu.parser.choice.PredictiveChoiceParser
import com.copperleaf.kudzu.parser.lazy.LazyParser
import com.copperleaf.kudzu.parser.many.ManyParser
import com.copperleaf.kudzu.parser.mapped.FlatMappedParser
import com.copperleaf.kudzu.parser.text.ScanParser

@ExperimentalStdlibApi
@Suppress("UNCHECKED_CAST")
object TagParserBuilder {

    @Suppress("UNUSED_PARAMETER")
    internal fun checkTagsAreValid(
        tags: List<TagBuilder<*, *>>
    ) {
        // no-op
    }

    internal fun createTagScanningParser(
        tags: List<TagBuilder<*, *>>,
        allowSameTagRecursion: Boolean
    ): Parser<ManyNode<Node>> {
        val tagsWithNode: List<TagBuilder<Node, Node>> = tags as List<TagBuilder<Node, Node>>

        // convert each TagBuilder into a SimpleTagParser with a shim for the actual inner content
        val tagsAsSimpleTagsWithLazyContent: List<Pair<SimpleTagParser<*, *, *>, LazyParser<ManyNode<Node>>>> =
            tagsWithNode.map {
                val tagContentParser = LazyParser<ManyNode<Node>>()
                SimpleTagParser(
                    name = it.name,
                    openingParser = it.openingParser,
                    contentParser = tagContentParser,
                    closingParser = it.closingParser,
                ) to tagContentParser
            }

        // update each tag's content shim to recursively parse scanned text, or more tags
        tagsAsSimpleTagsWithLazyContent.forEach { (thisSimpleTagParser, lazyContentForTag) ->
            val childParsers = if (allowSameTagRecursion) {
                // sub-parsing allows self, to allow more generic tag parsers that do not strictly mandate matching open
                // and close tags at the parser level, but rather enforce it during evaluation
                tagsAsSimpleTagsWithLazyContent.map { it.first }
            } else {
                // sub-parsing excludes self, so that tags with the same start and end conditions terminate rather than
                // parsing recursively as if it were an opening tag
                tagsAsSimpleTagsWithLazyContent.map { it.first }.filterNot { it === thisSimpleTagParser }
            }
            val tagChoiceExceptItselfParser = FlatMappedParser(
                PredictiveChoiceParser(childParsers)
            ) {
                it.node as TagNode<*, *, *>
            }

            // generic text parsing scans all characters until this tag's closing parser, or until a recursive tag is
            // encountered.
            val recursiveContentForTag = FlatMappedParser(
                PredictiveChoiceParser(
                    ScanParser(
                        stoppingCondition = PredictiveChoiceParser(
                            thisSimpleTagParser.closingParser,
                            tagChoiceExceptItselfParser,
                        )
                    ),
                    tagChoiceExceptItselfParser
                )
            ) { it.node }

            // a single tag can have multiple instances of scanned content and/or tags inside it
            lazyContentForTag uses ManyParser(recursiveContentForTag)
        }

        // the main parser is just a list of tags or scanned text. The tag choice includes all tags, unlike each tag's
        // subparser which excludes itself
        val tagChoiceParser = FlatMappedParser(
            PredictiveChoiceParser(
                tagsAsSimpleTagsWithLazyContent.map { it.first }
            )
        ) {
            it.node as TagNode<*, *, *>
        }

        return ManyParser(
            FlatMappedParser(
                PredictiveChoiceParser(
                    tagChoiceParser,
                    ScanParser(tagChoiceParser)
                )
            ) { it.node }
        )
    }
}

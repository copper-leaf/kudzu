package com.copperleaf.kudzu.parser

import com.copperleaf.kudzu.Node
import com.copperleaf.kudzu.NodeContext
import com.copperleaf.kudzu.Parser
import com.copperleaf.kudzu.ParserContext
import com.copperleaf.kudzu.ParserException
import com.copperleaf.kudzu.TerminalNode
import com.copperleaf.kudzu._isDigit
import com.copperleaf.kudzu._isLetter

class CharNode(private val char: Char, name: String, context: NodeContext) : TerminalNode(name, context) {
    override val text: String get() = "$char"
}

abstract class BaseCharParser(private val escapeChar: Char? = null, name: String = "") : Parser(name) {
    override fun predict(input: ParserContext): Boolean {
        return test(input)?.second?.isAfter(input) ?: false
    }

    fun nextChar(input: ParserContext): Triple<Char, ParserContext, Boolean> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)

        var nextChar = input.next()
        var remaining = input.remaining()
        var wasEscaped = false
        if(escapeChar != null && nextChar == escapeChar) {
            if(input.remaining().isEmpty()) throw ParserException("illegal use of escape character $escapeChar", this, input)
            nextChar = remaining.next()
            remaining = remaining.remaining()
            wasEscaped = true
        }

        return Triple(nextChar, remaining, wasEscaped)
    }
}

/**
 * Consumes a single character as long as it is not the escapeChar.
 *
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class CharParser(escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, _) = nextChar(input)

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consumes a single character from the set of input characters as long as it is not the escapeChar.
 * 
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not in the whitelisted characters
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class CharInParser(private vararg val chars: Char, escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, _) = nextChar(input)

        if (!chars.contains(nextChar)) throw ParserException("char '$nextChar' not in [${chars.joinToString()}]", this, input)

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consumes a single character not in the set of input characters as long as it is not the escapeChar.
 * 
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is in the blacklisted characters
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class CharNotInParser(private vararg val chars: Char, escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, wasEscaped) = nextChar(input)

        if(!wasEscaped) {
            if (chars.contains(nextChar)) throw ParserException("char '$nextChar' cannot be in [${chars.joinToString()}]", this, input)
        }

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consumes a single whitespace character as long as it is not the escapeChar.
 * 
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not whitespace
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class WhitespaceParser(escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, _) = nextChar(input)

        if (!input.next().isWhitespace()) throw ParserException("char '${input.next()}' must be whitespace", this, input)

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consumes a single digit character as long as it is not the escapeChar.
 * 
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not a digit
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class DigitParser(escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, _) = nextChar(input)
        if (!input.next()._isDigit()) throw ParserException("char '${input.next()}' must be a digit", this, input)

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}

/**
 * Consumes a single letter character as long as it is not the escapeChar.
 * 
 * Predicts true when:
 *   - parsing is successful
 *
 * Parsing fails when:
 *   - the next input character is not a letter
 *   - there is no more input
 *   - the escape character is used without input remaining to escape
 */
class LetterParser(escapeChar: Char? = null, name: String = "") : BaseCharParser(escapeChar, name) {
    override fun parse(input: ParserContext): Pair<Node, ParserContext> {
        val (nextChar, remaining, _) = nextChar(input)
        if (!input.next()._isLetter()) throw ParserException("char '${input.next()}' must be a letter", this, input)

        return Pair(CharNode(nextChar, name, NodeContext(input, remaining)), remaining)
    }
}
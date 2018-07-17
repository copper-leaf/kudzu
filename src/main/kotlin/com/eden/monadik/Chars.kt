package com.eden.monadik

class CharNode(private val char: Char) : Node() {
    override fun printAst(currentIndent: Int): String {
        return "${indent(currentIndent)}(CharNode: $char)"
    }
}

class CharParser : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        return Pair(CharNode(input.next()), input.remaining())
    }
}

class CharInParser(private vararg val chars: Char) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        if (!chars.contains(input.next())) throw ParserException("char '${input.remaining()}' not in [${chars.joinToString()}]", this, input)

        return Pair(CharNode(input.next()), input.remaining())
    }
}

class CharNotInParser(private vararg val chars: Char) : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        if (chars.contains(input.next())) throw ParserException("char '${input.next()}' cannot be in [${chars.joinToString()}]", this, input)

        return Pair(CharNode(input.next()), input.remaining())
    }
}

class WhitespaceParser : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        if (!input.next().isWhitespace()) throw ParserException("char '${input.next()}' must be whitespace", this, input)

        return Pair(CharNode(input.next()), input.remaining())
    }
}

class DigitParser : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        if (!input.next().isDigit()) throw ParserException("char '${input.next()}' must be a digit", this, input)

        return Pair(CharNode(input.next()), input.remaining())
    }
}

class LetterParser : Parser() {
    override fun parse(input: ParsingContext): Pair<Node, ParsingContext> {
        if (input.isEmpty()) throw ParserException("nothing to parse", this, input)
        if (!input.next().isLetter()) throw ParserException("char '${input.next()}' must be a letter", this, input)

        return Pair(CharNode(input.next()), input.remaining())
    }
}
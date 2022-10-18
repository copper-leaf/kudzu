---
---

# Kudzu
---

> A monadic (I think...) recursive-descent parser combinator written in Kotlin

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/kudzu)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/kudzu-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.7.20-orange)

Kudzu is a recursive-descent parser written in Kotlin, inspired by [Parsec](https://github.com/haskell/parsec), with the
goal of immutability, simplicity, testability, and multiplatform usability. It's designed to be a simple starting place
for writing smaller parsers to evaluate relatively simple grammars for other Copper-Leaf libraries, but flexible enough
to be used for larger languages.

## Installation

```kotlin
repositories {
    mavenCentral()
}

// for plain JVM or Android projects
dependencies {
    implementation("io.github.copper-leaf:kudzu-core:{{site.version}}")
}

// for multiplatform projects
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("io.github.copper-leaf:kudzu-core:{{site.version}}")
            }
        }
    }
}
```

## Basic Usage

See tests for example usage of every included parser. A basic example of parsing and evaluating in several different
formats follows:

### Combine several small parsers into a single larger one

```kotlin
val intLiteralParser = MappedParser(
    SequenceParser(
        MaybeParser(
            CharInParser('-')
        ),
        AtLeastParser(
            DigitParser(),
            minSize = 1
        )
    )
) { it.text.toInt() }

val (node, remainingText) = intLiteralParser.parse(ParserContext.fromString("-123"))
val parsedValue: Int = node.value
expectThat(parsedValue).isEqualTo(-123)
```

### Find-and-replace structured sequences within unstructured text

```kotlin
val variableMap = mapOf(
    "asdf" to 1,
    "qwerty" to 2,
)

val patternToReplace = MappedParser(
    SequenceParser(
        CharInParser('#'),
        CharInParser('{'),
        IdentifierTokenParser(),
        CharInParser('}'),
    )
) {
    val (_, _, identifier, _) = it.children
    variableMap[identifier.text]
}

val findAndReplaceParser = ManyParser(
    PredictiveChoiceParser(
        patternToReplace,
        ScanParser(patternToReplace),
    )
)

val (node, remainingText) = findAndReplaceParser.parse(ParserContext.fromString("the value of #{asdf} is 1, but #{qwerty} is 2"))
expectThat(node.text).isEqualTo("the value of 1 is 1, but 2 is 2")
```

### Construct and evaluate expressions with custom operators

```kotlin
val expressionParser = ExpressionParser<Int>(
    termParser = { IntLiteralParser() },

    Operator.Infix(op = "+", 40) { l, r -> l + r },
    Operator.Infix(op = "-", 40) { l, r -> l - r },
    Operator.Infix(op = "*", 60) { l, r -> l * r },
    Operator.Infix(op = "/", 60) { l, r -> l / r },

    Operator.Prefix(op = "-", 80) { r -> -r },
    Operator.Infixr(op = "^", 70) { l, r -> l.toDouble().pow(r).toInt() },
)

val (node, remainingText) = expressionParser.parse(ParserContext.fromString("2 ^ ((4 - 2) * 2)", skipWhitespace = true))
val value = expressionParser.evaluator.evaluate(node)
expectThat(value).isEqualTo(16)
```

## Implementation Details

In Kudzu, a Parser is a class that extends `Parser` and implements 2 methods: `predict`, and `parse`. `predict` is a
method that checks if the parser is capable of consuming the next character, and `parse` actually implements the parsing
logic, and returns a `Node`.

There are 2 types of nodes, `TerminalNode` and `NonTerminalNode`. A `TerminalNode` typically holds onto the raw text
that was parsed from the input, while a `NonTerminalNode` holds onto other nodes. In this manner, non-terminal nodes
comprise the inner nodes of the parse tree, while terminal nodes comprise the leaves of the parse tree.

Unlike some other parsing libraries, Kudzu does not impose any restrictions on the type of node that a parser produces
to keep type parameters to a minimum and code readability to a maximum. Instead of evaluating a parse tree by working
with specific subclasses, evaluation is done simply by knowledge of whether a node is a terminal or non-terminal node,
and the name of the node. There are APIs to aid in navigating the parse tree and finding specific nodes based on their
type or their name.

The APIs are designed that each step is kept very isolated, so that the code for one step can be easily swapped out or
reused as-needed, allowing great flexibility, while keeping the code for each phase clean and easy to understand. The
general process of parsing and evaluating text with Kudzu is as follows:

```
1) String
2) ParserContext
3) Parser.parse(ParserContext) -> Pair<Node, ParserContext>
4) Node.visit([Visitor]) -> Unit
```

1. The String text that is to be parsed.
2. Provides sole API for parsers to consume individual characters. Tracks source position.
3. Each grammar has a single root rule, which is defined as a simple instance of `Parser`. The result is a single root
   Node and a ParserContext representing the text that remains unconsumed. A successful parse is expected to return an
   empty ParserContext. This root parser will recursively call the same method on other parser objects, each one
   building more nodes in the full tree and advancing the position in the ParserContext.
4. The Node can be visited by any number of Visitor objects, which recognize and evaluate distinct nodes in the parse
   tree.

### Building Parsers

While you can create custom Parser subclasses which implement your parsing logic, it is typically better to use the
built-in parser primitives provided by Kudzu. A basic example of building a Parser which recognizes either a full word
or a number follows:

```kotlin
val wordParser = ManyParser(LetterParser())
val numberParser = ManyParser(DigitParser())
val tokenParser = ChoiceParser(
        wordParser, 
        numberParser
)
val statement = ManyParser(
    SequenceParser(
        tokenParser, 
        OptionalWhitespaceParser()
    )
)

val output = statement.parse("one two 1234 asdf 56 qwerty 7890")
```

This simple grammar will match an input string like `one two 1234 asdf 56 qwerty 7890`, and demonstrates how complex
parsers can be built from smaller ones, and introduces several of the important built-in parses available. Below is a
description of some of these parser types (browse source for all available parsers)

- `LetterParser`: Consumes a single letter from the input, as recognized by Kotlin's `char.isLetter()`
- `DigitParser`: Consumes a single digit from the input, as recognized by Kotlin's `char.isDigit()`
- `ManyParser`: Takes another Parser and repeatedly consumed input from that parser, for as long as that parser is able
  to. Since it is itself a `Parser`, and it takes a `Parser` as an input, the full grammar is now recursively-defined,
  and uses a predictive* approach to determining if the next iteration of its parser can continue. You can pass any
  other Parser to this, not just character-type parsers, and so arbitrarily-complex sub-grammars can be repeated as
  needed. You'll notice that we gave the parser a `name`. This name is attached to the nodes it produces, so that when
  we evaluate the parse tree, we can look for nodes named `word` or `number`, and take different actions accordingly.
- `PredictiveChoiceParser`: Takes a list of sub-parsers, and predicatively* picks one to continue parsing with.
- `SequenceParser`: Takes a list of sub-parsers, and executes each one a single time in order.
- `OptionalWhitespaceParser`: Consumes and throws away whitespace if it exists. As the whitespace is optional, and input
  such as `two1234` would still match and be parsed correctly.
- `LazyParser`: Some grammars have production rules that themselves are recursive, such as `A := B A`. The `LazyParser`
  acts as a placeholder, simply delegating to another parser. The recursive rules must be built using these lazy
  types, since we need a concrete instance to pass to another parsers. This lazy parser allows us to create the parser
  reference, passing it around to the parsers that need it, and at a later point fill in the details of the parser as
  needed.

* A predictive grammar tests if the parser can be used by first calling its `predict` method. This method is expected to
  check if it is able to consume the next character, and if it cannot consume the next character, then the entire parser
  cannot continue. For `many`-type parsers, this predictability is used to determine when to stop iterating. For
  `choice`-type parsers, this determines which sub-production is chosen: the first sub-parser for which `predict` returns
  true will be used, and other rules will not be tested. This is to improve performance and prevent infinite recursion.

### Evaluating Parse Trees

Once the full parser has been built, and text parsed into an AST, we can now evaluate it. Evaluating an AST consists of
a `Visitor.Callback`, or a simple lambda callback. A basic example, using a fictional grammar, follows:

```kotlin
val parser = constructParser()

val (node, _) = parser.parse(input)

// simple visiting, such as finding all nodes of a particular type and not caring about the structure
node.visit { node -> 
    // do something with each node as it is entered in the tree
}

// alternatively, visit with a full set of callbacks to also introspect the parse-tree's structure
node.visit(object : Visitor.Callback {
    var depth: Int = 0
    override fun enter(node: Node) {
        depth++
    }
    override fun exit(node: Node) {
        depth--
    }
    override fun onStart() { }
    override fun onFinish() { }
})
```

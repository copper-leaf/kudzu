# Kudzu

> A monadic (I think...) recursive-descent parser combinator written in Kotlin

![GitHub release (latest by date)](https://img.shields.io/github/v/release/copper-leaf/kudzu)
![Maven Central](https://img.shields.io/maven-central/v/io.github.copper-leaf/kudzu-core)
![Kotlin Version](https://img.shields.io/badge/Kotlin-1.4.32-orange)

Kudzu is a recursive-descent parser written in Kotlin, inspired by [Parsec](https://github.com/haskell/parsec), with the
goal of immutability, simplicity, testability, and multiplatform usability. It's designed to be a simple starting place 
for writing smaller parsers to evaluate relatively simple grammars for other Copper-Leaf libraries, but flexible enough 
to be used for larger languages.

Notable features:

- Multiplatform targets: JVM, Android, JS Legacy, JS IR, iOS
- No separate lexer/parser. You really just don't need it, so Kudzu omits it
- Parser combinator structure means every piece of your grammar is a complete parser, and thus smaller parsing units can 
  be tested in isolation, but the combination of them creates the full language
- Everything in Kudzu is immutable, and thus fully thread-safe
- Built on top of [DeepRecursiveFunction](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-deep-recursive-function/#:~:text=Defines%20deep%20recursive%20function%20that,function%20use%20its%20invoke%20function.&text=The%20block%20of%20code%20defines%20the%20body%20of%20a%20recursive%20function.)
  to provide readable stacktraces and prevent StackOverflowErrors
- No complicated DSL or arcane combinator operators required, just normal, readable Kotlin classes. You don't need a 
  Ph.D. in computational linguistics or functional programming to understand a Kudzu parser
- Many useful combinators provided out of the box
    - Boolean, Int, Double literals
    - String and Character literals, with standard escaped characters (i.e. \n) and Unicode sequences (i.e. \u00A2)
    - Identifiers
    - Choice, repetition, optional higher-kinded parsers
- Parses input to an Abstract Syntax Tree (AST) and provides facilities for simplifying and/or introspecting the AST 
- Line- and column-number source tracking
- Generic expression parser with all the fixin's
    - Customizable operator precedence
    - Operators with prefix, postfix, and infix with both left- and right-recursive associativity
    - Parentheses
    - Simplify deeply-nested expressions AST to simpler representation

# Supported Platforms/Features

| Platform |
| -------- |
| Android  |
| JVM      |
| iOS      |
| JS       |

# Installation

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

# Documentation

See the [website](https://copper-leaf.github.io/kudzu/) for detailed documentation and usage instructions.

# License

Kudzu is licensed under the BSD 3-Clause License, see [LICENSE.md](https://github.com/copper-leaf/kudzu/tree/master/LICENSE.md).

# References

- [Parsec](https://github.com/haskell/parsec)
- [JParsec](https://github.com/jparsec/jparsec)

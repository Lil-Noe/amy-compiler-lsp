# ZipLex Reference Guide

ZipLex is a formally verified lexer library written in Scala. Its primary purpose is to convert a raw input string of characters (like an `.amy` file) into a sequence of structured Tokens. Unlike standard regex libraries, it is built with strict mathematical properties to ensure verifiable correctness, which is why it requires purely regular languages to define rules.

## Write a lexer with ZipLex

To write a lexer with Ziplex, you need to define a list of rules based on regular expressions. 

To define a rule, one can use the `Rule` class, that maps a Regular Expression to a TokenValueTransformation, with the following parameters:
* `regex`: the regular expression to match
* `tag`: a string tag to identify the rule which must be unique among all rules
* `isSeparator`: a boolean indicating whether the matched token is a separator (not used in Amy)
* `transformation`: a TokenValueInjection to convert between matched characters and token values

The `transformation` object must implement two functions:
- `toValue`: Sequence[Char] => TokenValue
- `toCharacters`: TokenValue => Sequence[Char]
with the property that for all `l`: 
```scala 
Sequence[Char], toCharacters(toValue(l)) == l
```

## Regex combinators

Because ZipLex doesn't support complex Scala predicates inside the regex, the following combinators are provided in this sense in [this ZiplexUtils file](../amy-compiler/src/amyc/utils/ZiplexUtils.scala).

* `'c'.r`: matches the character `c` exactly
* `"word".r`: matches the sequence of characters in the string exactly
* `r1 | r2`: matches either expression `r1` or expression `r2`
* `r1 ~ r2`: matches `r1` followed immediately by `r2`
* `anyOf("xy")`: matches any single character in the string, (*e.g.'x' or 'y'*)
* `r*`: matches zero or more repetitions of `r`
* `.+`: matches one or more repetitions of `r`
* `opt(r)`: matches `r` or nothing at all (i.e., a shorthand for `r | ε`)
* `∅`: matches the empty language
* `ε`: matches the empty string

## Character sets

Are also provided, in the same utils file, some predefined regexes and strings for common character classes.

* `AZString` and `AZ`: the string of all uppercase letters, and the corresponding regex
* `azString` and `az`: the string of all lowercase letters, and the corresponding regex
* `azAZ`: any letter, uppercase or lowercase
* `digitsString` and `digits`: the string of all digits, and the corresponding regex
* `whiteSpacesString` and `whiteSpaces`: the string of common whitespace characters, and the corresponding regex
* `specialCharsString` and `specialChars`: the string of common special characters, and the corresponding regex
* `allString` and `all`: the string of all common characters, and the corresponding regex
 

## Use example

Here is an example of how one can define a rule to match weights in kilograms using Ziplex:

```scala
case class WeightValue(text: stainless.collection.List[Char]) extends TokenValue
case object WeightValueInjection:
    def toValue(v: Sequence[Char]): TokenValue = WeightValue(v.efficientList)
    def toCharacters(t: TokenValue): Sequence[Char] = t match
        case WeightValue(text) => seqFromList(text)
        case _ => emptySeq()
    val injection: TokenValueInjection[Char] = TokenValueInjection(toValue, toCharacters)
end WeightValueInjection

val weightRegex: Regex[Char] = digits.+ ~ "kg".r
val weightRule: Rule[Char] = Rule(regex = weightRegex, 
                                    tag = "weight", 
                                    isSeparator = false, 
                                    transformation = WeightValueInjection.injection)
```
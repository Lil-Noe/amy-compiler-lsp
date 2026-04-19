# Turning a grammar into an LL(1) grammar (practical checklist)

Informally, LL(1) means: **when parsing a nonterminal A, one lookahead token decides which production (grammar rule) with left hand side A to use**. To make it LL(1), we must modify the grammar so that each choice is unambiguous with 1 token of lookahead.

## 1. Usual LL(1) blockers

Here are the usual constructs that block a grammar from being LL(1):

* **Immediate left recursion**
  `A ::= A α | β` very common, this is fixed by the standard left recursion elimination transform.
* **Indirect left recursion**
  `A ::= B …` and `B ::= A …` this is fixed by inlining and then eliminating immediate left recursion.
* **Common prefixes**
  `A ::= α β1 | α β2` this is fixed by left factoring.
* **ε (empty) alternatives mixed with non-empty ones**
  `A ::= α | ε` this requires a FIRST/FOLLOW analysis and refactoring to ensure no overlap.
* **Ambiguous "dangling else", precedence, associativity issues**
  Typically fixed by restructuring statements/expressions.

Now we explore how to fix these issues.

## 2. Eliminate left recursion

### Immediate left recursion transform

If we have:

```ebnf
A ::= A α1 | A α2 | ... | β1 | β2 | ...
```

(where each β does not start with A)

We rewrite it as:

```ebnf
A  ::= β1 A' | β2 A' | ...
A' ::= α1 A' | α2 A' | ... | ε
```

**Example (classic expression with addition operator):**

```ebnf
E ::= E + T | T
```

becomes

```ebnf
E  ::= T E'
E' ::= + T E' | ε
```

### Indirect left recursion

We pick an order to explore nonterminals: `A1, A2, ... An`. For each `Ai`:

* For every rule `Ai ::= Aj γ` where `j < i`, we **inline** all productions of `Aj` into `Ai`.
* Then we eliminate **immediate** left recursion in `Ai` as explained in the previous section.

**Example:**

Let's say we have this grammar (A is **indirectly** left-recursive through B):

```ebnf
A ::= B a | c
B ::= A b | d
```

We first change A to eliminate left recursion:

1. **A**: no `A ::= A ...` yet, so we keep:

```ebnf
A ::= B a | c
```

1. **B**: we replace `B ::= A b` by inlining **A**:

Since `A ::= B a | c`, then

```ebnf
B ::= (B a | c) b | d
  ::= B a b | c b | d
```

Now **B** has immediate left recursion: `B ::= B a b | c b | d`

We eliminate it following the immediate left recursion transform:

```ebnf
B  ::= c b B' | d B'
B' ::= a b B' | ε
```

Now we got rid of the indirect left recursion (A may still reference B), and the remaining recursion in B is now right-recursion through `B'`, which is LL(1).

## 3. Left factor to remove common prefixes

If multiple alternatives for a nonterminal start with the same symbol (terminal or nonterminal), we need to factor it out. Let's say we have:

```ebnf
A ::= α β1 | α β2 | ...
```

We need to rewrite it as:

```ebnf
A  ::= α A'
A' ::= β1 | β2 | ...
```

**Example:**

```ebnf
Stmt ::= if Expr then Stmt else Stmt
     | if Expr then Stmt
```

We factor out the common prefix `if Expr then Stmt` out:

```ebnf
Stmt ::= if Expr then Stmt OptElse
OptElse ::= else Stmt | ε
```

Then we need to verify that `else` vs FOLLOW(OptElse) don't conflict, see step 5.

## 4. Encode precedence/associativity explicitly

For expressions, we **do not** keep a single `Expr ::= Expr op Expr | ...` rule. We instead use layers:

For example, we rewrite this generic grammar for the expressions `Expr ::= Expr op Expr | ...` as:

```ebnf
Expr   ::= Term Expr'
Expr'  ::= (+ Term Expr') | (- Term Expr') | ε

Term   ::= Factor Term'
Term'  ::= (* Factor Term') | (/ Factor Term') | ε

Factor ::= ( Expr ) | id | number | ...
```

This gives left-associativity for `+,-,*,/`. We now can add more layers to add more operators with different precedence.

## 5. Empty alternatives

When we have `A ::= α | ε`, LL(1) requires that tokens that can start `$\alpha` (`FIRST(α)`) **must not overlap** with tokens that can follow A (`FOLLOW(A)`).

If there is overlap, the solution is to **refactor** the grammar so that `ε` only appears in tail nonterminals (like `E'` or `Term'` in the example in the previous section).

### Lists example

**"One-or-more" type of patterns:**

```ebnf
List1 ::= Item List1'
List1' ::= sep Item List1' | ε
```

**"Zero-or-more" type of patterns:**

```ebnf
List ::= Item List' | ε
List' ::= sep Item List' | ε
```

## 6. Validate LL(1) conditions

For each nonterminal `A` with alternatives `A ::= α1 | α2 | ...`:

* We must ensure that `FIRST(αi)` are pairwise disjoint.
* If some `αk` can derive ε, we must ensure that the intersection of `FIRST(other αi)` and `FOLLOW(A)` is empty.
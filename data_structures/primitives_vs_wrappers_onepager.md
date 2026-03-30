# Primitives vs Wrapper Types -- One-Pager

## Core Concept

Java has two parallel type systems:

**Primitives** -- raw memory values stored directly on the stack (or inline in arrays). No object overhead, no GC pressure, no null.

**Wrapper classes** -- objects that "box" a primitive value on the heap. Required wherever Java needs an object: generics, collections, nullable return types.

| Primitive | Wrapper     | Size   |
|-----------|-------------|--------|
| `int`     | `Integer`   | 4 bytes |
| `long`    | `Long`      | 8 bytes |
| `double`  | `Double`    | 8 bytes |
| `char`    | `Character` | 2 bytes |
| `boolean` | `Boolean`   | 1 byte  |
| `byte`    | `Byte`      | 1 byte  |

**Why wrappers exist:** Java generics only work with objects. `List<int>` is illegal. `List<Integer>` works because `Integer` is a full object with `hashCode()`, `equals()`, etc.

---

## Autoboxing and Unboxing

The compiler silently inserts conversions between primitives and wrappers when the types don't match. This is purely a compile-time rewrite -- no magic at runtime.

```java
// Autoboxing: int -> Integer
// Compiler rewrites this as: Integer x = Integer.valueOf(42);
Integer x = 42;

// Autoboxing when adding to a collection
List<Integer> list = new ArrayList<>();
list.add(5);   // becomes: list.add(Integer.valueOf(5));

// Unboxing: Integer -> int
// Compiler rewrites this as: int y = x.intValue();
int y = x;

// Unboxing when reading from a collection
int val = list.get(0);  // becomes: int val = list.get(0).intValue();
```

---

## The Autoboxing in Loops Problem

This is the most important pitfall. When a **wrapper type** is on the left side of `+=` inside a loop, every iteration allocates a new object:

```java
// BAD: creates ~1,000,000 Long objects on the heap
Long sum = 0L;
for (long i = 0; i < 1_000_000; i++) {
    sum += i;
    // Compiler expands to:
    // sum = Long.valueOf(sum.longValue() + i);
    //                    ^unbox            ^rebox = NEW Long object every iteration
}
```

What happens each iteration:
1. **Unbox** `sum` → `sum.longValue()` extracts the `long` primitive
2. **Add** the two `long` primitives (fast, single CPU instruction)
3. **Rebox** the result → `Long.valueOf(result)` allocates a new `Long` on the heap
4. The old `Long` object is now unreachable → garbage collected

```java
// GOOD: zero allocations, single CPU add instruction per iteration
long sum = 0L;
for (long i = 0; i < 1_000_000; i++) {
    sum += i;
}
```

---

## The `==` Trap

Wrapper objects are compared by **reference** with `==`, not by value. This causes subtle bugs:

```java
Integer a = 127;
Integer b = 127;
a == b;      // true  -- but only because JVM caches -128 to 127
a.equals(b); // true

Integer c = 128;
Integer d = 128;
c == d;      // false -- different objects, outside the cache range!
c.equals(d); // true  -- always use equals() for wrappers
```

**Why the cache?** `Integer.valueOf()` (called by autoboxing) caches the range -128 to 127. Values outside this range always allocate a new object, making `==` unreliable.

**Rule:** Never use `==` to compare wrapper types. Always use `.equals()`.

---

## NullPointerException from Unboxing

Primitives can never be null. Wrappers can. Unboxing a null wrapper throws NPE:

```java
Integer x = null;
int y = x;  // NPE: compiler expands to x.intValue(), which is null.intValue()

// Also happens in method calls:
Map<String, Integer> map = new HashMap<>();
int count = map.get("missing");  // NPE! get() returns null, unboxing null throws NPE
int count = map.getOrDefault("missing", 0);  // safe
```

---

## Conversions Cheat Sheet

```java
// int <-> String
String s = String.valueOf(42);       // "42"
String s = Integer.toString(42);     // "42"  (same thing)
int n    = Integer.parseInt("42");   // 42

// long <-> String
String s = String.valueOf(123L);     // "123"
long n   = Long.parseLong("123");    // 123

// char arithmetic (no cast needed for int result)
int idx  = 'c' - 'a';               // 2
int code = 'A';                      // 65 (implicit widening)

// int -> char (explicit cast required)
char c   = (char)('a' + 2);         // 'c'
char c   = (char) 65;               // 'A'

// Character utility methods
Character.isLetter('a');             // true
Character.isDigit('3');              // true
Character.isLetterOrDigit('_');      // false
Character.toLowerCase('A');          // 'a'
Character.toUpperCase('a');          // 'A'

// int <-> Integer (autoboxed, but explicit also works)
Integer boxed = Integer.valueOf(5);  // explicit boxing
int prim      = boxed.intValue();    // explicit unboxing

// Useful Integer static methods
Integer.MAX_VALUE                    // 2^31 - 1 = 2,147,483,647
Integer.MIN_VALUE                    // -2^31 = -2,147,483,648
Integer.compare(a, b)                // safe comparator (no overflow risk)
Integer.toBinaryString(n)            // "1010"
Integer.bitCount(n)                  // count of 1 bits
```

---

## When to Use Which

| Situation | Use | Reason |
|-----------|-----|--------|
| Local variables, loop counters | `int`, `long`, `char` | No allocation, faster |
| Array of numbers | `int[]`, `long[]` | Contiguous memory, no boxing |
| DP tables | `int[][]` | Same -- never `Integer[][]` |
| Frequency arrays | `int[26]` | Faster than `Map<Character, Integer>` |
| `List`, `Set`, `Map` | `Integer`, `Character` | Generics require objects |
| Method return that can be absent | `Integer` (nullable) | Or use `OptionalInt` |
| Comparator lambda | `Integer.compare(a, b)` | Avoids overflow from `a - b` |

---

## Common Pitfalls Summary

```java
// 1. Wrapper in a loop (allocates objects every iteration)
Long sum = 0L;
for (...) sum += i;          // BAD
long sum = 0L;
for (...) sum += i;          // GOOD

// 2. == comparison on wrappers
if (a == b) ...              // WRONG for Integer, Character, etc.
if (a.equals(b)) ...         // CORRECT

// 3. Unboxing null
int x = map.get("key");      // NPE if key absent
int x = map.getOrDefault("key", 0);  // safe

// 4. Overflow in comparator
Comparator<Integer> bad  = (a, b) -> a - b;  // overflows for large/negative values
Comparator<Integer> good = (a, b) -> Integer.compare(a, b);  // safe

// 5. int[] is NOT an Integer[]
int[] arr = {1, 2, 3};
List<int[]> list = List.of(arr);      // works -- list of one int[] element
// Arrays.asList(arr) gives List<int[]>, NOT List<Integer>
Integer[] boxed = {1, 2, 3};
List<Integer> list = Arrays.asList(boxed);  // works as expected
```

---

## Quick Reference Card

```
Primitive → Wrapper:  Integer.valueOf(n)        (autoboxed as needed)
Wrapper → Primitive:  integer.intValue()        (autounboxed as needed)
int → String:         String.valueOf(n)
String → int:         Integer.parseInt(s)
char → int:           'a' + 0  or  (int)'a'     (implicit)
int → char:           (char)('a' + n)            (explicit cast required)
char arithmetic:      'c' - 'a' = 2             (produces int, no cast)
Safe compare:         Integer.compare(a, b)      (never a - b)
Null-safe get:        map.getOrDefault(key, 0)
```

# Strings -- One-Pager

## Core Concept

In Go, a **string** is an immutable, read-only sequence of bytes. Strings are UTF-8 encoded
by default, which means a single visible character can occupy 1 to 4 bytes. This distinction
between **bytes** and **runes** (Unicode code points) is fundamental to working with Go strings
correctly.

A string header contains two fields:
- **Pointer** to the underlying byte array
- **Length** (in bytes, not characters)

**Immutability** means you cannot modify individual characters in a string. Any "modification"
creates a new string. This has critical performance implications: naive string concatenation
in a loop is O(n^2) because each concatenation allocates and copies a new string.

For coding interviews, most problems use ASCII-only input, so bytes and runes are equivalent
(1 byte = 1 character). But understanding the distinction makes you a stronger Go programmer.

---

## Time Complexity Table

| Operation                 | Time Complexity | Notes                                      |
|---------------------------|------------------|--------------------------------------------|
| Access byte by index      | O(1)             | `s[i]` returns a `byte`                    |
| Access rune by index      | O(n)             | Must iterate from start (variable width)   |
| Iterate by byte           | O(n)             | `for i := 0; i < len(s); i++`             |
| Iterate by rune           | O(n)             | `for _, r := range s`                      |
| Concatenation (`+`)       | O(n + m)         | Creates entirely new string                |
| Substring / slice         | O(1)             | `s[low:high]` shares backing memory        |
| Length (bytes)             | O(1)             | `len(s)`                                   |
| Length (runes)             | O(n)             | `utf8.RuneCountInString(s)`                |
| Comparison (`==`)          | O(n)             | Byte-by-byte comparison                    |
| `strings.Contains`        | O(n * m)         | Naive search; n = haystack, m = needle     |
| `strings.Builder.Write`   | O(1) amortized   | Like slice append                          |
| `strings.Builder.String`  | O(1)             | Returns string without copy (Go 1.10+)     |

---

## Implementation Patterns

### 1. String Basics in Go

```go
s := "hello"
fmt.Println(len(s))     // 5 -- byte count
fmt.Println(s[0])       // 104 -- byte value of 'h'
fmt.Println(string(s[0])) // "h" -- convert byte to string

// Strings are immutable
// s[0] = 'H'  // COMPILE ERROR

// Substring (shares backing memory, O(1))
sub := s[1:4]  // "ell"
```

### 2. Byte vs Rune Iteration

```go
s := "cafe\u0301"  // "cafe" + combining acute accent = "cafe" visually

// Byte iteration -- may split multi-byte characters
for i := 0; i < len(s); i++ {
    fmt.Printf("%d: %c\n", i, s[i])
}

// Rune iteration -- handles multi-byte correctly
for i, r := range s {
    fmt.Printf("%d: %c (U+%04X)\n", i, r, r)
}
```

### 3. Efficient String Building

```go
// BAD: O(n^2) -- creates a new string each iteration
result := ""
for i := 0; i < 1000; i++ {
    result += "a"  // allocates and copies every time
}

// GOOD: O(n) -- amortized O(1) per write
var b strings.Builder
b.Grow(1000)  // optional: pre-allocate capacity
for i := 0; i < 1000; i++ {
    b.WriteByte('a')
}
result := b.String()
```

### 4. Mutable String Operations via Byte/Rune Slice

```go
// Convert to byte slice for ASCII mutation
bs := []byte(s)          // O(n) copy
bs[0] = 'H'
s = string(bs)           // O(n) copy back

// Convert to rune slice for Unicode-safe mutation
rs := []rune(s)          // O(n) copy
rs[0] = 'H'
s = string(rs)           // O(n) copy back
```

### 5. Character Frequency Counting

The foundational pattern for anagram, permutation, and substring problems.

```go
// Using an array (faster, for lowercase ASCII)
func charFreq(s string) [26]int {
    var freq [26]int
    for _, ch := range s {
        freq[ch-'a']++
    }
    return freq
}

// Using a map (flexible, for any character set)
func charFreqMap(s string) map[rune]int {
    freq := make(map[rune]int)
    for _, ch := range s {
        freq[ch]++
    }
    return freq
}

// Check if two strings are anagrams
func isAnagram(s, t string) bool {
    if len(s) != len(t) {
        return false
    }
    return charFreq(s) == charFreq(t)  // arrays are comparable in Go
}
```

### 6. Two-Pointer Palindrome Check

```go
func isPalindrome(s string) bool {
    left, right := 0, len(s)-1
    for left < right {
        if s[left] != s[right] {
            return false
        }
        left++
        right--
    }
    return true
}
```

### 7. Sliding Window on Strings

```go
// Longest substring without repeating characters
func lengthOfLongestSubstring(s string) int {
    seen := make(map[byte]int)  // char -> last seen index
    maxLen := 0
    left := 0
    for right := 0; right < len(s); right++ {
        ch := s[right]
        if idx, ok := seen[ch]; ok && idx >= left {
            left = idx + 1  // shrink window past the duplicate
        }
        seen[ch] = right
        if right-left+1 > maxLen {
            maxLen = right - left + 1
        }
    }
    return maxLen
}
```

### 8. Expand Around Center (Palindromic Substrings)

```go
// Count all palindromic substrings
func countSubstrings(s string) int {
    count := 0
    for center := 0; center < len(s); center++ {
        // Odd-length palindromes
        count += expandCount(s, center, center)
        // Even-length palindromes
        count += expandCount(s, center, center+1)
    }
    return count
}

func expandCount(s string, left, right int) int {
    count := 0
    for left >= 0 && right < len(s) && s[left] == s[right] {
        count++
        left--
        right++
    }
    return count
}
```

---

## Essential Standard Library Functions

```go
import "strings"

strings.Contains(s, "sub")           // substring check
strings.HasPrefix(s, "pre")          // starts with
strings.HasSuffix(s, "suf")          // ends with
strings.Index(s, "sub")             // first occurrence index (-1 if not found)
strings.Split(s, ",")               // split into slice
strings.Join(slice, ",")            // join slice into string
strings.ToLower(s)                  // lowercase
strings.ToUpper(s)                  // uppercase
strings.TrimSpace(s)                // trim leading/trailing whitespace
strings.Replace(s, "old", "new", n) // replace first n occurrences (-1 for all)
strings.Repeat(s, n)                // repeat string n times
strings.Map(func(r rune) rune {...}, s)  // transform each rune

import "strconv"
strconv.Itoa(42)                    // int to string: "42"
strconv.Atoi("42")                  // string to int: 42, err
```

---

## When to Use

| Scenario                                         | Approach                         |
|--------------------------------------------------|----------------------------------|
| Need to modify individual characters              | Convert to `[]byte` or `[]rune` |
| Building a string incrementally                   | `strings.Builder`               |
| Character frequency analysis                      | `[26]int` array or `map[rune]int` |
| Substring search/matching                          | Sliding window or two pointers  |
| Palindrome problems                                | Two pointers or expand from center |
| Anagram problems                                  | Frequency array comparison       |
| String needs to be a map key                       | Strings work directly as map keys |

---

## Common Pitfalls

1. **Concatenation in a loop.** `s += "x"` in a loop is O(n^2). Always use `strings.Builder`
   for iterative string construction.

2. **Byte vs rune confusion.** `s[i]` returns a `byte`, not a character. For multi-byte
   Unicode characters, this gives you a partial byte. Use `range` to iterate by rune.

3. **`len(s)` returns byte count, not character count.** For `"cafe"` with an accent mark,
   `len(s)` may be 6 while there are only 5 runes.

4. **Comparing with `==` is O(n).** String comparison is not constant time. For frequent
   comparisons, consider hashing.

5. **Forgetting that `[]byte(s)` and `string(bs)` both copy.** Converting between string and
   byte slice creates a copy each time. Minimize conversions in hot loops.

6. **Assuming ASCII.** Interview problems usually specify "lowercase English letters", but
   always confirm. If the input can contain Unicode, use rune-based operations.

---

## Interview Relevance

String problems are ubiquitous in coding interviews. Key pattern mapping:

| Pattern                  | Signal Words                                        | Example Problems                    |
|--------------------------|-----------------------------------------------------|-------------------------------------|
| Frequency Count          | "anagram", "permutation", "character count"          | Valid Anagram, Group Anagrams       |
| Sliding Window           | "substring", "window", "longest/shortest"            | Longest Substring Without Repeat    |
| Two Pointers             | "palindrome", "reverse", "in-place"                  | Valid Palindrome, Reverse String    |
| Expand from Center       | "palindromic substring", "longest palindrome"        | Longest Palindromic Substring       |
| Hash Map + String        | "first unique", "pattern matching"                   | First Unique Character              |
| String Building          | "encode", "decode", "serialize"                      | Encode and Decode Strings           |

---

## Practice Problems

| #  | Problem                                  | Difficulty | Key Pattern                  | LeetCode # |
|----|------------------------------------------|------------|------------------------------|------------|
| 1  | Valid Anagram                             | Easy       | Frequency array comparison   | 242        |
| 2  | Valid Palindrome                          | Easy       | Two pointers + char filtering| 125        |
| 3  | Longest Substring Without Repeating Chars | Medium     | Sliding window + hash map    | 3          |
| 4  | Longest Repeating Character Replacement   | Medium     | Sliding window + frequency   | 424        |
| 5  | Group Anagrams                            | Medium     | Frequency key + hash map     | 49         |
| 6  | Palindromic Substrings                    | Medium     | Expand around center         | 647        |
| 7  | Minimum Window Substring                  | Hard       | Sliding window + two maps    | 76         |

Start with 1-2 for basics, 3-5 for core interview patterns, and 6-7 for harder challenges.

---

## Quick Reference Card

```
Length:       len(s)                          // bytes
Rune count:  utf8.RuneCountInString(s)       // characters
Index byte:  s[i]                            // returns byte
Iterate:     for _, r := range s { ... }     // by rune
Substring:   s[low:high]                     // half-open interval
Build:       var b strings.Builder; b.WriteString("x"); b.String()
To bytes:    bs := []byte(s)                 // mutable copy
To runes:    rs := []rune(s)                 // Unicode-safe copy
To string:   string(bs) or string(rs)        // copy back
Frequency:   var freq [26]int; freq[ch-'a']++
Compare:     s1 == s2                        // O(n)
```

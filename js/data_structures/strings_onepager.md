# Strings -- One-Pager

## Core Concept

In JavaScript, a **string** is an immutable sequence of UTF-16 code units. Strings are
primitive values -- any "modification" creates a new string. This has critical performance
implications: naive string concatenation in a loop is O(n^2) because each concatenation
allocates a new string.

**Key facts:**
- **Immutable:** You cannot modify individual characters. `s[0] = 'H'` silently fails (or throws in strict mode).
- **UTF-16 internally:** Most characters occupy 1 code unit, but emoji and rare characters (astral plane) occupy 2 code units (surrogate pairs).
- **`s.length`** returns the number of **code units**, not visible characters.

For coding interviews, most problems use ASCII-only input, so `s.length` and `s[i]` work
correctly. But understanding UTF-16 makes you a stronger JS developer.

---

## Time Complexity Table

| Operation                   | Time Complexity | Notes                                      |
|-----------------------------|------------------|--------------------------------------------|
| Access character by index   | O(1)             | `s[i]` or `s.charAt(i)`                   |
| Iterate by character        | O(n)             | `for (const ch of s)` (handles surrogate pairs) |
| Concatenation (`+`)         | O(n + m)         | Creates entirely new string                |
| Substring / slice           | O(k)             | `s.slice(low, high)` -- creates new string |
| Length                       | O(1)             | `s.length`                                 |
| Comparison (`===`)           | O(n)             | Character-by-character comparison          |
| `s.includes(sub)`           | O(n * m)         | Naive search; n = haystack, m = needle     |
| Array join                   | O(n)             | `arr.join("")` -- efficient string building |
| `s.split("")`               | O(n)             | Creates array of characters                |

---

## Implementation Patterns

### 1. String Basics

```javascript
const s = "hello";
console.log(s.length);   // 5
console.log(s[0]);       // "h"
console.log(s.charAt(0)); // "h"
console.log(s.charCodeAt(0)); // 104 -- Unicode code point of 'h'

// Strings are immutable
// s[0] = 'H';  // silently fails (no error, but no effect)

// Substring (creates a new string)
const sub = s.slice(1, 4);  // "ell"
```

### 2. Character Iteration

```javascript
const s = "hello";

// Index-based iteration (works for ASCII)
for (let i = 0; i < s.length; i++) {
    console.log(i, s[i]);
}

// for...of iteration (handles Unicode correctly)
for (const ch of s) {
    console.log(ch);
}

// Spread into array (for mutation)
const chars = [...s];  // ["h", "e", "l", "l", "o"]
```

### 3. Efficient String Building

```javascript
// BAD: O(n^2) -- creates a new string each iteration
let result = "";
for (let i = 0; i < 1000; i++) {
    result += "a";  // allocates new string every time
}

// GOOD: O(n) -- collect in array, join once
const parts = [];
for (let i = 0; i < 1000; i++) {
    parts.push("a");
}
const result = parts.join("");

// Also GOOD: template literals for simple cases
const result = `${firstName} ${lastName}`;
```

### 4. Mutable String Operations via Array

```javascript
// Convert to array for mutation
const chars = [...s];   // or s.split("")
chars[0] = "H";
const modified = chars.join("");  // "Hello"

// Reverse a string
const reversed = [...s].reverse().join("");
```

### 5. Character Frequency Counting

The foundational pattern for anagram, permutation, and substring problems.

```javascript
// Using an array (faster, for lowercase ASCII)
function charFreq(s) {
    const freq = new Array(26).fill(0);
    for (const ch of s) {
        freq[ch.charCodeAt(0) - 97]++;
    }
    return freq;
}

// Using an object (flexible, for any character set)
function charFreqMap(s) {
    const freq = {};
    for (const ch of s) {
        freq[ch] = (freq[ch] || 0) + 1;
    }
    return freq;
}

// Check if two strings are anagrams
function isAnagram(s, t) {
    if (s.length !== t.length) return false;
    const sFreq = charFreq(s);
    const tFreq = charFreq(t);
    return sFreq.every((val, i) => val === tFreq[i]);
}
```

### 6. Two-Pointer Palindrome Check

```javascript
function isPalindrome(s) {
    let left = 0, right = s.length - 1;
    while (left < right) {
        if (s[left] !== s[right]) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

### 7. Sliding Window on Strings

```javascript
// Longest substring without repeating characters
function lengthOfLongestSubstring(s) {
    const seen = new Map();  // char -> last seen index
    let maxLen = 0;
    let left = 0;
    for (let right = 0; right < s.length; right++) {
        const ch = s[right];
        if (seen.has(ch) && seen.get(ch) >= left) {
            left = seen.get(ch) + 1;  // shrink window past the duplicate
        }
        seen.set(ch, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

### 8. Expand Around Center (Palindromic Substrings)

```javascript
// Count all palindromic substrings
function countSubstrings(s) {
    let count = 0;
    for (let center = 0; center < s.length; center++) {
        // Odd-length palindromes
        count += expandCount(s, center, center);
        // Even-length palindromes
        count += expandCount(s, center, center + 1);
    }
    return count;
}

function expandCount(s, left, right) {
    let count = 0;
    while (left >= 0 && right < s.length && s[left] === s[right]) {
        count++;
        left--;
        right++;
    }
    return count;
}
```

---

## Essential Built-in Methods

```javascript
s.includes("sub")              // substring check
s.startsWith("pre")            // starts with
s.endsWith("suf")              // ends with
s.indexOf("sub")               // first occurrence index (-1 if not found)
s.split(",")                   // split into array
arr.join(",")                  // join array into string
s.toLowerCase()                // lowercase
s.toUpperCase()                // uppercase
s.trim()                       // trim leading/trailing whitespace
s.replace("old", "new")        // replace first occurrence
s.replaceAll("old", "new")     // replace all occurrences
s.repeat(n)                    // repeat string n times
s.padStart(n, "0")             // pad from start to length n

// Conversions
String(42)                     // number to string: "42"
parseInt("42")                 // string to int: 42
Number("42")                   // string to number: 42
String.fromCharCode(104)       // code to char: "h"
"h".charCodeAt(0)              // char to code: 104
```

---

## When to Use

| Scenario                                         | Approach                         |
|--------------------------------------------------|----------------------------------|
| Need to modify individual characters              | Convert to array with `[...s]`  |
| Building a string incrementally                   | Push to array, `join("")` at end |
| Character frequency analysis                      | `[26]` array or object          |
| Substring search/matching                          | Sliding window or two pointers  |
| Palindrome problems                                | Two pointers or expand from center |
| Anagram problems                                  | Frequency array comparison       |
| String needs to be a map key                       | Strings work directly as keys   |

---

## Common Pitfalls

1. **Concatenation in a loop.** `s += "x"` in a loop is O(n^2). Collect parts in an array
   and use `.join("")` at the end.

2. **`s.length` counts code units, not characters.** For emoji: `"😀".length === 2` because
   it uses a surrogate pair. Use `[..."😀"].length` for character count.

3. **Comparing with `==` vs `===`.** Always use `===` for string comparison to avoid type
   coercion surprises.

4. **`split("")` vs spread `[...s]`.** Both create character arrays, but `[...s]` handles
   Unicode surrogate pairs correctly. `"😀".split("")` gives `["\uD83D", "\uDE00"]` (broken),
   while `[..."😀"]` gives `["😀"]`.

5. **`charCodeAt` returns UTF-16 code units.** For most interview problems (lowercase English),
   `s.charCodeAt(i) - 97` gives 0-25. For full Unicode, use `s.codePointAt(i)`.

6. **Strings are immutable.** Every operation that appears to modify a string actually creates
   a new one. This matters for performance in tight loops.

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
Length:       s.length                           // code units
Index:       s[i]  or  s.charAt(i)             // returns character
Code:        s.charCodeAt(i)                   // returns number
Iterate:     for (const ch of s) { ... }       // Unicode-safe
To array:    [...s]  or  s.split("")           // [...s] handles Unicode
Build:       parts.push("x"); parts.join("")   // O(n) total
Reverse:     [...s].reverse().join("")
Frequency:   const freq = new Array(26).fill(0); freq[ch.charCodeAt(0) - 97]++
Compare:     s1 === s2                         // O(n), strict equality
Substring:   s.slice(low, high)                // half-open interval
```

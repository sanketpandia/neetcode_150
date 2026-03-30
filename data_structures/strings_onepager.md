# Strings -- One-Pager

## Core Concept

In Java, a **String** is an immutable sequence of `char` values (UTF-16 encoded). You cannot modify individual characters in a String -- any "modification" creates a new String. This has critical performance implications: naive string concatenation in a loop is O(n²) because each `+` allocates and copies a new String.

The key types:
- **`String`** -- immutable, safe to share, use for constant text
- **`StringBuilder`** -- mutable, O(1) amortized append, use when building strings
- **`char`** -- primitive 16-bit Unicode code unit (use `s.charAt(i)`, not `s[i]`)

For most interview problems, input is lowercase ASCII (`'a'-'z'`), so a `char` maps 1-to-1 to a character. Character arithmetic works: `ch - 'a'` gives an index 0–25.

---

## Time Complexity Table

| Operation                    | Time Complexity  | Notes                                              |
|------------------------------|------------------|----------------------------------------------------|
| Access char by index         | O(1)             | `s.charAt(i)` -- no boxing                        |
| Length                       | O(1)             | `s.length()`                                       |
| Concatenation (`+`)          | O(n + m)         | Creates a new String -- avoid in loops             |
| `StringBuilder.append`       | O(1) amortized   | Backed by resizable char array                     |
| `StringBuilder.toString`     | O(n)             | Copies internal buffer into a new String           |
| Substring                    | O(n)             | `s.substring(from, to)` copies characters (Java 7+)|
| Comparison (`equals`)        | O(n)             | Never use `==` for String equality                 |
| `contains` / `indexOf`       | O(n * m)         | n = haystack length, m = needle length             |
| `toCharArray`                | O(n)             | Copies all chars into a new array                  |

---

## Implementation Patterns

### 1. String Basics in Java

```java
String s = "hello";
int len = s.length();           // 5
char c = s.charAt(0);           // 'h'
String sub = s.substring(1, 4); // "ell" -- [1, 4) exclusive end

// Strings are immutable -- this is a NEW string:
String upper = s.toUpperCase();

// ALWAYS use .equals() for comparison, not ==
String a = "foo", b = "foo";
a == b;         // unreliable (reference comparison)
a.equals(b);    // true (content comparison)
```

### 2. Efficient String Building

```java
// BAD: O(n^2) -- creates a new String on every iteration
String result = "";
for (int i = 0; i < 1000; i++) {
    result += "a";  // allocates and copies every time
}

// GOOD: O(n) -- amortized O(1) per append
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append('a');
}
String result = sb.toString();
```

### 3. Mutable String Operations via char[]

```java
// Convert to char array for in-place mutation
char[] chars = s.toCharArray();   // O(n) copy
chars[0] = 'H';
String modified = new String(chars);  // O(n) copy back

// Reverse a string
char[] arr = s.toCharArray();
int left = 0, right = arr.length - 1;
while (left < right) {
    char tmp = arr[left];
    arr[left++] = arr[right];
    arr[right--] = tmp;
}
String reversed = new String(arr);
```

### 4. Character Frequency Counting

The foundational pattern for anagram, permutation, and substring problems.

```java
// Array approach (faster, for lowercase ASCII a-z)
int[] freq(String s) {
    int[] freq = new int[26];
    for (char c : s.toCharArray()) {
        freq[c - 'a']++;
    }
    return freq;
}

// Map approach (flexible, any character set)
Map<Character, Integer> freqMap(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) {
        freq.merge(c, 1, Integer::sum);  // or: freq.put(c, freq.getOrDefault(c, 0) + 1)
    }
    return freq;
}

// Check if two strings are anagrams
boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    int[] freq = new int[26];
    for (char c : s.toCharArray()) freq[c - 'a']++;
    for (char c : t.toCharArray()) freq[c - 'a']--;
    for (int f : freq) if (f != 0) return false;
    return true;
}
```

### 5. Two-Pointer Palindrome Check

```java
boolean isPalindrome(String s) {
    int left = 0, right = s.length() - 1;
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) return false;
        left++;
        right--;
    }
    return true;
}
```

### 6. Sliding Window on Strings

```java
// Longest substring without repeating characters
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> seen = new HashMap<>(); // char -> last seen index
    int maxLen = 0, left = 0;
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (seen.containsKey(c) && seen.get(c) >= left) {
            left = seen.get(c) + 1;  // shrink window past duplicate
        }
        seen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

### 7. Expand Around Center (Palindromic Substrings)

```java
int countSubstrings(String s) {
    int count = 0;
    for (int center = 0; center < s.length(); center++) {
        count += expand(s, center, center);      // odd-length
        count += expand(s, center, center + 1);  // even-length
    }
    return count;
}

int expand(String s, int left, int right) {
    int count = 0;
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;
        left--;
        right++;
    }
    return count;
}
```

---

## Essential Standard Library Methods

```java
// String methods
s.length()                      // length
s.charAt(i)                     // char at index
s.substring(from, to)           // [from, to) exclusive end
s.indexOf("sub")                // first occurrence (-1 if not found)
s.contains("sub")               // boolean
s.startsWith("pre")             // boolean
s.endsWith("suf")               // boolean
s.toLowerCase() / toUpperCase() // new String
s.trim()                        // strip leading/trailing whitespace
s.replace("old", "new")         // replace all occurrences
s.split(",")                    // returns String[]
String.join(",", list)          // join collection
s.toCharArray()                 // char[]
s.equals(other)                 // content equality (NOT ==)
s.compareTo(other)              // lexicographic comparison

// Conversions
String.valueOf(42)              // int to String: "42"
Integer.parseInt("42")          // String to int: 42
Character.isLetter(c)           // true if letter
Character.isDigit(c)            // true if digit
Character.toLowerCase(c)        // lowercase char
```

---

## When to Use

| Scenario                                         | Approach                              |
|--------------------------------------------------|---------------------------------------|
| Need to modify individual characters              | Convert to `char[]`, modify, convert back |
| Building a string incrementally                   | `StringBuilder`                       |
| Character frequency analysis                      | `int[26]` for a-z, `Map<Character, Integer>` for arbitrary |
| Substring search/matching                          | Sliding window or two pointers        |
| Palindrome problems                                | Two pointers or expand from center    |
| Anagram problems                                  | Frequency array comparison            |
| Need string as a map key                          | Strings work directly as map keys     |

---

## Common Pitfalls

1. **Concatenation in a loop.** `result += "x"` in a loop is O(n²). Always use `StringBuilder` for iterative string construction.

2. **Using `==` to compare Strings.** `==` compares references, not content. Use `.equals()` always.

3. **`s.substring(from, to)` is O(n) in Java 7+.** Unlike Go, Java substrings copy the underlying array (changed in Java 7u6 to avoid memory leaks). Don't assume it's O(1).

4. **`s.charAt(i)` returns `char`, not `int`.** Be careful when doing arithmetic: cast explicitly if needed, or compare with character literals (`'a'`, `'z'`).

5. **`Arrays.asList()` with `String[]` is fixed-size.** Wrap in `new ArrayList<>()` for a mutable list.

6. **`String.split()` uses regex.** `s.split(".")` splits on nothing (`.` is regex wildcard). Use `s.split("\\.")` to split on a literal dot.

---

## Interview Relevance

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
Length:       s.length()
Access:       s.charAt(i)                      // returns char
Substring:    s.substring(from, to)            // [from, to) exclusive end -- O(n) copy
Compare:      s.equals(other)                  // NEVER use ==
Build:        StringBuilder sb = new StringBuilder(); sb.append("x"); sb.toString()
To chars:     char[] arr = s.toCharArray()     // mutable copy
From chars:   new String(arr)
Frequency:    int[] freq = new int[26]; freq[c - 'a']++
Int↔String:   String.valueOf(n) / Integer.parseInt(s)
```

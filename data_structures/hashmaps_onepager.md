# Hash Maps / Hash Sets -- One-Pager

## Core Concept

A **hash map** (also called hash table or dictionary) stores key-value pairs and provides
O(1) average-time lookup, insertion, and deletion by computing a hash of the key to determine
its storage location in an internal array of "buckets."

A **hash set** is a hash map where you only care about keys (existence), not values.

In Go, the built-in `map` type is a hash map. There is no built-in set type -- you simulate
sets using `map[T]struct{}` or `map[T]bool`.

**How hashing works:**
1. A hash function converts the key into an integer (hash code).
2. The hash code is mapped to a bucket index (typically via modulo).
3. Collisions (two keys mapping to the same bucket) are handled by chaining (linked list in
   each bucket) or open addressing.

Go's map uses a variant of hash tables with buckets that hold up to 8 key-value pairs each,
with overflow buckets for collision handling.

---

## Time Complexity Table

| Operation          | Average | Worst Case | Notes                                |
|--------------------|---------|------------|--------------------------------------|
| Insert / Update    | O(1)    | O(n)       | Worst case: all keys collide         |
| Lookup             | O(1)    | O(n)       | Same                                 |
| Delete             | O(1)    | O(n)       | Same                                 |
| Check existence    | O(1)    | O(n)       | Comma-ok idiom: `val, ok := m[key]`  |
| Iterate all        | O(n)    | O(n)       | Order is randomized in Go            |
| Get length         | O(1)    | O(1)       | `len(m)`                             |

**Space Complexity:** O(n) where n is the number of key-value pairs stored.

**Important:** The O(n) worst case is theoretical (all keys hash to the same bucket). In
practice with Go's built-in map, the hash function is well-distributed, and you can treat
all operations as O(1) for interview analysis.

---

## Implementation Patterns

### 1. Map Basics

```go
// Create
m := make(map[string]int)           // empty map
m := map[string]int{"a": 1, "b": 2} // literal initialization

// Insert / Update
m["alice"] = 90
m["alice"] = 95  // update existing key

// Lookup with existence check (comma-ok idiom)
if val, ok := m["alice"]; ok {
    fmt.Println("Found:", val)
} else {
    fmt.Println("Not found")
}

// Delete
delete(m, "alice")  // no-op if key doesn't exist

// Iterate (order is NOT guaranteed)
for key, val := range m {
    fmt.Println(key, val)
}

// Length
fmt.Println(len(m))
```

### 2. Hash Set Pattern

```go
// Preferred: map[T]struct{} -- zero bytes per value
seen := make(map[int]struct{})
seen[42] = struct{}{}
if _, exists := seen[42]; exists {
    // 42 is in the set
}

// Alternative: map[T]bool -- slightly cleaner syntax
seen := make(map[int]bool)
seen[42] = true
if seen[42] {  // missing keys return false (zero value)
    // 42 is in the set
}
```

`map[T]struct{}` is preferred for performance: `struct{}` occupies zero bytes, while `bool`
occupies 1 byte per entry. For most interview problems, either works fine.

### 3. Frequency Counting

The bread-and-butter hash map pattern. Count occurrences of each element.

```go
func frequencyCount(nums []int) map[int]int {
    freq := make(map[int]int)
    for _, num := range nums {
        freq[num]++  // zero value for int is 0, so this works out of the box
    }
    return freq
}

// Character frequency for strings
func charFreq(s string) map[rune]int {
    freq := make(map[rune]int)
    for _, ch := range s {
        freq[ch]++
    }
    return freq
}
```

### 4. Two-Sum Pattern (Complement Lookup)

Store values you have seen so far; check if the complement exists.

```go
func twoSum(nums []int, target int) []int {
    seen := make(map[int]int)  // value -> index
    for i, num := range nums {
        complement := target - num
        if j, ok := seen[complement]; ok {
            return []int{j, i}
        }
        seen[num] = i
    }
    return nil
}
```

This transforms an O(n^2) brute-force into O(n) by trading space for time.

### 5. Grouping Pattern

Group elements by a computed key. Classic example: group anagrams.

```go
func groupAnagrams(strs []string) [][]string {
    groups := make(map[[26]int][]string)
    for _, s := range strs {
        var key [26]int
        for _, ch := range s {
            key[ch-'a']++
        }
        groups[key] = append(groups[key], s)
    }
    result := make([][]string, 0, len(groups))
    for _, group := range groups {
        result = append(result, group)
    }
    return result
}
```

**Key insight:** Arrays like `[26]int` are valid map keys in Go (they are comparable), but
slices are NOT. This is why we use `[26]int` instead of `[]int` as the frequency key.

### 6. Deduplication Pattern

Remove duplicates using a set.

```go
func removeDuplicates(nums []int) []int {
    seen := make(map[int]struct{})
    result := make([]int, 0)
    for _, num := range nums {
        if _, exists := seen[num]; !exists {
            seen[num] = struct{}{}
            result = append(result, num)
        }
    }
    return result
}
```

### 7. Index Mapping Pattern

Map elements to their indices or positions for O(1) lookups later.

```go
// Find the first non-repeating character
func firstUniqChar(s string) int {
    freq := make(map[rune]int)
    for _, ch := range s {
        freq[ch]++
    }
    for i, ch := range s {
        if freq[ch] == 1 {
            return i
        }
    }
    return -1
}
```

### 8. Sliding Window + Map

Combine hash map with a sliding window for substring/subarray constraint problems.

```go
// Check if s2 contains a permutation of s1
func checkInclusion(s1, s2 string) bool {
    if len(s1) > len(s2) {
        return false
    }
    var s1Freq, windowFreq [26]int
    for _, ch := range s1 {
        s1Freq[ch-'a']++
    }
    for i := 0; i < len(s2); i++ {
        windowFreq[s2[i]-'a']++
        if i >= len(s1) {
            windowFreq[s2[i-len(s1)]-'a']--
        }
        if s1Freq == windowFreq {
            return true
        }
    }
    return false
}
```

---

## What Can Be a Map Key?

Go requires map keys to be **comparable** types. This includes:

| Type               | Valid Key? | Notes                                      |
|--------------------|------------|---------------------------------------------|
| `int`, `float`, etc.| Yes       | All numeric types                           |
| `string`           | Yes        | Very common                                 |
| `bool`             | Yes        | Rarely useful                               |
| `[N]int` (array)   | Yes        | Fixed-size arrays are comparable            |
| `struct` (no slices)| Yes       | All fields must be comparable               |
| `[]int` (slice)    | **No**     | Slices are not comparable                   |
| `map[K]V`          | **No**     | Maps are not comparable                     |
| `func`             | **No**     | Functions are not comparable                |

**Workaround for slice keys:** Convert to a string (`fmt.Sprint(slice)`) or use a fixed-size
array (`[26]int` for character frequencies).

---

## When to Use

| Scenario                                          | Use Hash Map? |
|---------------------------------------------------|---------------|
| Need O(1) lookup by key/value                     | Yes           |
| Frequency counting                                | Yes           |
| Checking for duplicates                           | Yes (hash set)|
| Need to find complement/pair in O(1)              | Yes           |
| Grouping elements by a property                   | Yes           |
| Need ordered iteration                            | No -- use sorted slice or tree |
| Need ordered keys (min/max)                       | No -- use heap or BST |
| Concurrent access without locking                 | No -- use `sync.Map` or mutex |

---

## Common Pitfalls

1. **Iteration order is random.** Go intentionally randomizes map iteration order. Never
   depend on insertion order. If you need ordered output, extract keys into a slice and sort.

2. **Zero value confusion.** Accessing a missing key returns the zero value (`0` for int,
   `""` for string, `false` for bool). Always use the comma-ok idiom to distinguish
   "key exists with zero value" from "key doesn't exist."

   ```go
   // BAD: can't tell if key exists with value 0
   val := m["key"]

   // GOOD: explicitly check existence
   val, ok := m["key"]
   ```

3. **Slices cannot be map keys.** Use arrays (`[26]int`), strings, or structs instead.

4. **Maps are not safe for concurrent use.** Concurrent reads are fine, but concurrent
   read+write causes a runtime panic. Use `sync.RWMutex` or `sync.Map` for concurrent access.

5. **Nil map panic.** Reading from a nil map returns the zero value (safe), but writing to
   a nil map panics. Always initialize with `make(map[K]V)` or a literal.

   ```go
   var m map[string]int  // nil map
   _ = m["key"]          // OK: returns 0
   m["key"] = 1          // PANIC: assignment to entry in nil map
   ```

6. **Memory not released on delete.** Deleting keys from a map does not shrink the
   underlying memory. For maps that grow large then shrink, create a new map and copy
   the remaining entries.

---

## Interview Relevance

Hash maps are the single most versatile data structure for coding interviews. Pattern mapping:

| Pattern               | Signal Words                                     | Example Problems                 |
|-----------------------|--------------------------------------------------|----------------------------------|
| Frequency Count       | "count", "frequency", "most common"              | Top K Frequent Elements          |
| Complement Lookup     | "two sum", "pair", "target"                       | Two Sum, 4Sum II                 |
| Grouping              | "group", "anagram", "categorize"                  | Group Anagrams                   |
| Deduplication         | "duplicate", "unique", "distinct"                 | Contains Duplicate               |
| Index Tracking        | "first occurrence", "last position"               | First Unique Character           |
| Sliding Window + Map  | "substring", "permutation", "window"              | Minimum Window Substring         |
| Design                | "implement", "design", "cache"                    | LRU Cache, Insert Delete GetRandom|

**Interview tip:** When brute force is O(n^2) because of a nested lookup, ask yourself: "Can
I replace the inner loop with a hash map lookup?" The answer is usually yes.

---

## Practice Problems

| #  | Problem                         | Difficulty | Key Pattern                  | LeetCode # |
|----|---------------------------------|------------|------------------------------|------------|
| 1  | Contains Duplicate              | Easy       | Hash set                     | 217        |
| 2  | Two Sum                         | Easy       | Complement lookup            | 1          |
| 3  | Valid Anagram                   | Easy       | Frequency comparison         | 242        |
| 4  | Group Anagrams                  | Medium     | Grouping by frequency key    | 49         |
| 5  | Top K Frequent Elements         | Medium     | Frequency map + bucket sort  | 347        |
| 6  | Longest Consecutive Sequence    | Medium     | Hash set + sequence building | 128        |
| 7  | Subarray Sum Equals K           | Medium     | Prefix sum + hash map        | 560        |

Start with 1-3 for fundamentals, then 4-6 for pattern mastery. Problem 7 combines prefix
sums with hash maps -- a powerful interview technique.

---

## Quick Reference Card

```
Create:     m := make(map[string]int)
Insert:     m[key] = val
Lookup:     val, ok := m[key]
Delete:     delete(m, key)
Length:     len(m)
Iterate:    for k, v := range m { ... }
Set:        seen := make(map[int]struct{})
Set add:    seen[val] = struct{}{}
Set check:  _, exists := seen[val]
Freq count: freq[item]++              // zero value is 0
Keys:       for k := range m { keys = append(keys, k) }
```

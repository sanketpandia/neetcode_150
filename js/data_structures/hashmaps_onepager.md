# Hash Maps / Hash Sets -- One-Pager

## Core Concept

A **hash map** (also called hash table or dictionary) stores key-value pairs and provides
O(1) average-time lookup, insertion, and deletion by computing a hash of the key to determine
its storage location.

A **hash set** is a hash map where you only care about keys (existence), not values.

In JavaScript, you have two options:
- **`Map`** -- true hash map that accepts any key type (objects, functions, primitives)
- **Plain objects `{}`** -- hash map with string/Symbol keys only

For sets, JavaScript provides the built-in **`Set`** class.

**How hashing works:**
1. A hash function converts the key into an integer (hash code).
2. The hash code is mapped to a bucket index (typically via modulo).
3. Collisions (two keys mapping to the same bucket) are handled by chaining or open addressing.

**Map vs Object -- when to use which:**
- Use `Map` when keys are dynamic, non-string, or when you need `.size`
- Use plain objects when keys are fixed string literals (like config, frequency maps with string keys)
- For interviews, both work -- `Map` is technically more correct, but objects are often faster to write

---

## Time Complexity Table

| Operation          | Average | Worst Case | Notes                                       |
|--------------------|---------|------------|---------------------------------------------|
| Insert / Update    | O(1)    | O(n)       | `map.set(key, val)` or `obj[key] = val`     |
| Lookup             | O(1)    | O(n)       | `map.get(key)` or `obj[key]`                |
| Delete             | O(1)    | O(n)       | `map.delete(key)` or `delete obj[key]`      |
| Check existence    | O(1)    | O(n)       | `map.has(key)` or `key in obj`              |
| Iterate all        | O(n)    | O(n)       | `Map` preserves insertion order             |
| Get size           | O(1)    | O(1)       | `map.size` or `Object.keys(obj).length`     |

**Space Complexity:** O(n) where n is the number of key-value pairs stored.

**Important:** The O(n) worst case is theoretical (all keys hash to the same bucket). In
practice, you can treat all operations as O(1) for interview analysis.

---

## Implementation Patterns

### 1. Map Basics

```javascript
// Create
const map = new Map();
const map = new Map([["a", 1], ["b", 2]]);  // from entries

// Insert / Update
map.set("alice", 90);
map.set("alice", 95);  // update existing key

// Lookup with existence check
if (map.has("alice")) {
    console.log("Found:", map.get("alice"));
} else {
    console.log("Not found");
}

// Delete
map.delete("alice");  // returns true if key existed

// Iterate (insertion order is preserved)
for (const [key, val] of map) {
    console.log(key, val);
}

// Size
console.log(map.size);
```

### 2. Object as Map (Common in Interviews)

```javascript
// Create
const obj = {};
const obj = { a: 1, b: 2 };

// Insert / Update
obj["alice"] = 90;
obj["alice"] = 95;

// Lookup
if ("alice" in obj) {
    console.log("Found:", obj["alice"]);
}
// Or use hasOwnProperty to avoid prototype chain
if (obj.hasOwnProperty("alice")) { ... }

// Delete
delete obj["alice"];

// Iterate
for (const key in obj) {
    console.log(key, obj[key]);
}
// Or
for (const [key, val] of Object.entries(obj)) {
    console.log(key, val);
}
```

### 3. Hash Set Pattern

```javascript
// Built-in Set
const seen = new Set();
seen.add(42);
seen.add(42);        // no-op, already exists
seen.has(42);        // true
seen.delete(42);     // removes element
seen.size;           // number of elements

// Initialize from array
const unique = new Set([1, 2, 3, 2, 1]);  // Set {1, 2, 3}

// Convert back to array
const arr = [...unique];  // [1, 2, 3]
```

### 4. Frequency Counting

The bread-and-butter hash map pattern. Count occurrences of each element.

```javascript
// Using Map
function frequencyCount(nums) {
    const freq = new Map();
    for (const num of nums) {
        freq.set(num, (freq.get(num) || 0) + 1);
    }
    return freq;
}

// Using plain object (for string/number keys)
function charFreq(s) {
    const freq = {};
    for (const ch of s) {
        freq[ch] = (freq[ch] || 0) + 1;
    }
    return freq;
}
```

### 5. Two-Sum Pattern (Complement Lookup)

Store values you have seen so far; check if the complement exists.

```javascript
function twoSum(nums, target) {
    const seen = new Map();  // value -> index
    for (let i = 0; i < nums.length; i++) {
        const complement = target - nums[i];
        if (seen.has(complement)) {
            return [seen.get(complement), i];
        }
        seen.set(nums[i], i);
    }
    return null;
}
```

This transforms an O(n^2) brute-force into O(n) by trading space for time.

### 6. Grouping Pattern

Group elements by a computed key. Classic example: group anagrams.

```javascript
function groupAnagrams(strs) {
    const groups = new Map();
    for (const s of strs) {
        // Sort characters to create a canonical key
        const key = [...s].sort().join("");
        if (!groups.has(key)) {
            groups.set(key, []);
        }
        groups.get(key).push(s);
    }
    return [...groups.values()];
}

// Alternative: frequency array as key (faster than sorting)
function groupAnagramsFreq(strs) {
    const groups = new Map();
    for (const s of strs) {
        const freq = new Array(26).fill(0);
        for (const ch of s) {
            freq[ch.charCodeAt(0) - 97]++;
        }
        const key = freq.join(",");  // convert array to string key
        if (!groups.has(key)) {
            groups.set(key, []);
        }
        groups.get(key).push(s);
    }
    return [...groups.values()];
}
```

**Key insight:** Unlike Go where `[26]int` arrays can be map keys directly, in JavaScript
you must convert arrays to strings to use them as keys (arrays are compared by reference).

### 7. Deduplication Pattern

Remove duplicates using a Set.

```javascript
function removeDuplicates(nums) {
    const seen = new Set();
    const result = [];
    for (const num of nums) {
        if (!seen.has(num)) {
            seen.add(num);
            result.push(num);
        }
    }
    return result;
}

// One-liner (doesn't preserve order guarantees in older engines)
const unique = [...new Set(nums)];
```

### 8. Index Mapping Pattern

Map elements to their indices or positions for O(1) lookups later.

```javascript
// Find the first non-repeating character
function firstUniqChar(s) {
    const freq = {};
    for (const ch of s) {
        freq[ch] = (freq[ch] || 0) + 1;
    }
    for (let i = 0; i < s.length; i++) {
        if (freq[s[i]] === 1) {
            return i;
        }
    }
    return -1;
}
```

### 9. Sliding Window + Map

Combine hash map with a sliding window for substring/subarray constraint problems.

```javascript
// Check if s2 contains a permutation of s1
function checkInclusion(s1, s2) {
    if (s1.length > s2.length) return false;

    const s1Freq = new Array(26).fill(0);
    const windowFreq = new Array(26).fill(0);

    for (const ch of s1) {
        s1Freq[ch.charCodeAt(0) - 97]++;
    }

    for (let i = 0; i < s2.length; i++) {
        windowFreq[s2.charCodeAt(i) - 97]++;
        if (i >= s1.length) {
            windowFreq[s2.charCodeAt(i - s1.length) - 97]--;
        }
        if (s1Freq.join(",") === windowFreq.join(",")) {
            return true;
        }
    }
    return false;
}
```

---

## What Can Be a Map Key?

JavaScript `Map` accepts **any value** as a key. Plain objects only accept strings and Symbols.

| Key Type           | `Map` Key? | Object Key? | Notes                                     |
|--------------------|------------|-------------|-------------------------------------------|
| Strings            | Yes        | Yes         | Most common                               |
| Numbers            | Yes        | Coerced to string | `obj[1]` same as `obj["1"]`        |
| Objects            | Yes        | Coerced to string | All objects become `"[object Object]"` |
| Arrays             | Yes (by ref)| Coerced to string | `[1,2]` becomes `"1,2"`            |
| `null`/`undefined` | Yes        | Coerced to string | `"null"` / `"undefined"`           |
| `NaN`              | Yes        | Coerced to string | `Map` treats `NaN === NaN` for keys |
| Booleans           | Yes        | Coerced to string | `true` becomes `"true"`            |

**Workaround for complex keys:** Convert to a string representation.
- Frequency array key: `freq.join(",")` → `"0,1,2,0,..."`
- Coordinate key: `${row},${col}` → `"3,5"`
- Sorted string key: `[...s].sort().join("")` → `"aet"` for anagrams

---

## When to Use

| Scenario                                          | Use Hash Map? |
|---------------------------------------------------|---------------|
| Need O(1) lookup by key/value                     | Yes           |
| Frequency counting                                | Yes           |
| Checking for duplicates                           | Yes (Set)     |
| Need to find complement/pair in O(1)              | Yes           |
| Grouping elements by a property                   | Yes           |
| Need ordered iteration                            | Map preserves insertion order |
| Need ordered keys (min/max)                       | No -- use sorted array or heap |

---

## Common Pitfalls

1. **Object keys are always strings.** `obj[1]` and `obj["1"]` refer to the same property.
   Use `Map` if you need numeric keys to be distinct.

2. **`undefined` on missing keys.** Both `map.get(missing)` and `obj[missing]` return
   `undefined`. Use `map.has(key)` or `key in obj` to check existence explicitly.

   ```javascript
   // BAD: can't tell if key exists with value undefined
   const val = map.get("key");

   // GOOD: explicitly check existence
   if (map.has("key")) { ... }
   ```

3. **Arrays/Objects compared by reference.** `new Set([[1,2], [1,2]]).size` is 2, not 1.
   Convert to strings for value-based comparison.

4. **`for...in` iterates prototype chain.** Use `Object.keys()`, `Object.entries()`, or
   `hasOwnProperty` to avoid inherited properties.

5. **Object.keys() returns strings.** Even for numeric keys: `Object.keys({1: "a"})` returns
   `["1"]`, not `[1]`.

6. **`delete` performance.** `delete obj[key]` can deoptimize V8's hidden classes. Use `Map`
   when frequently adding/deleting keys.

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
Map:
  Create:     const map = new Map()
  Insert:     map.set(key, val)
  Lookup:     map.get(key)
  Exists:     map.has(key)
  Delete:     map.delete(key)
  Size:       map.size
  Iterate:    for (const [k, v] of map) { ... }

Object:
  Create:     const obj = {}
  Insert:     obj[key] = val
  Lookup:     obj[key]
  Exists:     key in obj  or  obj.hasOwnProperty(key)
  Delete:     delete obj[key]
  Keys:       Object.keys(obj)
  Entries:    Object.entries(obj)

Set:
  Create:     const set = new Set()
  Add:        set.add(val)
  Check:      set.has(val)
  Delete:     set.delete(val)
  Size:       set.size
  To array:   [...set]

Freq count:   freq[ch] = (freq[ch] || 0) + 1
              map.set(x, (map.get(x) || 0) + 1)
```

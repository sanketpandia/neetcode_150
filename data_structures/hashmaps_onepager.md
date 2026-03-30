# Hash Maps / Hash Sets -- One-Pager

## Core Concept

A **hash map** (also called hash table or dictionary) stores key-value pairs and provides O(1) average-time lookup, insertion, and deletion by computing a hash of the key to determine its storage location in an internal array of "buckets."

A **hash set** is a hash map where you only care about keys (existence), not values.

In Java:
- **`HashMap<K, V>`** -- hash map, O(1) average operations, iteration order not guaranteed
- **`HashSet<E>`** -- hash set built on top of `HashMap`
- **`LinkedHashMap<K, V>`** -- insertion-order-preserving hash map
- **`TreeMap<K, V>`** -- sorted map (Red-Black tree), O(log n) operations

**How hashing works:**
1. A hash function converts the key into an integer (via `hashCode()`).
2. The hash code is mapped to a bucket index (typically via modulo).
3. Collisions are handled by chaining (linked list/tree in each bucket).

Java's `HashMap` uses chaining and converts bucket chains to balanced trees when a bucket exceeds 8 entries (Java 8+), giving O(log n) worst case instead of O(n).

---

## Time Complexity Table

| Operation          | Average | Worst Case | Notes                                |
|--------------------|---------|------------|--------------------------------------|
| Insert / Update    | O(1)    | O(log n)   | Worst case: bucket treeification     |
| Lookup             | O(1)    | O(log n)   | Same                                 |
| Delete             | O(1)    | O(log n)   | Same                                 |
| Check existence    | O(1)    | O(log n)   | `containsKey(key)`                   |
| Iterate all        | O(n)    | O(n)       | Order not guaranteed in `HashMap`    |
| Get length         | O(1)    | O(1)       | `map.size()`                         |

**Space Complexity:** O(n) where n is the number of key-value pairs stored.

---

## Implementation Patterns

### 1. HashMap Basics

```java
// Create
Map<String, Integer> map = new HashMap<>();

// Insert / Update
map.put("alice", 90);
map.put("alice", 95);  // update existing key

// Lookup with existence check
if (map.containsKey("alice")) {
    int val = map.get("alice");
}

// Get with default (avoids null check)
int val = map.getOrDefault("bob", 0);

// Delete
map.remove("alice");  // no-op if key doesn't exist

// Iterate
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}

// Size
System.out.println(map.size());
```

### 2. HashSet Pattern

```java
Set<Integer> seen = new HashSet<>();
seen.add(42);
if (seen.contains(42)) {
    // 42 is in the set
}
seen.remove(42);

// Set operations
set1.containsAll(set2);  // subset check
set1.addAll(set2);       // union (modifies set1)
set1.retainAll(set2);    // intersection (modifies set1)
```

### 3. Frequency Counting

The bread-and-butter hash map pattern. Count occurrences of each element.

```java
Map<Integer, Integer> frequencyCount(int[] nums) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) {
        freq.put(num, freq.getOrDefault(num, 0) + 1);
        // OR: freq.merge(num, 1, Integer::sum);
    }
    return freq;
}

// Character frequency for strings
Map<Character, Integer> charFreq(String s) {
    Map<Character, Integer> freq = new HashMap<>();
    for (char c : s.toCharArray()) {
        freq.put(c, freq.getOrDefault(c, 0) + 1);
    }
    return freq;
}
```

### 4. Two-Sum Pattern (Complement Lookup)

Store values you have seen so far; check if the complement exists.

```java
int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();  // value -> index
    for (int i = 0; i < nums.length; i++) {
        int complement = target - nums[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(nums[i], i);
    }
    return new int[]{};
}
```

This transforms an O(n²) brute-force into O(n) by trading space for time.

### 5. Grouping Pattern

Group elements by a computed key. Classic example: group anagrams.

```java
List<List<String>> groupAnagrams(String[] strs) {
    Map<String, List<String>> groups = new HashMap<>();
    for (String s : strs) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        String key = new String(chars);  // sorted chars as key
        groups.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(groups.values());
}
```

**Alternative key:** A frequency array encoded as a string `"1#0#0#..."` avoids sorting (O(n) instead of O(n log n)):

```java
char[] freq = new char[26];
for (char c : s.toCharArray()) freq[c - 'a']++;
String key = new String(freq);  // 26-char key
```

### 6. Deduplication Pattern

Remove duplicates using a set.

```java
List<Integer> removeDuplicates(int[] nums) {
    Set<Integer> seen = new HashSet<>();
    List<Integer> result = new ArrayList<>();
    for (int num : nums) {
        if (seen.add(num)) {  // add() returns false if already present
            result.add(num);
        }
    }
    return result;
}
```

### 7. Sliding Window + Map

Combine hash map with a sliding window for substring/subarray constraint problems.

```java
// Check if s2 contains a permutation of s1
boolean checkInclusion(String s1, String s2) {
    if (s1.length() > s2.length()) return false;
    int[] s1Freq = new int[26], windowFreq = new int[26];
    for (char c : s1.toCharArray()) s1Freq[c - 'a']++;
    for (int i = 0; i < s2.length(); i++) {
        windowFreq[s2.charAt(i) - 'a']++;
        if (i >= s1.length()) {
            windowFreq[s2.charAt(i - s1.length()) - 'a']--;
        }
        if (Arrays.equals(s1Freq, windowFreq)) return true;
    }
    return false;
}
```

---

## What Can Be a Map Key?

Java requires map keys to have `hashCode()` and `equals()` implemented. This includes:

| Type                  | Valid Key? | Notes                                      |
|-----------------------|------------|---------------------------------------------|
| `Integer`, `Long`, etc.| Yes       | All boxed numeric types                     |
| `String`              | Yes        | Very common, well-optimized                 |
| `Character`, `Boolean`| Yes        | Boxed primitives                            |
| Any custom class      | Yes*       | *Must override `hashCode()` and `equals()`  |
| `int[]` (array)       | **No**     | Arrays use identity hash -- won't work as expected |
| `List<Integer>`       | Possible** | **Works if `equals()` matches -- but mutable keys are dangerous |

**Workaround for array keys:** Convert to a `String` (`Arrays.toString(arr)`) or use a sorted `String` as the key.

---

## When to Use

| Scenario                                          | Use Hash Map? |
|---------------------------------------------------|---------------|
| Need O(1) lookup by key/value                     | Yes           |
| Frequency counting                                | Yes           |
| Checking for duplicates                           | Yes (HashSet) |
| Need to find complement/pair in O(1)              | Yes           |
| Grouping elements by a property                   | Yes           |
| Need ordered iteration                            | Use `LinkedHashMap` or `TreeMap` |
| Need ordered keys (min/max)                       | Use `TreeMap` or heap |

---

## Common Pitfalls

1. **Using `==` to compare Integer keys.** `Integer` objects are cached only for -128 to 127. For values outside this range, `map.get(myInt)` works fine, but `key1 == key2` can fail. HashMap uses `.equals()` internally, so this usually isn't an issue -- but be careful when comparing keys manually.

2. **Using arrays as map keys.** `int[]` uses identity hash (memory address) as its `hashCode`, so two arrays with the same contents won't match as map keys. Use `Arrays.toString(arr)` or a `String` instead.

3. **`NullPointerException` from `get()`.** `map.get(key)` returns `null` if the key is absent. Using `map.get(key) + 1` on a missing key throws NPE. Always use `getOrDefault()` or check with `containsKey()` first.

4. **Iterating while modifying.** Modifying a `HashMap` while iterating its `entrySet()` throws `ConcurrentModificationException`. Collect keys to remove, then remove after iteration.

5. **Memory not released on `remove()`.** Like Go's map, Java's `HashMap` doesn't shrink its internal array. For maps that grow large then shrink significantly, create a new map.

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

**Interview tip:** When brute force is O(n²) because of a nested lookup, ask yourself: "Can I replace the inner loop with a hash map lookup?" The answer is usually yes.

---

## Practice Problems

| #  | Problem                         | Difficulty | Key Pattern                  | LeetCode # |
|----|---------------------------------|------------|------------------------------|------------|
| 1  | Contains Duplicate              | Easy       | HashSet                      | 217        |
| 2  | Two Sum                         | Easy       | Complement lookup            | 1          |
| 3  | Valid Anagram                   | Easy       | Frequency comparison         | 242        |
| 4  | Group Anagrams                  | Medium     | Grouping by frequency key    | 49         |
| 5  | Top K Frequent Elements         | Medium     | Frequency map + bucket sort  | 347        |
| 6  | Longest Consecutive Sequence    | Medium     | HashSet + sequence building  | 128        |
| 7  | Subarray Sum Equals K           | Medium     | Prefix sum + hash map        | 560        |

Start with 1-3 for fundamentals, then 4-6 for pattern mastery. Problem 7 combines prefix sums with hash maps -- a powerful interview technique.

---

## Quick Reference Card

```
Create:       Map<String, Integer> map = new HashMap<>();
Insert:       map.put(key, val)
Lookup:       map.get(key)                         // null if missing
Safe get:     map.getOrDefault(key, defaultVal)
Check:        map.containsKey(key)
Delete:       map.remove(key)
Size:         map.size()
Iterate:      for (Map.Entry<K,V> e : map.entrySet()) { e.getKey(); e.getValue(); }
Keys:         map.keySet()
Values:       map.values()
Merge:        map.merge(key, 1, Integer::sum)       // increment counter
Upsert:       map.computeIfAbsent(key, k -> new ArrayList<>()).add(val)

Set create:   Set<Integer> set = new HashSet<>();
Set add:      set.add(val)                          // returns false if duplicate
Set check:    set.contains(val)
```

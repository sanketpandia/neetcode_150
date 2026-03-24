# Arrays -- One-Pager

## Core Concept

An **array** stores elements in contiguous memory, enabling O(1) random access by index. In
JavaScript, arrays are **dynamically-sized**, automatically growing and shrinking as elements
are added or removed. Unlike statically-typed languages, JS arrays can hold mixed types
(though you should avoid this in interviews).

Under the hood, V8 (Chrome/Node) uses two array representations:
- **Packed (dense):** Contiguous memory, fast access -- used when all indices are filled
- **Holey (sparse):** Hash-map-like storage -- used when indices are skipped or deleted

For coding interviews, treat JS arrays as dynamic arrays with amortized O(1) push/pop and
O(1) random access.

---

## Time Complexity Table

| Operation              | Time Complexity  | Notes                                         |
|------------------------|------------------|-----------------------------------------------|
| Access by index        | O(1)             | `arr[i]` -- direct memory offset              |
| Search (unsorted)      | O(n)             | Linear scan                                   |
| Search (sorted)        | O(log n)         | Binary search                                 |
| Push (append to end)   | O(1) amortized   | `arr.push(val)`                               |
| Pop (remove from end)  | O(1)             | `arr.pop()`                                   |
| Unshift (insert front) | O(n)             | `arr.unshift(val)` -- must shift all elements |
| Shift (remove front)   | O(n)             | `arr.shift()` -- must shift all elements      |
| Insert at index        | O(n)             | `arr.splice(i, 0, val)` -- shift elements     |
| Delete at index        | O(n)             | `arr.splice(i, 1)` -- shift elements          |
| Delete (unordered)     | O(1)             | Swap with last element, pop                   |
| Get length             | O(1)             | `arr.length`                                  |
| Copy                   | O(n)             | `[...arr]` or `Array.from(arr)`               |
| Slice                  | O(k)             | `arr.slice(low, high)` -- creates a new array |
| Sort                   | O(n log n)       | `arr.sort((a, b) => a - b)`                   |

**Key difference from Go:** `arr.slice()` creates a **new array** (a copy), it does NOT share
backing memory like Go sub-slices. This means no shared-memory bugs, but also no O(1) slicing.

---

## Implementation Patterns

### 1. Declaration and Initialization

```javascript
// Empty array
const arr = [];

// Literal initialization
const arr = [1, 2, 3, 4, 5];

// Pre-filled with zeros
const arr = new Array(5).fill(0);    // [0, 0, 0, 0, 0]

// Pre-filled with index values
const arr = Array.from({ length: 5 }, (_, i) => i);  // [0, 1, 2, 3, 4]

// 2D array (matrix)
const matrix = Array.from({ length: m }, () => new Array(n).fill(0));
```

### 2. Push and Pop

```javascript
const arr = [];
arr.push(1, 2, 3);    // [1, 2, 3]
arr.push(4, 5);        // [1, 2, 3, 4, 5]
const last = arr.pop(); // 5, arr is now [1, 2, 3, 4]
```

### 3. Delete Element

```javascript
// Order-preserving delete at index i -- O(n)
arr.splice(i, 1);

// Unordered delete at index i -- O(1)
arr[i] = arr[arr.length - 1];
arr.pop();
```

### 4. Copy (Shallow Clone)

```javascript
// Spread operator (most common)
const copy = [...arr];

// Array.from
const copy = Array.from(arr);

// Slice with no args
const copy = arr.slice();
```

### 5. Two-Pointer Pattern

Used when the input is sorted or when you need to compare elements from both ends.

```javascript
// Example: Two Sum on a sorted array
function twoSumSorted(nums, target) {
    let left = 0, right = nums.length - 1;
    while (left < right) {
        const sum = nums[left] + nums[right];
        if (sum === target) {
            return [left, right];
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return null;
}
```

### 6. Sliding Window Pattern

Used for contiguous subarray/substring problems with a constraint.

```javascript
// Example: Maximum sum subarray of size k
function maxSumSubarray(nums, k) {
    let windowSum = 0;
    for (let i = 0; i < k; i++) {
        windowSum += nums[i];
    }
    let maxSum = windowSum;
    for (let i = k; i < nums.length; i++) {
        windowSum += nums[i] - nums[i - k];  // slide: add right, remove left
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
```

### 7. Prefix Sum Pattern

Pre-compute cumulative sums for O(1) range sum queries.

```javascript
// Build prefix sum
const prefix = new Array(nums.length + 1).fill(0);
for (let i = 0; i < nums.length; i++) {
    prefix[i + 1] = prefix[i] + nums[i];
}
// Sum of nums[l..r] inclusive
const rangeSum = prefix[r + 1] - prefix[l];
```

### 8. In-Place Reversal

```javascript
function reverse(arr) {
    let left = 0, right = arr.length - 1;
    while (left < right) {
        [arr[left], arr[right]] = [arr[right], arr[left]];
        left++;
        right--;
    }
}

// Or simply use the built-in (mutates in place)
arr.reverse();
```

---

## When to Use

| Scenario                                        | Use Arrays?        |
|-------------------------------------------------|--------------------|
| Need O(1) random access by index                | Yes                |
| Mostly appending to the end                     | Yes                |
| Frequent insert/delete in the middle            | No -- use linked list |
| Need to maintain sorted order with insertions   | Maybe -- consider a heap |
| Dynamic collection with unknown size            | Yes                |
| Need O(1) lookup by value                       | No -- use Set or Map |
| Need a stack (LIFO)                             | Yes -- push/pop    |
| Need a queue (FIFO)                             | Caution -- shift is O(n) |

---

## Common Pitfalls

1. **Sort is lexicographic by default.** `[10, 2, 1].sort()` gives `[1, 10, 2]` (string order).
   Always pass a comparator: `arr.sort((a, b) => a - b)`.

2. **Reference vs copy.** `const b = a` does NOT copy -- both point to the same array. Use
   `[...a]` or `Array.from(a)` to create a true copy.

3. **Using `delete` on arrays.** `delete arr[i]` creates a hole (sparse array), it does NOT
   shift elements. Use `arr.splice(i, 1)` instead.

4. **Off-by-one in slice bounds.** `arr.slice(low, high)` is half-open: includes index `low`,
   excludes index `high`. The length of the result is `high - low`.

5. **Mutation surprises.** `sort()`, `reverse()`, `splice()` all mutate in place. If you need
   the original, copy first: `[...arr].sort(...)`.

6. **O(n) shift/unshift.** `arr.shift()` and `arr.unshift()` are O(n) because all elements
   must be re-indexed. For queue operations, consider a linked list or deque implementation.

---

## Interview Relevance

Arrays appear in nearly every NeetCode 150 category. The most important patterns:

| Pattern           | Signal Words                                    | Example Problems              |
|-------------------|-------------------------------------------------|-------------------------------|
| Two Pointers      | "sorted array", "pair", "triplet", "in-place"   | Two Sum II, 3Sum, Container   |
| Sliding Window    | "subarray", "substring", "contiguous", "window" | Max Subarray, Min Size Subarray |
| Prefix Sum        | "range sum", "subarray sum equals k"             | Subarray Sum Equals K         |
| Binary Search     | "sorted", "minimum/maximum", "search"            | Search in Rotated Array       |
| In-Place Modify   | "O(1) space", "in-place", "remove"               | Remove Duplicates, Move Zeros |
| Kadane's Algorithm| "maximum subarray sum"                            | Maximum Subarray              |

---

## Practice Problems

| #  | Problem                                | Difficulty | Key Pattern                | LeetCode # |
|----|----------------------------------------|------------|----------------------------|------------|
| 1  | Contains Duplicate                     | Easy       | Hash set / sorting         | 217        |
| 2  | Two Sum                                | Easy       | Hash map complement        | 1          |
| 3  | Best Time to Buy and Sell Stock        | Easy       | Sliding window / Kadane    | 121        |
| 4  | Product of Array Except Self           | Medium     | Prefix/suffix products     | 238        |
| 5  | Maximum Subarray                       | Medium     | Kadane's algorithm         | 53         |
| 6  | 3Sum                                   | Medium     | Sort + two pointers        | 15         |
| 7  | Container With Most Water              | Medium     | Two pointers (greedy)      | 11         |

Start with problems 1-3 to build confidence, then tackle 4-7 for pattern mastery.

---

## Quick Reference Card

```
Create:    const arr = [] or new Array(n).fill(0)
Append:    arr.push(val)
Delete:    arr.splice(i, 1)                    // ordered
           arr[i] = arr[arr.length-1]; arr.pop()  // unordered
Access:    arr[i]
Length:    arr.length
Copy:      [...arr] or Array.from(arr)
Slice:     arr.slice(low, high)                // returns new array
Sort:      arr.sort((a, b) => a - b)           // MUST pass comparator
Reverse:   arr.reverse()                       // mutates in place
Includes:  arr.includes(val)                   // O(n)
IndexOf:   arr.indexOf(val)                    // O(n)
```

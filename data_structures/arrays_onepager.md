# Arrays (Slices in Go) -- One-Pager

## Core Concept

An **array** stores elements in contiguous memory, enabling O(1) random access by index. In Go,
arrays have a fixed size determined at compile time, while **slices** are dynamically-sized views
backed by an underlying array. Slices are what you will use in virtually every Go program and
every coding interview problem.

A slice header contains three fields:
- **Pointer** to the underlying array
- **Length** (`len`) -- number of elements currently in the slice
- **Capacity** (`cap`) -- number of elements the underlying array can hold before reallocation

Understanding this header is critical because it explains why slices share memory, why `append`
sometimes creates a new backing array, and why sub-slicing is O(1).

---

## Time Complexity Table

| Operation              | Time Complexity  | Notes                                         |
|------------------------|------------------|-----------------------------------------------|
| Access by index        | O(1)             | `s[i]` -- direct memory offset                |
| Search (unsorted)      | O(n)             | Linear scan                                   |
| Search (sorted)        | O(log n)         | Binary search                                 |
| Append                 | O(1) amortized   | `append()` -- may trigger reallocation O(n)    |
| Insert at index        | O(n)             | Must shift elements right                     |
| Delete at index        | O(n)             | Must shift elements left (order-preserving)   |
| Delete (unordered)     | O(1)             | Swap with last element, shrink                |
| Get length / capacity  | O(1)             | `len(s)` / `cap(s)`                           |
| Copy                   | O(n)             | `copy(dst, src)`                              |
| Sub-slice              | O(1)             | `s[low:high]` -- shares backing array         |
| Sort                   | O(n log n)       | `slices.Sort(s)` or `sort.Ints(s)`            |

**Capacity growth strategy:** When `append` needs to grow, Go roughly doubles capacity for
slices under 256 elements, then grows by ~25% for larger slices. The exact formula includes
memory alignment rounding, but the key insight is that growth is geometric, guaranteeing
amortized O(1) appends.

---

## Implementation Patterns

### 1. Declaration and Initialization

```go
// Fixed-size array (rarely used directly in interviews)
var arr [5]int                    // [0 0 0 0 0]

// Slice -- the standard choice
s := []int{1, 2, 3, 4, 5}        // literal initialization
s := make([]int, 5)               // len=5, cap=5, all zeros
s := make([]int, 0, 10)           // len=0, cap=10 (pre-allocated)
```

### 2. Append and Grow

```go
s := make([]int, 0, 4)
s = append(s, 1, 2, 3)           // len=3, cap=4
s = append(s, 4, 5)              // triggers reallocation, new cap >= 8
```

Always reassign the result of `append`: `s = append(s, val)`. The old variable may point to
a stale backing array after reallocation.

### 3. Delete Element

```go
// Order-preserving delete at index i -- O(n)
s = append(s[:i], s[i+1:]...)

// Unordered delete at index i -- O(1)
s[i] = s[len(s)-1]
s = s[:len(s)-1]
```

### 4. Copy (Break Shared Memory)

```go
dst := make([]int, len(src))
copy(dst, src)
```

### 5. Two-Pointer Pattern

Used when the input is sorted or when you need to compare elements from both ends.

```go
// Example: Two Sum on a sorted array
func twoSumSorted(nums []int, target int) []int {
    left, right := 0, len(nums)-1
    for left < right {
        sum := nums[left] + nums[right]
        if sum == target {
            return []int{left, right}
        } else if sum < target {
            left++
        } else {
            right--
        }
    }
    return nil
}
```

### 6. Sliding Window Pattern

Used for contiguous subarray/substring problems with a constraint.

```go
// Example: Maximum sum subarray of size k
func maxSumSubarray(nums []int, k int) int {
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += nums[i]
    }
    maxSum := windowSum
    for i := k; i < len(nums); i++ {
        windowSum += nums[i] - nums[i-k]  // slide: add right, remove left
        if windowSum > maxSum {
            maxSum = windowSum
        }
    }
    return maxSum
}
```

### 7. Prefix Sum Pattern

Pre-compute cumulative sums for O(1) range sum queries.

```go
// Build prefix sum
prefix := make([]int, len(nums)+1)
for i, num := range nums {
    prefix[i+1] = prefix[i] + num
}
// Sum of nums[l..r] inclusive
rangeSum := prefix[r+1] - prefix[l]
```

### 8. In-Place Reversal

```go
func reverse(s []int) {
    left, right := 0, len(s)-1
    for left < right {
        s[left], s[right] = s[right], s[left]
        left++
        right--
    }
}
```

---

## When to Use

| Scenario                                        | Use Arrays/Slices? |
|-------------------------------------------------|--------------------|
| Need O(1) random access by index                | Yes                |
| Mostly appending to the end                     | Yes                |
| Frequent insert/delete in the middle            | No -- use linked list |
| Need to maintain sorted order with insertions   | Maybe -- consider a BST or heap |
| Fixed-size collection known at compile time      | Yes (Go array)     |
| Dynamic collection with unknown size            | Yes (Go slice)     |
| Need O(1) lookup by value                       | No -- use hash map |

---

## Common Pitfalls

1. **Shared backing arrays.** Sub-slices and the original share memory. Modifying one affects
   the other. Use `copy` to break the connection when needed.

2. **Stale references after append.** `append` may allocate a new backing array. If another
   variable still points to the old array, they diverge silently. Always use `s = append(s, ...)`.

3. **Off-by-one in slice bounds.** `s[low:high]` is half-open: includes index `low`, excludes
   index `high`. The length of the result is `high - low`.

4. **Memory leaks with sub-slicing.** `s[1:]` still references the original backing array.
   The garbage collector cannot reclaim the first element. For long-lived sub-slices, copy
   the data into a new slice.

5. **Forgetting to pre-allocate.** When you know the final size, use `make([]T, 0, n)` to
   avoid repeated reallocations. This can be a significant performance improvement.

6. **Sorting instability.** `sort.Ints` (and `slices.Sort`) use an introsort variant that is
   NOT stable. Use `slices.SortStableFunc` if you need stability.

---

## Interview Relevance

Arrays and slices appear in nearly every NeetCode 150 category. The most important patterns:

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
Create:    s := make([]int, 0, n)
Append:    s = append(s, val)
Delete:    s = append(s[:i], s[i+1:]...)    // ordered
           s[i] = s[len(s)-1]; s = s[:len(s)-1]  // unordered
Access:    s[i]
Length:    len(s)
Capacity:  cap(s)
Copy:      copy(dst, src)
Sort:      slices.Sort(s)  or  sort.Ints(s)
Reverse:   slices.Reverse(s)
```

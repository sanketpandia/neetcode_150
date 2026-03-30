# Arrays / ArrayList -- One-Pager

## Core Concept

An **array** stores elements in contiguous memory, enabling O(1) random access by index. In Java, arrays have a fixed size determined at declaration time. **ArrayList** is the dynamic alternative, backed by a resizable array that doubles when full.

The key distinction:
- `int[] arr` -- fixed size, stores primitives (no boxing overhead)
- `ArrayList<Integer>` -- dynamic size, stores objects (autoboxing overhead)
- For interview problems, use `int[]` when size is known, `ArrayList` when not.

Understanding capacity vs size is important: an ArrayList tracks how many elements are in it (size) separately from its internal array length (capacity). Adding an element when full triggers a copy to a new array twice the size -- this is why appending is O(1) amortized.

---

## Time Complexity Table

| Operation              | Time Complexity  | Notes                                          |
|------------------------|------------------|------------------------------------------------|
| Access by index        | O(1)             | `arr[i]` / `list.get(i)` -- direct offset     |
| Search (unsorted)      | O(n)             | Linear scan                                    |
| Search (sorted)        | O(log n)         | Binary search                                  |
| Add to end             | O(1) amortized   | ArrayList doubles capacity when full            |
| Insert at index        | O(n)             | Must shift elements right                      |
| Delete at index        | O(n)             | Must shift elements left (order-preserving)    |
| Delete (unordered)     | O(1)             | Swap with last element, shrink size            |
| Get length / size      | O(1)             | `arr.length` / `list.size()`                   |
| Copy                   | O(n)             | `Arrays.copyOf(arr, n)`                        |
| Sub-array              | O(n)             | `Arrays.copyOfRange(arr, from, to)` -- copies  |
| Sort                   | O(n log n)       | `Arrays.sort(arr)` / `Collections.sort(list)`  |

**Capacity growth:** ArrayList roughly doubles capacity (uses `(oldCap * 3) / 2 + 1` in Java). Growth is geometric, guaranteeing amortized O(1) appends.

---

## Implementation Patterns

### 1. Declaration and Initialization

```java
// Fixed-size array
int[] arr = new int[5];              // [0, 0, 0, 0, 0]
int[] init = {1, 2, 3, 4, 5};       // literal initialization
int[][] matrix = new int[3][4];     // 2D array

// ArrayList (dynamic)
List<Integer> list = new ArrayList<>();
List<Integer> preAlloc = new ArrayList<>(100); // pre-allocate capacity
List<Integer> fromArr = new ArrayList<>(Arrays.asList(1, 2, 3));
```

### 2. Add and Access

```java
list.add(42);            // append to end O(1) amortized
list.add(0, 99);         // insert at index 0 O(n)
list.get(0);             // access O(1)
list.set(0, 100);        // update O(1)
list.size();             // length O(1)
arr.length;              // fixed array length
```

### 3. Delete Element

```java
// ArrayList -- order-preserving delete at index i -- O(n)
list.remove(i);  // shifts elements left

// ArrayList -- unordered delete at index i -- O(1)
list.set(i, list.get(list.size() - 1));
list.remove(list.size() - 1);

// Array -- manual order-preserving delete
// Use System.arraycopy or create a new array
```

### 4. Copy (Avoid Shared Memory)

```java
int[] copy = Arrays.copyOf(arr, arr.length);
int[] range = Arrays.copyOfRange(arr, 1, 4); // [1, 4) exclusive end
List<Integer> listCopy = new ArrayList<>(original);
```

### 5. Two-Pointer Pattern

Used when the input is sorted or when you need to process from both ends.

```java
// Two Sum on a sorted array
int[] twoSumSorted(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    while (left < right) {
        int sum = nums[left] + nums[right];
        if (sum == target) return new int[]{left, right};
        else if (sum < target) left++;
        else right--;
    }
    return new int[]{};
}
```

### 6. Sliding Window Pattern

Used for contiguous subarray/substring problems with a constraint.

```java
// Maximum sum subarray of size k
int maxSumSubarray(int[] nums, int k) {
    int windowSum = 0;
    for (int i = 0; i < k; i++) windowSum += nums[i];
    int maxSum = windowSum;
    for (int i = k; i < nums.length; i++) {
        windowSum += nums[i] - nums[i - k]; // slide: add right, remove left
        maxSum = Math.max(maxSum, windowSum);
    }
    return maxSum;
}
```

### 7. Prefix Sum Pattern

Pre-compute cumulative sums for O(1) range sum queries.

```java
// Build prefix sum
int[] prefix = new int[nums.length + 1];
for (int i = 0; i < nums.length; i++) {
    prefix[i + 1] = prefix[i] + nums[i];
}
// Sum of nums[l..r] inclusive
int rangeSum = prefix[r + 1] - prefix[l];
```

### 8. In-Place Reversal

```java
void reverse(int[] arr) {
    int left = 0, right = arr.length - 1;
    while (left < right) {
        int tmp = arr[left];
        arr[left++] = arr[right];
        arr[right--] = tmp;
    }
}
```

---

## When to Use

| Scenario                                        | Use Arrays/ArrayList? |
|-------------------------------------------------|-----------------------|
| Need O(1) random access by index                | Yes                   |
| Mostly appending to the end                     | Yes                   |
| Frequent insert/delete in the middle            | No -- use LinkedList  |
| Need to maintain sorted order with insertions   | Maybe -- consider BST or heap |
| Fixed-size collection known at declaration      | Yes (array)           |
| Dynamic collection with unknown size            | Yes (ArrayList)       |
| Need O(1) lookup by value                       | No -- use HashMap     |

---

## Common Pitfalls

1. **`Arrays.asList()` returns a fixed-size list.** You cannot add or remove elements from it -- only `set`. Wrap in `new ArrayList<>(Arrays.asList(...))` for a fully mutable list.

2. **Autoboxing overhead.** `List<Integer>` boxes every `int`. For tight loops, prefer `int[]` to avoid GC pressure. For interview purposes, either is fine.

3. **Off-by-one in `Arrays.copyOfRange(arr, from, to)`.** `to` is exclusive. Length of result = `to - from`.

4. **`int[]` vs `Integer[]`.** `Arrays.sort(int[])` uses dual-pivot quicksort (not stable). `Arrays.sort(Integer[])` uses TimSort (stable). Use `Integer[]` when stability matters.

5. **Array default values.** `int[]` defaults to 0, `boolean[]` to false, `Object[]` to null. Don't assume uninitialized arrays are safe to read.

6. **2D array pitfall.** `int[][] matrix = new int[3][]` creates an array of null rows -- you must initialize each row separately.

---

## Interview Relevance

Arrays appear in nearly every NeetCode 150 category. The most important patterns:

| Pattern           | Signal Words                                    | Example Problems               |
|-------------------|-------------------------------------------------|--------------------------------|
| Two Pointers      | "sorted array", "pair", "triplet", "in-place"   | Two Sum II, 3Sum, Container    |
| Sliding Window    | "subarray", "substring", "contiguous", "window" | Max Subarray, Min Size Subarray |
| Prefix Sum        | "range sum", "subarray sum equals k"             | Subarray Sum Equals K          |
| Binary Search     | "sorted", "minimum/maximum", "search"            | Search in Rotated Array        |
| In-Place Modify   | "O(1) space", "in-place", "remove"               | Remove Duplicates, Move Zeros  |
| Kadane's Algorithm| "maximum subarray sum"                           | Maximum Subarray               |

---

## Practice Problems

| #  | Problem                                | Difficulty | Key Pattern                | LeetCode # |
|----|----------------------------------------|------------|----------------------------|------------|
| 1  | Contains Duplicate                     | Easy       | Hash set / sorting         | 217        |
| 2  | Two Sum                                | Easy       | HashMap complement         | 1          |
| 3  | Best Time to Buy and Sell Stock        | Easy       | Sliding window / Kadane    | 121        |
| 4  | Product of Array Except Self           | Medium     | Prefix/suffix products     | 238        |
| 5  | Maximum Subarray                       | Medium     | Kadane's algorithm         | 53         |
| 6  | 3Sum                                   | Medium     | Sort + two pointers        | 15         |
| 7  | Container With Most Water              | Medium     | Two pointers (greedy)      | 11         |

Start with problems 1-3 to build confidence, then tackle 4-7 for pattern mastery.

---

## Copying Arrays: `Arrays.copyOfRange` vs `System.arraycopy`

These two methods look similar but serve opposite purposes.

### `Arrays.copyOfRange` -- creates a new array

```java
int[] sub = Arrays.copyOfRange(arr, from, to);
//                                   ^     ^
//                                   inclusive  exclusive
// Length of result = to - from
```

Use when you need a **new independent array** -- e.g., splitting into halves for merge sort:

```java
int[] left  = Arrays.copyOfRange(arr, 0, mid);   // new array
int[] right = Arrays.copyOfRange(arr, mid, arr.length);  // new array
```

### `System.arraycopy` -- writes into an existing array

```java
System.arraycopy(src, srcPos, dest, destPos, length);
//               from  from-idx  into  into-idx  how many
```

Use when you want to **write into an existing array** without allocating -- e.g., writing a merged result back:

```java
System.arraycopy(temp, 0, arr, lo, temp.length);
// "copy temp.length elements from temp[0] into arr starting at arr[lo]"
```

### Side-by-side comparison

| | `Arrays.copyOfRange` | `System.arraycopy` |
|---|---|---|
| Creates new array? | Yes | No |
| Writes into existing array? | No | Yes |
| Returns | new `int[]` | `void` |
| Use case | splitting into halves | writing merged result back |

### Why merge sort uses both

```java
// Split phase -- new arrays needed to recurse on
int[] left  = Arrays.copyOfRange(arr, lo, mid);
int[] right = Arrays.copyOfRange(arr, mid, hi);

// Merge phase -- merge left+right into temp, then copy back in-place
System.arraycopy(temp, 0, arr, lo, temp.length);
```

This is why merge sort is **O(n) space** -- the split phase allocates O(n) total across all levels.

---

## Why Merge Sort is O(n log n), Not O(n²)

Merge sort is faster than bubble sort because of **how many times each element is compared**.

**Bubble sort is O(n²):** Every element is compared against every other element, repeatedly. After comparing 5 and 3, it forgets and may compare 5 again next pass. n elements × n comparisons each = n².

**Merge sort is O(n log n):** The recursion tree has log n levels. At each level, every element is touched exactly once across all merges on that level = O(n) per level. Total = n × log n.

```
Level 0:  [5, 3, 8, 1]              -- split
Level 1:  [5, 3]    [8, 1]          -- split
Level 2:  [5][3]   [8][1]           -- base cases

Merge up:
Level 2→1:  [3,5]      [1,8]        -- n/2 + n/2 = n comparisons total
Level 1→0:  [1,3,5,8]              -- n comparisons total
```

**The key insight:** merge sort never re-does work. When merging two sorted halves, each comparison permanently places an element. Once placed, it's never re-compared at that level.

```java
// Merging [3,5] and [1,8] -- 3 comparisons, 4 elements placed, done
compare 3 vs 1 → take 1   (done)
compare 3 vs 8 → take 3   (done)
                   take 5   (done)
                   take 8   (done)
```

---

## Quick Reference Card

```
Create:         int[] arr = new int[n]
                List<Integer> list = new ArrayList<>()
Add:            list.add(val)
Delete:         list.remove(i)                              // ordered, O(n)
                list.set(i, list.get(list.size()-1));       // unordered, O(1)
                list.remove(list.size()-1);
Access:         arr[i]  /  list.get(i)
Length:         arr.length  /  list.size()
Copy (new):     Arrays.copyOfRange(arr, from, to)          // [from, to) exclusive end
Copy (inplace): System.arraycopy(src, srcPos, dest, destPos, len)
Sort:           Arrays.sort(arr)  /  Collections.sort(list)
Fill:           Arrays.fill(arr, 0)
BinSearch:      Arrays.binarySearch(sorted, target)
```

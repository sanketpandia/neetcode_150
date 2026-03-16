# Sorting Algorithms Syllabus

A comprehensive reference for every sorting algorithm you need for coding interviews and the NeetCode 150. Each section covers the concept, time/space complexity, stability, when to use it, Go pseudocode, common pitfalls, and NeetCode relevance.

---

## Table of Contents

**Fundamental (Quadratic):**
1. [Bubble Sort](#1-bubble-sort)
2. [Selection Sort](#2-selection-sort)
3. [Insertion Sort](#3-insertion-sort)

**Efficient (Linearithmic):**
4. [Merge Sort](#4-merge-sort)
5. [Quick Sort](#5-quick-sort)
6. [Heap Sort](#6-heap-sort)

**Non-Comparison-Based (Linear):**
7. [Counting Sort](#7-counting-sort)
8. [Radix Sort](#8-radix-sort)
9. [Bucket Sort](#9-bucket-sort)

**Hybrid and Practical:**
10. [Tim Sort](#10-tim-sort)
11. [Dutch National Flag / 3-Way Partition](#11-dutch-national-flag--3-way-partition)

---

## Complexity Overview

| Algorithm | Best | Average | Worst | Space | Stable? | In-Place? |
|-----------|------|---------|-------|-------|---------|-----------|
| Bubble Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes | Yes |
| Selection Sort | O(n^2) | O(n^2) | O(n^2) | O(1) | No | Yes |
| Insertion Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes | Yes |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes | No |
| Quick Sort | O(n log n) | O(n log n) | O(n^2) | O(log n) | No | Yes |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No | Yes |
| Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Yes | No |
| Radix Sort | O(d*(n+k)) | O(d*(n+k)) | O(d*(n+k)) | O(n+k) | Yes | No |
| Bucket Sort | O(n+k) | O(n+k) | O(n^2) | O(n+k) | Yes* | No |
| Tim Sort | O(n) | O(n log n) | O(n log n) | O(n) | Yes | No |

*k = range of input values, d = number of digits.*
*\* Bucket sort stability depends on the inner sort used.*

**Key Terms:**
- **Stable:** Equal elements maintain their relative order from the input.
- **In-Place:** Uses O(1) extra space (not counting the input).

---

## Fundamental (Quadratic) Sorts

These are simple, educational algorithms. They're too slow for large inputs but important to understand as building blocks.

---

### 1. Bubble Sort

**Difficulty:** Beginner

**Concept:**
Repeatedly walk through the array, comparing adjacent elements and swapping them if they're in the wrong order. After each pass, the largest unsorted element "bubbles up" to its correct position. Repeat until no swaps are needed.

**Time Complexity:** O(n^2) average/worst, O(n) best (already sorted, with early exit)
**Space Complexity:** O(1)
**Stable:** Yes
**In-Place:** Yes

**Go Implementation:**

```go
func bubbleSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if arr[j] > arr[j+1] {
                arr[j], arr[j+1] = arr[j+1], arr[j]
                swapped = true
            }
        }
        if !swapped {
            break // array is already sorted
        }
    }
}
```

**How It Works (Visual):**

```
[5, 3, 8, 1, 2]
Pass 1: [3, 5, 1, 2, 8]  -- 8 bubbles to end
Pass 2: [3, 1, 2, 5, 8]  -- 5 bubbles to position
Pass 3: [1, 2, 3, 5, 8]  -- 3 bubbles to position
Pass 4: [1, 2, 3, 5, 8]  -- no swaps, done early
```

**When to Use:**
- Never in production (too slow)
- Educational purposes
- When you need a stable, in-place sort on very small data

> **Key Insight:** The early-exit optimization (stopping when no swaps occur) makes bubble sort O(n) on already-sorted data. This is the only advantage it has.

---

### 2. Selection Sort

**Difficulty:** Beginner

**Concept:**
Find the minimum element in the unsorted portion and swap it to the front. Repeat for each position. Selection sort minimizes the number of swaps (exactly n-1), but always does O(n^2) comparisons.

**Time Complexity:** O(n^2) always
**Space Complexity:** O(1)
**Stable:** No (swapping can change relative order of equal elements)
**In-Place:** Yes

**Go Implementation:**

```go
func selectionSort(arr []int) {
    n := len(arr)
    for i := 0; i < n-1; i++ {
        minIdx := i
        for j := i + 1; j < n; j++ {
            if arr[j] < arr[minIdx] {
                minIdx = j
            }
        }
        arr[i], arr[minIdx] = arr[minIdx], arr[i]
    }
}
```

**How It Works (Visual):**

```
[5, 3, 8, 1, 2]
Pass 1: min=1 at idx 3 → swap with idx 0 → [1, 3, 8, 5, 2]
Pass 2: min=2 at idx 4 → swap with idx 1 → [1, 2, 8, 5, 3]
Pass 3: min=3 at idx 4 → swap with idx 2 → [1, 2, 3, 5, 8]
Pass 4: min=5 at idx 3 → no swap needed  → [1, 2, 3, 5, 8]
```

**When to Use:**
- When write/swap cost is very high (minimizes swaps to n-1)
- Educational purposes
- Very small arrays

> **Key Insight:** Selection sort does the fewest swaps of any comparison sort (exactly n-1). If swaps are expensive (e.g., writing to flash memory), selection sort can be practical.

---

### 3. Insertion Sort

**Difficulty:** Beginner

**Concept:**
Build the sorted array one element at a time. Take the next unsorted element and insert it into its correct position among the already-sorted elements by shifting larger elements right. Like sorting cards in your hand.

**Time Complexity:** O(n^2) average/worst, O(n) best (already sorted)
**Space Complexity:** O(1)
**Stable:** Yes
**In-Place:** Yes

**Go Implementation:**

```go
func insertionSort(arr []int) {
    for i := 1; i < len(arr); i++ {
        key := arr[i]
        j := i - 1
        // Shift elements that are greater than key
        for j >= 0 && arr[j] > key {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = key
    }
}
```

**How It Works (Visual):**

```
[5, 3, 8, 1, 2]
i=1: key=3, shift 5 right → [3, 5, 8, 1, 2]
i=2: key=8, no shift needed → [3, 5, 8, 1, 2]
i=3: key=1, shift 8,5,3 right → [1, 3, 5, 8, 2]
i=4: key=2, shift 8,5,3 right → [1, 2, 3, 5, 8]
```

**When to Use:**
- Data is nearly sorted (O(n) in this case)
- Very small arrays (fewer than ~20 elements)
- Online algorithm (can sort as data arrives)
- Used as the base case in hybrid sorts (Tim Sort, intro sort)

> **Key Insight:** Insertion sort is the best quadratic sort for practical use. It's O(n) on nearly-sorted data, stable, in-place, and has excellent cache performance on small arrays. This is why efficient algorithms like Tim Sort use insertion sort for small subarrays.

**NeetCode Relevance:** Understanding insertion sort helps with "insertion sort list" (linked list variant) and understanding Tim Sort.

---

## Efficient (Linearithmic) Sorts

These are the workhorses of sorting. O(n log n) is the theoretical lower bound for comparison-based sorting.

---

### 4. Merge Sort

**Difficulty:** Intermediate

**Concept:**
Divide the array in half, recursively sort each half, then merge the two sorted halves. This is the classic divide-and-conquer algorithm.

**Time Complexity:** O(n log n) always
**Space Complexity:** O(n) for the temporary merge array
**Stable:** Yes
**In-Place:** No

**Go Implementation:**

```go
func mergeSort(arr []int) []int {
    if len(arr) <= 1 {
        return arr
    }

    mid := len(arr) / 2
    left := mergeSort(arr[:mid])
    right := mergeSort(arr[mid:])

    return merge(left, right)
}

func merge(left, right []int) []int {
    result := make([]int, 0, len(left)+len(right))
    i, j := 0, 0

    for i < len(left) && j < len(right) {
        if left[i] <= right[j] {  // <= for stability
            result = append(result, left[i])
            i++
        } else {
            result = append(result, right[j])
            j++
        }
    }

    result = append(result, left[i:]...)
    result = append(result, right[j:]...)
    return result
}
```

**In-Place Merge Sort Variant (for arrays):**

```go
func mergeSortInPlace(arr []int, lo, hi int) {
    if hi-lo <= 1 {
        return
    }
    mid := lo + (hi-lo)/2
    mergeSortInPlace(arr, lo, mid)
    mergeSortInPlace(arr, mid, hi)
    mergeInPlace(arr, lo, mid, hi)
}

func mergeInPlace(arr []int, lo, mid, hi int) {
    temp := make([]int, hi-lo)
    i, j, k := lo, mid, 0

    for i < mid && j < hi {
        if arr[i] <= arr[j] {
            temp[k] = arr[i]
            i++
        } else {
            temp[k] = arr[j]
            j++
        }
        k++
    }
    for i < mid {
        temp[k] = arr[i]
        i++
        k++
    }
    for j < hi {
        temp[k] = arr[j]
        j++
        k++
    }
    copy(arr[lo:hi], temp)
}
```

**Merge Sort for Linked Lists:**
Merge sort is ideal for linked lists because:
- Splitting is O(n) with slow/fast pointers (no random access needed)
- Merging is O(1) extra space (just rearrange pointers)

```go
func sortList(head *ListNode) *ListNode {
    if head == nil || head.Next == nil {
        return head
    }

    // Find middle with slow/fast
    slow, fast := head, head.Next
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
    }
    mid := slow.Next
    slow.Next = nil // split

    left := sortList(head)
    right := sortList(mid)
    return mergeLists(left, right)
}

func mergeLists(l1, l2 *ListNode) *ListNode {
    dummy := &ListNode{}
    curr := dummy
    for l1 != nil && l2 != nil {
        if l1.Val <= l2.Val {
            curr.Next = l1
            l1 = l1.Next
        } else {
            curr.Next = l2
            l2 = l2.Next
        }
        curr = curr.Next
    }
    if l1 != nil {
        curr.Next = l1
    } else {
        curr.Next = l2
    }
    return dummy.Next
}
```

**When to Use:**
- Need guaranteed O(n log n) (no worst-case degradation)
- Need stability
- Sorting linked lists (O(1) extra space)
- External sorting (sorting data too large for memory)
- Counting inversions (modified merge step)

**Common Pitfalls:**
- Forgetting to use `<=` in the merge step (breaks stability)
- Not handling empty subarrays in the merge
- Excessive memory allocation (reuse the temp array across calls)

> **Key Insight:** Merge sort's guarantee of O(n log n) in ALL cases makes it reliable. Its merge step is the key operation -- it also solves problems like "merge K sorted lists" and "count inversions."

**NeetCode Relevance:** Linked List (sort list, merge K sorted lists), used conceptually in divide-and-conquer problems.

---

### 5. Quick Sort

**Difficulty:** Intermediate

**Concept:**
Choose a pivot element, partition the array so all elements less than the pivot come before it and all greater elements come after. Then recursively sort both partitions. The key is the partition step.

**Time Complexity:** O(n log n) average, O(n^2) worst case (bad pivot choices)
**Space Complexity:** O(log n) for recursion stack
**Stable:** No
**In-Place:** Yes

### 5.1 Lomuto Partition Scheme

Simpler to understand. Pivot is typically the last element.

```go
func quickSortLomuto(arr []int, lo, hi int) {
    if lo < hi {
        p := lomutoPartition(arr, lo, hi)
        quickSortLomuto(arr, lo, p-1)
        quickSortLomuto(arr, p+1, hi)
    }
}

func lomutoPartition(arr []int, lo, hi int) int {
    pivot := arr[hi]
    i := lo - 1

    for j := lo; j < hi; j++ {
        if arr[j] <= pivot {
            i++
            arr[i], arr[j] = arr[j], arr[i]
        }
    }
    arr[i+1], arr[hi] = arr[hi], arr[i+1]
    return i + 1
}
```

### 5.2 Hoare Partition Scheme

More efficient (fewer swaps on average). Two pointers move inward.

```go
func quickSortHoare(arr []int, lo, hi int) {
    if lo < hi {
        p := hoarePartition(arr, lo, hi)
        quickSortHoare(arr, lo, p)
        quickSortHoare(arr, p+1, hi)
    }
}

func hoarePartition(arr []int, lo, hi int) int {
    pivot := arr[lo+(hi-lo)/2]
    i, j := lo-1, hi+1

    for {
        for {
            i++
            if arr[i] >= pivot {
                break
            }
        }
        for {
            j--
            if arr[j] <= pivot {
                break
            }
        }
        if i >= j {
            return j
        }
        arr[i], arr[j] = arr[j], arr[i]
    }
}
```

### 5.3 Pivot Selection Strategies

| Strategy | Description | Worst Case |
|----------|-------------|------------|
| First/Last | Fixed position | O(n^2) on sorted data |
| Random | Random index | O(n^2) very unlikely |
| Median of Three | Median of first, middle, last | O(n^2) rare |

```go
// Median of three pivot selection
func medianOfThree(arr []int, lo, hi int) int {
    mid := lo + (hi-lo)/2
    if arr[lo] > arr[mid] {
        arr[lo], arr[mid] = arr[mid], arr[lo]
    }
    if arr[lo] > arr[hi] {
        arr[lo], arr[hi] = arr[hi], arr[lo]
    }
    if arr[mid] > arr[hi] {
        arr[mid], arr[hi] = arr[hi], arr[mid]
    }
    return mid
}
```

### 5.4 Quickselect (Kth Smallest/Largest)

A variant that only recurses into one partition -- finds the Kth element in O(n) average time.

```go
func findKthSmallest(arr []int, k int) int {
    return quickselect(arr, 0, len(arr)-1, k-1) // 0-indexed
}

func quickselect(arr []int, lo, hi, k int) int {
    if lo == hi {
        return arr[lo]
    }

    pivotIdx := lomutoPartition(arr, lo, hi)

    if k == pivotIdx {
        return arr[k]
    } else if k < pivotIdx {
        return quickselect(arr, lo, pivotIdx-1, k)
    } else {
        return quickselect(arr, pivotIdx+1, hi, k)
    }
}

// Kth largest = (n-k)th smallest
func findKthLargest(arr []int, k int) int {
    return findKthSmallest(arr, len(arr)-k+1)
}
```

**When to Use:**
- General-purpose sorting (fastest in practice for most data)
- When you need in-place sorting
- Quickselect for Kth element problems (avoids full sort)

**Common Pitfalls:**
- O(n^2) on already sorted data with naive pivot (use random or median-of-three)
- Hoare partition: the recursive calls are `(lo, p)` and `(p+1, hi)`, NOT `(lo, p-1)` and `(p+1, hi)`
- Lomuto partition: the recursive calls are `(lo, p-1)` and `(p+1, hi)`
- Stack overflow on large sorted arrays (use iterative or randomized pivot)

> **Key Insight:** Quick sort is the fastest comparison sort in practice due to excellent cache locality and low constant factors. Quickselect is the interview trick for "Kth largest/smallest" -- O(n) average vs O(n log n) for sorting.

**NeetCode Relevance:** Heap / Priority Queue (Kth largest element -- quickselect is an alternative to heap).

---

### 6. Heap Sort

**Difficulty:** Intermediate

**Concept:**
Build a max-heap from the array, then repeatedly extract the maximum and place it at the end. Uses the heap data structure to achieve guaranteed O(n log n) with O(1) extra space.

**Time Complexity:** O(n log n) always
**Space Complexity:** O(1)
**Stable:** No
**In-Place:** Yes

**Go Implementation:**

```go
func heapSort(arr []int) {
    n := len(arr)

    // Build max heap (start from last non-leaf node)
    for i := n/2 - 1; i >= 0; i-- {
        heapify(arr, n, i)
    }

    // Extract elements one by one
    for i := n - 1; i > 0; i-- {
        arr[0], arr[i] = arr[i], arr[0] // move max to end
        heapify(arr, i, 0)               // heapify reduced heap
    }
}

func heapify(arr []int, size, root int) {
    largest := root
    left := 2*root + 1
    right := 2*root + 2

    if left < size && arr[left] > arr[largest] {
        largest = left
    }
    if right < size && arr[right] > arr[largest] {
        largest = right
    }

    if largest != root {
        arr[root], arr[largest] = arr[largest], arr[root]
        heapify(arr, size, largest) // recursively fix the subtree
    }
}
```

**How It Works (Visual):**

```
Array:   [4, 10, 3, 5, 1]

Build max heap:
         10
        /  \
       5    3
      / \
     4   1

Extract max (10), heapify:
         5
        / \
       4   3
      /
     1
Array so far: [..., 10]

Repeat until empty → sorted array
```

**When to Use:**
- Need guaranteed O(n log n) AND O(1) extra space
- When worst-case performance matters more than average-case speed
- When you can't afford O(n) extra space for merge sort

**Common Pitfalls:**
- Off-by-one in child index calculations (left = `2i+1`, right = `2i+2` for 0-indexed)
- Forgetting to reduce the heap size after each extraction
- Building the heap bottom-up is O(n), NOT O(n log n)

> **Key Insight:** Heap sort is the only comparison sort that is both O(n log n) worst-case AND O(1) space. In practice, it's slower than quicksort due to poor cache locality (jumping around the array), but it has the best worst-case guarantees.

**NeetCode Relevance:** Understanding heapify is essential for Heap / Priority Queue problems.

---

## Non-Comparison-Based (Linear) Sorts

These break the O(n log n) comparison sort barrier by exploiting properties of the data.

---

### 7. Counting Sort

**Difficulty:** Intermediate

**Concept:**
Count the occurrences of each value, then use those counts to place elements in order. Only works when the range of values (k) is known and not too large.

**Time Complexity:** O(n + k) where k = range of values
**Space Complexity:** O(k) for the count array
**Stable:** Yes (with the proper implementation)
**In-Place:** No

**Go Implementation:**

```go
// Simple counting sort (for non-negative integers)
func countingSort(arr []int) []int {
    if len(arr) == 0 {
        return arr
    }

    // Find range
    maxVal := arr[0]
    for _, v := range arr {
        if v > maxVal {
            maxVal = v
        }
    }

    // Count occurrences
    count := make([]int, maxVal+1)
    for _, v := range arr {
        count[v]++
    }

    // Build sorted array
    result := make([]int, 0, len(arr))
    for val, cnt := range count {
        for i := 0; i < cnt; i++ {
            result = append(result, val)
        }
    }
    return result
}

// Stable counting sort (preserves relative order)
func countingSortStable(arr []int, maxVal int) []int {
    n := len(arr)
    count := make([]int, maxVal+1)
    output := make([]int, n)

    // Count occurrences
    for _, v := range arr {
        count[v]++
    }

    // Cumulative count (each count[i] = number of elements <= i)
    for i := 1; i <= maxVal; i++ {
        count[i] += count[i-1]
    }

    // Build output array (iterate backwards for stability)
    for i := n - 1; i >= 0; i-- {
        output[count[arr[i]]-1] = arr[i]
        count[arr[i]]--
    }
    return output
}
```

**When to Use:**
- Integer data with a small, known range
- When you need O(n) sorting
- As a subroutine in radix sort

**When NOT to Use:**
- Range of values is much larger than n (wastes space)
- Floating-point or string data
- Negative numbers (needs offset adjustment)

> **Key Insight:** Counting sort doesn't compare elements at all -- it counts them. This is why it can beat the O(n log n) comparison sort lower bound. But it trades space for speed: O(k) extra memory.

---

### 8. Radix Sort

**Difficulty:** Intermediate

**Concept:**
Sort numbers digit by digit, from least significant to most significant (LSD) or vice versa (MSD). Each digit-level sort uses a stable sort like counting sort. After processing all digits, the array is sorted.

**Time Complexity:** O(d * (n + k)) where d = number of digits, k = base (usually 10)
**Space Complexity:** O(n + k)
**Stable:** Yes
**In-Place:** No

**Go Implementation (LSD Radix Sort):**

```go
func radixSort(arr []int) []int {
    if len(arr) == 0 {
        return arr
    }

    // Find maximum to determine number of digits
    maxVal := arr[0]
    for _, v := range arr {
        if v > maxVal {
            maxVal = v
        }
    }

    // Sort by each digit (1s, 10s, 100s, ...)
    for exp := 1; maxVal/exp > 0; exp *= 10 {
        arr = countingSortByDigit(arr, exp)
    }
    return arr
}

func countingSortByDigit(arr []int, exp int) []int {
    n := len(arr)
    output := make([]int, n)
    count := make([]int, 10) // digits 0-9

    // Count occurrences of each digit
    for _, v := range arr {
        digit := (v / exp) % 10
        count[digit]++
    }

    // Cumulative count
    for i := 1; i < 10; i++ {
        count[i] += count[i-1]
    }

    // Build output (backwards for stability)
    for i := n - 1; i >= 0; i-- {
        digit := (arr[i] / exp) % 10
        output[count[digit]-1] = arr[i]
        count[digit]--
    }
    return output
}
```

**How It Works (Visual):**

```
Input: [170, 45, 75, 90, 802, 24, 2, 66]

Sort by 1s digit: [170, 90, 802, 2, 24, 45, 75, 66]
Sort by 10s digit: [802, 2, 24, 45, 66, 170, 75, 90]
Sort by 100s digit: [2, 24, 45, 66, 75, 90, 170, 802]
```

**When to Use:**
- Sorting integers or fixed-length strings
- When d (number of digits) is small relative to log(n)
- Large datasets of bounded-length keys

> **Key Insight:** Radix sort is effectively O(n) when the number of digits is constant (e.g., sorting 32-bit integers: d=10 in base 10, or d=4 in base 256). It's faster than comparison sorts for large datasets with short keys.

---

### 9. Bucket Sort

**Difficulty:** Intermediate

**Concept:**
Distribute elements into "buckets" based on their value range, sort each bucket individually (with insertion sort or any other sort), then concatenate. Works best when input is uniformly distributed.

**Time Complexity:** O(n + k) average, O(n^2) worst case (all elements in one bucket)
**Space Complexity:** O(n + k) where k = number of buckets
**Stable:** Depends on inner sort
**In-Place:** No

**Go Implementation:**

```go
func bucketSort(arr []float64) []float64 {
    n := len(arr)
    if n == 0 {
        return arr
    }

    // Create buckets
    buckets := make([][]float64, n)
    for i := range buckets {
        buckets[i] = []float64{}
    }

    // Distribute elements into buckets (assuming values in [0, 1))
    for _, v := range arr {
        idx := int(v * float64(n))
        if idx >= n {
            idx = n - 1
        }
        buckets[idx] = append(buckets[idx], v)
    }

    // Sort each bucket (insertion sort for small buckets)
    for i := range buckets {
        insertionSortFloat(buckets[i])
    }

    // Concatenate
    result := make([]float64, 0, n)
    for _, bucket := range buckets {
        result = append(result, bucket...)
    }
    return result
}

func insertionSortFloat(arr []float64) {
    for i := 1; i < len(arr); i++ {
        key := arr[i]
        j := i - 1
        for j >= 0 && arr[j] > key {
            arr[j+1] = arr[j]
            j--
        }
        arr[j+1] = key
    }
}
```

**When to Use:**
- Input is uniformly distributed over a known range
- Combined with other sorts (insertion sort for small buckets)
- Sorting floating-point numbers

**When NOT to Use:**
- Data is clustered (most elements end up in the same bucket)
- Range is not known in advance

> **Key Insight:** Bucket sort achieves O(n) by leveraging the distribution of data. If you know the data is roughly uniform, bucket sort is incredibly efficient. It's a distribution sort, not a comparison sort.

---

## Hybrid and Practical Sorts

---

### 10. Tim Sort

**Difficulty:** Intermediate (conceptual)

**Concept:**
Tim Sort is a hybrid sorting algorithm combining merge sort and insertion sort. It's the algorithm behind Go's `sort.Slice`, Python's `sorted()`, and Java's `Arrays.sort()` for objects. It's designed for real-world data that often has existing order.

**How It Works:**
1. Divide the array into small chunks called "runs" (typically 32-64 elements).
2. Sort each run using insertion sort (fast on small, nearly-sorted data).
3. Merge runs using a modified merge sort with optimizations:
   - **Galloping mode:** When one run consistently "wins" the merge, skip ahead exponentially.
   - **Run detection:** Find existing sorted subsequences (natural runs) and extend them.
   - **Min-run size:** Chosen to make the number of merges a power of 2 (efficient).

**Time Complexity:** O(n) best (already sorted), O(n log n) average/worst
**Space Complexity:** O(n)
**Stable:** Yes

**Go Standard Library Usage:**

```go
import "sort"

// Sort a slice of ints
nums := []int{5, 3, 8, 1, 2}
sort.Ints(nums)

// Sort with custom comparator
sort.Slice(nums, func(i, j int) bool {
    return nums[i] < nums[j]
})

// Sort a struct slice
type Person struct {
    Name string
    Age  int
}
people := []Person{{"Bob", 30}, {"Alice", 25}, {"Charlie", 35}}
sort.Slice(people, func(i, j int) bool {
    return people[i].Age < people[j].Age
})

// Check if sorted
isSorted := sort.IntsAreSorted(nums)

// sort.SliceStable guarantees stability
sort.SliceStable(people, func(i, j int) bool {
    return people[i].Name < people[j].Name
})
```

**Why Tim Sort is Practical:**
- Real data often has "runs" of already-sorted elements
- Insertion sort's O(n) on nearly-sorted data makes the run-sorting phase fast
- The merge phase benefits from galloping when runs are large
- Stable: equal elements keep their original order

> **Key Insight:** In interviews, just use `sort.Slice()` or `sort.Ints()` when you need to sort. Know that it's O(n log n), stable (with `SliceStable`), and uses Tim Sort internally. Understanding Tim Sort conceptually shows depth of knowledge.

**NeetCode Relevance:** Many problems start with "sort the input" -- use Go's built-in sort.

---

### 11. Dutch National Flag / 3-Way Partition

**Difficulty:** Intermediate

**Concept:**
Partition an array into three sections based on a pivot value: elements less than pivot, equal to pivot, and greater than pivot. Uses three pointers (low, mid, high) in a single pass.

**Time Complexity:** O(n) single pass
**Space Complexity:** O(1)
**Stable:** No
**In-Place:** Yes

**Go Implementation:**

```go
// Sort Colors (LeetCode 75): sort array of 0s, 1s, and 2s
func sortColors(nums []int) {
    lo, mid, hi := 0, 0, len(nums)-1

    for mid <= hi {
        switch nums[mid] {
        case 0:
            nums[lo], nums[mid] = nums[mid], nums[lo]
            lo++
            mid++
        case 1:
            mid++
        case 2:
            nums[mid], nums[hi] = nums[hi], nums[mid]
            hi--
            // Don't increment mid -- the swapped element needs checking
        }
    }
}

// General 3-way partition around a pivot
func threeWayPartition(arr []int, pivot int) {
    lo, mid, hi := 0, 0, len(arr)-1

    for mid <= hi {
        if arr[mid] < pivot {
            arr[lo], arr[mid] = arr[mid], arr[lo]
            lo++
            mid++
        } else if arr[mid] == pivot {
            mid++
        } else {
            arr[mid], arr[hi] = arr[hi], arr[mid]
            hi--
        }
    }
    // Result: arr[0..lo-1] < pivot, arr[lo..hi] == pivot, arr[hi+1..n-1] > pivot
}
```

**How It Works (Visual):**

```
Input: [2, 0, 2, 1, 1, 0]
       lo,mid           hi

Step 1: nums[mid]=2, swap with hi → [0, 0, 2, 1, 1, 2]  hi--
Step 2: nums[mid]=0, swap with lo → [0, 0, 2, 1, 1, 2]  lo++, mid++
Step 3: nums[mid]=0, swap with lo → [0, 0, 2, 1, 1, 2]  lo++, mid++
Step 4: nums[mid]=2, swap with hi → [0, 0, 1, 1, 2, 2]  hi--
Step 5: nums[mid]=1, mid++
Step 6: nums[mid]=1, mid++
mid > hi → done: [0, 0, 1, 1, 2, 2]
```

**When to Use:**
- Partitioning into exactly 3 groups
- Sort Colors problem (0, 1, 2)
- As an improvement to quicksort when there are many duplicate elements (3-way quicksort)

**Common Pitfalls:**
- Incrementing `mid` after swapping with `hi` (the swapped-in element hasn't been checked yet)
- Off-by-one in the termination condition (`mid <= hi`, not `mid < hi`)

> **Key Insight:** The Dutch National Flag is a single-pass O(n) partitioning algorithm. It's a direct interview question (Sort Colors) and also improves quicksort performance on arrays with many duplicates.

**NeetCode Relevance:** Two Pointers (Sort Colors), and understanding 3-way partition improves quicksort.

---

## Decision Guide: Which Sort to Use?

```
Is the data already mostly sorted?
├── Yes → Insertion Sort (small) or Tim Sort / sort.Slice (large)
└── No
    ├── Are the values integers with a small range?
    │   └── Yes → Counting Sort
    ├── Are the values integers with a large range but few digits?
    │   └── Yes → Radix Sort
    ├── Is the data uniformly distributed?
    │   └── Yes → Bucket Sort
    ├── Do you need stability?
    │   ├── Yes → Merge Sort or sort.SliceStable
    │   └── No
    │       ├── Need O(1) extra space?
    │       │   ├── Yes → Heap Sort (guaranteed) or Quick Sort (usually faster)
    │       │   └── No → Merge Sort
    │       └── Need fastest average case?
    │           └── Quick Sort
    └── In an interview?
        └── Use sort.Slice() and explain what it does internally
```

---

## Progress Checklist

- [ ] Bubble Sort
- [ ] Selection Sort
- [ ] Insertion Sort
- [ ] Merge Sort (arrays)
- [ ] Merge Sort (linked lists)
- [ ] Quick Sort (Lomuto partition)
- [ ] Quick Sort (Hoare partition)
- [ ] Quickselect (Kth element)
- [ ] Heap Sort
- [ ] Counting Sort
- [ ] Radix Sort
- [ ] Bucket Sort
- [ ] Tim Sort (conceptual + Go's sort.Slice)
- [ ] Dutch National Flag / 3-Way Partition

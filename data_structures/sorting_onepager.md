# Sorting Algorithms -- One-Pager

## Core Concept

Sorting rearranges elements into a defined order. The choice of algorithm depends on **input size**,
**whether the data is nearly sorted**, **space constraints**, and **whether stability matters**
(preserving relative order of equal elements).

For coding interviews, you need to *know* how the fundamental sorts work (to implement variants),
and *recognize* when a non-comparison sort (bucket/counting) gives you O(n).

---

## Complexity Overview

| Algorithm      | Best      | Average    | Worst      | Space   | Stable? | In-Place? |
|----------------|-----------|------------|------------|---------|---------|-----------|
| Insertion Sort | O(n)      | O(n^2)    | O(n^2)    | O(1)    | Yes     | Yes       |
| Selection Sort | O(n^2)   | O(n^2)    | O(n^2)    | O(1)    | No      | Yes       |
| Bubble Sort    | O(n)      | O(n^2)    | O(n^2)    | O(1)    | Yes     | Yes       |
| Merge Sort     | O(n log n)| O(n log n)| O(n log n)| O(n)    | Yes     | No        |
| Quick Sort     | O(n log n)| O(n log n)| O(n^2)    | O(log n)| No      | Yes       |
| Heap Sort      | O(n log n)| O(n log n)| O(n log n)| O(1)    | No      | Yes       |
| Counting Sort  | O(n+k)   | O(n+k)    | O(n+k)    | O(k)    | Yes     | No        |
| Bucket Sort    | O(n+k)   | O(n+k)    | O(n^2)    | O(n+k)  | Yes*    | No        |

*k = range of input values. Bucket sort stability depends on the inner sort.*

---

## Quadratic Sorts

### 1. Insertion Sort

Build a sorted portion from left to right. Take each element and insert it into its correct
position among the already-sorted elements by shifting.

**Best for:** small arrays, nearly-sorted data (O(n) best case).

```go
func insertionSort(nums []int) {
    for i := 1; i < len(nums); i++ {
        key := nums[i]
        j := i - 1
        for j >= 0 && nums[j] > key {
            nums[j+1] = nums[j]
            j--
        }
        nums[j+1] = key
    }
}
```

**How it works on `[5, 3, 8, 1]`:**
```
[5 | 3, 8, 1]  → insert 3 → [3, 5 | 8, 1]
[3, 5 | 8, 1]  → insert 8 → [3, 5, 8 | 1]
[3, 5, 8 | 1]  → insert 1 → [1, 3, 5, 8]
```

This is what you used in q5 (`mapInsSort`). The inner loop shifts elements right to make room.

---

### 2. Selection Sort

Find the minimum element in the unsorted portion, swap it to the front. Repeat.

**Best for:** when you need minimal swaps (each element moves at most once).

```go
func selectionSort(nums []int) {
    for i := 0; i < len(nums)-1; i++ {
        minIdx := i
        for j := i + 1; j < len(nums); j++ {
            if nums[j] < nums[minIdx] {
                minIdx = j
            }
        }
        nums[i], nums[minIdx] = nums[minIdx], nums[i]
    }
}
```

**How it works on `[5, 3, 8, 1]`:**
```
find min(5,3,8,1)=1 → swap → [1 | 3, 8, 5]
find min(3,8,5)=3   → swap → [1, 3 | 8, 5]
find min(8,5)=5     → swap → [1, 3, 5 | 8]
```

---

### 3. Bubble Sort

Repeatedly walk through the array, swapping adjacent elements that are out of order.
Each pass "bubbles" the largest unsorted element to the end.

**Best for:** almost never in practice. Know it for interviews, but prefer insertion sort.

```go
func bubbleSort(nums []int) {
    n := len(nums)
    for i := 0; i < n-1; i++ {
        swapped := false
        for j := 0; j < n-1-i; j++ {
            if nums[j] > nums[j+1] {
                nums[j], nums[j+1] = nums[j+1], nums[j]
                swapped = true
            }
        }
        if !swapped {
            break // already sorted → O(n) best case
        }
    }
}
```

---

## Efficient Sorts

### 4. Merge Sort

Divide the array in half recursively, sort each half, then merge the two sorted halves.

**Best for:** when you need guaranteed O(n log n) and stability. Used in linked list sorting.

```go
func mergeSort(nums []int) []int {
    if len(nums) <= 1 {
        return nums
    }
    mid := len(nums) / 2
    left := mergeSort(nums[:mid])
    right := mergeSort(nums[mid:])
    return merge(left, right)
}

func merge(a, b []int) []int {
    result := make([]int, 0, len(a)+len(b))
    i, j := 0, 0
    for i < len(a) && j < len(b) {
        if a[i] <= b[j] {
            result = append(result, a[i])
            i++
        } else {
            result = append(result, b[j])
            j++
        }
    }
    result = append(result, a[i:]...)
    result = append(result, b[j:]...)
    return result
}
```

**Trade-off:** O(n) extra space for the merge step. That's why it's not in-place.

---

### 5. Quick Sort

Pick a pivot, partition the array so everything < pivot is left and everything > pivot is right.
Recurse on both sides.

**Best for:** general-purpose. Fastest in practice due to cache locality. Go's `sort.Slice` uses
a quicksort variant internally.

```go
func quickSort(nums []int, lo, hi int) {
    if lo >= hi {
        return
    }
    pivot := partition(nums, lo, hi)
    quickSort(nums, lo, pivot-1)
    quickSort(nums, pivot+1, hi)
}

func partition(nums []int, lo, hi int) int {
    pivot := nums[hi]
    i := lo
    for j := lo; j < hi; j++ {
        if nums[j] < pivot {
            nums[i], nums[j] = nums[j], nums[i]
            i++
        }
    }
    nums[i], nums[hi] = nums[hi], nums[i]
    return i
}

// Usage: quickSort(nums, 0, len(nums)-1)
```

**Pitfall:** worst case O(n^2) happens when the pivot is always the smallest/largest element
(e.g., already sorted array with last-element pivot). Fix with randomized pivot selection.

---

### 6. Heap Sort

Build a max-heap from the array, then repeatedly extract the max and place it at the end.

**Best for:** when you need guaranteed O(n log n) with O(1) space.

```go
func heapSort(nums []int) {
    n := len(nums)

    // Build max heap (start from last non-leaf)
    for i := n/2 - 1; i >= 0; i-- {
        heapify(nums, n, i)
    }

    // Extract max one by one
    for i := n - 1; i > 0; i-- {
        nums[0], nums[i] = nums[i], nums[0]
        heapify(nums, i, 0)
    }
}

func heapify(nums []int, size, root int) {
    largest := root
    left := 2*root + 1
    right := 2*root + 2

    if left < size && nums[left] > nums[largest] {
        largest = left
    }
    if right < size && nums[right] > nums[largest] {
        largest = right
    }
    if largest != root {
        nums[root], nums[largest] = nums[largest], nums[root]
        heapify(nums, size, largest)
    }
}
```

---

## Non-Comparison Sorts (Linear Time)

These break the O(n log n) barrier by not comparing elements — they use the values directly.

### 7. Counting Sort

Count occurrences of each value, then reconstruct the array from counts.

**Best for:** integers in a small, known range. **This is the optimal approach for q5
(Top K Frequent Elements)** — count frequencies, then use frequency as the bucket index.

```go
func countingSort(nums []int, maxVal int) []int {
    counts := make([]int, maxVal+1)
    for _, v := range nums {
        counts[v]++
    }

    result := make([]int, 0, len(nums))
    for val, cnt := range counts {
        for c := 0; c < cnt; c++ {
            result = append(result, val)
        }
    }
    return result
}
```

**Limitation:** needs to know the range. Space is O(k) where k = max value, so impractical
if values are huge (e.g., sorting arbitrary 64-bit ints).

---

### 8. Bucket Sort

Distribute elements into buckets (sub-arrays), sort each bucket, then concatenate.

**Best for:** uniformly distributed data. This is the key insight for the Top K Frequent
Elements problem — use frequency as the bucket index, max frequency is bounded by n.

```go
func bucketSort(nums []int, bucketCount int) []int {
    if len(nums) == 0 {
        return nums
    }

    // Find range
    maxVal := nums[0]
    for _, v := range nums {
        if v > maxVal {
            maxVal = v
        }
    }

    // Distribute into buckets
    buckets := make([][]int, bucketCount)
    for _, v := range nums {
        idx := v * (bucketCount - 1) / maxVal
        buckets[idx] = append(buckets[idx], v)
    }

    // Sort each bucket (insertion sort for small buckets)
    result := make([]int, 0, len(nums))
    for _, bucket := range buckets {
        insertionSort(bucket)
        result = append(result, bucket...)
    }
    return result
}
```

**Q5 connection:** In Top K Frequent, you make `n+1` buckets where index = frequency.
`buckets[3]` holds all numbers that appeared 3 times. Walk from the end to collect the top k.

---

## When to Use Which

| Situation                              | Best Choice      | Why                                    |
|----------------------------------------|------------------|----------------------------------------|
| Small array (n < 20)                   | Insertion Sort   | Low overhead, fast for small n         |
| Nearly sorted data                     | Insertion Sort   | O(n) best case                         |
| General purpose                        | Quick Sort       | Fastest average case, cache-friendly   |
| Need guaranteed O(n log n)             | Merge Sort       | No O(n^2) worst case                   |
| O(n log n) with O(1) space            | Heap Sort        | In-place, guaranteed                   |
| Integers in small range                | Counting Sort    | O(n+k), beats comparison sorts         |
| Need to sort by frequency              | Bucket Sort      | Group by count, O(n)                   |
| Sorting linked lists                   | Merge Sort       | No random access needed                |
| Need stability                         | Merge Sort       | Stable + efficient                     |

---

## Interview Relevance

You rarely implement a full sort in interviews. What matters is:

1. **Knowing the complexities** — to analyze your solution
2. **Recognizing when non-comparison sorts apply** — this is the O(n) insight for problems like q5
3. **Understanding partitioning** — Quick Select (quicksort's partition step) solves "kth largest" in O(n) average

| Pattern                  | Sort Concept Used         | Example Problems               |
|--------------------------|---------------------------|--------------------------------|
| Frequency + Top K        | Bucket Sort / Counting    | Top K Frequent Elements        |
| Kth Largest/Smallest     | Quick Select (partition)  | Kth Largest Element            |
| Custom ordering          | Comparator with sort.Slice| Largest Number, Sort Colors    |
| Interval problems        | Sort by start time        | Merge Intervals                |
| Meet in the middle       | Sort + two pointers       | 3Sum, Two Sum II               |

---

## Quick Reference Card

```
Insertion:  shift elements right, insert in position    O(n^2) / O(1) space
Selection:  find min, swap to front                     O(n^2) / O(1) space
Bubble:     swap adjacent out-of-order pairs            O(n^2) / O(1) space
Merge:      divide, sort halves, merge                  O(n log n) / O(n) space
Quick:      pick pivot, partition, recurse              O(n log n) avg / O(log n) space
Heap:       build max-heap, extract max repeatedly      O(n log n) / O(1) space
Counting:   count values, reconstruct                   O(n+k) / O(k) space
Bucket:     distribute into buckets, sort each          O(n+k) avg / O(n+k) space

Go built-in:  sort.Ints(s)  or  slices.Sort(s)         Introsort variant, O(n log n)
```

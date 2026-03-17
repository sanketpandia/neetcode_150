# Heaps / Priority Queues -- One-Pager

## Core Concept

A **heap** is a complete binary tree where each node satisfies the **heap property**: in a **min-heap**, every parent is smaller than its children; in a **max-heap**, every parent is larger. This structure enables O(1) access to the minimum (or maximum) element and O(log n) insertions and deletions.

**Priority queue** is the abstract data type; **heap** is the concrete implementation. Think of a priority queue as an interface that promises "give me the highest (or lowest) priority element efficiently."

**Complete Binary Tree:** All levels are fully filled except possibly the last, which fills left to right. This property allows heaps to be efficiently stored in an array without pointers:
- For 0-indexed array at position `i`:
  - Parent: `(i - 1) / 2`
  - Left child: `2*i + 1`
  - Right child: `2*i + 2`

In Go, the `container/heap` package provides heap operations but requires you to implement the `heap.Interface` (Len, Less, Swap, Push, Pop). This design gives flexibility but requires some boilerplate.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert (push) | O(log n) | Add to end, sift up to restore heap property |
| Extract min/max (pop) | O(log n) | Remove root, move last to root, sift down |
| Peek min/max | O(1) | Root element (index 0) |
| Build heap from array | O(n) | Bottom-up heapify (Floyd's algorithm) |
| Search arbitrary element | O(n) | No ordering beyond parent-child |
| Delete arbitrary element | O(log n) | If you have the index (rare) |
| Heapify (re-establish property) | O(log n) | Per element, O(n) for entire array |

**Space Complexity:** O(n) for storing n elements in the array.

**Why build is O(n), not O(n log n):** Bottom-up heapify processes lower levels (many nodes) with small sift-down distances, and upper levels (few nodes) with large distances. The math works out to O(n) total.

---

## Implementation Patterns

### 1. Min-Heap Using container/heap

```go
import "container/heap"

// Min-heap of integers
type MinHeap []int

func (h MinHeap) Len() int            { return len(h) }
func (h MinHeap) Less(i, j int) bool  { return h[i] < h[j] }  // < for min-heap
func (h MinHeap) Swap(i, j int)       { h[i], h[j] = h[j], h[i] }

func (h *MinHeap) Push(x interface{}) {
    *h = append(*h, x.(int))
}

func (h *MinHeap) Pop() interface{} {
    old := *h
    n := len(old)
    x := old[n-1]
    *h = old[:n-1]
    return x
}

// Usage
func main() {
    h := &MinHeap{5, 3, 8, 1, 9}
    heap.Init(h)              // O(n) heapify
    heap.Push(h, 2)           // O(log n)
    min := heap.Pop(h).(int)  // O(log n), returns 1
    peek := (*h)[0]           // O(1) peek at minimum
}
```

### 2. Max-Heap (Flip the Comparison)

```go
type MaxHeap []int

func (h MaxHeap) Len() int            { return len(h) }
func (h MaxHeap) Less(i, j int) bool  { return h[i] > h[j] }  // > for max-heap
func (h MaxHeap) Swap(i, j int)       { h[i], h[j] = h[j], h[i] }

func (h *MaxHeap) Push(x interface{}) {
    *h = append(*h, x.(int))
}

func (h *MaxHeap) Pop() interface{} {
    old := *h
    n := len(old)
    x := old[n-1]
    *h = old[:n-1]
    return x
}
```

### 3. Custom Type Heap (e.g., pairs)

```go
type Item struct {
    value    int
    priority int
}

type PriorityQueue []*Item

func (pq PriorityQueue) Len() int { return len(pq) }
func (pq PriorityQueue) Less(i, j int) bool {
    return pq[i].priority < pq[j].priority  // min-heap by priority
}
func (pq PriorityQueue) Swap(i, j int) { pq[i], pq[j] = pq[j], pq[i] }

func (pq *PriorityQueue) Push(x interface{}) {
    item := x.(*Item)
    *pq = append(*pq, item)
}

func (pq *PriorityQueue) Pop() interface{} {
    old := *pq
    n := len(old)
    item := old[n-1]
    *pq = old[:n-1]
    return item
}
```

### 4. Top K Elements Pattern

Use a min-heap of size K to track the K largest elements.

```go
func findKthLargest(nums []int, k int) int {
    h := &MinHeap{}
    heap.Init(h)

    for _, num := range nums {
        heap.Push(h, num)
        if h.Len() > k {
            heap.Pop(h)  // remove smallest
        }
    }
    return (*h)[0]  // kth largest is the min of the K largest
}
```

**Why min-heap for K largest?** We want to maintain the K largest elements seen so far. The smallest of these K is the Kth largest overall. When a new element comes in, if it's larger than the current smallest, we evict the smallest and add the new element.

### 5. Merge K Sorted Lists

Use a min-heap to track the smallest current element from each list.

```go
type ListNode struct {
    Val  int
    Next *ListNode
}

type HeapNode struct {
    node *ListNode
}

type MinListHeap []HeapNode

func (h MinListHeap) Len() int            { return len(h) }
func (h MinListHeap) Less(i, j int) bool  { return h[i].node.Val < h[j].node.Val }
func (h MinListHeap) Swap(i, j int)       { h[i], h[j] = h[j], h[i] }

func (h *MinListHeap) Push(x interface{}) {
    *h = append(*h, x.(HeapNode))
}

func (h *MinListHeap) Pop() interface{} {
    old := *h
    n := len(old)
    x := old[n-1]
    *h = old[:n-1]
    return x
}

func mergeKLists(lists []*ListNode) *ListNode {
    h := &MinListHeap{}
    heap.Init(h)

    // Initialize heap with first node of each list
    for _, list := range lists {
        if list != nil {
            heap.Push(h, HeapNode{node: list})
        }
    }

    dummy := &ListNode{}
    current := dummy

    for h.Len() > 0 {
        smallest := heap.Pop(h).(HeapNode)
        current.Next = smallest.node
        current = current.Next

        if smallest.node.Next != nil {
            heap.Push(h, HeapNode{node: smallest.node.Next})
        }
    }

    return dummy.Next
}
```

### 6. Two-Heap Median Finder

Maintain two heaps: max-heap for lower half, min-heap for upper half.

```go
type MedianFinder struct {
    lower *MaxHeap  // max-heap for smaller half
    upper *MinHeap  // min-heap for larger half
}

func Constructor() MedianFinder {
    lower := &MaxHeap{}
    upper := &MinHeap{}
    heap.Init(lower)
    heap.Init(upper)
    return MedianFinder{lower: lower, upper: upper}
}

func (mf *MedianFinder) AddNum(num int) {
    // Add to lower half by default
    heap.Push(mf.lower, num)

    // Balance: ensure all elements in lower <= all elements in upper
    if mf.lower.Len() > 0 && mf.upper.Len() > 0 && (*mf.lower)[0] > (*mf.upper)[0] {
        val := heap.Pop(mf.lower).(int)
        heap.Push(mf.upper, val)
    }

    // Balance sizes: lower can have at most 1 more element than upper
    if mf.lower.Len() > mf.upper.Len()+1 {
        val := heap.Pop(mf.lower).(int)
        heap.Push(mf.upper, val)
    }
    if mf.upper.Len() > mf.lower.Len() {
        val := heap.Pop(mf.upper).(int)
        heap.Push(mf.lower, val)
    }
}

func (mf *MedianFinder) FindMedian() float64 {
    if mf.lower.Len() > mf.upper.Len() {
        return float64((*mf.lower)[0])
    }
    return float64((*mf.lower)[0]+(*mf.upper)[0]) / 2.0
}
```

---

## When to Use

| Scenario | Use Heap? | Alternative |
|----------|-----------|-------------|
| Need repeated access to min/max | Yes | Sorting + iteration (O(n log n) once, but inflexible) |
| Top K elements | Yes | Sorting entire array (O(n log n) vs O(n log k)) |
| Dynamic dataset with changing priorities | Yes | Sorted array (O(n) insert) |
| Find median in a stream | Yes (two heaps) | Sorting after each insert (expensive) |
| Merge K sorted lists/arrays | Yes | Merge two at a time (less efficient) |
| Task scheduling by priority | Yes | - |
| Static dataset, one-time min/max | No | Simple linear scan (O(n)) |
| Need to access arbitrary elements | No | Array or hash map |

---

## Common Pitfalls

1. **Confusing `heap.Push` with `h.Push`.** Use `heap.Push(h, val)` (the package function), not `h.Push(val)`. The package function calls your `Push` method then fixes the heap property.

2. **Forgetting to use pointer receivers.** The `Push` and `Pop` methods must have pointer receivers (`*MinHeap`) to modify the underlying slice.

3. **Wrong comparison for max-heap.** Min-heap uses `<`; max-heap uses `>`. Easy to mix up.

4. **Not calling `heap.Init`.** If you start with a non-empty slice, you must call `heap.Init(h)` to establish the heap property. Skipping this leads to incorrect behavior.

5. **Accessing last element instead of first for peek.** The min/max is always at index 0, not `len(h)-1`.

6. **Modifying heap directly.** Don't do `(*h)[0] = newVal` to change the root. Use `heap.Pop` followed by `heap.Push`, or use `heap.Fix` if you know the index.

7. **Using heap for problems that don't need it.** If you can solve the problem with a single pass or simple sorting, a heap adds unnecessary complexity.

8. **Integer overflow in two-heap median.** When computing `(lower + upper) / 2`, cast to `float64` before division to avoid truncation.

---

## Interview Relevance

Heaps are essential for "Top K", "Kth largest/smallest", and streaming data problems.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Top K elements | "K largest", "K smallest", "K closest" | Kth Largest Element, K Closest Points to Origin |
| Merge K sorted | "merge K", "K sorted lists/arrays" | Merge K Sorted Lists |
| Streaming median | "median from data stream", "running median" | Find Median from Data Stream |
| Scheduling / Priority | "task scheduler", "meeting rooms", "priority" | Task Scheduler, Meeting Rooms II |
| Greedy with ordering | "last stone weight", "connect ropes" | Last Stone Weight, Minimum Cost to Connect Sticks |

**Interview Insight:** Whenever you see "Kth", think heap. When you need to repeatedly find min/max from a changing dataset, think heap. Heaps turn O(n) repeated scans into O(log n) operations.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Kth Largest Element in an Array | Medium | Min-heap of size K | 215 |
| 2 | Last Stone Weight | Easy | Max-heap simulation | 1046 |
| 3 | K Closest Points to Origin | Medium | Max-heap of size K (or min-heap) | 973 |
| 4 | Task Scheduler | Medium | Max-heap for frequencies | 621 |
| 5 | Find Median from Data Stream | Hard | Two heaps (max + min) | 295 |
| 6 | Merge K Sorted Lists | Hard | Min-heap with K list heads | 23 |
| 7 | Top K Frequent Elements | Medium | Min-heap of size K by frequency | 347 |

Start with 1-2 to understand basic heap operations. Problem 5 is a classic two-heap pattern. Problem 6 demonstrates heap efficiency in merging.

---

## Heap vs Other Structures

| Need | Heap | Sorted Array | BST |
|------|------|--------------|-----|
| Insert | O(log n) | O(n) | O(log n) avg, O(n) worst |
| Find min/max | O(1) | O(1) | O(log n) avg, O(n) worst |
| Extract min/max | O(log n) | O(n) shift | O(log n) avg, O(n) worst |
| Search arbitrary | O(n) | O(log n) binary search | O(log n) avg, O(n) worst |
| Space | O(n) | O(n) | O(n) |
| Implementation | Medium (interface) | Simple | Complex (rotations for balance) |

**Choose heap when:** You only care about min/max, and need efficient dynamic updates.

---

## Quick Reference Card

```
Import:     import "container/heap"
Define:     type MinHeap []int
Methods:    Len(), Less(), Swap(), Push(), Pop()
Min-heap:   Less: h[i] < h[j]
Max-heap:   Less: h[i] > h[j]
Init:       heap.Init(h)          // O(n) heapify
Push:       heap.Push(h, val)     // O(log n)
Pop:        val := heap.Pop(h)    // O(log n)
Peek:       min := (*h)[0]        // O(1)
Fix:        heap.Fix(h, i)        // O(log n) after modifying h[i]

Top K pattern:     Min-heap of size K for K largest
Two-heap median:   Max-heap (lower) + Min-heap (upper)
```

---

> **Key Insight:** Heaps give you O(1) access to the min/max and O(log n) updates. When you see "Kth largest" or "repeatedly find min/max", immediately think heap. Remember: min-heap for K largest (counterintuitive but correct).

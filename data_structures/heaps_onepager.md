# Heaps / Priority Queues -- One-Pager

## Core Concept

A **heap** is a complete binary tree where each node satisfies the **heap property**: in a **min-heap**, every parent is smaller than its children; in a **max-heap**, every parent is larger. This structure enables O(1) access to the minimum (or maximum) element and O(log n) insertions and deletions.

**Priority queue** is the abstract data type; **heap** is the concrete implementation. Think of a priority queue as an interface that promises "give me the highest (or lowest) priority element efficiently."

In Java, `PriorityQueue<E>` is a **min-heap** by default. To get a max-heap, pass `Collections.reverseOrder()` or a custom comparator. No boilerplate needed -- unlike Go's `container/heap`.

**Complete Binary Tree:** Stored implicitly in an array without pointers:
- For 0-indexed position `i`: Parent = `(i - 1) / 2`, Left child = `2*i + 1`, Right child = `2*i + 2`

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert (offer) | O(log n) | Add to end, sift up to restore heap property |
| Extract min/max (poll) | O(log n) | Remove root, move last to root, sift down |
| Peek min/max | O(1) | `peek()` -- root element |
| Build heap from array | O(n) | Bottom-up heapify (Floyd's algorithm) |
| Search arbitrary element | O(n) | No ordering beyond parent-child |
| Contains | O(n) | Linear scan |

**Space Complexity:** O(n) for storing n elements.

**Why build is O(n), not O(n log n):** Bottom-up heapify processes lower levels (many nodes) with small sift-down distances, and upper levels (few nodes) with large distances. The math works out to O(n) total.

---

## Implementation Patterns

### 1. Min-Heap (Default)

```java
// Min-heap of integers
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);
minHeap.offer(3);
minHeap.offer(8);
int min = minHeap.peek();   // 3 -- O(1)
int removed = minHeap.poll(); // 3 -- O(log n)
int size = minHeap.size();
```

### 2. Max-Heap (Reverse Order)

```java
// Max-heap using reverseOrder comparator
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
maxHeap.offer(5);
maxHeap.offer(3);
maxHeap.offer(8);
int max = maxHeap.peek();   // 8
maxHeap.poll();             // removes 8
```

### 3. Custom Comparator Heap (e.g., pairs or objects)

```java
// Min-heap ordered by second element of int[] pair
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
pq.offer(new int[]{1, 5});
pq.offer(new int[]{2, 3});
int[] smallest = pq.poll();  // [2, 3] -- smallest second element

// Min-heap of strings by length
PriorityQueue<String> byLength = new PriorityQueue<>(
    Comparator.comparingInt(String::length)
);
```

### 4. Build Heap from Existing Collection

```java
// O(n) initialization from collection
List<Integer> nums = Arrays.asList(5, 3, 8, 1, 9);
PriorityQueue<Integer> heap = new PriorityQueue<>(nums);
// Now heap contains all elements with heap property
```

### 5. Top K Elements Pattern

Use a min-heap of size K to track the K largest elements.

```java
int findKthLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();  // remove smallest
        }
    }
    return minHeap.peek();  // kth largest is the min of the K largest
}
```

**Why min-heap for K largest?** We maintain the K largest elements seen so far. The smallest of these K is the Kth largest overall. When a new element arrives, if it's larger than the current smallest, we evict the smallest.

### 6. Merge K Sorted Lists

Use a min-heap to track the smallest current element from each list.

```java
ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> heap = new PriorityQueue<>(
        (a, b) -> a.val - b.val
    );
    // Initialize heap with first node of each list
    for (ListNode list : lists) {
        if (list != null) heap.offer(list);
    }
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (!heap.isEmpty()) {
        ListNode node = heap.poll();
        curr.next = node;
        curr = curr.next;
        if (node.next != null) heap.offer(node.next);
    }
    return dummy.next;
}
```

### 7. Two-Heap Median Finder

Maintain two heaps: max-heap for lower half, min-heap for upper half.

```java
class MedianFinder {
    PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder()); // max-heap
    PriorityQueue<Integer> upper = new PriorityQueue<>();  // min-heap

    void addNum(int num) {
        lower.offer(num);
        // Ensure all in lower <= all in upper
        if (!upper.isEmpty() && lower.peek() > upper.peek()) {
            upper.offer(lower.poll());
        }
        // Balance sizes: lower can have at most 1 more element
        if (lower.size() > upper.size() + 1) upper.offer(lower.poll());
        if (upper.size() > lower.size())    lower.offer(upper.poll());
    }

    double findMedian() {
        if (lower.size() > upper.size()) return lower.peek();
        return (lower.peek() + upper.peek()) / 2.0;
    }
}
```

---

## When to Use

| Scenario | Use Heap? | Alternative |
|----------|-----------|-------------|
| Need repeated access to min/max | Yes | Sorting + iteration (O(n log n) once, inflexible) |
| Top K elements | Yes | Sorting entire array (O(n log n) vs O(n log k)) |
| Dynamic dataset with changing priorities | Yes | Sorted array (O(n) insert) |
| Find median in a stream | Yes (two heaps) | Sorting after each insert (expensive) |
| Merge K sorted lists/arrays | Yes | Merge two at a time (less efficient) |
| Task scheduling by priority | Yes | -- |
| Static dataset, one-time min/max | No | Simple linear scan O(n) |

---

## Common Pitfalls

1. **Java's `PriorityQueue` is a min-heap.** Many candidates assume max-heap. Use `Collections.reverseOrder()` or negate values `(-val)` for max-heap behavior.

2. **Comparator integer overflow.** Using `(a, b) -> a - b` can overflow if values are large (e.g., `Integer.MIN_VALUE`). Use `Integer.compare(a, b)` instead.

3. **`PriorityQueue` doesn't support O(1) `contains`.** `contains()` is O(n). If you need frequent membership checks, maintain a separate `HashSet`.

4. **Modifying elements after insertion.** `PriorityQueue` doesn't reorder when you modify an element directly. Remove and re-add if priority changes.

5. **`poll()` on empty heap returns `null` (no exception).** Check `isEmpty()` or use `peek()` first if you're unsure.

6. **Using heap when problem doesn't need it.** If you can solve the problem with a single pass or simple sorting, a heap adds unnecessary complexity.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Top K elements | "K largest", "K smallest", "K closest" | Kth Largest Element, K Closest Points to Origin |
| Merge K sorted | "merge K", "K sorted lists/arrays" | Merge K Sorted Lists |
| Streaming median | "median from data stream", "running median" | Find Median from Data Stream |
| Scheduling / Priority | "task scheduler", "meeting rooms", "priority" | Task Scheduler, Meeting Rooms II |
| Greedy with ordering | "last stone weight", "connect ropes" | Last Stone Weight |

**Interview Insight:** Whenever you see "Kth", think heap. When you need to repeatedly find min/max from a changing dataset, think heap. Heaps turn O(n) repeated scans into O(log n) operations.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Kth Largest Element in an Array | Medium | Min-heap of size K | 215 |
| 2 | Last Stone Weight | Easy | Max-heap simulation | 1046 |
| 3 | K Closest Points to Origin | Medium | Max-heap of size K | 973 |
| 4 | Task Scheduler | Medium | Max-heap for frequencies | 621 |
| 5 | Find Median from Data Stream | Hard | Two heaps (max + min) | 295 |
| 6 | Merge K Sorted Lists | Hard | Min-heap with K list heads | 23 |
| 7 | Top K Frequent Elements | Medium | Min-heap of size K by frequency | 347 |

---

## Heap vs Other Structures

| Need | Heap | Sorted Array | TreeMap (BST) |
|------|------|--------------|---------------|
| Insert | O(log n) | O(n) | O(log n) |
| Find min/max | O(1) | O(1) | O(log n) |
| Extract min/max | O(log n) | O(n) shift | O(log n) |
| Search arbitrary | O(n) | O(log n) binary search | O(log n) |
| Space | O(n) | O(n) | O(n) |

**Choose heap when:** You only care about min/max, and need efficient dynamic updates.

---

## Quick Reference Card

```
Import:     java.util.PriorityQueue, java.util.Collections

Min-heap:   PriorityQueue<Integer> pq = new PriorityQueue<>();
Max-heap:   PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());
Custom:     PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
From coll:  PriorityQueue<Integer> pq = new PriorityQueue<>(list);

offer:      pq.offer(val)       // O(log n) insert
poll:       pq.poll()           // O(log n) remove and return min/max
peek:       pq.peek()           // O(1) view min/max without removing
size:       pq.size()
isEmpty:    pq.isEmpty()

Top K pattern:     Min-heap of size K for K largest
Two-heap median:   Max-heap (lower) + Min-heap (upper)
```

> **Key Insight:** Java's `PriorityQueue` is a min-heap by default. Use `Collections.reverseOrder()` for max-heap. Min-heap for K largest is counterintuitive but correct: we maintain K "candidates" and evict the smallest when a new larger element arrives.

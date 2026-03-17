# Segment Trees / Binary Indexed Trees -- One-Pager

## Core Concept

**Segment Trees** and **Binary Indexed Trees (BIT / Fenwick Tree)** are advanced data structures for efficiently answering range queries (sum, min, max, GCD) and performing point updates on arrays. They bridge the gap between static prefix sums (fast queries, slow updates) and brute force (slow queries, fast updates).

**The Problem They Solve:**
- Given an array, answer queries like "what is the sum of elements from index L to R?"
- Allow updates like "change element at index i to value x"
- Both queries and updates in O(log n) time

**Key Differences:**

| Feature | Segment Tree | Fenwick Tree (BIT) |
|---------|--------------|-------------------|
| Query types | Sum, min, max, GCD, any associative operation | Prefix sums (and range sum derived from two prefix sums) |
| Implementation complexity | Complex (tree structure, recursive) | Simple (array with bit manipulation) |
| Space | O(4n) | O(n) |
| Constants | Higher | Lower (faster in practice) |
| Use case | General range queries | Range sum / prefix sum specifically |

**When to Use What:**
- **Prefix sum array:** Static array, no updates, O(1) queries
- **Segment tree:** Dynamic updates + range min/max/sum/GCD queries
- **Fenwick tree:** Dynamic updates + range sum queries (simpler than segment tree)
- **Sparse table:** Static array, O(1) range min/max queries (RMQ), O(n log n) build

For **NeetCode 150**, segment trees and Fenwick trees rarely appear. They're more common in competitive programming and advanced LeetCode Hard problems. **Understand the concept and complexity; don't memorize implementation details unless explicitly needed.**

---

## Time Complexity Table

| Operation | Prefix Sum | Segment Tree | Fenwick Tree |
|-----------|-----------|--------------|--------------|
| Build | O(n) | O(n) | O(n log n) or O(n) with optimization |
| Point update | O(n) rebuild | O(log n) | O(log n) |
| Range query | O(1) | O(log n) | O(log n) |
| Space | O(n) | O(4n) ≈ O(n) | O(n) |

**Space Explanation:**
- Segment tree: Full binary tree with n leaves requires up to 4n nodes (safe upper bound)
- Fenwick tree: Same size as input array

---

## Implementation Patterns

### 1. Fenwick Tree (Binary Indexed Tree)

Simpler to implement, supports prefix sum and range sum queries.

```go
type BIT struct {
    tree []int
    n    int
}

func NewBIT(n int) *BIT {
    return &BIT{
        tree: make([]int, n+1),  // 1-indexed
        n:    n,
    }
}

// Update: Add delta to index i (1-indexed)
func (b *BIT) Update(i, delta int) {
    for ; i <= b.n; i += i & (-i) {  // move to next responsible index
        b.tree[i] += delta
    }
}

// Query: Get prefix sum from 1 to i (1-indexed)
func (b *BIT) Query(i int) int {
    sum := 0
    for ; i > 0; i -= i & (-i) {  // move to parent
        sum += b.tree[i]
    }
    return sum
}

// RangeQuery: Get sum from L to R (1-indexed, inclusive)
func (b *BIT) RangeQuery(L, R int) int {
    return b.Query(R) - b.Query(L-1)
}
```

**How It Works:**
- Each index i is responsible for a range of size (i & -i) -- the lowest set bit
- Update: Propagate change up to parent indices
- Query: Accumulate sum from parent indices
- The bit manipulation `i & (-i)` extracts the lowest set bit

**Example:** Index 12 (binary 1100) is responsible for range [9, 12] (size 4 = 0100).

### 2. Segment Tree (Basic Sum Query)

More flexible but more complex. Supports min, max, sum, GCD, etc.

```go
type SegmentTree struct {
    tree []int
    n    int
}

func NewSegmentTree(arr []int) *SegmentTree {
    n := len(arr)
    tree := make([]int, 4*n)  // safe upper bound
    st := &SegmentTree{tree: tree, n: n}
    st.build(arr, 0, 0, n-1)
    return st
}

// Build tree from array (node, range [L, R])
func (st *SegmentTree) build(arr []int, node, L, R int) {
    if L == R {
        st.tree[node] = arr[L]  // leaf node
        return
    }
    mid := (L + R) / 2
    leftChild := 2*node + 1
    rightChild := 2*node + 2
    st.build(arr, leftChild, L, mid)
    st.build(arr, rightChild, mid+1, R)
    st.tree[node] = st.tree[leftChild] + st.tree[rightChild]  // combine
}

// Update element at index idx to value val
func (st *SegmentTree) Update(idx, val int) {
    st.updateHelper(0, 0, st.n-1, idx, val)
}

func (st *SegmentTree) updateHelper(node, L, R, idx, val int) {
    if L == R {
        st.tree[node] = val
        return
    }
    mid := (L + R) / 2
    leftChild := 2*node + 1
    rightChild := 2*node + 2
    if idx <= mid {
        st.updateHelper(leftChild, L, mid, idx, val)
    } else {
        st.updateHelper(rightChild, mid+1, R, idx, val)
    }
    st.tree[node] = st.tree[leftChild] + st.tree[rightChild]
}

// Query sum in range [qL, qR]
func (st *SegmentTree) Query(qL, qR int) int {
    return st.queryHelper(0, 0, st.n-1, qL, qR)
}

func (st *SegmentTree) queryHelper(node, L, R, qL, qR int) int {
    if qL > R || qR < L {
        return 0  // no overlap
    }
    if qL <= L && R <= qR {
        return st.tree[node]  // total overlap
    }
    mid := (L + R) / 2
    leftChild := 2*node + 1
    rightChild := 2*node + 2
    leftSum := st.queryHelper(leftChild, L, mid, qL, qR)
    rightSum := st.queryHelper(rightChild, mid+1, R, qL, qR)
    return leftSum + rightSum
}
```

### 3. Range Update with Lazy Propagation (Advanced)

For efficiency when doing range updates (e.g., "add x to all elements in range [L, R]").

```go
// Conceptual -- full implementation is lengthy
type LazySegmentTree struct {
    tree []int
    lazy []int  // pending updates not yet propagated
    n    int
}

// Key idea: Mark lazy[node] with pending update
// Only propagate when needed (on query or further update)
// This makes range updates O(log n) instead of O(n)
```

**Lazy Propagation:** Postpone updates to children until necessary. Reduces range update from O(n) to O(log n).

---

## When to Use

| Problem | Recommended Structure |
|---------|----------------------|
| Static array, range sum | Prefix sum array |
| Dynamic point updates + range sum | Fenwick tree |
| Dynamic updates + range min/max/GCD | Segment tree |
| Static array, range min/max | Sparse table (O(1) query) |
| Range updates + range queries | Segment tree with lazy propagation |
| 2D range queries | 2D BIT or 2D segment tree |

**For NeetCode 150:** Most problems don't require these. If you see "range sum with updates", consider Fenwick tree. If you see "range min/max with updates", consider segment tree. Otherwise, simpler structures suffice.

---

## Common Pitfalls

1. **Using segment tree when prefix sum suffices.** If there are no updates, prefix sum is simpler and faster.

2. **Off-by-one in Fenwick tree indexing.** BIT is 1-indexed. Adjust input indices accordingly.

3. **Incorrect tree size for segment tree.** Use `4*n` as a safe upper bound. The exact size depends on tree height, but 4n is always sufficient.

4. **Forgetting to update parent nodes.** When updating a leaf in segment tree, propagate changes up to the root.

5. **Wrong combine operation.** For sum queries, combine with `+`. For min queries, use `min()`. For max, use `max()`. For GCD, use `gcd()`.

6. **Not handling 0-indexed vs 1-indexed.** Be consistent throughout the implementation.

7. **Overusing these structures.** They're powerful but complex. Only use when simpler alternatives don't work.

---

## Interview Relevance

Segment trees and Fenwick trees are **rare in NeetCode 150** and standard interviews. They appear in:
- Competitive programming contests
- Advanced LeetCode Hard problems
- System design discussions (e.g., distributed range query systems)

**What to Know:**
- **Conceptual understanding:** What problem they solve, time complexity, when to use
- **Basic Fenwick tree implementation:** Simpler, more likely to be asked
- **Don't memorize segment tree details:** Too complex for most interviews. Know it exists and its O(log n) query/update complexity.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Range sum with updates | "mutable", "update element", "range sum" | Range Sum Query - Mutable (LeetCode 307) |
| Count smaller numbers | "count smaller", "inversions" | Count of Smaller Numbers After Self (LeetCode 315) |
| Range min/max queries | "range minimum", "range maximum" | Not common in NeetCode 150 |

**Interview Insight:** If asked about range queries, start with simpler solutions (prefix sum, sliding window). Only escalate to segment/Fenwick trees if the interviewer pushes for optimal solution with updates.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Range Sum Query - Immutable | Easy | Prefix sum (no tree needed) | 303 |
| 2 | Range Sum Query - Mutable | Medium | Fenwick tree or segment tree | 307 |
| 3 | Count of Smaller Numbers After Self | Hard | BIT with coordinate compression | 315 |
| 4 | Range Sum Query 2D - Mutable | Hard | 2D BIT or 2D segment tree | 308 (premium) |

**Recommendation:** Master prefix sums first. If pursuing competitive programming, learn Fenwick tree. Segment trees are optional for most software engineering interviews.

---

## Comparison Summary

```
Prefix Sum Array:
  Build: O(n)
  Query: O(1)
  Update: O(n)
  Use: Static array, range sums

Fenwick Tree (BIT):
  Build: O(n log n) or O(n)
  Query: O(log n)
  Update: O(log n)
  Use: Dynamic range sums

Segment Tree:
  Build: O(n)
  Query: O(log n)
  Update: O(log n)
  Use: Dynamic range min/max/sum/GCD

Segment Tree + Lazy:
  Build: O(n)
  Range Query: O(log n)
  Range Update: O(log n)
  Use: Both range updates and range queries
```

---

## Quick Reference Card

```
Fenwick Tree (1-indexed):
  Update:  for i += i & (-i)  { tree[i] += delta }
  Query:   for i -= i & (-i)  { sum += tree[i] }
  Range:   Query(R) - Query(L-1)

Segment Tree:
  Size:    4 * n nodes
  Build:   Recursive, O(n)
  Update:  Navigate to leaf, update parents, O(log n)
  Query:   Split range into tree nodes, O(log n)

Bit trick:  i & (-i)  extracts lowest set bit
            Determines responsibility range in BIT
```

---

> **Key Insight:** Segment trees and Fenwick trees are powerful but complex. For NeetCode 150 and most interviews, simpler structures (prefix sums, hash maps, sliding windows) suffice. Know that these exist, understand their O(log n) query/update complexity, and when they're appropriate. Only implement them when explicitly required or when pursuing competitive programming.

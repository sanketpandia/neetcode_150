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
| Query types | Sum, min, max, GCD, any associative operation | Prefix sums (range sum derived from two prefix sums) |
| Implementation complexity | Complex (recursive tree) | Simple (array + bit manipulation) |
| Space | O(4n) | O(n) |
| Constants | Higher | Lower (faster in practice) |
| Use case | General range queries | Range sum / prefix sum specifically |

**When to Use What:**
- **Prefix sum array:** Static array, no updates, O(1) queries
- **Segment tree:** Dynamic updates + range min/max/sum/GCD queries
- **Fenwick tree:** Dynamic updates + range sum queries (simpler than segment tree)

For **NeetCode 150**, these rarely appear. **Understand the concept and complexity; don't memorize details unless explicitly needed.**

---

## Time Complexity Table

| Operation | Prefix Sum | Segment Tree | Fenwick Tree |
|-----------|-----------|--------------|--------------|
| Build | O(n) | O(n) | O(n log n) or O(n) |
| Point update | O(n) rebuild | O(log n) | O(log n) |
| Range query | O(1) | O(log n) | O(log n) |
| Space | O(n) | O(4n) | O(n) |

---

## Implementation Patterns

### 1. Fenwick Tree (Binary Indexed Tree)

Simpler to implement, supports prefix sum and range sum queries. 1-indexed.

```java
class BIT {
    int[] tree;
    int n;

    BIT(int n) {
        this.n = n;
        tree = new int[n + 1];  // 1-indexed
    }

    // Add delta to index i (1-indexed)
    void update(int i, int delta) {
        for (; i <= n; i += i & (-i)) {
            tree[i] += delta;
        }
    }

    // Get prefix sum from 1 to i (1-indexed)
    int query(int i) {
        int sum = 0;
        for (; i > 0; i -= i & (-i)) {
            sum += tree[i];
        }
        return sum;
    }

    // Get sum from L to R (1-indexed, inclusive)
    int rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }
}
```

**How It Works:**
- Each index `i` is responsible for a range of size `(i & -i)` -- the lowest set bit
- `update`: Propagate change up to parent indices
- `query`: Accumulate sum from parent indices
- Bit trick `i & (-i)` extracts the lowest set bit

**Example:** Index 12 (binary 1100) is responsible for range [9, 12] (size 4 = 0100).

### 2. Segment Tree (Basic Sum Query)

More flexible but more complex. Supports min, max, sum, GCD, etc.

```java
class SegmentTree {
    int[] tree;
    int n;

    SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n];  // safe upper bound
        build(arr, 0, 0, n - 1);
    }

    void build(int[] arr, int node, int l, int r) {
        if (l == r) { tree[node] = arr[l]; return; }
        int mid = (l + r) / 2;
        build(arr, 2 * node + 1, l, mid);
        build(arr, 2 * node + 2, mid + 1, r);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    void update(int idx, int val) {
        update(0, 0, n - 1, idx, val);
    }

    void update(int node, int l, int r, int idx, int val) {
        if (l == r) { tree[node] = val; return; }
        int mid = (l + r) / 2;
        if (idx <= mid) update(2 * node + 1, l, mid, idx, val);
        else            update(2 * node + 2, mid + 1, r, idx, val);
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }

    int query(int ql, int qr) {
        return query(0, 0, n - 1, ql, qr);
    }

    int query(int node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;           // no overlap
        if (ql <= l && r <= qr) return tree[node]; // total overlap
        int mid = (l + r) / 2;
        return query(2 * node + 1, l, mid, ql, qr)
             + query(2 * node + 2, mid + 1, r, ql, qr);
    }
}
```

### 3. Usage Example

```java
// LeetCode 307: Range Sum Query - Mutable
SegmentTree st = new SegmentTree(new int[]{1, 3, 5, 7, 9});
st.query(0, 2);     // 9 (1 + 3 + 5)
st.update(1, 2);    // change index 1 to 2
st.query(0, 2);     // 8 (1 + 2 + 5)

BIT bit = new BIT(5);
bit.update(1, 1); bit.update(2, 3); bit.update(3, 5);  // 1-indexed
bit.rangeQuery(1, 3);  // 9
```

---

## When to Use

| Problem | Recommended Structure |
|---------|----------------------|
| Static array, range sum | Prefix sum array |
| Dynamic point updates + range sum | Fenwick tree |
| Dynamic updates + range min/max/GCD | Segment tree |
| Static array, range min/max | Sparse table (O(1) query) |
| Range updates + range queries | Segment tree with lazy propagation |

---

## Common Pitfalls

1. **Using segment tree when prefix sum suffices.** If there are no updates, prefix sum is simpler and O(1) per query.

2. **Off-by-one in Fenwick tree indexing.** BIT is 1-indexed. Adjust input indices by +1 when building.

3. **Incorrect tree size for segment tree.** Use `4*n` as a safe upper bound. The exact size depends on tree height, but 4n is always sufficient.

4. **Forgetting to update parent nodes.** When updating a leaf in segment tree, the recursive implementation propagates up automatically -- but in iterative versions, be careful.

5. **Wrong combine operation.** For sum queries, combine with `+`. For min, use `Math.min`. For max, use `Math.max`.

---

## Interview Relevance

Segment trees and Fenwick trees are **rare in NeetCode 150** and standard interviews. They appear more in competitive programming.

**What to Know:**
- **Conceptual understanding:** What problem they solve, time complexity, when to use
- **Basic Fenwick tree:** Simpler, more likely to be asked
- **Segment tree:** Know it exists and its O(log n) complexity; implement if explicitly required

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Range sum with updates | "mutable", "update element", "range sum" | Range Sum Query - Mutable (307) |
| Count smaller numbers | "count smaller", "inversions" | Count of Smaller Numbers After Self (315) |

**Interview Insight:** If asked about range queries, start with prefix sum. Only escalate to segment/Fenwick trees if the interviewer pushes for O(log n) updates.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Range Sum Query - Immutable | Easy | Prefix sum (no tree needed) | 303 |
| 2 | Range Sum Query - Mutable | Medium | Fenwick tree or segment tree | 307 |
| 3 | Count of Smaller Numbers After Self | Hard | BIT with coordinate compression | 315 |

---

## Quick Reference Card

```java
// Fenwick Tree (1-indexed)
void update(int i, int delta) {
    for (; i <= n; i += i & (-i)) tree[i] += delta;
}
int query(int i) {
    int sum = 0;
    for (; i > 0; i -= i & (-i)) sum += tree[i];
    return sum;
}
int rangeQuery(int l, int r) { return query(r) - query(l - 1); }

// Bit trick: i & (-i) extracts lowest set bit
// Segment Tree: build O(n), update O(log n), query O(log n)
// Tree size: 4 * n nodes (safe upper bound)

Comparison:
  Prefix sum: build O(n), query O(1), update O(n) -- no updates allowed
  Fenwick:    build O(n log n), query O(log n), update O(log n) -- range sums
  Segment:    build O(n), query O(log n), update O(log n) -- any associative op
```

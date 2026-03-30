# Union-Find (Disjoint Set) -- One-Pager

## Core Concept

**Union-Find** (also called Disjoint Set Union, DSU) is a data structure that efficiently tracks a partition of elements into disjoint (non-overlapping) sets. It supports two primary operations:
- **Find:** Determine which set an element belongs to (returns a representative root)
- **Union:** Merge two sets into one

With two optimizations -- **path compression** and **union by rank** -- both operations achieve nearly O(1) amortized time complexity, technically **O(α(n))** where α is the inverse Ackermann function (effectively constant for all practical inputs).

**Core Idea:**
- Each set is represented as a tree with a root node as the representative
- Initially, each element is its own root (singleton set)
- `find(x)` climbs to the root and compresses the path
- `union(x, y)` attaches one root to another, preferring the shorter tree

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Initialize | O(n) | Create n singleton sets |
| Find (with path compression) | O(α(n)) ≈ O(1) | α(n) < 5 for all practical n |
| Union (with union by rank) | O(α(n)) ≈ O(1) | Attach smaller tree to larger |
| Connected (same set?) | O(α(n)) ≈ O(1) | Two Finds and compare roots |
| Count components | O(1) | Track count during unions |

**Space Complexity:** O(n) for parent and rank arrays.

---

## Implementation Patterns

### 1. Basic Union-Find Class

```java
class UnionFind {
    int[] parent;
    int[] rank;
    int count;  // number of disjoint sets

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;
        for (int i = 0; i < n; i++) parent[i] = i;  // each element is its own root
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);  // path compression
        }
        return parent[x];
    }

    boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false;  // already in the same set

        // Union by rank: attach smaller tree under larger
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        count--;
        return true;
    }

    boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    int count() {
        return count;
    }
}
```

### 2. Number of Connected Components (Graph)

```java
int countComponents(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    for (int[] edge : edges) {
        uf.union(edge[0], edge[1]);
    }
    return uf.count();
}
```

### 3. Redundant Connection (Find Cycle-Causing Edge)

```java
int[] findRedundantConnection(int[][] edges) {
    int n = edges.length;
    UnionFind uf = new UnionFind(n + 1);  // nodes are 1-indexed

    for (int[] edge : edges) {
        if (uf.connected(edge[0], edge[1])) {
            return edge;  // adding this edge creates a cycle
        }
        uf.union(edge[0], edge[1]);
    }
    return new int[]{};
}
```

### 4. 2D Grid Union-Find

For problems like Number of Islands, map 2D coordinates to 1D indices.

```java
int numIslands(char[][] grid) {
    int m = grid.length, n = grid[0].length;
    UnionFind uf = new UnionFind(m * n);

    int waterCount = 0;
    int[][] dirs = {{1, 0}, {0, 1}};  // only check down and right to avoid duplicates

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (grid[i][j] == '0') {
                waterCount++;
                continue;
            }
            for (int[] d : dirs) {
                int ni = i + d[0], nj = j + d[1];
                if (ni < m && nj < n && grid[ni][nj] == '1') {
                    uf.union(i * n + j, ni * n + nj);
                }
            }
        }
    }
    return uf.count() - waterCount;
}
```

**Helper:** Convert `(row, col)` to 1D index: `row * cols + col`

### 5. Union by Size Variant

Track set sizes instead of rank -- slightly simpler to reason about.

```java
class UnionFindBySize {
    int[] parent, size;
    int count;

    UnionFindBySize(int n) {
        parent = new int[n]; size = new int[n];
        count = n;
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    void union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (size[rx] < size[ry]) { parent[rx] = ry; size[ry] += size[rx]; }
        else                     { parent[ry] = rx; size[rx] += size[ry]; }
        count--;
    }
}
```

---

## Union-Find vs DFS/BFS

| Criteria | Union-Find | DFS/BFS |
|----------|------------|---------|
| Dynamic connectivity | Excellent (O(α(n)) per query) | Poor (need re-traversal) |
| Static graph traversal | Not ideal | Excellent |
| Find all reachable nodes | Doesn't track nodes, only sets | Natural |
| Shortest path | No | Yes (BFS) |
| Cycle detection | Yes (incremental) | Yes (DFS with stack) |
| Connected components | Yes (efficient) | Yes (but static) |
| When to use | Growing graph, many connectivity queries | One-time traversal, path finding |

---

## When to Use

| Scenario | Use Union-Find? |
|----------|-----------------|
| "Are X and Y connected?" queries | Yes |
| "How many connected components?" | Yes |
| Adding edges and checking connectivity | Yes |
| Detecting redundant edge (cycle) | Yes |
| Kruskal's MST algorithm | Yes |
| Finding shortest path | No -- use BFS |
| Traversing all nodes | No -- use DFS/BFS |

---

## Common Pitfalls

1. **Forgetting path compression.** Without compression, `find` degrades to O(n) in worst case (linked-list tree). Always compress paths during `find`.

2. **Forgetting union by rank.** Without rank optimization, trees become unbalanced, degrading performance. Always attach smaller tree to larger.

3. **Not mapping 2D grid to 1D.** For grid problems, convert `(i, j)` to `i * cols + j` to use Union-Find.

4. **Incorrectly handling 1-indexed inputs.** Problem inputs may be 1-indexed. Create `n+1` elements if needed.

5. **Checking connectivity before union.** If you union first then check, you've already merged the sets. Check before union if detecting cycles.

6. **Not tracking component count.** Decrement `count` on successful union. Don't recompute by scanning parents.

7. **Confusing parent with root.** `parent[x]` is the immediate parent, not necessarily the root. Use `find(x)` to get the root.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Connected components | "connected", "groups", "components" | Number of Connected Components |
| Redundant connection | "redundant edge", "cycle", "remove edge" | Redundant Connection |
| Accounts merge | "merge", "group", "emails/accounts" | Accounts Merge |
| Islands/grid connectivity | "islands", "grid", "connected cells" | Number of Islands |
| Graph validity | "valid tree", "no cycles", "connected" | Graph Valid Tree |

**Interview Insight:** When the problem involves incremental edge additions and connectivity queries, immediately think Union-Find. Keywords: "connected", "group", "merge", "redundant". It's often faster than DFS/BFS for these problems.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Number of Connected Components | Medium | Basic Union-Find | 323 / 547 |
| 2 | Redundant Connection | Medium | Cycle detection | 684 |
| 3 | Accounts Merge | Medium | Union by mapping | 721 |
| 4 | Graph Valid Tree | Medium | n-1 edges + connectivity | 261 |
| 5 | Number of Islands | Medium | 2D grid Union-Find | 200 |

---

## Quick Reference Card

```java
class UnionFind {
    int[] parent, rank; int count;
    UnionFind(int n) {
        parent = new int[n]; rank = new int[n]; count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);  // path compression
        return parent[x];
    }
    boolean union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return false;
        if (rank[rx] < rank[ry]) parent[rx] = ry;
        else if (rank[rx] > rank[ry]) parent[ry] = rx;
        else { parent[ry] = rx; rank[rx]++; }
        count--; return true;
    }
    boolean connected(int x, int y) { return find(x) == find(y); }
}

2D to 1D:   idx = row * cols + col
Time:       O(α(n)) ≈ O(1) for find and union
Space:      O(n)
```

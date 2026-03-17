# Union-Find (Disjoint Set) -- One-Pager

## Core Concept

**Union-Find** (also called Disjoint Set Union, DSU) is a data structure that efficiently tracks a partition of elements into disjoint (non-overlapping) sets. It supports two primary operations:
- **Find:** Determine which set an element belongs to (returns a representative)
- **Union:** Merge two sets into one

With two optimizations -- **path compression** and **union by rank** -- both operations achieve nearly O(1) amortized time complexity, technically **O(α(n))** where α is the inverse Ackermann function (grows extremely slowly, effectively constant for all practical inputs).

**Core Idea:**
- Each set is represented as a tree with a root node as the representative
- Initially, each element is its own root (singleton set)
- `Find(x)` climbs to the root and compresses the path
- `Union(x, y)` attaches one root to another, preferring the shorter tree

**Use Cases:** Union-Find excels at problems involving dynamic connectivity: "Are these two elements connected?", "How many connected components?", "Does adding this edge create a cycle?" Think of it as a specialized graph structure optimized for connectivity queries.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| MakeSet (initialize) | O(1) | Create singleton set for each element |
| Find (with path compression) | O(α(n)) ≈ O(1) | α(n) < 5 for all practical n |
| Union (with union by rank) | O(α(n)) ≈ O(1) | Attach smaller tree to larger |
| Connected (same set?) | O(α(n)) ≈ O(1) | Two Finds and compare roots |
| Count components | O(1) | Track count during unions |
| Build from n elements | O(n) | Initialize each element |

**Space Complexity:** O(n) for parent and rank arrays.

**Why so fast?** Path compression flattens trees during Find, making future operations faster. Union by rank keeps trees shallow. Together, they bound tree height to O(log* n) in practice, and O(α(n)) in theory.

---

## Implementation Patterns

### 1. Basic Union-Find Structure

```go
type UnionFind struct {
    parent []int
    rank   []int
    count  int  // number of disjoint sets
}

func NewUnionFind(n int) *UnionFind {
    parent := make([]int, n)
    rank := make([]int, n)
    for i := 0; i < n; i++ {
        parent[i] = i  // each element is its own root initially
        rank[i] = 0    // single-node tree has rank 0
    }
    return &UnionFind{
        parent: parent,
        rank:   rank,
        count:  n,  // n disjoint sets initially
    }
}
```

### 2. Find with Path Compression

```go
func (uf *UnionFind) Find(x int) int {
    if uf.parent[x] != x {
        // Path compression: make x point directly to root
        uf.parent[x] = uf.Find(uf.parent[x])
    }
    return uf.parent[x]
}

// Iterative version (same effect, no recursion)
func (uf *UnionFind) FindIterative(x int) int {
    root := x
    for root != uf.parent[root] {
        root = uf.parent[root]
    }
    // Path compression pass
    for x != root {
        next := uf.parent[x]
        uf.parent[x] = root
        x = next
    }
    return root
}
```

### 3. Union by Rank

```go
func (uf *UnionFind) Union(x, y int) bool {
    rootX := uf.Find(x)
    rootY := uf.Find(y)

    if rootX == rootY {
        return false  // already in the same set
    }

    // Union by rank: attach smaller tree under larger
    if uf.rank[rootX] < uf.rank[rootY] {
        uf.parent[rootX] = rootY
    } else if uf.rank[rootX] > uf.rank[rootY] {
        uf.parent[rootY] = rootX
    } else {
        // Same rank: pick one and increment its rank
        uf.parent[rootY] = rootX
        uf.rank[rootX]++
    }

    uf.count--  // merged two sets into one
    return true
}
```

### 4. Connected Check

```go
func (uf *UnionFind) Connected(x, y int) bool {
    return uf.Find(x) == uf.Find(y)
}
```

### 5. Get Component Count

```go
func (uf *UnionFind) Count() int {
    return uf.count
}
```

### 6. Number of Connected Components (Graph)

```go
func countComponents(n int, edges [][]int) int {
    uf := NewUnionFind(n)
    for _, edge := range edges {
        uf.Union(edge[0], edge[1])
    }
    return uf.Count()
}
```

### 7. Redundant Connection (Find Cycle-Causing Edge)

```go
func findRedundantConnection(edges [][]int) []int {
    n := len(edges)
    uf := NewUnionFind(n + 1)  // nodes are 1-indexed

    for _, edge := range edges {
        u, v := edge[0], edge[1]
        if uf.Connected(u, v) {
            return edge  // adding this edge creates a cycle
        }
        uf.Union(u, v)
    }
    return nil
}
```

### 8. 2D Grid Union-Find

For problems like Number of Islands, map 2D coordinates to 1D indices.

```go
func numIslands(grid [][]byte) int {
    if len(grid) == 0 {
        return 0
    }
    m, n := len(grid), len(grid[0])
    uf := NewUnionFind(m * n)

    // Count water cells to subtract later
    waterCount := 0
    for i := 0; i < m; i++ {
        for j := 0; j < n; j++ {
            if grid[i][j] == '0' {
                waterCount++
            }
        }
    }

    directions := [][]int{{1, 0}, {0, 1}}  // only check down and right to avoid duplicates
    for i := 0; i < m; i++ {
        for j := 0; j < n; j++ {
            if grid[i][j] == '1' {
                for _, dir := range directions {
                    ni, nj := i+dir[0], j+dir[1]
                    if ni < m && nj < n && grid[ni][nj] == '1' {
                        idx1 := i*n + j
                        idx2 := ni*n + nj
                        uf.Union(idx1, idx2)
                    }
                }
            }
        }
    }

    return uf.Count() - waterCount
}

// Helper: convert (row, col) to 1D index
func toIndex(row, col, cols int) int {
    return row*cols + col
}
```

### 9. Accounts Merge (Union by Email)

```go
func accountsMerge(accounts [][]string) [][]string {
    emailToOwner := make(map[string]string)
    emailToID := make(map[string]int)
    id := 0

    // Assign unique ID to each email
    for _, account := range accounts {
        name := account[0]
        for i := 1; i < len(account); i++ {
            email := account[i]
            if _, ok := emailToID[email]; !ok {
                emailToID[email] = id
                emailToOwner[email] = name
                id++
            }
        }
    }

    uf := NewUnionFind(id)

    // Union emails in the same account
    for _, account := range accounts {
        firstEmailID := emailToID[account[1]]
        for i := 2; i < len(account); i++ {
            uf.Union(firstEmailID, emailToID[account[i]])
        }
    }

    // Group emails by root
    rootToEmails := make(map[int][]string)
    for email, emailID := range emailToID {
        root := uf.Find(emailID)
        rootToEmails[root] = append(rootToEmails[root], email)
    }

    // Build result
    result := [][]string{}
    for _, emails := range rootToEmails {
        sort.Strings(emails)
        name := emailToOwner[emails[0]]
        account := append([]string{name}, emails...)
        result = append(result, account)
    }

    return result
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
| Space complexity | O(n) | O(n) |
| When to use | Growing graph, many connectivity queries | One-time traversal, path finding |

**Rule of thumb:** Use Union-Find when edges are added incrementally and you need to check connectivity dynamically. Use DFS/BFS for one-time graph exploration or when you need paths/distances.

---

## When to Use

| Scenario | Use Union-Find? |
|----------|-----------------|
| "Are X and Y connected?" queries | Yes |
| "How many connected components?" | Yes |
| Adding edges and checking connectivity | Yes |
| Detecting redundant edge (cycle) | Yes |
| Kruskal's MST algorithm | Yes |
| Network connectivity | Yes |
| Finding shortest path | No -- use BFS |
| Traversing all nodes | No -- use DFS/BFS |
| Graph has weights and need path costs | No -- use Dijkstra |

---

## Common Pitfalls

1. **Forgetting path compression.** Without compression, Find degrades to O(n) in worst case (linked-list tree). Always compress paths during Find.

2. **Forgetting union by rank.** Without rank optimization, trees become unbalanced, degrading performance. Always attach smaller tree to larger.

3. **Not mapping 2D grid to 1D.** For grid problems, convert `(i, j)` to `i * cols + j` to use Union-Find.

4. **Incorrectly handling 0-indexed vs 1-indexed.** Problem inputs may be 1-indexed. Create `n+1` elements if needed.

5. **Checking connectivity before union.** If you union first then check, you've already merged the sets. Check before union if detecting cycles.

6. **Not tracking component count.** Decrement `count` on successful union. Don't recompute by scanning parents.

7. **Confusing parent with root.** `parent[x]` is the immediate parent, not necessarily the root. Use `Find(x)` to get the root.

8. **Modifying parent without considering rank.** Always use the Union method to maintain the rank invariant.

---

## Interview Relevance

Union-Find is a high-signal data structure that separates strong candidates.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Connected components | "connected", "groups", "components" | Number of Connected Components |
| Redundant connection | "redundant edge", "cycle", "remove edge" | Redundant Connection |
| Accounts merge | "merge", "group", "emails/accounts" | Accounts Merge |
| Islands/grid connectivity | "islands", "grid", "connected cells" | Number of Islands (Union-Find variant) |
| Graph validity | "valid tree", "no cycles", "connected" | Graph Valid Tree |
| Minimize connections | "minimize", "connect", "union" | Minimize Connections (various) |

**Interview Insight:** When the problem involves incremental edge additions and connectivity queries, immediately think Union-Find. Keywords: "connected", "group", "merge", "redundant". It's often faster than DFS/BFS for these problems.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Number of Connected Components | Medium | Basic Union-Find | 323 (premium) / 547 |
| 2 | Redundant Connection | Medium | Cycle detection | 684 |
| 3 | Accounts Merge | Medium | Union by mapping | 721 |
| 4 | Graph Valid Tree | Medium | n-1 edges + connectivity | 261 (premium) |
| 5 | Number of Islands II | Hard | Dynamic 2D grid Union-Find | 305 (premium) |
| 6 | Smallest String With Swaps | Medium | Union-Find + sorting within groups | 1202 |

Start with 1-2 to master basic structure. Problem 3 tests mapping non-integer elements. Problems 4-5 combine Union-Find with additional constraints.

---

## Optimizations Summary

**Path Compression:**
- During Find, make every node on path point directly to root
- Flattens tree structure
- Amortizes future Finds

**Union by Rank:**
- Always attach smaller tree under root of larger tree
- Keeps trees shallow
- Rank approximates tree height

**Alternative: Union by Size:**
- Track size instead of rank
- Attach smaller set to larger set
- Similar performance, slightly simpler

```go
// Union by size variant
type UnionFind struct {
    parent []int
    size   []int
    count  int
}

func (uf *UnionFind) Union(x, y int) {
    rootX, rootY := uf.Find(x), uf.Find(y)
    if rootX == rootY {
        return
    }
    if uf.size[rootX] < uf.size[rootY] {
        uf.parent[rootX] = rootY
        uf.size[rootY] += uf.size[rootX]
    } else {
        uf.parent[rootY] = rootX
        uf.size[rootX] += uf.size[rootY]
    }
    uf.count--
}
```

---

## Quick Reference Card

```
Define:     type UnionFind struct { parent, rank []int; count int }
Init:       parent[i] = i; rank[i] = 0; count = n
Find:       if parent[x] != x { parent[x] = Find(parent[x]) }; return parent[x]
Union:      rootX, rootY := Find(x), Find(y)
            Attach smaller rank to larger rank; increment rank if equal
            Decrement count on successful union
Connected:  Find(x) == Find(y)
Count:      return count

2D to 1D:   idx = row * cols + col

Time:       O(α(n)) ≈ O(1) for Find and Union
Space:      O(n)
```

---

> **Key Insight:** Union-Find is the ultimate tool for dynamic connectivity. With path compression and union by rank, it achieves nearly O(1) per operation. Use it when edges arrive incrementally and you need fast connectivity checks. Don't forget to map 2D grids to 1D indices.

# Graphs -- One-Pager

## Core Concept

A **graph** is a collection of **vertices (nodes)** connected by **edges**. Unlike trees, graphs can have cycles, multiple paths between nodes, and disconnected components. Graphs model relationships in networks, social connections, maps, dependencies, and countless other domains.

**Key Classifications:**
- **Directed vs Undirected:** Directed edges have a direction (A → B ≠ B → A). Undirected edges are bidirectional (A — B).
- **Weighted vs Unweighted:** Edges can have weights (costs, distances). Unweighted edges all have the same cost.
- **Cyclic vs Acyclic:** A graph with cycles contains closed loops. A **DAG (Directed Acyclic Graph)** has no cycles -- useful for dependency resolution.
- **Connected vs Disconnected:** In a connected graph, every vertex is reachable from every other vertex.

**The Graph Problem Template:**
1. **Build the graph** from input (adjacency list, matrix, or edge list)
2. **Traverse the graph** (DFS or BFS) with appropriate state tracking
3. **Extract the answer** (path, connectivity, cycle detection, etc.)

Understanding graph representations and traversals is fundamental -- nearly every graph problem builds on these primitives.

---

## Time Complexity Table

| Operation | Adjacency List | Adjacency Matrix | Notes |
|-----------|----------------|------------------|-------|
| Space | O(V + E) | O(V²) | List scales with edges; matrix with vertices |
| Add edge | O(1) | O(1) | Append to list or set matrix cell |
| Remove edge | O(E) | O(1) | Must find and remove in list |
| Check if edge exists | O(degree) | O(1) | List: scan neighbors; matrix: direct lookup |
| Iterate neighbors | O(degree) | O(V) | List: only neighbors; matrix: scan entire row |
| DFS / BFS traversal | O(V + E) | O(V²) | Visit each vertex and edge once |

**Choosing a Representation:**
- **Adjacency list:** Best default choice for interviews. Efficient for sparse graphs (E << V²).
- **Adjacency matrix:** Use when edges are dense (E ≈ V²) or when you need O(1) edge existence checks.
- **Edge list:** Rarely used except for algorithms like Kruskal's MST.

---

## Implementation Patterns

### 1. Graph Representations

```go
// Adjacency list (most common)
graph := make(map[int][]int)  // vertex -> slice of neighbors

// Add directed edge u -> v
graph[u] = append(graph[u], v)

// Add undirected edge u — v
graph[u] = append(graph[u], v)
graph[v] = append(graph[v], u)

// Weighted adjacency list
type Edge struct {
    To     int
    Weight int
}
graph := make(map[int][]Edge)
graph[u] = append(graph[u], Edge{To: v, Weight: w})

// Adjacency matrix
n := 5  // number of vertices
matrix := make([][]int, n)
for i := range matrix {
    matrix[i] = make([]int, n)
}
matrix[u][v] = 1  // or weight for weighted graphs
```

### 2. DFS (Recursive)

Explores as deep as possible before backtracking. Use for connectivity, cycle detection, topological sort.

```go
func dfs(node int, graph map[int][]int, visited map[int]bool) {
    if visited[node] {
        return
    }
    visited[node] = true
    // Process node here
    fmt.Println(node)

    for _, neighbor := range graph[node] {
        dfs(neighbor, graph, visited)
    }
}

// Usage
visited := make(map[int]bool)
dfs(startNode, graph, visited)
```

### 3. DFS (Iterative with Stack)

Useful when recursion depth might cause stack overflow.

```go
func dfsIterative(start int, graph map[int][]int) {
    visited := make(map[int]bool)
    stack := []int{start}

    for len(stack) > 0 {
        node := stack[len(stack)-1]
        stack = stack[:len(stack)-1]

        if visited[node] {
            continue
        }
        visited[node] = true
        fmt.Println(node)

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                stack = append(stack, neighbor)
            }
        }
    }
}
```

### 4. BFS (Level-Order Traversal)

Explores neighbors level by level. Use for shortest path in unweighted graphs.

```go
func bfs(start int, graph map[int][]int) {
    visited := make(map[int]bool)
    queue := []int{start}
    visited[start] = true

    for len(queue) > 0 {
        node := queue[0]
        queue = queue[1:]
        fmt.Println(node)

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                visited[neighbor] = true
                queue = append(queue, neighbor)
            }
        }
    }
}
```

### 5. Shortest Path (Unweighted Graph with BFS)

```go
func shortestPath(start, end int, graph map[int][]int) int {
    if start == end {
        return 0
    }

    visited := make(map[int]bool)
    queue := []int{start}
    visited[start] = true
    distance := 0

    for len(queue) > 0 {
        levelSize := len(queue)
        distance++

        for i := 0; i < levelSize; i++ {
            node := queue[0]
            queue = queue[1:]

            for _, neighbor := range graph[node] {
                if neighbor == end {
                    return distance
                }
                if !visited[neighbor] {
                    visited[neighbor] = true
                    queue = append(queue, neighbor)
                }
            }
        }
    }

    return -1  // no path exists
}
```

### 6. Cycle Detection (Undirected Graph)

Use DFS with parent tracking.

```go
func hasCycle(graph map[int][]int, n int) bool {
    visited := make(map[int]bool)

    var dfs func(node, parent int) bool
    dfs = func(node, parent int) bool {
        visited[node] = true

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                if dfs(neighbor, node) {
                    return true
                }
            } else if neighbor != parent {
                return true  // visited non-parent neighbor = cycle
            }
        }
        return false
    }

    for i := 0; i < n; i++ {
        if !visited[i] {
            if dfs(i, -1) {
                return true
            }
        }
    }
    return false
}
```

### 7. Cycle Detection (Directed Graph)

Use DFS with recursion stack tracking.

```go
func hasCycleDirected(graph map[int][]int, n int) bool {
    visited := make(map[int]bool)
    recStack := make(map[int]bool)

    var dfs func(node int) bool
    dfs = func(node int) bool {
        visited[node] = true
        recStack[node] = true

        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                if dfs(neighbor) {
                    return true
                }
            } else if recStack[neighbor] {
                return true  // back edge to node in current path
            }
        }

        recStack[node] = false  // backtrack
        return false
    }

    for i := 0; i < n; i++ {
        if !visited[i] {
            if dfs(i) {
                return true
            }
        }
    }
    return false
}
```

### 8. Topological Sort (Kahn's Algorithm - BFS)

Orders nodes so all edges point forward. Only works on DAGs.

```go
func topologicalSort(graph map[int][]int, n int) []int {
    inDegree := make([]int, n)

    // Calculate in-degrees
    for _, neighbors := range graph {
        for _, neighbor := range neighbors {
            inDegree[neighbor]++
        }
    }

    // Start with nodes that have no incoming edges
    queue := []int{}
    for i := 0; i < n; i++ {
        if inDegree[i] == 0 {
            queue = append(queue, i)
        }
    }

    result := []int{}
    for len(queue) > 0 {
        node := queue[0]
        queue = queue[1:]
        result = append(result, node)

        for _, neighbor := range graph[node] {
            inDegree[neighbor]--
            if inDegree[neighbor] == 0 {
                queue = append(queue, neighbor)
            }
        }
    }

    if len(result) != n {
        return nil  // cycle detected (not a DAG)
    }
    return result
}
```

### 9. Number of Connected Components

```go
func countComponents(n int, edges [][]int) int {
    // Build graph
    graph := make(map[int][]int)
    for _, edge := range edges {
        u, v := edge[0], edge[1]
        graph[u] = append(graph[u], v)
        graph[v] = append(graph[v], u)
    }

    visited := make(map[int]bool)
    count := 0

    var dfs func(node int)
    dfs = func(node int) {
        visited[node] = true
        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                dfs(neighbor)
            }
        }
    }

    for i := 0; i < n; i++ {
        if !visited[i] {
            count++
            dfs(i)
        }
    }

    return count
}
```

---

## DFS vs BFS: When to Use Which

| Criteria | DFS | BFS |
|----------|-----|-----|
| Shortest path (unweighted) | No | Yes |
| Connectivity / reachability | Yes | Yes |
| Cycle detection | Yes | Possible but less natural |
| Topological sort | Yes (DFS-based) | Yes (Kahn's algorithm) |
| Space complexity | O(h) recursion depth | O(w) width of graph |
| Path finding | Finds *a* path | Finds *shortest* path |
| Maze solving | Works | Better (shortest) |
| Detect bipartiteness | Works | Natural (coloring by level) |

**Rule of thumb:** Use BFS for shortest path in unweighted graphs. Use DFS for everything else unless BFS is simpler.

---

## When to Use

| Scenario | Graph Approach |
|----------|----------------|
| Modeling relationships (social, dependencies) | Yes |
| Shortest path (unweighted) | BFS |
| Shortest path (weighted, non-negative) | Dijkstra's |
| Shortest path (weighted, negative edges) | Bellman-Ford |
| Detect cycles | DFS with stack/parent tracking |
| Topological ordering (task scheduling) | Topological sort (Kahn's or DFS) |
| Connected components | DFS or Union-Find |
| Bipartite check | BFS with 2-coloring |

---

## Common Pitfalls

1. **Forgetting to mark nodes as visited.** Leads to infinite loops in cyclic graphs. Mark visited BEFORE recursing (DFS) or BEFORE adding to queue (BFS).

2. **Confusing directed vs undirected.** When building adjacency lists, undirected edges require adding both `u -> v` and `v -> u`.

3. **Off-by-one errors with 0-indexed vs 1-indexed.** Problem inputs may use 1-indexed nodes. Adjust when building the graph.

4. **Not handling disconnected components.** Many graphs have multiple components. Loop through all nodes to ensure full traversal.

5. **Using DFS for shortest path in unweighted graphs.** DFS finds *a* path, not the *shortest* path. Use BFS.

6. **Stack overflow with deep DFS recursion.** For graphs with 10^5+ nodes, use iterative DFS with an explicit stack.

7. **Incorrect cycle detection in directed graphs.** Need a recursion stack (or coloring: white/gray/black), not just visited set.

8. **Modifying graph during traversal.** Avoid adding/removing edges while traversing unless you understand the implications.

---

## Interview Relevance

Graphs are a high-signal topic in interviews. Master DFS, BFS, and graph building.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Connected components | "islands", "groups", "connected" | Number of Islands, Connected Components |
| Shortest path | "shortest", "minimum moves", "BFS" | Word Ladder, Shortest Path in Binary Matrix |
| Cycle detection | "cycle", "circular dependency", "deadlock" | Course Schedule, Redundant Connection |
| Topological sort | "prerequisites", "order", "dependencies" | Course Schedule II, Alien Dictionary |
| Clone graph | "deep copy", "clone", "graph" | Clone Graph |
| Bipartite | "two groups", "bipartite", "coloring" | Is Graph Bipartite? |

**Interview Insight:** Most graph problems boil down to: (1) Identify nodes and edges from the problem statement (not always obvious), (2) Build the graph, (3) Run DFS or BFS with appropriate state. Practice translating problems into graphs.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Number of Islands | Medium | DFS/BFS on 2D grid | 200 |
| 2 | Clone Graph | Medium | DFS/BFS with hash map | 133 |
| 3 | Course Schedule | Medium | Cycle detection in directed graph | 207 |
| 4 | Course Schedule II | Medium | Topological sort (Kahn's) | 210 |
| 5 | Pacific Atlantic Water Flow | Medium | DFS from boundaries | 417 |
| 6 | Number of Connected Components | Medium | DFS/Union-Find | 323 (premium) / 547 |
| 7 | Graph Valid Tree | Medium | Cycle detection + connectivity | 261 (premium) |

Start with 1-2 to master DFS/BFS. Problems 3-4 are essential for topological sort. Problem 5 tests creative DFS application.

---

## Special Graph Types

**DAG (Directed Acyclic Graph):**
- No cycles
- Enables topological ordering
- Used for: task scheduling, build systems, version control

**Bipartite Graph:**
- Nodes can be split into two groups with no edges within groups
- Check with 2-coloring via BFS
- Used for: matching problems, resource allocation

**Weighted Graph:**
- Edges have costs
- Requires specialized algorithms: Dijkstra, Bellman-Ford, Floyd-Warshall

---

## Quick Reference Card

```
Adjacency list:   graph := make(map[int][]int)
Add edge:         graph[u] = append(graph[u], v)
DFS:              Recursion or stack; mark visited before recursing
BFS:              Queue; mark visited before adding to queue
Cycle (directed): DFS with recursion stack
Cycle (undirected): DFS with parent tracking
Topological:      Kahn's (BFS with in-degrees) or DFS + reverse postorder
Shortest path:    BFS (unweighted); Dijkstra (weighted)
Components:       DFS/BFS from each unvisited node

Always:           Mark visited; handle disconnected components
```

---

> **Key Insight:** Most graph problems reduce to "build the graph, run DFS/BFS with state." The hard part is often recognizing what the nodes and edges represent -- sometimes they're not explicit in the problem. Practice translating problems into graphs.

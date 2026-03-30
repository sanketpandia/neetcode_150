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

---

## Implementation Patterns

### 1. Graph Representations

```java
// Adjacency list (most common)
Map<Integer, List<Integer>> graph = new HashMap<>();

// Add directed edge u -> v
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// Add undirected edge u — v
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
graph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);

// Adjacency list as int[][] (when vertices are 0..n-1)
List<Integer>[] adj = new List[n];
for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
adj[u].add(v);

// Adjacency matrix
int[][] matrix = new int[n][n];
matrix[u][v] = 1;  // or weight for weighted graphs
```

### 2. DFS (Recursive)

Explores as deep as possible before backtracking. Use for connectivity, cycle detection, topological sort.

```java
void dfs(int node, Map<Integer, List<Integer>> graph, boolean[] visited) {
    if (visited[node]) return;
    visited[node] = true;
    // Process node here

    for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
        dfs(neighbor, graph, visited);
    }
}
```

### 3. DFS (Iterative with Stack)

Useful when recursion depth might cause StackOverflowError.

```java
void dfsIterative(int start, Map<Integer, List<Integer>> graph, int n) {
    boolean[] visited = new boolean[n];
    Deque<Integer> stack = new ArrayDeque<>();
    stack.push(start);

    while (!stack.isEmpty()) {
        int node = stack.pop();
        if (visited[node]) continue;
        visited[node] = true;
        // Process node here

        for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited[neighbor]) stack.push(neighbor);
        }
    }
}
```

### 4. BFS (Level-Order Traversal)

Explores neighbors level by level. Use for shortest path in unweighted graphs.

```java
void bfs(int start, Map<Integer, List<Integer>> graph) {
    boolean[] visited = new boolean[/* n */];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.offer(start);
    visited[start] = true;

    while (!queue.isEmpty()) {
        int node = queue.poll();
        // Process node here

        for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                queue.offer(neighbor);
            }
        }
    }
}
```

### 5. Shortest Path (Unweighted Graph with BFS)

```java
int shortestPath(int start, int end, Map<Integer, List<Integer>> graph) {
    if (start == end) return 0;
    boolean[] visited = new boolean[/* n */];
    Queue<Integer> queue = new ArrayDeque<>();
    queue.offer(start);
    visited[start] = true;
    int distance = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();
        distance++;
        for (int i = 0; i < size; i++) {
            int node = queue.poll();
            for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (neighbor == end) return distance;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
    }
    return -1;  // no path exists
}
```

### 6. Cycle Detection (Undirected Graph)

Use DFS with parent tracking.

```java
boolean hasCycleUndirected(int[][] edges, int n) {
    List<Integer>[] adj = new List[n];
    for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    for (int[] e : edges) { adj[e[0]].add(e[1]); adj[e[1]].add(e[0]); }

    boolean[] visited = new boolean[n];
    for (int i = 0; i < n; i++) {
        if (!visited[i] && dfsUndirected(i, -1, adj, visited)) return true;
    }
    return false;
}

boolean dfsUndirected(int node, int parent, List<Integer>[] adj, boolean[] visited) {
    visited[node] = true;
    for (int neighbor : adj[node]) {
        if (!visited[neighbor]) {
            if (dfsUndirected(neighbor, node, adj, visited)) return true;
        } else if (neighbor != parent) {
            return true;  // visited non-parent neighbor = cycle
        }
    }
    return false;
}
```

### 7. Topological Sort (Kahn's Algorithm - BFS)

Orders nodes so all edges point forward. Only works on DAGs.

```java
int[] topologicalSort(int[][] prerequisites, int n) {
    List<Integer>[] adj = new List[n];
    for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    int[] inDegree = new int[n];

    for (int[] pre : prerequisites) {
        adj[pre[1]].add(pre[0]);  // pre[1] -> pre[0]
        inDegree[pre[0]]++;
    }

    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (inDegree[i] == 0) queue.offer(i);

    int[] order = new int[n];
    int idx = 0;
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order[idx++] = node;
        for (int neighbor : adj[node]) {
            if (--inDegree[neighbor] == 0) queue.offer(neighbor);
        }
    }
    return idx == n ? order : new int[]{};  // empty if cycle detected
}
```

### 8. Number of Connected Components

```java
int countComponents(int n, int[][] edges) {
    List<Integer>[] adj = new List[n];
    for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
    for (int[] e : edges) { adj[e[0]].add(e[1]); adj[e[1]].add(e[0]); }

    boolean[] visited = new boolean[n];
    int count = 0;
    for (int i = 0; i < n; i++) {
        if (!visited[i]) {
            dfsComponent(i, adj, visited);
            count++;
        }
    }
    return count;
}

void dfsComponent(int node, List<Integer>[] adj, boolean[] visited) {
    visited[node] = true;
    for (int neighbor : adj[node]) if (!visited[neighbor]) dfsComponent(neighbor, adj, visited);
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
| Space in balanced graph | O(h) | O(w) width of graph |
| Path finding | Finds *a* path | Finds *shortest* path |
| Detect bipartiteness | Works | Natural (coloring by level) |

---

## Common Pitfalls

1. **Forgetting to mark nodes as visited.** Leads to infinite loops in cyclic graphs. Mark visited BEFORE recursing (DFS) or BEFORE adding to queue (BFS).

2. **Confusing directed vs undirected.** When building adjacency lists, undirected edges require adding both `u -> v` and `v -> u`.

3. **Not handling disconnected components.** Many graphs have multiple components. Loop through all nodes to ensure full traversal.

4. **Using DFS for shortest path.** DFS finds *a* path, not the *shortest* path. Use BFS for unweighted shortest path.

5. **Stack overflow with deep DFS recursion.** For graphs with 10^5+ nodes, use iterative DFS with an explicit `Deque`.

6. **Incorrect cycle detection in directed graphs.** Need a recursion stack (or coloring: white/gray/black), not just visited array.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Connected components | "islands", "groups", "connected" | Number of Islands, Connected Components |
| Shortest path | "shortest", "minimum moves", "BFS" | Word Ladder, Shortest Path in Binary Matrix |
| Cycle detection | "cycle", "circular dependency", "deadlock" | Course Schedule, Redundant Connection |
| Topological sort | "prerequisites", "order", "dependencies" | Course Schedule II, Alien Dictionary |
| Clone graph | "deep copy", "clone", "graph" | Clone Graph |
| Bipartite | "two groups", "bipartite", "coloring" | Is Graph Bipartite? |

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Number of Islands | Medium | DFS/BFS on 2D grid | 200 |
| 2 | Clone Graph | Medium | DFS/BFS with hash map | 133 |
| 3 | Course Schedule | Medium | Cycle detection in directed graph | 207 |
| 4 | Course Schedule II | Medium | Topological sort (Kahn's) | 210 |
| 5 | Pacific Atlantic Water Flow | Medium | DFS from boundaries | 417 |
| 6 | Number of Connected Components | Medium | DFS/Union-Find | 323 / 547 |

---

## Quick Reference Card

```java
// Adjacency list (vertices 0..n-1)
List<Integer>[] adj = new List[n];
for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
adj[u].add(v);  // directed; also adj[v].add(u) for undirected

// DFS (recursive)
void dfs(int node) {
    visited[node] = true;
    for (int nb : adj[node]) if (!visited[nb]) dfs(nb);
}

// BFS
Queue<Integer> q = new ArrayDeque<>();
q.offer(start); visited[start] = true;
while (!q.isEmpty()) {
    int node = q.poll();
    for (int nb : adj[node]) if (!visited[nb]) { visited[nb] = true; q.offer(nb); }
}

Always:  Mark visited; handle disconnected components with outer loop
```

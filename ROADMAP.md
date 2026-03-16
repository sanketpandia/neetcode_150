# DSA Study Roadmap

A master overview and study plan for Data Structures, Searching, and Sorting -- everything you need to tackle the NeetCode 150 with confidence.

---

## Suggested Study Order

Study data structures first, then searching algorithms, then sorting algorithms. Each layer builds on the previous.

```
1. Data Structures          --> Foundation for everything
2. Searching Algorithms     --> Uses arrays, trees, graphs, hash maps
3. Sorting Algorithms       --> Uses arrays, heaps, divide-and-conquer
4. NeetCode 150 Problems    --> Apply all of the above
```

**Within each area, follow the order in the respective syllabus -- topics are sequenced so each builds on what came before.**

---

## Syllabus Files

| File | What It Covers |
|------|----------------|
| [data_structures/SYLLABUS.md](data_structures/SYLLABUS.md) | 15 core data structures: arrays, strings, linked lists, stacks, queues, hash maps, trees, BSTs, heaps, tries, graphs, union-find, monotonic stack/queue, segment trees, LRU cache |
| [searches/SYLLABUS.md](searches/SYLLABUS.md) | 8 searching algorithms: linear search, binary search, two pointers, sliding window, DFS, BFS, interpolation search, exponential search |
| [sorts/SYLLABUS.md](sorts/SYLLABUS.md) | 11 sorting algorithms: bubble, selection, insertion, merge, quick, heap sort, counting, radix, bucket, Tim sort, Dutch National Flag |

---

## Master Complexity Cheat Sheet

### Data Structure Operations

| Structure | Access | Search | Insert | Delete | Space |
|-----------|--------|--------|--------|--------|-------|
| Array / Slice | O(1) | O(n) | O(n) | O(n) | O(n) |
| Linked List | O(n) | O(n) | O(1)* | O(1)* | O(n) |
| Hash Map | O(1) avg | O(1) avg | O(1) avg | O(1) avg | O(n) |
| BST (balanced) | O(log n) | O(log n) | O(log n) | O(log n) | O(n) |
| Heap | O(n) | O(n) | O(log n) | O(log n) | O(n) |
| Trie | -- | O(m) | O(m) | O(m) | O(n * m) |
| Stack | O(n) | O(n) | O(1) | O(1) | O(n) |
| Queue | O(n) | O(n) | O(1) | O(1) | O(n) |

\* *When you already have a pointer to the node.*
*m = key/word length for tries.*

### Searching Algorithms

| Algorithm | Best | Average | Worst | Space | Prerequisite |
|-----------|------|---------|-------|-------|-------------|
| Linear Search | O(1) | O(n) | O(n) | O(1) | None |
| Binary Search | O(1) | O(log n) | O(log n) | O(1) | Sorted data |
| Two Pointers | O(n) | O(n) | O(n) | O(1) | Usually sorted |
| Sliding Window | O(n) | O(n) | O(n) | O(1)-O(k) | Sequential data |
| DFS | O(V+E) | O(V+E) | O(V+E) | O(V) | Graph/tree |
| BFS | O(V+E) | O(V+E) | O(V+E) | O(V) | Graph/tree |
| Interpolation Search | O(1) | O(log log n) | O(n) | O(1) | Sorted + uniform |
| Exponential Search | O(1) | O(log n) | O(log n) | O(1) | Sorted data |

### Sorting Algorithms

| Algorithm | Best | Average | Worst | Space | Stable? |
|-----------|------|---------|-------|-------|---------|
| Bubble Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes |
| Selection Sort | O(n^2) | O(n^2) | O(n^2) | O(1) | No |
| Insertion Sort | O(n) | O(n^2) | O(n^2) | O(1) | Yes |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) | Yes |
| Quick Sort | O(n log n) | O(n log n) | O(n^2) | O(log n) | No |
| Heap Sort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |
| Counting Sort | O(n+k) | O(n+k) | O(n+k) | O(k) | Yes |
| Radix Sort | O(d*(n+k)) | O(d*(n+k)) | O(d*(n+k)) | O(n+k) | Yes |
| Bucket Sort | O(n+k) | O(n+k) | O(n^2) | O(n+k) | Yes* |
| Tim Sort | O(n) | O(n log n) | O(n log n) | O(n) | Yes |

*k = range of input values, d = number of digits.*
*\* Depends on inner sort.*

---

## NeetCode 150 Category Mapping

This table maps each NeetCode category to the data structures and algorithms you should know before attempting it.

| NeetCode Category | Data Structures Needed | Algorithms Needed |
|-------------------|----------------------|-------------------|
| Arrays & Hashing | Arrays, Hash Maps, Hash Sets | Linear Search, Sorting basics |
| Two Pointers | Arrays | Two Pointers, Binary Search |
| Sliding Window | Arrays, Hash Maps | Sliding Window |
| Stack | Stacks, Monotonic Stack | -- |
| Binary Search | Arrays | Binary Search (all variants) |
| Linked List | Linked Lists | Two Pointers (fast/slow) |
| Trees | Binary Trees, BST | DFS (pre/in/post order), BFS |
| Tries | Tries | DFS |
| Heap / Priority Queue | Heaps | Quickselect |
| Backtracking | Arrays, Hash Sets | DFS, Backtracking |
| Graphs | Graphs, Union-Find | DFS, BFS, Topological Sort |
| Advanced Graphs | Graphs, Heaps | Dijkstra, Prim/Kruskal |
| 1-D Dynamic Programming | Arrays | -- (DP is its own topic) |
| 2-D Dynamic Programming | 2D Arrays | -- (DP is its own topic) |
| Greedy | Arrays, Heaps | Sorting |
| Intervals | Arrays | Sorting |
| Math & Geometry | Arrays | -- |
| Bit Manipulation | -- | -- (Bit ops are their own topic) |

---

## Study Plan: Suggested Weekly Schedule

This is a rough guide. Adjust based on your pace.

### Week 1-2: Foundations
- [ ] Arrays / Slices
- [ ] Strings
- [ ] Hash Maps / Hash Sets
- [ ] Linear Search
- [ ] Binary Search (standard)
- **Practice:** NeetCode Arrays & Hashing (9 problems)

### Week 3: Linear Structures
- [ ] Linked Lists
- [ ] Stacks
- [ ] Queues
- [ ] Two Pointers
- [ ] Sliding Window
- **Practice:** NeetCode Two Pointers, Sliding Window, Stack (12 problems)

### Week 4: Sorting
- [ ] Bubble Sort, Selection Sort, Insertion Sort
- [ ] Merge Sort
- [ ] Quick Sort (+ Quickselect)
- [ ] Heap Sort
- [ ] Counting Sort, Radix Sort, Bucket Sort
- **Practice:** NeetCode Binary Search (7 problems)

### Week 5-6: Trees
- [ ] Binary Trees (all traversals)
- [ ] Binary Search Trees
- [ ] Heaps / Priority Queues
- [ ] Tries
- **Practice:** NeetCode Trees, Tries, Heap (22 problems)

### Week 7-8: Graphs
- [ ] Graph representations
- [ ] DFS and BFS
- [ ] Union-Find
- [ ] Topological Sort
- **Practice:** NeetCode Graphs, Advanced Graphs (13 problems)

### Week 9+: Advanced
- [ ] Monotonic Stack / Queue
- [ ] LRU Cache
- [ ] Segment Trees / Fenwick Trees (optional for interviews)
- **Practice:** Remaining NeetCode categories

---

## Progress Tracker

### Data Structures
- [ ] Arrays / Slices
- [ ] Strings
- [ ] Linked Lists
- [ ] Stacks
- [ ] Queues
- [ ] Hash Maps / Hash Sets
- [ ] Binary Trees
- [ ] Binary Search Trees
- [ ] Heaps / Priority Queues
- [ ] Tries
- [ ] Graphs
- [ ] Union-Find
- [ ] Monotonic Stack / Queue
- [ ] Segment Trees / Fenwick Trees
- [ ] LRU Cache

### Searching Algorithms
- [ ] Linear Search
- [ ] Binary Search
- [ ] Two Pointers
- [ ] Sliding Window
- [ ] DFS
- [ ] BFS
- [ ] Interpolation Search
- [ ] Exponential Search

### Sorting Algorithms
- [ ] Bubble Sort
- [ ] Selection Sort
- [ ] Insertion Sort
- [ ] Merge Sort
- [ ] Quick Sort
- [ ] Heap Sort
- [ ] Counting Sort
- [ ] Radix Sort
- [ ] Bucket Sort
- [ ] Tim Sort (conceptual)
- [ ] Dutch National Flag / 3-Way Partition

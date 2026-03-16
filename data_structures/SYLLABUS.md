# Data Structures Syllabus

A comprehensive reference for the core data structures you need to master for coding interviews and the NeetCode 150. Each section covers the concept, key operations with complexity, when to use it, Go implementation notes, common pitfalls, and NeetCode relevance.

---

## Table of Contents

1. [Arrays / Slices](#1-arrays--slices)
2. [Strings](#2-strings)
3. [Linked Lists](#3-linked-lists)
4. [Stacks](#4-stacks)
5. [Queues](#5-queues)
6. [Hash Maps / Hash Sets](#6-hash-maps--hash-sets)
7. [Binary Trees](#7-binary-trees)
8. [Binary Search Trees (BST)](#8-binary-search-trees-bst)
9. [Heaps / Priority Queues](#9-heaps--priority-queues)
10. [Tries (Prefix Trees)](#10-tries-prefix-trees)
11. [Graphs](#11-graphs)
12. [Union-Find (Disjoint Set)](#12-union-find-disjoint-set)
13. [Monotonic Stack / Monotonic Queue](#13-monotonic-stack--monotonic-queue)
14. [Segment Trees / Binary Indexed Trees](#14-segment-trees--binary-indexed-trees-fenwick)
15. [LRU Cache](#15-lru-cache)

---

## Master Operations Complexity Table

| Operation | Array / Slice | Linked List | Hash Map | BST (balanced) | Heap |
|-----------|---------------|-------------|----------|----------------|------|
| Access    | O(1)          | O(n)        | O(1) avg | O(log n)       | O(n) |
| Search    | O(n)          | O(n)        | O(1) avg | O(log n)       | O(n) |
| Insert    | O(n)          | O(1)*       | O(1) avg | O(log n)       | O(log n) |
| Delete    | O(n)          | O(1)*       | O(1) avg | O(log n)       | O(log n) |

\* *Linked list insert/delete is O(1) only when you already have a pointer to the node; finding it is O(n).*

---

## Linear Structures

---

### 1. Arrays / Slices

**Difficulty:** Beginner

**Concept:**
Arrays store elements in contiguous memory, allowing O(1) access by index. In Go, arrays have a fixed size at compile time, while slices are dynamically-sized views over an underlying array.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Access by index | O(1) | `arr[i]` |
| Search (unsorted) | O(n) | Linear scan |
| Search (sorted) | O(log n) | Binary search |
| Append | O(1) amortized | Go `append()` doubles capacity when full |
| Insert at index | O(n) | Must shift elements right |
| Delete at index | O(n) | Must shift elements left |
| Get length | O(1) | `len(s)` |
| Get capacity | O(1) | `cap(s)` |

**Go Implementation Notes:**

```go
// Fixed-size array
var arr [5]int

// Slice (dynamic)
s := make([]int, 0, 10)  // len=0, cap=10
s = append(s, 42)

// Slice from array
a := [5]int{1, 2, 3, 4, 5}
sub := a[1:4]  // [2, 3, 4] -- shares underlying memory

// Copy (to avoid shared memory issues)
dst := make([]int, len(src))
copy(dst, src)

// Delete element at index i (order preserved)
s = append(s[:i], s[i+1:]...)

// Delete element at index i (order NOT preserved, O(1))
s[i] = s[len(s)-1]
s = s[:len(s)-1]
```

**Common Pitfalls:**
- Slices share underlying arrays -- modifying one can affect another
- `append()` may allocate a new backing array, breaking shared references
- Off-by-one errors in slice bounds `[low:high)` -- high is exclusive

> **Key Insight:** When you need O(1) random access and mostly append to the end, slices are your go-to. If you frequently insert/delete in the middle, consider a linked list.

**NeetCode Relevance:** Arrays & Hashing, Two Pointers, Sliding Window -- nearly every problem uses slices.

---

### 2. Strings

**Difficulty:** Beginner

**Concept:**
Strings in Go are immutable sequences of bytes. They are UTF-8 encoded by default, meaning a single character can be 1-4 bytes. The `rune` type represents a Unicode code point.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Access byte by index | O(1) | `s[i]` returns a byte |
| Iterate by rune | O(n) | `for _, r := range s` |
| Concatenation | O(n+m) | Creates new string |
| Substring | O(1)* | Shares backing memory |
| Length (bytes) | O(1) | `len(s)` |
| Length (runes) | O(n) | `utf8.RuneCountInString(s)` |

**Go Implementation Notes:**

```go
// Strings are immutable -- this creates a new string each time
s := "hello"
s += " world"  // O(n) -- avoid in loops

// Use strings.Builder for efficient concatenation
var b strings.Builder
for i := 0; i < 1000; i++ {
    b.WriteString("a")
}
result := b.String()

// Convert to rune slice for mutation
runes := []rune(s)
runes[0] = 'H'
s = string(runes)

// Byte vs Rune
s := "café"
fmt.Println(len(s))                    // 5 (bytes)
fmt.Println(utf8.RuneCountInString(s)) // 4 (runes)

// Common string operations
strings.Contains(s, "sub")
strings.Split(s, ",")
strings.ToLower(s)
strings.TrimSpace(s)
```

**Common Pitfalls:**
- `s[i]` gives you a byte, not a rune -- this breaks with multi-byte characters
- String concatenation in a loop is O(n^2) -- use `strings.Builder`
- Comparing strings is O(n), not O(1)

> **Key Insight:** Always think about whether you're working with bytes or runes. For interview problems with ASCII-only input, bytes are fine. For Unicode-aware code, iterate with `range` to get runes.

**NeetCode Relevance:** Arrays & Hashing (anagram problems), Sliding Window (substring problems), Two Pointers.

---

### 3. Linked Lists

**Difficulty:** Beginner

**Concept:**
A linked list is a chain of nodes where each node holds a value and a pointer to the next node (singly linked) or both next and previous (doubly linked). Unlike arrays, elements are not stored contiguously.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Access by index | O(n) | Must traverse from head |
| Search | O(n) | Linear traversal |
| Insert at head | O(1) | Update head pointer |
| Insert at tail | O(1) | If you maintain a tail pointer |
| Insert after node | O(1) | Given pointer to the node |
| Delete node | O(1) | Given pointer + previous pointer |
| Delete by value | O(n) | Must find it first |

**Go Implementation Notes:**

```go
// Singly linked list node
type ListNode struct {
    Val  int
    Next *ListNode
}

// Dummy head technique (simplifies edge cases)
dummy := &ListNode{Next: head}
curr := dummy
for curr.Next != nil {
    if curr.Next.Val == target {
        curr.Next = curr.Next.Next  // delete
    } else {
        curr = curr.Next
    }
}
return dummy.Next

// Reverse a linked list (iterative)
func reverseList(head *ListNode) *ListNode {
    var prev *ListNode
    curr := head
    for curr != nil {
        next := curr.Next
        curr.Next = prev
        prev = curr
        curr = next
    }
    return prev
}

// Fast and slow pointer (find middle)
slow, fast := head, head
for fast != nil && fast.Next != nil {
    slow = slow.Next
    fast = fast.Next.Next
}
// slow is now at the middle
```

**Common Pitfalls:**
- Forgetting to handle `nil` head or single-node lists
- Losing references when rearranging pointers (always save `next` before overwriting)
- Not using dummy nodes -- leads to special-casing head operations

> **Key Insight:** Use the dummy head pattern to eliminate edge cases. Use fast/slow pointers to find midpoints, detect cycles, and find the kth node from the end.

**NeetCode Relevance:** Linked List category (reverse, merge, detect cycle, reorder).

---

### 4. Stacks

**Difficulty:** Beginner

**Concept:**
A stack is a Last-In-First-Out (LIFO) data structure. You push elements onto the top and pop them from the top. In Go, a slice is the natural implementation.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Push | O(1) amortized | `append(stack, val)` |
| Pop | O(1) | `stack = stack[:len(stack)-1]` |
| Peek / Top | O(1) | `stack[len(stack)-1]` |
| IsEmpty | O(1) | `len(stack) == 0` |

**Go Implementation Notes:**

```go
// Stack using a slice
stack := []int{}

// Push
stack = append(stack, 42)

// Peek
top := stack[len(stack)-1]

// Pop
top = stack[len(stack)-1]
stack = stack[:len(stack)-1]

// IsEmpty
if len(stack) == 0 {
    // empty
}
```

**Patterns to Know:**

1. **Matching brackets:** Push opening brackets, pop and compare for closing brackets.
2. **Monotonic stack:** Maintain a stack where elements are always in increasing (or decreasing) order. Used for "next greater element" and histogram problems.
3. **Expression evaluation:** Convert infix to postfix, then evaluate with a stack.
4. **DFS (iterative):** Use a stack instead of recursion.

```go
// Monotonic decreasing stack -- next greater element
func nextGreaterElement(nums []int) []int {
    n := len(nums)
    result := make([]int, n)
    for i := range result {
        result[i] = -1
    }
    stack := []int{} // stores indices
    for i := 0; i < n; i++ {
        for len(stack) > 0 && nums[i] > nums[stack[len(stack)-1]] {
            idx := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[idx] = nums[i]
        }
        stack = append(stack, i)
    }
    return result
}
```

**Common Pitfalls:**
- Popping from an empty stack (always check `len(stack) > 0`)
- Forgetting that Go slices keep the underlying memory -- for large stacks, this can cause memory leaks

> **Key Insight:** Whenever you see "matching", "nesting", or "next greater/smaller" in a problem, think stack.

**NeetCode Relevance:** Stack category (valid parentheses, daily temperatures, evaluate RPN, min stack).

---

### 5. Queues

**Difficulty:** Beginner

**Concept:**
A queue is a First-In-First-Out (FIFO) data structure. Elements are enqueued at the back and dequeued from the front. A deque (double-ended queue) allows operations at both ends.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Enqueue | O(1) amortized | Append to back |
| Dequeue | O(1)* | Remove from front |
| Peek front | O(1) | `queue[0]` |
| IsEmpty | O(1) | `len(queue) == 0` |

\* *Dequeue from a slice is O(n) due to shifting. Use a ring buffer or linked list for true O(1).*

**Go Implementation Notes:**

```go
// Simple queue using slice (fine for interview problems)
queue := []int{}

// Enqueue
queue = append(queue, 42)

// Dequeue
front := queue[0]
queue = queue[1:]  // Note: O(n) shift, but OK for most interviews

// For true O(1) dequeue, use container/list
import "container/list"
q := list.New()
q.PushBack(42)           // enqueue
front := q.Front().Value // peek
q.Remove(q.Front())      // dequeue
```

**Patterns to Know:**

1. **BFS traversal:** The core use case for queues.
2. **Level-order traversal:** Process tree nodes level by level.
3. **Sliding window maximum:** Use a monotonic deque.

```go
// BFS level-order traversal
func levelOrder(root *TreeNode) [][]int {
    if root == nil {
        return nil
    }
    result := [][]int{}
    queue := []*TreeNode{root}
    for len(queue) > 0 {
        level := []int{}
        size := len(queue)
        for i := 0; i < size; i++ {
            node := queue[0]
            queue = queue[1:]
            level = append(level, node.Val)
            if node.Left != nil {
                queue = append(queue, node.Left)
            }
            if node.Right != nil {
                queue = append(queue, node.Right)
            }
        }
        result = append(result, level)
    }
    return result
}
```

**Common Pitfalls:**
- Using `queue[1:]` repeatedly leaks memory (the old elements can't be GC'd)
- For performance-critical code, use a circular buffer or `container/list`

> **Key Insight:** Queue = BFS. If you need shortest path in an unweighted graph or level-by-level processing, reach for a queue.

**NeetCode Relevance:** Trees (level-order traversal), Graphs (BFS shortest path).

---

## Hashing

---

### 6. Hash Maps / Hash Sets

**Difficulty:** Beginner

**Concept:**
A hash map stores key-value pairs with O(1) average-time lookup, insertion, and deletion by hashing keys to array indices. A hash set is a hash map where you only care about keys (existence).

**Key Operations:**

| Operation | Average | Worst Case | Notes |
|-----------|---------|------------|-------|
| Insert | O(1) | O(n) | Worst case with many collisions |
| Lookup | O(1) | O(n) | Same |
| Delete | O(1) | O(n) | Same |
| Iterate | O(n) | O(n) | Order is random in Go |

**Go Implementation Notes:**

```go
// Hash map
m := make(map[string]int)
m["alice"] = 90
m["bob"] = 85

// Check existence
if val, ok := m["alice"]; ok {
    fmt.Println("Found:", val)
}

// Delete
delete(m, "bob")

// Iterate (order is NOT guaranteed)
for key, val := range m {
    fmt.Println(key, val)
}

// Hash set (use map[T]bool or map[T]struct{})
seen := make(map[int]struct{})
seen[42] = struct{}{}
if _, exists := seen[42]; exists {
    // 42 is in the set
}
// map[T]struct{} uses zero bytes per value vs map[T]bool
```

**Patterns to Know:**

1. **Frequency counting:** Count occurrences of each element.
2. **Two-sum pattern:** Store complements for O(1) lookup.
3. **Grouping:** Group items by a computed key (e.g., anagram grouping).
4. **Deduplication:** Use a set to track seen elements.

```go
// Frequency counting
freq := make(map[rune]int)
for _, ch := range s {
    freq[ch]++
}

// Two-sum pattern
seen := make(map[int]int) // value -> index
for i, num := range nums {
    complement := target - num
    if j, ok := seen[complement]; ok {
        return []int{j, i}
    }
    seen[num] = i
}
```

**Common Pitfalls:**
- Map iteration order is randomized in Go -- never depend on it
- Maps are not safe for concurrent access (use `sync.Map` or a mutex)
- The zero value for missing keys can be misleading (always use the comma-ok idiom)
- Slices cannot be map keys (use arrays like `[26]int` or convert to string)

> **Key Insight:** Whenever you need to look something up by value in O(1), think hash map. It's the most versatile data structure for interview problems.

**NeetCode Relevance:** Arrays & Hashing (contains duplicate, two sum, group anagrams, top K frequent).

---

## Trees

---

### 7. Binary Trees

**Difficulty:** Beginner-Intermediate

**Concept:**
A binary tree is a hierarchical structure where each node has at most two children (left and right). Key terminology:
- **Root:** The topmost node
- **Leaf:** A node with no children
- **Height:** Longest path from root to a leaf
- **Depth:** Distance from root to a given node
- **Complete:** Every level filled except possibly the last, which fills left to right
- **Full:** Every node has 0 or 2 children
- **Perfect:** All leaves at the same depth, all internal nodes have 2 children

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| DFS traversal | O(n) | Visit every node |
| BFS traversal | O(n) | Visit every node |
| Height | O(n) | Recursive DFS |
| Count nodes | O(n) | Traverse all |

**Go Implementation Notes:**

```go
type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}

// Preorder: Root -> Left -> Right
func preorder(root *TreeNode) {
    if root == nil {
        return
    }
    fmt.Println(root.Val)  // process
    preorder(root.Left)
    preorder(root.Right)
}

// Inorder: Left -> Root -> Right (gives sorted order for BST)
func inorder(root *TreeNode) {
    if root == nil {
        return
    }
    inorder(root.Left)
    fmt.Println(root.Val)  // process
    inorder(root.Right)
}

// Postorder: Left -> Right -> Root
func postorder(root *TreeNode) {
    if root == nil {
        return
    }
    postorder(root.Left)
    postorder(root.Right)
    fmt.Println(root.Val)  // process
}

// Height of tree
func height(root *TreeNode) int {
    if root == nil {
        return 0
    }
    left := height(root.Left)
    right := height(root.Right)
    if left > right {
        return left + 1
    }
    return right + 1
}
```

**Traversal Summary:**

| Traversal | Order | Common Use |
|-----------|-------|------------|
| Preorder | Root, Left, Right | Copy/serialize a tree |
| Inorder | Left, Root, Right | BST sorted output |
| Postorder | Left, Right, Root | Delete tree, evaluate expressions |
| Level-order | Level by level | BFS, shortest path |

**Common Pitfalls:**
- Forgetting the `nil` base case in recursive functions
- Confusing height (root-down) with depth (root-down from top)
- Stack overflow on very deep trees -- consider iterative DFS

> **Key Insight:** Most binary tree problems follow a pattern: recursively solve for left subtree, solve for right subtree, combine results. Think "What info do I need from my children?"

**NeetCode Relevance:** Trees category (invert tree, max depth, diameter, balanced tree, subtree check).

---

### 8. Binary Search Trees (BST)

**Difficulty:** Intermediate

**Concept:**
A BST is a binary tree where for every node: all values in the left subtree are less, and all values in the right subtree are greater. This ordering property enables O(log n) search, insert, and delete on average -- but O(n) in the worst case (degenerate/skewed tree).

**Key Operations:**

| Operation | Average | Worst Case | Notes |
|-----------|---------|------------|-------|
| Search | O(log n) | O(n) | Degenerate tree |
| Insert | O(log n) | O(n) | Same |
| Delete | O(log n) | O(n) | Same |
| Inorder traversal | O(n) | O(n) | Produces sorted output |
| Find min/max | O(log n) | O(n) | Go leftmost / rightmost |

**Go Implementation Notes:**

```go
// Search
func searchBST(root *TreeNode, val int) *TreeNode {
    if root == nil || root.Val == val {
        return root
    }
    if val < root.Val {
        return searchBST(root.Left, val)
    }
    return searchBST(root.Right, val)
}

// Insert
func insertBST(root *TreeNode, val int) *TreeNode {
    if root == nil {
        return &TreeNode{Val: val}
    }
    if val < root.Val {
        root.Left = insertBST(root.Left, val)
    } else {
        root.Right = insertBST(root.Right, val)
    }
    return root
}

// Validate BST
func isValidBST(root *TreeNode) bool {
    return validate(root, math.MinInt64, math.MaxInt64)
}

func validate(node *TreeNode, min, max int) bool {
    if node == nil {
        return true
    }
    if node.Val <= min || node.Val >= max {
        return false
    }
    return validate(node.Left, min, node.Val) &&
           validate(node.Right, node.Val, max)
}
```

**Balanced BST Variants (conceptual):**
- **AVL Tree:** Strictly balanced (height diff <= 1), faster lookups
- **Red-Black Tree:** Loosely balanced, fewer rotations on insert/delete
- Go's standard library does not include a BST; use sorted slices + binary search or third-party packages

**Common Pitfalls:**
- BST property is about ALL descendants, not just immediate children
- Duplicate values: decide a convention (left or right) and stay consistent
- Deletion with two children: replace with inorder successor (or predecessor)

> **Key Insight:** Inorder traversal of a BST always gives sorted output. If a problem says "BST", think about leveraging the sorted property.

**NeetCode Relevance:** Trees (validate BST, kth smallest, LCA of BST).

---

### 9. Heaps / Priority Queues

**Difficulty:** Intermediate

**Concept:**
A heap is a complete binary tree where the parent is always smaller (min-heap) or larger (max-heap) than its children. It's typically stored as an array. A priority queue is the abstract data type; a heap is the implementation.

**Key Relations (0-indexed array):**
- Parent of `i`: `(i - 1) / 2`
- Left child of `i`: `2*i + 1`
- Right child of `i`: `2*i + 2`

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert (push) | O(log n) | Sift up |
| Extract min/max (pop) | O(log n) | Sift down |
| Peek min/max | O(1) | Root element |
| Build heap from array | O(n) | Bottom-up heapify |
| Search | O(n) | No ordering guarantee beyond parent-child |

**Go Implementation Notes:**

Go provides `container/heap` which requires implementing the `heap.Interface`:

```go
import "container/heap"

// Min-heap of ints
type MinHeap []int

func (h MinHeap) Len() int            { return len(h) }
func (h MinHeap) Less(i, j int) bool   { return h[i] < h[j] }  // < for min, > for max
func (h MinHeap) Swap(i, j int)        { h[i], h[j] = h[j], h[i] }

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
h := &MinHeap{5, 3, 8, 1}
heap.Init(h)            // O(n) heapify
heap.Push(h, 2)         // O(log n)
min := heap.Pop(h).(int) // O(log n), returns 1
```

**Patterns to Know:**

1. **Top K elements:** Use a min-heap of size K. Push all elements; if heap size > K, pop. Final heap contains top K.
2. **Kth largest/smallest:** Same as top K, peek the root.
3. **Merge K sorted lists:** Push first element of each list, pop smallest, push next from that list.
4. **Median from data stream:** Use two heaps (max-heap for lower half, min-heap for upper half).

**Common Pitfalls:**
- Go's `container/heap` uses an interface with pointer receivers for Push/Pop
- Default is min-heap; for max-heap, flip the `Less` comparison
- Don't confuse `heap.Push` (the package function) with `h.Push` (the interface method)

> **Key Insight:** Whenever you need to repeatedly find the minimum (or maximum) from a dynamic collection, think heap. "Top K" and "Kth largest" are immediate heap signals.

**NeetCode Relevance:** Heap / Priority Queue (Kth largest in stream, last stone weight, K closest points, task scheduler, median finder).

---

### 10. Tries (Prefix Trees)

**Difficulty:** Intermediate

**Concept:**
A trie is a tree-like structure where each node represents a character. Paths from root to nodes form prefixes of stored strings. It enables O(m) search/insert where m is the word length, regardless of how many words are stored.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert word | O(m) | m = word length |
| Search word | O(m) | Exact match |
| Search prefix | O(m) | Check if any word starts with prefix |
| Delete word | O(m) | May need cleanup of empty nodes |
| Autocomplete | O(m + k) | m = prefix length, k = results |

**Go Implementation Notes:**

```go
type TrieNode struct {
    Children map[rune]*TrieNode  // or [26]*TrieNode for lowercase ASCII
    IsEnd    bool
}

type Trie struct {
    Root *TrieNode
}

func NewTrie() *Trie {
    return &Trie{Root: &TrieNode{Children: make(map[rune]*TrieNode)}}
}

func (t *Trie) Insert(word string) {
    node := t.Root
    for _, ch := range word {
        if _, ok := node.Children[ch]; !ok {
            node.Children[ch] = &TrieNode{Children: make(map[rune]*TrieNode)}
        }
        node = node.Children[ch]
    }
    node.IsEnd = true
}

func (t *Trie) Search(word string) bool {
    node := t.Root
    for _, ch := range word {
        if _, ok := node.Children[ch]; !ok {
            return false
        }
        node = node.Children[ch]
    }
    return node.IsEnd
}

func (t *Trie) StartsWith(prefix string) bool {
    node := t.Root
    for _, ch := range prefix {
        if _, ok := node.Children[ch]; !ok {
            return false
        }
        node = node.Children[ch]
    }
    return true
}
```

**Common Pitfalls:**
- Using `map[rune]*TrieNode` is flexible but slower; `[26]*TrieNode` is faster for lowercase ASCII
- Forgetting to mark `IsEnd` -- "app" and "apple" need distinct flags
- Memory usage can be high with sparse tries

> **Key Insight:** Tries excel when you need prefix-based operations. If a problem involves dictionaries, word search, or autocomplete, a trie is likely the answer.

**NeetCode Relevance:** Tries (implement trie, word search II, design add and search words).

---

## Graphs

---

### 11. Graphs

**Difficulty:** Intermediate

**Concept:**
A graph is a set of vertices (nodes) connected by edges. Graphs can be:
- **Directed** or **Undirected**
- **Weighted** or **Unweighted**
- **Cyclic** or **Acyclic** (DAG = Directed Acyclic Graph)

**Representations:**

| Representation | Space | Add Edge | Check Edge | Iterate Neighbors |
|---------------|-------|----------|------------|-------------------|
| Adjacency List | O(V+E) | O(1) | O(degree) | O(degree) |
| Adjacency Matrix | O(V^2) | O(1) | O(1) | O(V) |
| Edge List | O(E) | O(1) | O(E) | O(E) |

**Go Implementation Notes:**

```go
// Adjacency list (most common for interviews)
graph := make(map[int][]int)  // node -> list of neighbors

// Add undirected edge
graph[u] = append(graph[u], v)
graph[v] = append(graph[v], u)

// Add directed edge
graph[u] = append(graph[u], v)

// Adjacency matrix (when V is small and edge queries are frequent)
n := 5
matrix := make([][]bool, n)
for i := range matrix {
    matrix[i] = make([]bool, n)
}
matrix[u][v] = true  // directed edge from u to v

// Weighted adjacency list
type Edge struct {
    To, Weight int
}
graph := make(map[int][]Edge)
graph[u] = append(graph[u], Edge{To: v, Weight: w})
```

**Key Algorithms (brief, see Searches syllabus for details):**
- **DFS:** Explore as deep as possible, then backtrack. Use for connectivity, cycle detection, topological sort.
- **BFS:** Explore level by level. Use for shortest path (unweighted).
- **Topological Sort:** Order nodes so all edges go forward (DAGs only). Use Kahn's algorithm (BFS with in-degrees) or DFS-based.
- **Dijkstra's:** Shortest path in weighted graphs (non-negative weights). Uses a priority queue.

**Common Pitfalls:**
- Forgetting to track visited nodes leads to infinite loops in cyclic graphs
- Confusing directed vs undirected when building adjacency lists
- Off-by-one with 0-indexed vs 1-indexed nodes

> **Key Insight:** Most graph problems boil down to: (1) build the graph from the input, (2) run DFS or BFS with appropriate state tracking. Identify what the "nodes" and "edges" represent -- sometimes they're not obvious.

**NeetCode Relevance:** Graphs (number of islands, clone graph, course schedule, Pacific Atlantic water flow).

---

### 12. Union-Find (Disjoint Set)

**Difficulty:** Intermediate-Advanced

**Concept:**
Union-Find maintains a collection of disjoint sets and supports two operations efficiently: **Find** (which set does element X belong to?) and **Union** (merge two sets). With path compression and union by rank, both operations run in nearly O(1) amortized -- technically O(alpha(n)) where alpha is the inverse Ackermann function.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Find | O(alpha(n)) ~ O(1) | With path compression |
| Union | O(alpha(n)) ~ O(1) | With union by rank |
| Connected? | O(alpha(n)) ~ O(1) | Same root = same set |
| Count components | O(1) | Track during unions |

**Go Implementation Notes:**

```go
type UnionFind struct {
    parent []int
    rank   []int
    count  int  // number of components
}

func NewUnionFind(n int) *UnionFind {
    parent := make([]int, n)
    rank := make([]int, n)
    for i := 0; i < n; i++ {
        parent[i] = i  // each element is its own root
    }
    return &UnionFind{parent: parent, rank: rank, count: n}
}

func (uf *UnionFind) Find(x int) int {
    if uf.parent[x] != x {
        uf.parent[x] = uf.Find(uf.parent[x])  // path compression
    }
    return uf.parent[x]
}

func (uf *UnionFind) Union(x, y int) bool {
    rootX, rootY := uf.Find(x), uf.Find(y)
    if rootX == rootY {
        return false  // already in same set
    }
    // union by rank
    if uf.rank[rootX] < uf.rank[rootY] {
        uf.parent[rootX] = rootY
    } else if uf.rank[rootX] > uf.rank[rootY] {
        uf.parent[rootY] = rootX
    } else {
        uf.parent[rootY] = rootX
        uf.rank[rootX]++
    }
    uf.count--
    return true
}

func (uf *UnionFind) Connected(x, y int) bool {
    return uf.Find(x) == uf.Find(y)
}
```

**When to Use Union-Find vs BFS/DFS:**
- **Union-Find:** Dynamic connectivity queries, incremental edge additions, redundant connection detection
- **BFS/DFS:** Static graph traversal, shortest path, exploring all reachable nodes

**Common Pitfalls:**
- Forgetting path compression (makes Find slow)
- Forgetting union by rank (makes trees unbalanced)
- Not mapping 2D grid coordinates to 1D indices (`i * cols + j`)

> **Key Insight:** Union-Find is the go-to when the problem asks about connected components that grow over time. Keywords: "connected", "group", "redundant connection", "number of islands" (alternative to DFS).

**NeetCode Relevance:** Graphs (redundant connection, accounts merge, number of connected components).

---

## Advanced Structures

---

### 13. Monotonic Stack / Monotonic Queue

**Difficulty:** Intermediate-Advanced

**Concept:**
A monotonic stack maintains elements in strictly increasing or decreasing order. When a new element violates the order, elements are popped until the invariant is restored. A monotonic deque extends this to support operations at both ends (used for sliding window max/min).

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Push | O(1) amortized | May pop multiple elements |
| Pop | O(1) | Standard pop |
| Overall for n elements | O(n) | Each element pushed/popped at most once |

**Go Implementation Notes:**

```go
// Monotonic decreasing stack: next greater element to the right
func nextGreater(nums []int) []int {
    n := len(nums)
    ans := make([]int, n)
    for i := range ans { ans[i] = -1 }
    stack := []int{}  // indices

    for i := 0; i < n; i++ {
        for len(stack) > 0 && nums[i] > nums[stack[len(stack)-1]] {
            top := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            ans[top] = nums[i]
        }
        stack = append(stack, i)
    }
    return ans
}

// Monotonic deque: sliding window maximum
func maxSlidingWindow(nums []int, k int) []int {
    deque := []int{}  // indices, front has the max
    result := []int{}

    for i := 0; i < len(nums); i++ {
        // Remove indices outside window
        if len(deque) > 0 && deque[0] <= i-k {
            deque = deque[1:]
        }
        // Remove smaller elements from back
        for len(deque) > 0 && nums[deque[len(deque)-1]] <= nums[i] {
            deque = deque[:len(deque)-1]
        }
        deque = append(deque, i)
        if i >= k-1 {
            result = append(result, nums[deque[0]])
        }
    }
    return result
}
```

> **Key Insight:** Each element enters and leaves the stack/deque exactly once, giving O(n) total time. Use monotonic structures when you need to efficiently find the next/previous greater/smaller element.

**NeetCode Relevance:** Stack (daily temperatures, largest rectangle in histogram), Sliding Window (sliding window maximum).

---

### 14. Segment Trees / Binary Indexed Trees (Fenwick)

**Difficulty:** Advanced

**Concept:**
These are specialized tree structures for answering range queries (sum, min, max) and point updates efficiently on an array.

| Structure | Build | Point Update | Range Query | Space |
|-----------|-------|-------------|-------------|-------|
| Prefix Sum Array | O(n) | O(n) | O(1) | O(n) |
| Segment Tree | O(n) | O(log n) | O(log n) | O(4n) |
| Fenwick Tree (BIT) | O(n log n) | O(log n) | O(log n) | O(n) |

**When to Use:**
- **Prefix sums:** Static array, range sum queries only, no updates
- **Segment tree:** Dynamic updates + range queries, supports min/max/sum/GCD
- **Fenwick tree:** Dynamic updates + prefix sum queries (simpler to implement than segment tree)

**Go Pseudocode (Fenwick Tree):**

```go
type BIT struct {
    tree []int
    n    int
}

func NewBIT(n int) *BIT {
    return &BIT{tree: make([]int, n+1), n: n}
}

func (b *BIT) Update(i, delta int) {
    for ; i <= b.n; i += i & (-i) {
        b.tree[i] += delta
    }
}

func (b *BIT) Query(i int) int {  // prefix sum [1..i]
    sum := 0
    for ; i > 0; i -= i & (-i) {
        sum += b.tree[i]
    }
    return sum
}

func (b *BIT) RangeQuery(l, r int) int {
    return b.Query(r) - b.Query(l-1)
}
```

> **Key Insight:** For most NeetCode 150 problems, prefix sums suffice. Segment trees and Fenwick trees appear in hard contest problems. Know they exist and their complexity -- you can learn implementation details when needed.

**NeetCode Relevance:** Rarely needed for NeetCode 150, but useful for advanced problems and contests.

---

### 15. LRU Cache

**Difficulty:** Intermediate

**Concept:**
An LRU (Least Recently Used) Cache evicts the least recently accessed item when full. It combines a hash map (O(1) key lookup) with a doubly linked list (O(1) move-to-front and remove-from-end).

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Get | O(1) | Lookup + move to front |
| Put | O(1) | Insert/update + possibly evict |

**Go Implementation Notes:**

```go
type LRUNode struct {
    Key, Val   int
    Prev, Next *LRUNode
}

type LRUCache struct {
    cap        int
    cache      map[int]*LRUNode
    head, tail *LRUNode  // dummy nodes
}

func NewLRUCache(capacity int) *LRUCache {
    head := &LRUNode{}
    tail := &LRUNode{}
    head.Next = tail
    tail.Prev = head
    return &LRUCache{
        cap:   capacity,
        cache: make(map[int]*LRUNode),
        head:  head,
        tail:  tail,
    }
}

// Remove node from its current position
func (l *LRUCache) remove(node *LRUNode) {
    node.Prev.Next = node.Next
    node.Next.Prev = node.Prev
}

// Insert node right after head (most recent)
func (l *LRUCache) insertFront(node *LRUNode) {
    node.Next = l.head.Next
    node.Prev = l.head
    l.head.Next.Prev = node
    l.head.Next = node
}

func (l *LRUCache) Get(key int) int {
    if node, ok := l.cache[key]; ok {
        l.remove(node)
        l.insertFront(node)
        return node.Val
    }
    return -1
}

func (l *LRUCache) Put(key, value int) {
    if node, ok := l.cache[key]; ok {
        l.remove(node)
        node.Val = value
        l.insertFront(node)
        return
    }
    node := &LRUNode{Key: key, Val: value}
    l.cache[key] = node
    l.insertFront(node)
    if len(l.cache) > l.cap {
        lru := l.tail.Prev
        l.remove(lru)
        delete(l.cache, lru.Key)
    }
}
```

**Common Pitfalls:**
- Forgetting to store the key in the node (needed for eviction to delete from the map)
- Not using dummy head/tail nodes (leads to nil-check edge cases)
- Forgetting to update the map on put when key already exists

> **Key Insight:** LRU Cache = HashMap + Doubly Linked List. The map gives O(1) access, the list gives O(1) ordering. This is a classic design question.

**NeetCode Relevance:** Linked List (LRU Cache design problem).

---

## Progress Checklist

Use this to track which data structures you've studied and implemented:

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

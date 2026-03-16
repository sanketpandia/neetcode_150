# Searching Algorithms Syllabus

A comprehensive reference for every searching algorithm and search-based technique you need for coding interviews and the NeetCode 150. Each section covers the concept, time/space complexity, when to use it, Go pseudocode, common pitfalls, and NeetCode relevance.

---

## Table of Contents

1. [Linear Search](#1-linear-search)
2. [Binary Search](#2-binary-search)
3. [Two Pointers](#3-two-pointers)
4. [Sliding Window](#4-sliding-window)
5. [Depth-First Search (DFS)](#5-depth-first-search-dfs)
6. [Breadth-First Search (BFS)](#6-breadth-first-search-bfs)
7. [Interpolation Search](#7-interpolation-search)
8. [Exponential Search](#8-exponential-search)

---

## Complexity Overview

| Algorithm | Best | Average | Worst | Space | Prerequisite |
|-----------|------|---------|-------|-------|-------------|
| Linear Search | O(1) | O(n) | O(n) | O(1) | None |
| Binary Search | O(1) | O(log n) | O(log n) | O(1) | Sorted data |
| Two Pointers | O(n) | O(n) | O(n) | O(1) | Usually sorted |
| Sliding Window | O(n) | O(n) | O(n) | O(1)-O(k) | Sequential data |
| DFS | O(1) | O(V+E) | O(V+E) | O(V) | Graph/tree |
| BFS | O(1) | O(V+E) | O(V+E) | O(V) | Graph/tree |
| Interpolation Search | O(1) | O(log log n) | O(n) | O(1) | Sorted + uniform |
| Exponential Search | O(1) | O(log n) | O(log n) | O(1) | Sorted data |

---

## 1. Linear Search

**Difficulty:** Beginner

**Concept:**
The simplest search: scan every element one by one until you find the target or exhaust the collection. Works on any data -- sorted or unsorted.

**Time Complexity:** O(n)
**Space Complexity:** O(1)

**Go Implementation:**

```go
func linearSearch(arr []int, target int) int {
    for i, v := range arr {
        if v == target {
            return i
        }
    }
    return -1 // not found
}
```

**When to Use:**
- Data is unsorted and you can't sort it (or sorting is overkill)
- Collection is very small
- You need to find ALL occurrences (not just the first)

**Common Pitfalls:**
- Using linear search when the data is sorted (use binary search instead)
- Forgetting to handle the "not found" case

> **Key Insight:** Linear search is your baseline. Every other search algorithm is an optimization over it for specific conditions. If O(n) is too slow and the data is sorted, reach for binary search.

**NeetCode Relevance:** Foundational -- many problems start with a brute-force linear scan that you then optimize.

---

## 2. Binary Search

**Difficulty:** Beginner-Intermediate

**Concept:**
Binary search works on sorted data by repeatedly halving the search space. Compare the target with the middle element: if it matches, you're done; if the target is smaller, search the left half; if larger, search the right half.

**Time Complexity:** O(log n)
**Space Complexity:** O(1) iterative, O(log n) recursive

### 2.1 Standard Binary Search

```go
func binarySearch(arr []int, target int) int {
    lo, hi := 0, len(arr)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2 // avoids integer overflow
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return -1
}
```

### 2.2 Lower Bound (First Occurrence)

Find the first position where `arr[i] >= target`:

```go
func lowerBound(arr []int, target int) int {
    lo, hi := 0, len(arr)
    for lo < hi {
        mid := lo + (hi-lo)/2
        if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid
        }
    }
    return lo // first index where arr[i] >= target
}
```

### 2.3 Upper Bound (After Last Occurrence)

Find the first position where `arr[i] > target`:

```go
func upperBound(arr []int, target int) int {
    lo, hi := 0, len(arr)
    for lo < hi {
        mid := lo + (hi-lo)/2
        if arr[mid] <= target {
            lo = mid + 1
        } else {
            hi = mid
        }
    }
    return lo // first index where arr[i] > target
}
```

### 2.4 Search in Rotated Sorted Array

A sorted array rotated at some pivot (e.g., `[4,5,6,7,0,1,2]`). One half is always sorted:

```go
func searchRotated(nums []int, target int) int {
    lo, hi := 0, len(nums)-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        if nums[mid] == target {
            return mid
        }
        // Left half is sorted
        if nums[lo] <= nums[mid] {
            if nums[lo] <= target && target < nums[mid] {
                hi = mid - 1
            } else {
                lo = mid + 1
            }
        } else {
            // Right half is sorted
            if nums[mid] < target && target <= nums[hi] {
                lo = mid + 1
            } else {
                hi = mid - 1
            }
        }
    }
    return -1
}
```

### 2.5 Search a 2D Matrix

Treat the 2D matrix as a flat sorted array:

```go
func searchMatrix(matrix [][]int, target int) bool {
    rows, cols := len(matrix), len(matrix[0])
    lo, hi := 0, rows*cols-1
    for lo <= hi {
        mid := lo + (hi-lo)/2
        val := matrix[mid/cols][mid%cols]
        if val == target {
            return true
        } else if val < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return false
}
```

### 2.6 Binary Search on Answer

Sometimes you don't search an array -- you binary search on the answer space. Example: "What is the minimum capacity to ship packages in D days?"

```go
func shipWithinDays(weights []int, days int) int {
    lo, hi := maxElement(weights), sum(weights)
    for lo < hi {
        mid := lo + (hi-lo)/2
        if canShip(weights, days, mid) {
            hi = mid
        } else {
            lo = mid + 1
        }
    }
    return lo
}
```

**Go Standard Library:**

```go
import "sort"

// sort.Search returns the smallest index i in [0, n) where f(i) is true
// This is equivalent to lower bound
idx := sort.Search(len(arr), func(i int) bool {
    return arr[i] >= target
})

// sort.SearchInts is a convenience wrapper
idx := sort.SearchInts(arr, target)
```

**Common Pitfalls:**
- Off-by-one: `lo <= hi` vs `lo < hi` -- depends on whether `hi` is inclusive or exclusive
- Integer overflow: use `lo + (hi-lo)/2` instead of `(lo+hi)/2`
- Not handling duplicates: standard binary search finds *any* occurrence, not first/last
- Infinite loops: make sure `lo` or `hi` changes every iteration

> **Key Insight:** Binary search applies whenever you have a monotonic condition (something that is false for a range then true for the rest, or vice versa). The data doesn't have to be a literal sorted array -- you can binary search on the answer.

**NeetCode Relevance:** Binary Search category (search in rotated array, find minimum in rotated array, search 2D matrix, Koko eating bananas, time-based key-value store).

---

## 3. Two Pointers

**Difficulty:** Beginner-Intermediate

**Concept:**
Use two pointers (indices) to traverse the data structure, typically from both ends moving inward, or both from the start at different speeds. This eliminates the need for nested loops, reducing O(n^2) to O(n).

**Time Complexity:** O(n)
**Space Complexity:** O(1)

### 3.1 Opposite-Direction Pointers

Start one pointer at the beginning and one at the end. Move them toward each other.

```go
// Two Sum II (sorted array)
func twoSumSorted(numbers []int, target int) []int {
    lo, hi := 0, len(numbers)-1
    for lo < hi {
        sum := numbers[lo] + numbers[hi]
        if sum == target {
            return []int{lo, hi}
        } else if sum < target {
            lo++
        } else {
            hi--
        }
    }
    return []int{} // no solution
}

// Valid Palindrome
func isPalindrome(s string) bool {
    lo, hi := 0, len(s)-1
    for lo < hi {
        if s[lo] != s[hi] {
            return false
        }
        lo++
        hi--
    }
    return true
}

// Container With Most Water
func maxArea(height []int) int {
    lo, hi := 0, len(height)-1
    best := 0
    for lo < hi {
        w := hi - lo
        h := min(height[lo], height[hi])
        area := w * h
        if area > best {
            best = area
        }
        if height[lo] < height[hi] {
            lo++
        } else {
            hi--
        }
    }
    return best
}
```

### 3.2 Same-Direction Pointers (Fast / Slow)

Both pointers start at the beginning. The fast pointer moves ahead; the slow pointer tracks a condition.

```go
// Remove duplicates in-place from sorted array
func removeDuplicates(nums []int) int {
    if len(nums) == 0 {
        return 0
    }
    slow := 0
    for fast := 1; fast < len(nums); fast++ {
        if nums[fast] != nums[slow] {
            slow++
            nums[slow] = nums[fast]
        }
    }
    return slow + 1
}

// Linked list cycle detection (Floyd's algorithm)
func hasCycle(head *ListNode) bool {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
        if slow == fast {
            return true
        }
    }
    return false
}
```

### 3.3 Three Pointers (3Sum Pattern)

Fix one pointer, then use two pointers on the remainder:

```go
func threeSum(nums []int) [][]int {
    sort.Ints(nums)
    result := [][]int{}
    for i := 0; i < len(nums)-2; i++ {
        if i > 0 && nums[i] == nums[i-1] {
            continue // skip duplicates
        }
        lo, hi := i+1, len(nums)-1
        for lo < hi {
            sum := nums[i] + nums[lo] + nums[hi]
            if sum == 0 {
                result = append(result, []int{nums[i], nums[lo], nums[hi]})
                for lo < hi && nums[lo] == nums[lo+1] { lo++ }
                for lo < hi && nums[hi] == nums[hi-1] { hi-- }
                lo++
                hi--
            } else if sum < 0 {
                lo++
            } else {
                hi--
            }
        }
    }
    return result
}
```

**When to Use Two Pointers:**
- Array is sorted (or can be sorted)
- Looking for pairs/triplets that satisfy a condition
- Need to process from both ends (palindrome, container with water)
- Remove duplicates in-place
- Linked list cycle detection / finding middle

**Common Pitfalls:**
- Forgetting to sort the array first
- Not handling duplicate elements (infinite loops or duplicate results)
- Off-by-one when skipping duplicates

> **Key Insight:** Two pointers work when moving a pointer in one direction gives you useful information about whether to move the other. If the problem involves a sorted array and pairs, two pointers is almost always the approach.

**NeetCode Relevance:** Two Pointers (valid palindrome, two sum II, 3Sum, container with most water, trapping rain water).

---

## 4. Sliding Window

**Difficulty:** Intermediate

**Concept:**
A sliding window maintains a "window" (contiguous subarray/substring) that expands or contracts as you iterate. Instead of recalculating from scratch for each position, you update incrementally -- adding the new element and removing the old one.

**Time Complexity:** O(n)
**Space Complexity:** O(1) to O(k) depending on auxiliary data structures

### 4.1 Fixed-Size Window

Window size is known in advance. Slide it across the array.

```go
// Maximum sum of subarray of size k
func maxSumSubarray(arr []int, k int) int {
    // Build first window
    windowSum := 0
    for i := 0; i < k; i++ {
        windowSum += arr[i]
    }
    best := windowSum

    // Slide the window
    for i := k; i < len(arr); i++ {
        windowSum += arr[i] - arr[i-k]  // add new, remove old
        if windowSum > best {
            best = windowSum
        }
    }
    return best
}
```

### 4.2 Variable-Size Window

Window expands until a condition is violated, then contracts from the left.

```go
// Longest substring without repeating characters
func lengthOfLongestSubstring(s string) int {
    charIndex := make(map[byte]int) // last seen index
    best := 0
    left := 0

    for right := 0; right < len(s); right++ {
        if idx, ok := charIndex[s[right]]; ok && idx >= left {
            left = idx + 1 // shrink window past the duplicate
        }
        charIndex[s[right]] = right
        if right-left+1 > best {
            best = right - left + 1
        }
    }
    return best
}

// Minimum window substring
func minWindow(s string, t string) string {
    need := make(map[byte]int)
    for i := 0; i < len(t); i++ {
        need[t[i]]++
    }

    have := make(map[byte]int)
    formed := 0
    required := len(need)
    bestLen := len(s) + 1
    bestStart := 0
    left := 0

    for right := 0; right < len(s); right++ {
        ch := s[right]
        have[ch]++
        if have[ch] == need[ch] {
            formed++
        }

        // Contract window from left
        for formed == required {
            // Update best
            if right-left+1 < bestLen {
                bestLen = right - left + 1
                bestStart = left
            }
            leftCh := s[left]
            have[leftCh]--
            if have[leftCh] < need[leftCh] {
                formed--
            }
            left++
        }
    }

    if bestLen > len(s) {
        return ""
    }
    return s[bestStart : bestStart+bestLen]
}
```

### 4.3 Sliding Window Template

Most variable-size window problems follow this template:

```go
func slidingWindow(arr []int) int {
    left := 0
    // state variables (sum, count map, etc.)

    for right := 0; right < len(arr); right++ {
        // Expand: add arr[right] to window state

        for /* window is invalid */ {
            // Contract: remove arr[left] from window state
            left++
        }

        // Update answer (depends on whether you want max or min window)
    }
    return answer
}
```

**When to Use Sliding Window:**
- Contiguous subarray or substring problems
- "Longest/shortest subarray with condition X"
- "Number of subarrays satisfying condition X"
- The condition can be checked/maintained incrementally

**Common Pitfalls:**
- Confusing when to expand vs contract
- Not handling the window state correctly when removing elements
- Using sliding window when the subarray isn't contiguous (use DP instead)

> **Key Insight:** Sliding window is an optimization of the brute-force "check every subarray" approach. If you can express the problem as "find the best contiguous subarray where some condition holds", sliding window likely applies.

**NeetCode Relevance:** Sliding Window (longest substring without repeating, longest repeating character replacement, minimum window substring, permutation in string).

---

## 5. Depth-First Search (DFS)

**Difficulty:** Intermediate

**Concept:**
DFS explores as deep as possible along each branch before backtracking. It uses a stack (either the call stack via recursion, or an explicit stack). DFS is the foundation for tree traversals, graph exploration, and backtracking.

**Time Complexity:** O(V + E) for graphs, O(n) for trees
**Space Complexity:** O(V) worst case (recursion depth or explicit stack)

### 5.1 DFS on Trees (Recursive)

```go
// Preorder traversal
func preorder(root *TreeNode) []int {
    if root == nil {
        return nil
    }
    result := []int{root.Val}
    result = append(result, preorder(root.Left)...)
    result = append(result, preorder(root.Right)...)
    return result
}

// Max depth of binary tree
func maxDepth(root *TreeNode) int {
    if root == nil {
        return 0
    }
    left := maxDepth(root.Left)
    right := maxDepth(root.Right)
    if left > right {
        return left + 1
    }
    return right + 1
}
```

### 5.2 DFS on Trees (Iterative with Stack)

```go
func preorderIterative(root *TreeNode) []int {
    if root == nil {
        return nil
    }
    result := []int{}
    stack := []*TreeNode{root}

    for len(stack) > 0 {
        node := stack[len(stack)-1]
        stack = stack[:len(stack)-1]
        result = append(result, node.Val)
        // Push right first so left is processed first (LIFO)
        if node.Right != nil {
            stack = append(stack, node.Right)
        }
        if node.Left != nil {
            stack = append(stack, node.Left)
        }
    }
    return result
}
```

### 5.3 DFS on Graphs

```go
// DFS traversal of a graph (adjacency list)
func dfsGraph(graph map[int][]int, start int) []int {
    visited := make(map[int]bool)
    result := []int{}

    var dfs func(node int)
    dfs = func(node int) {
        visited[node] = true
        result = append(result, node)
        for _, neighbor := range graph[node] {
            if !visited[neighbor] {
                dfs(neighbor)
            }
        }
    }

    dfs(start)
    return result
}

// Number of islands (2D grid DFS)
func numIslands(grid [][]byte) int {
    rows, cols := len(grid), len(grid[0])
    count := 0

    var dfs func(r, c int)
    dfs = func(r, c int) {
        if r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] == '0' {
            return
        }
        grid[r][c] = '0' // mark visited
        dfs(r+1, c)
        dfs(r-1, c)
        dfs(r, c+1)
        dfs(r, c-1)
    }

    for r := 0; r < rows; r++ {
        for c := 0; c < cols; c++ {
            if grid[r][c] == '1' {
                count++
                dfs(r, c)
            }
        }
    }
    return count
}
```

### 5.4 Backtracking (DFS with Undo)

Backtracking builds solutions incrementally, abandoning a path as soon as it's invalid.

```go
// Generate all subsets
func subsets(nums []int) [][]int {
    result := [][]int{}
    current := []int{}

    var backtrack func(start int)
    backtrack = func(start int) {
        // Make a copy and add to result
        temp := make([]int, len(current))
        copy(temp, current)
        result = append(result, temp)

        for i := start; i < len(nums); i++ {
            current = append(current, nums[i])   // choose
            backtrack(i + 1)                       // explore
            current = current[:len(current)-1]     // undo
        }
    }

    backtrack(0)
    return result
}

// Permutations
func permute(nums []int) [][]int {
    result := [][]int{}

    var backtrack func(start int)
    backtrack = func(start int) {
        if start == len(nums) {
            temp := make([]int, len(nums))
            copy(temp, nums)
            result = append(result, temp)
            return
        }
        for i := start; i < len(nums); i++ {
            nums[start], nums[i] = nums[i], nums[start]   // swap
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]   // undo swap
        }
    }

    backtrack(0)
    return result
}
```

### 5.5 Topological Sort (DFS-based)

Order nodes in a DAG so all edges point forward:

```go
func topologicalSort(numNodes int, edges [][]int) []int {
    graph := make(map[int][]int)
    for _, e := range edges {
        graph[e[0]] = append(graph[e[0]], e[1])
    }

    visited := make(map[int]int) // 0=unvisited, 1=in-progress, 2=done
    result := []int{}
    hasCycle := false

    var dfs func(node int)
    dfs = func(node int) {
        if hasCycle {
            return
        }
        visited[node] = 1 // in-progress
        for _, neighbor := range graph[node] {
            if visited[neighbor] == 1 {
                hasCycle = true // back edge = cycle
                return
            }
            if visited[neighbor] == 0 {
                dfs(neighbor)
            }
        }
        visited[node] = 2 // done
        result = append(result, node)
    }

    for i := 0; i < numNodes; i++ {
        if visited[i] == 0 {
            dfs(i)
        }
    }

    // Reverse result for topological order
    for i, j := 0, len(result)-1; i < j; i, j = i+1, j-1 {
        result[i], result[j] = result[j], result[i]
    }
    return result
}
```

**When to Use DFS:**
- Tree traversals (pre/in/post order)
- Graph connectivity and cycle detection
- Finding all paths, all solutions (backtracking)
- Topological sorting
- Problems where you need to explore exhaustively

**Common Pitfalls:**
- Forgetting visited tracking in graphs (infinite loops on cycles)
- Stack overflow on very deep recursion (consider iterative DFS)
- Not making a copy of the current state before adding to results (backtracking)

> **Key Insight:** DFS is your Swiss Army knife for exploration. Think recursively: "If I solve this for my children, can I combine to solve for myself?" For graphs, always track visited nodes.

**NeetCode Relevance:** Trees (nearly all), Graphs (islands, clone graph, course schedule), Backtracking (subsets, permutations, combination sum, word search).

---

## 6. Breadth-First Search (BFS)

**Difficulty:** Intermediate

**Concept:**
BFS explores all neighbors at the current depth before moving deeper. It uses a queue and naturally finds the shortest path in unweighted graphs.

**Time Complexity:** O(V + E) for graphs, O(n) for trees
**Space Complexity:** O(V) (the queue can hold an entire level)

### 6.1 BFS on Trees (Level-Order Traversal)

```go
func levelOrder(root *TreeNode) [][]int {
    if root == nil {
        return nil
    }
    result := [][]int{}
    queue := []*TreeNode{root}

    for len(queue) > 0 {
        levelSize := len(queue)
        level := []int{}
        for i := 0; i < levelSize; i++ {
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

### 6.2 BFS on Graphs (Shortest Path)

```go
// Shortest path in unweighted graph
func shortestPath(graph map[int][]int, start, end int) int {
    visited := make(map[int]bool)
    queue := []int{start}
    visited[start] = true
    distance := 0

    for len(queue) > 0 {
        size := len(queue)
        for i := 0; i < size; i++ {
            node := queue[0]
            queue = queue[1:]
            if node == end {
                return distance
            }
            for _, neighbor := range graph[node] {
                if !visited[neighbor] {
                    visited[neighbor] = true
                    queue = append(queue, neighbor)
                }
            }
        }
        distance++
    }
    return -1 // unreachable
}
```

### 6.3 BFS on Grid

```go
// Shortest path in a binary matrix (0 = passable, 1 = blocked)
func shortestPathGrid(grid [][]int) int {
    n := len(grid)
    if grid[0][0] == 1 || grid[n-1][n-1] == 1 {
        return -1
    }

    dirs := [][2]int{{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}}
    queue := [][2]int{{0, 0}}
    grid[0][0] = 1 // mark visited
    dist := 1

    for len(queue) > 0 {
        size := len(queue)
        for i := 0; i < size; i++ {
            cell := queue[0]
            queue = queue[1:]
            if cell[0] == n-1 && cell[1] == n-1 {
                return dist
            }
            for _, d := range dirs {
                nr, nc := cell[0]+d[0], cell[1]+d[1]
                if nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0 {
                    grid[nr][nc] = 1
                    queue = append(queue, [2]int{nr, nc})
                }
            }
        }
        dist++
    }
    return -1
}
```

### 6.4 Multi-Source BFS

Start BFS from multiple sources simultaneously (e.g., "rotting oranges"):

```go
func orangesRotting(grid [][]int) int {
    rows, cols := len(grid), len(grid[0])
    queue := [][2]int{}
    fresh := 0

    // Enqueue all rotten oranges (multiple sources)
    for r := 0; r < rows; r++ {
        for c := 0; c < cols; c++ {
            if grid[r][c] == 2 {
                queue = append(queue, [2]int{r, c})
            } else if grid[r][c] == 1 {
                fresh++
            }
        }
    }

    dirs := [][2]int{{0,1},{0,-1},{1,0},{-1,0}}
    minutes := 0

    for len(queue) > 0 && fresh > 0 {
        size := len(queue)
        for i := 0; i < size; i++ {
            cell := queue[0]
            queue = queue[1:]
            for _, d := range dirs {
                nr, nc := cell[0]+d[0], cell[1]+d[1]
                if nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1 {
                    grid[nr][nc] = 2
                    fresh--
                    queue = append(queue, [2]int{nr, nc})
                }
            }
        }
        minutes++
    }

    if fresh > 0 {
        return -1
    }
    return minutes
}
```

### 6.5 Kahn's Algorithm (BFS Topological Sort)

```go
func topologicalSortBFS(numCourses int, prerequisites [][]int) []int {
    graph := make(map[int][]int)
    inDegree := make([]int, numCourses)

    for _, pre := range prerequisites {
        graph[pre[1]] = append(graph[pre[1]], pre[0])
        inDegree[pre[0]]++
    }

    // Start with nodes that have no prerequisites
    queue := []int{}
    for i := 0; i < numCourses; i++ {
        if inDegree[i] == 0 {
            queue = append(queue, i)
        }
    }

    order := []int{}
    for len(queue) > 0 {
        node := queue[0]
        queue = queue[1:]
        order = append(order, node)
        for _, neighbor := range graph[node] {
            inDegree[neighbor]--
            if inDegree[neighbor] == 0 {
                queue = append(queue, neighbor)
            }
        }
    }

    if len(order) != numCourses {
        return nil // cycle exists
    }
    return order
}
```

**When to Use BFS vs DFS:**

| Use BFS When | Use DFS When |
|-------------|-------------|
| Shortest path (unweighted) | Exploring all paths |
| Level-by-level processing | Tree traversals (pre/in/post) |
| Nearest neighbor / closest | Cycle detection |
| Multi-source spreading | Backtracking / exhaustive search |
| Topological sort (Kahn's) | Topological sort (recursive) |

**Common Pitfalls:**
- Forgetting to mark nodes as visited BEFORE enqueueing (leads to duplicate entries)
- Not processing by levels (forgetting the inner `size` loop)
- Using BFS when DFS would be simpler (e.g., tree traversals)

> **Key Insight:** BFS guarantees shortest path in unweighted graphs because it explores in order of distance. If you need "minimum steps", "shortest path", or "nearest X", think BFS.

**NeetCode Relevance:** Trees (level-order, right side view), Graphs (rotting oranges, course schedule, word ladder, Pacific Atlantic water flow).

---

## 7. Interpolation Search

**Difficulty:** Advanced

**Concept:**
An optimization of binary search for uniformly distributed sorted data. Instead of always picking the middle, it estimates the target's position using linear interpolation -- like how you'd search a phone book (skipping to roughly the right letter).

**Time Complexity:** O(log log n) average for uniform data, O(n) worst case
**Space Complexity:** O(1)

**Formula:**
```
pos = lo + ((target - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo])
```

**Go Implementation:**

```go
func interpolationSearch(arr []int, target int) int {
    lo, hi := 0, len(arr)-1

    for lo <= hi && target >= arr[lo] && target <= arr[hi] {
        if lo == hi {
            if arr[lo] == target {
                return lo
            }
            return -1
        }

        // Estimate position
        pos := lo + ((target - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo])

        if arr[pos] == target {
            return pos
        } else if arr[pos] < target {
            lo = pos + 1
        } else {
            hi = pos - 1
        }
    }
    return -1
}
```

**When to Use:**
- Data is sorted AND uniformly distributed (e.g., IDs, timestamps with regular intervals)
- Very large datasets where O(log log n) vs O(log n) matters

**When NOT to Use:**
- Data is not uniformly distributed (degrades to O(n))
- Small datasets (overhead isn't worth it)

**Common Pitfalls:**
- Division by zero when `arr[hi] == arr[lo]`
- Integer overflow in the position calculation
- Forgetting the bounds check `target >= arr[lo] && target <= arr[hi]`

> **Key Insight:** Interpolation search is a niche optimization. In interviews, binary search is almost always sufficient. Know that interpolation search exists and when it theoretically helps, but don't reach for it by default.

**NeetCode Relevance:** Rarely needed directly, but good to know for system design discussions about searching large datasets.

---

## 8. Exponential Search

**Difficulty:** Advanced

**Concept:**
Exponential search finds the range where the target might exist by doubling the index (1, 2, 4, 8, 16, ...) until it overshoots, then runs binary search within that range. It's useful for unbounded or very large sorted data.

**Time Complexity:** O(log n)
**Space Complexity:** O(1)

**Go Implementation:**

```go
func exponentialSearch(arr []int, target int) int {
    n := len(arr)
    if n == 0 {
        return -1
    }
    if arr[0] == target {
        return 0
    }

    // Find range [bound/2, bound]
    bound := 1
    for bound < n && arr[bound] <= target {
        bound *= 2
    }

    // Binary search within the range
    lo := bound / 2
    hi := bound
    if hi >= n {
        hi = n - 1
    }
    return binarySearch(arr, target, lo, hi)
}

func binarySearch(arr []int, target, lo, hi int) int {
    for lo <= hi {
        mid := lo + (hi-lo)/2
        if arr[mid] == target {
            return mid
        } else if arr[mid] < target {
            lo = mid + 1
        } else {
            hi = mid - 1
        }
    }
    return -1
}
```

**When to Use:**
- Sorted data with unknown or very large size
- Searching in unbounded/infinite lists
- When the target is likely near the beginning

**Common Pitfalls:**
- Out-of-bounds access when `bound` exceeds array length
- Not handling the edge case where `arr[0]` is the target

> **Key Insight:** Exponential search is binary search with an adaptive range-finding step. It's O(log i) where i is the target's position, making it faster than standard binary search when the target is near the start of a very large dataset.

**NeetCode Relevance:** Rarely needed directly, but the concept of "doubling to find range" appears in problems like "search in sorted array of unknown size."

---

## Progress Checklist

- [ ] Linear Search
- [ ] Binary Search (standard)
- [ ] Binary Search (lower/upper bound)
- [ ] Binary Search (rotated sorted array)
- [ ] Binary Search (2D matrix)
- [ ] Binary Search on Answer
- [ ] Two Pointers (opposite direction)
- [ ] Two Pointers (fast/slow)
- [ ] Two Pointers (3Sum pattern)
- [ ] Sliding Window (fixed size)
- [ ] Sliding Window (variable size)
- [ ] DFS on Trees (recursive)
- [ ] DFS on Trees (iterative)
- [ ] DFS on Graphs
- [ ] Backtracking
- [ ] Topological Sort (DFS)
- [ ] BFS on Trees (level-order)
- [ ] BFS on Graphs (shortest path)
- [ ] BFS on Grid
- [ ] Multi-Source BFS
- [ ] Topological Sort (BFS / Kahn's)
- [ ] Interpolation Search
- [ ] Exponential Search

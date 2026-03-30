# Searching Algorithms Syllabus

A comprehensive reference for every searching algorithm and search-based technique you need for coding interviews and the NeetCode 150. Each section covers the concept, time/space complexity, when to use it, Java implementation, common pitfalls, and NeetCode relevance.

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

**Java Implementation:**

```java
int linearSearch(int[] arr, int target) {
    for (int i = 0; i < arr.length; i++) {
        if (arr[i] == target) return i;
    }
    return -1; // not found
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

```java
int binarySearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2; // avoids integer overflow
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

### 2.2 Lower Bound (First Occurrence)

Find the first position where `arr[i] >= target`:

```java
int lowerBound(int[] arr, int target) {
    int lo = 0, hi = arr.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] < target) lo = mid + 1;
        else hi = mid;
    }
    return lo; // first index where arr[i] >= target
}
```

### 2.3 Upper Bound (After Last Occurrence)

Find the first position where `arr[i] > target`:

```java
int upperBound(int[] arr, int target) {
    int lo = 0, hi = arr.length;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] <= target) lo = mid + 1;
        else hi = mid;
    }
    return lo; // first index where arr[i] > target
}
```

### 2.4 Search in Rotated Sorted Array

A sorted array rotated at some pivot (e.g., `[4,5,6,7,0,1,2]`). One half is always sorted:

```java
int searchRotated(int[] nums, int target) {
    int lo = 0, hi = nums.length - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) return mid;

        // Left half is sorted
        if (nums[lo] <= nums[mid]) {
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
            else lo = mid + 1;
        } else {
            // Right half is sorted
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
            else hi = mid - 1;
        }
    }
    return -1;
}
```

### 2.5 Search a 2D Matrix

Treat the 2D matrix as a flat sorted array:

```java
boolean searchMatrix(int[][] matrix, int target) {
    int rows = matrix.length, cols = matrix[0].length;
    int lo = 0, hi = rows * cols - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int val = matrix[mid / cols][mid % cols];
        if (val == target) return true;
        else if (val < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return false;
}
```

### 2.6 Binary Search on Answer

Sometimes you don't search an array -- you binary search on the answer space. Example: "What is the minimum capacity to ship packages in D days?"

```java
int shipWithinDays(int[] weights, int days) {
    int lo = max(weights), hi = sum(weights);
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (canShip(weights, days, mid)) hi = mid;
        else lo = mid + 1;
    }
    return lo;
}

boolean canShip(int[] weights, int days, int capacity) {
    int daysNeeded = 1, currentLoad = 0;
    for (int w : weights) {
        if (currentLoad + w > capacity) {
            daysNeeded++;
            currentLoad = 0;
        }
        currentLoad += w;
    }
    return daysNeeded <= days;
}
```

**Java Standard Library:**

```java
// Arrays.binarySearch returns the index if found,
// or -(insertion point) - 1 if not found
int idx = Arrays.binarySearch(sorted, target);
if (idx < 0) {
    int insertionPoint = -(idx + 1); // where it would be inserted
}

// For a List
int idx = Collections.binarySearch(list, target);
```

**Common Pitfalls:**
- Off-by-one: `lo <= hi` vs `lo < hi` -- depends on whether `hi` is inclusive or exclusive
- Integer overflow: use `lo + (hi - lo) / 2` instead of `(lo + hi) / 2`
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

```java
// Two Sum II (sorted array)
int[] twoSumSorted(int[] numbers, int target) {
    int lo = 0, hi = numbers.length - 1;
    while (lo < hi) {
        int sum = numbers[lo] + numbers[hi];
        if (sum == target) return new int[]{lo, hi};
        else if (sum < target) lo++;
        else hi--;
    }
    return new int[]{}; // no solution
}

// Valid Palindrome
boolean isPalindrome(String s) {
    int lo = 0, hi = s.length() - 1;
    while (lo < hi) {
        if (s.charAt(lo) != s.charAt(hi)) return false;
        lo++;
        hi--;
    }
    return true;
}

// Container With Most Water
int maxArea(int[] height) {
    int lo = 0, hi = height.length - 1, best = 0;
    while (lo < hi) {
        int area = (hi - lo) * Math.min(height[lo], height[hi]);
        best = Math.max(best, area);
        if (height[lo] < height[hi]) lo++;
        else hi--;
    }
    return best;
}
```

### 3.2 Same-Direction Pointers (Fast / Slow)

Both pointers start at the beginning. The fast pointer moves ahead; the slow pointer tracks a condition.

```java
// Remove duplicates in-place from sorted array
int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    int slow = 0;
    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[slow]) {
            slow++;
            nums[slow] = nums[fast];
        }
    }
    return slow + 1;
}

// Linked list cycle detection (Floyd's algorithm)
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

### 3.3 Three Pointers (3Sum Pattern)

Fix one pointer, then use two pointers on the remainder:

```java
List<List<Integer>> threeSum(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();

    for (int i = 0; i < nums.length - 2; i++) {
        if (i > 0 && nums[i] == nums[i - 1]) continue; // skip duplicates

        int lo = i + 1, hi = nums.length - 1;
        while (lo < hi) {
            int sum = nums[i] + nums[lo] + nums[hi];
            if (sum == 0) {
                result.add(Arrays.asList(nums[i], nums[lo], nums[hi]));
                while (lo < hi && nums[lo] == nums[lo + 1]) lo++;
                while (lo < hi && nums[hi] == nums[hi - 1]) hi--;
                lo++;
                hi--;
            } else if (sum < 0) {
                lo++;
            } else {
                hi--;
            }
        }
    }
    return result;
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

```java
// Maximum sum of subarray of size k
int maxSumSubarray(int[] arr, int k) {
    int windowSum = 0;
    // Build first window
    for (int i = 0; i < k; i++) windowSum += arr[i];
    int best = windowSum;

    // Slide the window
    for (int i = k; i < arr.length; i++) {
        windowSum += arr[i] - arr[i - k]; // add new, remove old
        best = Math.max(best, windowSum);
    }
    return best;
}
```

### 4.2 Variable-Size Window

Window expands until a condition is violated, then contracts from the left.

```java
// Longest substring without repeating characters
int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> charIndex = new HashMap<>(); // last seen index
    int best = 0, left = 0;

    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
            left = charIndex.get(c) + 1; // shrink window past the duplicate
        }
        charIndex.put(c, right);
        best = Math.max(best, right - left + 1);
    }
    return best;
}

// Minimum window substring
String minWindow(String s, String t) {
    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) need.merge(c, 1, Integer::sum);

    Map<Character, Integer> have = new HashMap<>();
    int formed = 0, required = need.size();
    int bestLen = Integer.MAX_VALUE, bestStart = 0;
    int left = 0;

    for (int right = 0; right < s.length(); right++) {
        char ch = s.charAt(right);
        have.merge(ch, 1, Integer::sum);
        if (need.containsKey(ch) && have.get(ch).equals(need.get(ch))) {
            formed++;
        }

        // Contract window from left
        while (formed == required) {
            if (right - left + 1 < bestLen) {
                bestLen = right - left + 1;
                bestStart = left;
            }
            char leftCh = s.charAt(left);
            have.merge(leftCh, -1, Integer::sum);
            if (need.containsKey(leftCh) && have.get(leftCh) < need.get(leftCh)) {
                formed--;
            }
            left++;
        }
    }

    return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
}
```

### 4.3 Sliding Window Template

Most variable-size window problems follow this template:

```java
int slidingWindow(int[] arr) {
    int left = 0;
    // state variables (sum, count map, etc.)

    for (int right = 0; right < arr.length; right++) {
        // Expand: add arr[right] to window state

        while (/* window is invalid */) {
            // Contract: remove arr[left] from window state
            left++;
        }

        // Update answer (depends on whether you want max or min window)
    }
    return answer;
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
- Using `Integer` instead of `int` in the `have` map and using `==` to compare -- always use `.equals()` for `Integer` objects
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

```java
// Preorder traversal
List<Integer> preorder(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    result.add(root.val);
    result.addAll(preorder(root.left));
    result.addAll(preorder(root.right));
    return result;
}

// Max depth of binary tree
int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

### 5.2 DFS on Trees (Iterative with Stack)

```java
List<Integer> preorderIterative(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;

    Deque<TreeNode> stack = new ArrayDeque<>();
    stack.push(root);

    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        result.add(node.val);
        // Push right first so left is processed first (LIFO)
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return result;
}
```

### 5.3 DFS on Graphs

```java
// DFS traversal of a graph (adjacency list)
List<Integer> dfsGraph(Map<Integer, List<Integer>> graph, int start) {
    Set<Integer> visited = new HashSet<>();
    List<Integer> result = new ArrayList<>();
    dfs(graph, start, visited, result);
    return result;
}

void dfs(Map<Integer, List<Integer>> graph, int node,
         Set<Integer> visited, List<Integer> result) {
    visited.add(node);
    result.add(node);
    for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
        if (!visited.contains(neighbor)) {
            dfs(graph, neighbor, visited, result);
        }
    }
}

// Number of islands (2D grid DFS)
int numIslands(char[][] grid) {
    int rows = grid.length, cols = grid[0].length, count = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == '1') {
                count++;
                dfsIsland(grid, r, c);
            }
        }
    }
    return count;
}

void dfsIsland(char[][] grid, int r, int c) {
    if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] == '0')
        return;
    grid[r][c] = '0'; // mark visited
    dfsIsland(grid, r + 1, c);
    dfsIsland(grid, r - 1, c);
    dfsIsland(grid, r, c + 1);
    dfsIsland(grid, r, c - 1);
}
```

### 5.4 Backtracking (DFS with Undo)

Backtracking builds solutions incrementally, abandoning a path as soon as it's invalid.

```java
// Generate all subsets
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

void backtrack(int[] nums, int start, List<Integer> current,
               List<List<Integer>> result) {
    result.add(new ArrayList<>(current)); // make a copy

    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);       // choose
        backtrack(nums, i + 1, current, result); // explore
        current.remove(current.size() - 1);      // undo
    }
}

// Permutations
List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    permuteHelper(nums, 0, result);
    return result;
}

void permuteHelper(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        List<Integer> perm = new ArrayList<>();
        for (int n : nums) perm.add(n);
        result.add(perm);
        return;
    }
    for (int i = start; i < nums.length; i++) {
        int tmp = nums[start]; nums[start] = nums[i]; nums[i] = tmp; // swap
        permuteHelper(nums, start + 1, result);
        tmp = nums[start]; nums[start] = nums[i]; nums[i] = tmp;     // undo swap
    }
}
```

### 5.5 Topological Sort (DFS-based)

Order nodes in a DAG so all edges point forward:

```java
int[] topologicalSort(int numNodes, int[][] edges) {
    Map<Integer, List<Integer>> graph = new HashMap<>();
    for (int[] e : edges) {
        graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
    }

    int[] visited = new int[numNodes]; // 0=unvisited, 1=in-progress, 2=done
    List<Integer> result = new ArrayList<>();
    boolean[] hasCycle = {false};

    for (int i = 0; i < numNodes; i++) {
        if (visited[i] == 0) {
            dfsTopoSort(i, graph, visited, result, hasCycle);
        }
    }

    if (hasCycle[0]) return new int[0];
    Collections.reverse(result);
    return result.stream().mapToInt(Integer::intValue).toArray();
}

void dfsTopoSort(int node, Map<Integer, List<Integer>> graph,
                 int[] visited, List<Integer> result, boolean[] hasCycle) {
    if (hasCycle[0]) return;
    visited[node] = 1; // in-progress
    for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
        if (visited[neighbor] == 1) { hasCycle[0] = true; return; }
        if (visited[neighbor] == 0) dfsTopoSort(neighbor, graph, visited, result, hasCycle);
    }
    visited[node] = 2; // done
    result.add(node);
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
- Stack overflow on very deep recursion -- consider iterative DFS with `ArrayDeque`
- Not making a deep copy of the current state before adding to results (backtracking)
- In Java, `current.remove(current.size() - 1)` -- don't use `remove(int)` on a `List<Integer>` with an index, or it will autobox -- use `remove(Integer.valueOf(nums[i]))` carefully, or remove by index

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

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size(); // snapshot size before processing level
        List<Integer> level = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            TreeNode node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

### 6.2 BFS on Graphs (Shortest Path)

```java
// Shortest path in unweighted graph
int shortestPath(Map<Integer, List<Integer>> graph, int start, int end) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> queue = new ArrayDeque<>();
    queue.offer(start);
    visited.add(start);
    int distance = 0;

    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int node = queue.poll();
            if (node == end) return distance;
            for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        distance++;
    }
    return -1; // unreachable
}
```

### 6.3 BFS on Grid

```java
// Shortest path in a binary matrix (0 = passable, 1 = blocked)
int shortestPathGrid(int[][] grid) {
    int n = grid.length;
    if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};
    Queue<int[]> queue = new ArrayDeque<>();
    queue.offer(new int[]{0, 0});
    grid[0][0] = 1; // mark visited
    int dist = 1;

    while (!queue.isEmpty()) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            if (cell[0] == n - 1 && cell[1] == n - 1) return dist;
            for (int[] d : dirs) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr >= 0 && nr < n && nc >= 0 && nc < n && grid[nr][nc] == 0) {
                    grid[nr][nc] = 1;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
        dist++;
    }
    return -1;
}
```

### 6.4 Multi-Source BFS

Start BFS from multiple sources simultaneously (e.g., "rotting oranges"):

```java
int orangesRotting(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    Queue<int[]> queue = new ArrayDeque<>();
    int fresh = 0;

    // Enqueue all rotten oranges (multiple sources)
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == 2) queue.offer(new int[]{r, c});
            else if (grid[r][c] == 1) fresh++;
        }
    }

    int[][] dirs = {{0,1},{0,-1},{1,0},{-1,0}};
    int minutes = 0;

    while (!queue.isEmpty() && fresh > 0) {
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            for (int[] d : dirs) {
                int nr = cell[0] + d[0], nc = cell[1] + d[1];
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                    grid[nr][nc] = 2;
                    fresh--;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }
        minutes++;
    }

    return fresh > 0 ? -1 : minutes;
}
```

### 6.5 Kahn's Algorithm (BFS Topological Sort)

```java
int[] topologicalSortBFS(int numCourses, int[][] prerequisites) {
    Map<Integer, List<Integer>> graph = new HashMap<>();
    int[] inDegree = new int[numCourses];

    for (int[] pre : prerequisites) {
        graph.computeIfAbsent(pre[1], k -> new ArrayList<>()).add(pre[0]);
        inDegree[pre[0]]++;
    }

    // Start with nodes that have no prerequisites
    Queue<Integer> queue = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) queue.offer(i);
    }

    int[] order = new int[numCourses];
    int idx = 0;
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order[idx++] = node;
        for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (--inDegree[neighbor] == 0) queue.offer(neighbor);
        }
    }

    return idx == numCourses ? order : new int[0]; // empty if cycle exists
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
- Not snapshotting `queue.size()` before the inner loop (the size changes as you enqueue children)
- Using BFS when DFS would be simpler (e.g., tree traversals)
- `queue.poll()` returns `null` on empty -- always check `!queue.isEmpty()` or use the size loop

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

**Java Implementation:**

```java
int interpolationSearch(int[] arr, int target) {
    int lo = 0, hi = arr.length - 1;

    while (lo <= hi && target >= arr[lo] && target <= arr[hi]) {
        if (lo == hi) {
            return arr[lo] == target ? lo : -1;
        }

        // Estimate position
        int pos = lo + ((target - arr[lo]) * (hi - lo)) / (arr[hi] - arr[lo]);

        if (arr[pos] == target) return pos;
        else if (arr[pos] < target) lo = pos + 1;
        else hi = pos - 1;
    }
    return -1;
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
- Integer overflow in the position calculation -- cast to `long` if values are large
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

**Java Implementation:**

```java
int exponentialSearch(int[] arr, int target) {
    int n = arr.length;
    if (n == 0) return -1;
    if (arr[0] == target) return 0;

    // Find range [bound/2, bound]
    int bound = 1;
    while (bound < n && arr[bound] <= target) bound *= 2;

    // Binary search within the range
    int lo = bound / 2;
    int hi = Math.min(bound, n - 1);
    return binarySearchRange(arr, target, lo, hi);
}

int binarySearchRange(int[] arr, int target, int lo, int hi) {
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (arr[mid] == target) return mid;
        else if (arr[mid] < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return -1;
}
```

**When to Use:**
- Sorted data with unknown or very large size
- Searching in unbounded/infinite lists
- When the target is likely near the beginning

**Common Pitfalls:**
- Out-of-bounds access when `bound` exceeds array length -- cap with `Math.min(bound, n - 1)`
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

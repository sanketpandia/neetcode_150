# Data Structures Syllabus

A comprehensive reference for the core data structures you need to master for coding interviews and the NeetCode 150. Each section covers the concept, key operations with complexity, when to use it, Java implementation notes, common pitfalls, and NeetCode relevance.

---

## Table of Contents

1. [Arrays / ArrayList](#1-arrays--arraylist)
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

| Operation | Array / ArrayList | Linked List | Hash Map | BST (balanced) | Heap |
|-----------|-------------------|-------------|----------|----------------|------|
| Access    | O(1)              | O(n)        | O(1) avg | O(log n)       | O(n) |
| Search    | O(n)              | O(n)        | O(1) avg | O(log n)       | O(n) |
| Insert    | O(n)              | O(1)*       | O(1) avg | O(log n)       | O(log n) |
| Delete    | O(n)              | O(1)*       | O(1) avg | O(log n)       | O(log n) |

\* *Linked list insert/delete is O(1) only when you already have a pointer to the node; finding it is O(n).*

---

## Linear Structures

---

### 1. Arrays / ArrayList

**Difficulty:** Beginner

**Concept:**
Arrays store elements in contiguous memory, allowing O(1) access by index. In Java, arrays have a fixed size. `ArrayList` is the dynamic alternative backed by a resizable array.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Access by index | O(1) | `arr[i]` / `list.get(i)` |
| Search (unsorted) | O(n) | Linear scan |
| Search (sorted) | O(log n) | Binary search |
| Add to end | O(1) amortized | ArrayList doubles capacity when full |
| Insert at index | O(n) | Must shift elements right |
| Delete at index | O(n) | Must shift elements left |
| Get length | O(1) | `arr.length` / `list.size()` |

**Java Implementation Notes:**

```java
// Fixed-size array
int[] arr = new int[5];
int[] initialized = {1, 2, 3, 4, 5};

// ArrayList (dynamic)
List<Integer> list = new ArrayList<>();
list.add(42);                    // append O(1) amortized
list.add(0, 99);                 // insert at index O(n)
list.get(0);                     // access O(1)
list.set(0, 100);                // update O(1)
list.remove(list.size() - 1);   // remove last O(1)
list.remove(Integer.valueOf(42)); // remove by value O(n)
list.size();

// Copy an array
int[] copy = Arrays.copyOf(arr, arr.length);
int[] rangeCopy = Arrays.copyOfRange(arr, 1, 4); // [1, 4)

// Fill
Arrays.fill(arr, 0);

// 2D array
int[][] matrix = new int[3][4];

// Convert array to list and back
List<Integer> fromArr = new ArrayList<>(Arrays.asList(1, 2, 3));
Integer[] backToArr = fromArr.toArray(new Integer[0]);

// Delete element at index i (order preserved)
list.remove(i);  // shifts elements left

// Delete element at index i (order NOT preserved, O(1))
list.set(i, list.get(list.size() - 1));
list.remove(list.size() - 1);
```

**Common Pitfalls:**
- `Arrays.asList()` returns a fixed-size list backed by the array -- you can't add/remove from it
- Autoboxing overhead: `List<Integer>` vs `int[]` -- prefer primitives in performance-critical code
- Off-by-one in `Arrays.copyOfRange(arr, from, to)` -- `to` is exclusive
- `int[]` cannot be used as a generic type parameter; use `Integer[]` or `List<Integer>`

> **Key Insight:** When you need O(1) random access and mostly append to the end, ArrayList is your go-to. If you frequently insert/delete in the middle, consider a LinkedList or deque.

**NeetCode Relevance:** Arrays & Hashing, Two Pointers, Sliding Window -- nearly every problem uses arrays or ArrayLists.

---

### 2. Strings

**Difficulty:** Beginner

**Concept:**
Strings in Java are immutable sequences of UTF-16 characters. Once created, the content cannot be changed. Use `StringBuilder` for efficient mutable string construction.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Access char by index | O(1) | `s.charAt(i)` |
| Substring | O(n) | Creates new String in Java 7u6+ |
| Concatenation (`+`) | O(n+m) | Creates new String |
| StringBuilder append | O(1) amortized | Mutable buffer |
| Length | O(1) | `s.length()` |
| Compare | O(n) | `s.equals(t)` |

**Java Implementation Notes:**

```java
// Strings are immutable -- each + creates a new object
String s = "hello";
s = s + " world";  // O(n) -- avoid in loops

// Use StringBuilder for efficient concatenation
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append('a');
}
String result = sb.toString();

// Common operations
s.charAt(i);                        // get char at index
s.length();                         // length
s.substring(1, 4);                  // [1, 4) exclusive end
s.indexOf("sub");                   // -1 if not found
s.contains("sub");
s.startsWith("he");
s.endsWith("lo");
s.toLowerCase();
s.toUpperCase();
s.trim();                           // remove leading/trailing whitespace
s.strip();                          // Unicode-aware trim (Java 11+)
s.split(",");                       // returns String[]
String.valueOf(42);                 // int to String
Integer.parseInt("42");             // String to int
s.replace('a', 'b');               // replace all chars
s.replaceAll("\\s+", " ");         // regex replace

// Convert to char array for mutation
char[] chars = s.toCharArray();
chars[0] = 'H';
String modified = new String(chars);

// Check if two strings are anagrams
char[] a = s1.toCharArray(); Arrays.sort(a);
char[] b = s2.toCharArray(); Arrays.sort(b);
Arrays.equals(a, b); // true if anagram

// String comparison
s.equals(t);          // content equality (use this, not ==)
s.equalsIgnoreCase(t);
s.compareTo(t);       // lexicographic comparison
```

**Common Pitfalls:**
- Never use `==` to compare strings (compares references, not content) -- always use `.equals()`
- String concatenation in a loop is O(n^2) -- use `StringBuilder`
- `substring()` in modern Java (7u6+) copies the data -- it's O(n), not O(1)
- `charAt()` returns a `char` (primitive), not a `Character` -- watch for autoboxing

> **Key Insight:** Treat Java strings as read-only. When you need to manipulate characters (reverse, replace, rearrange), convert to `char[]` or use `StringBuilder`. For interview problems with ASCII input, a `int[26]` frequency array is often faster than a `HashMap<Character, Integer>`.

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

**Java Implementation Notes:**

```java
// LeetCode's standard ListNode definition
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

// Dummy head technique (simplifies edge cases)
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode curr = dummy;
while (curr.next != null) {
    if (curr.next.val == target) {
        curr.next = curr.next.next;  // delete
    } else {
        curr = curr.next;
    }
}
return dummy.next;

// Reverse a linked list (iterative)
ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}

// Fast and slow pointer (find middle)
ListNode slow = head, fast = head;
while (fast != null && fast.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
// slow is now at the middle

// Java's built-in LinkedList (doubly linked, implements Deque)
LinkedList<Integer> ll = new LinkedList<>();
ll.addFirst(1);   // O(1)
ll.addLast(2);    // O(1)
ll.removeFirst(); // O(1)
ll.removeLast();  // O(1)
ll.get(i);        // O(n) -- avoid random access
```

**Common Pitfalls:**
- Forgetting to handle `null` head or single-node lists
- Losing references when rearranging pointers -- always save `next` before overwriting
- Not using dummy nodes -- leads to special-casing head operations
- Java's `LinkedList` is rarely the right choice for interviews; use `ArrayDeque` for stack/queue

> **Key Insight:** Use the dummy head pattern to eliminate edge cases. Use fast/slow pointers to find midpoints, detect cycles, and find the kth node from the end.

**NeetCode Relevance:** Linked List category (reverse, merge, detect cycle, reorder).

---

### 4. Stacks

**Difficulty:** Beginner

**Concept:**
A stack is a Last-In-First-Out (LIFO) data structure. You push elements onto the top and pop them from the top. In Java, prefer `ArrayDeque` over the legacy `Stack` class.

**Key Operations:**

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Push | O(1) amortized | `deque.push(val)` |
| Pop | O(1) | `deque.pop()` |
| Peek / Top | O(1) | `deque.peek()` |
| IsEmpty | O(1) | `deque.isEmpty()` |

**Java Implementation Notes:**

```java
// Use ArrayDeque -- faster than Stack, not synchronized
Deque<Integer> stack = new ArrayDeque<>();

// Push
stack.push(42);         // adds to front (top of stack)

// Peek (without removing)
int top = stack.peek(); // throws NoSuchElementException if empty
int topSafe = stack.isEmpty() ? -1 : stack.peek();

// Pop
int val = stack.pop();  // removes and returns top

// IsEmpty
if (stack.isEmpty()) { /* empty */ }

// Size
int size = stack.size();

// Why NOT to use java.util.Stack:
// - Stack extends Vector (synchronized, slow)
// - ArrayDeque is faster and the recommended replacement
```

**Patterns to Know:**

1. **Matching brackets:** Push opening brackets, pop and compare for closing brackets.
2. **Monotonic stack:** Maintain a stack where elements are always in increasing (or decreasing) order. Used for "next greater element" and histogram problems.
3. **Expression evaluation:** Convert infix to postfix, then evaluate with a stack.
4. **DFS (iterative):** Use a stack instead of recursion.

```java
// Monotonic decreasing stack -- next greater element
int[] nextGreaterElement(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            int idx = stack.pop();
            result[idx] = nums[i];
        }
        stack.push(i);
    }
    return result;
}
```

**Common Pitfalls:**
- Never use `java.util.Stack` -- use `ArrayDeque` instead
- `peek()` and `pop()` throw `NoSuchElementException` on an empty deque -- always check `isEmpty()` first, or use `peekFirst()`/`pollFirst()` which return `null`
- When storing indices in the stack, remember to dereference with `nums[stack.peek()]`

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
| Enqueue | O(1) | `queue.offer(val)` |
| Dequeue | O(1) | `queue.poll()` |
| Peek front | O(1) | `queue.peek()` |
| IsEmpty | O(1) | `queue.isEmpty()` |

**Java Implementation Notes:**

```java
// Queue interface, backed by LinkedList or ArrayDeque
Queue<Integer> queue = new ArrayDeque<>();  // preferred: O(1) for all ops

// Enqueue (returns false instead of throwing on capacity limit)
queue.offer(42);

// Peek (returns null if empty, unlike element() which throws)
Integer front = queue.peek();

// Dequeue (returns null if empty, unlike remove() which throws)
Integer val = queue.poll();

// Check empty
queue.isEmpty();
queue.size();

// Deque for double-ended operations
Deque<Integer> deque = new ArrayDeque<>();
deque.offerFirst(1);   // add to front
deque.offerLast(2);    // add to back
deque.pollFirst();     // remove from front
deque.pollLast();      // remove from back
deque.peekFirst();     // peek front
deque.peekLast();      // peek back

// prefer offer/poll/peek over add/remove/element
// offer/poll/peek return null on empty; add/remove/element throw exceptions
```

**Patterns to Know:**

1. **BFS traversal:** The core use case for queues.
2. **Level-order traversal:** Process tree nodes level by level.
3. **Sliding window maximum:** Use a monotonic deque.

```java
// BFS level-order traversal
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;

    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int size = queue.size();
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

**Common Pitfalls:**
- `LinkedList` implements `Queue` but has more overhead than `ArrayDeque` -- prefer `ArrayDeque`
- Using `remove()` instead of `poll()` -- `remove()` throws on empty queue
- Forgetting to snapshot `queue.size()` before the inner loop in level-order BFS (the size changes as you add children)

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
| Iterate | O(n) | O(n) | Order not guaranteed for HashMap |

**Java Implementation Notes:**

```java
// HashMap
Map<String, Integer> map = new HashMap<>();
map.put("alice", 90);
map.get("alice");                        // null if missing
map.getOrDefault("bob", 0);             // safe default
map.containsKey("alice");
map.containsValue(90);
map.remove("bob");
map.size();

// Idiomatic frequency counting
map.put(key, map.getOrDefault(key, 0) + 1);
// or in Java 8+:
map.merge(key, 1, Integer::sum);

// Iterate
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    String k = entry.getKey();
    int v = entry.getValue();
}
for (String key : map.keySet()) { }
for (int val : map.values()) { }

// computeIfAbsent (great for grouping)
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

// HashSet
Set<Integer> set = new HashSet<>();
set.add(42);
set.contains(42);   // O(1)
set.remove(42);
set.size();

// LinkedHashMap: preserves insertion order
Map<String, Integer> ordered = new LinkedHashMap<>();

// TreeMap: sorted by key, O(log n) ops
Map<String, Integer> sorted = new TreeMap<>();
((TreeMap<String, Integer>) sorted).firstKey();
((TreeMap<String, Integer>) sorted).lastKey();
((TreeMap<String, Integer>) sorted).floorKey("m");  // largest key <= "m"
((TreeMap<String, Integer>) sorted).ceilingKey("m"); // smallest key >= "m"
```

**Patterns to Know:**

1. **Frequency counting:** Count occurrences of each element.
2. **Two-sum pattern:** Store complements for O(1) lookup.
3. **Grouping:** Group items by a computed key (e.g., anagram grouping).
4. **Deduplication:** Use a set to track seen elements.

```java
// Frequency counting
Map<Character, Integer> freq = new HashMap<>();
for (char c : s.toCharArray()) {
    freq.merge(c, 1, Integer::sum);
}

// Two-sum pattern
Map<Integer, Integer> seen = new HashMap<>(); // value -> index
for (int i = 0; i < nums.length; i++) {
    int complement = target - nums[i];
    if (seen.containsKey(complement)) {
        return new int[]{seen.get(complement), i};
    }
    seen.put(nums[i], i);
}

// Group anagrams
Map<String, List<String>> groups = new HashMap<>();
for (String word : words) {
    char[] arr = word.toCharArray();
    Arrays.sort(arr);
    String key = new String(arr);
    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
}
```

**Common Pitfalls:**
- `HashMap` iteration order is not guaranteed -- use `LinkedHashMap` if order matters
- Using mutable objects (e.g., arrays) as keys -- arrays don't override `hashCode()`/`equals()`; use `Arrays.toString(arr)` as key instead
- `map.get(key)` returns `null` if missing, not 0 -- always use `getOrDefault` for numeric values
- `HashMap` is not thread-safe; use `ConcurrentHashMap` for concurrent access

> **Key Insight:** Whenever you need to look something up by value in O(1), think HashMap. It's the most versatile data structure for interview problems.

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

**Java Implementation Notes:**

```java
// LeetCode's standard TreeNode definition
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

// Preorder: Root -> Left -> Right
void preorder(TreeNode root) {
    if (root == null) return;
    System.out.println(root.val);  // process
    preorder(root.left);
    preorder(root.right);
}

// Inorder: Left -> Root -> Right (gives sorted order for BST)
void inorder(TreeNode root) {
    if (root == null) return;
    inorder(root.left);
    System.out.println(root.val);  // process
    inorder(root.right);
}

// Postorder: Left -> Right -> Root
void postorder(TreeNode root) {
    if (root == null) return;
    postorder(root.left);
    postorder(root.right);
    System.out.println(root.val);  // process
}

// Height of tree
int height(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(height(root.left), height(root.right));
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
- Forgetting the `null` base case in recursive functions
- Confusing height (root-down) with depth (root-down from top)
- Stack overflow on very deep trees -- consider iterative DFS with an explicit `Deque`

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

**Java Implementation Notes:**

```java
// Custom BST search
TreeNode searchBST(TreeNode root, int val) {
    if (root == null || root.val == val) return root;
    if (val < root.val) return searchBST(root.left, val);
    return searchBST(root.right, val);
}

// Custom BST insert
TreeNode insertBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insertBST(root.left, val);
    else root.right = insertBST(root.right, val);
    return root;
}

// Validate BST
boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&
           validate(node.right, node.val, max);
}

// Java's built-in balanced BST implementations:
// TreeMap: sorted key-value map (Red-Black tree internally)
TreeMap<Integer, String> treeMap = new TreeMap<>();
treeMap.put(5, "five");
treeMap.firstKey();          // smallest key
treeMap.lastKey();           // largest key
treeMap.floorKey(6);         // largest key <= 6
treeMap.ceilingKey(4);       // smallest key >= 4
treeMap.lowerKey(5);         // largest key < 5
treeMap.higherKey(5);        // smallest key > 5

// TreeSet: sorted set (Red-Black tree internally)
TreeSet<Integer> treeSet = new TreeSet<>();
treeSet.add(5);
treeSet.floor(6);   // largest element <= 6
treeSet.ceiling(4); // smallest element >= 4
```

**Balanced BST Variants:**
- **TreeMap / TreeSet:** Java's built-in Red-Black tree -- O(log n) for all operations. Use these in interviews instead of implementing a custom BST.
- **AVL Tree:** Strictly balanced (height diff <= 1), faster lookups but more complex rotations.
- **Red-Black Tree:** Loosely balanced, fewer rotations on insert/delete -- what `TreeMap` uses.

**Common Pitfalls:**
- BST property is about ALL descendants, not just immediate children
- Using `int` bounds for validation -- use `long` to handle `Integer.MIN_VALUE` and `Integer.MAX_VALUE` as node values
- Deletion with two children: replace with inorder successor (or predecessor)

> **Key Insight:** Inorder traversal of a BST always gives sorted output. In Java, use `TreeMap`/`TreeSet` whenever you need a sorted structure with O(log n) ops.

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
| Insert (offer) | O(log n) | Sift up |
| Extract min/max (poll) | O(log n) | Sift down |
| Peek min/max | O(1) | Root element |
| Build heap from collection | O(n) | Bottom-up heapify |
| Search | O(n) | No ordering guarantee beyond parent-child |

**Java Implementation Notes:**

Java's `PriorityQueue` is a min-heap by default:

```java
// Min-heap (default) -- smallest element at the top
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(5);           // O(log n) insert
minHeap.offer(3);
minHeap.peek();             // O(1) -- returns 3 (min)
minHeap.poll();             // O(log n) -- removes and returns 3

// Max-heap -- largest element at the top
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
// or: new PriorityQueue<>((a, b) -> b - a)

// Custom comparator (e.g., sort by second element of int[])
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
pq.offer(new int[]{1, 5});
pq.offer(new int[]{2, 3});
pq.poll(); // returns {2, 3} (smaller second element)

// Build from existing collection
List<Integer> data = Arrays.asList(5, 3, 8, 1);
PriorityQueue<Integer> heap = new PriorityQueue<>(data); // O(n)
```

**Patterns to Know:**

1. **Top K elements:** Use a min-heap of size K. Push all elements; if heap size > K, pop. Final heap contains top K.
2. **Kth largest/smallest:** Same as top K, peek the root.
3. **Merge K sorted lists:** Push first element of each list, poll smallest, push next from that list.
4. **Median from data stream:** Use two heaps (max-heap for lower half, min-heap for upper half).

```java
// Top K frequent elements
PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]); // min-heap by freq
Map<Integer, Integer> freq = new HashMap<>();
for (int num : nums) freq.merge(num, 1, Integer::sum);

for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
    pq.offer(new int[]{e.getKey(), e.getValue()});
    if (pq.size() > k) pq.poll(); // evict least frequent
}
```

**Common Pitfalls:**
- `PriorityQueue` default is min-heap -- remember to reverse for max-heap
- Don't use `(a, b) -> b - a` as a comparator for large integers (integer overflow) -- use `Integer.compare(b, a)` or `Collections.reverseOrder()`
- `PriorityQueue` does not support O(1) access to arbitrary elements or O(log n) decrease-key

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

**Java Implementation Notes:**

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26]; // for lowercase a-z
    boolean isEnd = false;
}

class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return node.isEnd;
    }

    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return false;
            node = node.children[idx];
        }
        return true;
    }
}

// For non-ASCII or variable alphabets, use HashMap instead:
class TrieNodeGeneral {
    Map<Character, TrieNodeGeneral> children = new HashMap<>();
    boolean isEnd = false;
}
```

**Common Pitfalls:**
- `children[c - 'a']` only works for lowercase ASCII -- use a `HashMap` for general characters
- Forgetting to mark `isEnd = true` -- "app" and "apple" need distinct flags
- Returning `true` from `search` just because the path exists (without checking `isEnd`)

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

**Java Implementation Notes:**

```java
// Adjacency list (most common for interviews)
Map<Integer, List<Integer>> graph = new HashMap<>();

// Add undirected edge
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
graph.computeIfAbsent(v, k -> new ArrayList<>()).add(u);

// Add directed edge
graph.computeIfAbsent(u, k -> new ArrayList<>()).add(v);

// For dense graphs or when nodes are 0..n-1, use array of lists
List<List<Integer>> adjList = new ArrayList<>();
for (int i = 0; i < n; i++) adjList.add(new ArrayList<>());
adjList.get(u).add(v);

// Adjacency matrix (when V is small and edge queries are frequent)
boolean[][] matrix = new boolean[n][n];
matrix[u][v] = true;  // directed edge from u to v

// Weighted adjacency list
// Use int[] or a helper class to store (neighbor, weight)
Map<Integer, List<int[]>> weightedGraph = new HashMap<>();
weightedGraph.computeIfAbsent(u, k -> new ArrayList<>())
             .add(new int[]{v, weight});
```

**Key Algorithms (brief, see Searches syllabus for details):**
- **DFS:** Explore as deep as possible, then backtrack. Use for connectivity, cycle detection, topological sort.
- **BFS:** Explore level by level. Use for shortest path (unweighted).
- **Topological Sort:** Order nodes so all edges go forward (DAGs only). Use Kahn's algorithm (BFS with in-degrees) or DFS-based.
- **Dijkstra's:** Shortest path in weighted graphs (non-negative weights). Uses a `PriorityQueue`.

**Common Pitfalls:**
- Forgetting to track visited nodes leads to infinite loops in cyclic graphs
- Confusing directed vs undirected when building adjacency lists
- Off-by-one with 0-indexed vs 1-indexed nodes
- Using `graph.get(node)` without a null check -- use `getOrDefault(node, Collections.emptyList())`

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

**Java Implementation Notes:**

```java
class UnionFind {
    int[] parent, rank;
    int count; // number of components

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }

    boolean union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX == rootY) return false; // already connected

        // union by rank
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

**Java Implementation Notes:**

```java
// Monotonic decreasing stack: next greater element to the right
int[] nextGreater(int[] nums) {
    int n = nums.length;
    int[] ans = new int[n];
    Arrays.fill(ans, -1);
    Deque<Integer> stack = new ArrayDeque<>(); // stores indices

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            int top = stack.pop();
            ans[top] = nums[i];
        }
        stack.push(i);
    }
    return ans;
}

// Monotonic deque: sliding window maximum
int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>(); // indices, front has max

    for (int i = 0; i < n; i++) {
        // Remove indices outside window
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        // Remove smaller elements from back
        while (!deque.isEmpty() && nums[deque.peekLast()] <= nums[i]) {
            deque.pollLast();
        }
        deque.offerLast(i);
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    return result;
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

**Java Implementation (Fenwick Tree):**

```java
class BIT {
    int[] tree;
    int n;

    BIT(int n) {
        this.n = n;
        this.tree = new int[n + 1]; // 1-indexed
    }

    void update(int i, int delta) {
        for (; i <= n; i += i & (-i)) {
            tree[i] += delta;
        }
    }

    int query(int i) { // prefix sum [1..i]
        int sum = 0;
        for (; i > 0; i -= i & (-i)) {
            sum += tree[i];
        }
        return sum;
    }

    int rangeQuery(int l, int r) {
        return query(r) - query(l - 1);
    }
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

**Java Implementation Notes:**

```java
// Option 1: Use LinkedHashMap (simplest -- know this for interviews)
class LRUCache extends LinkedHashMap<Integer, Integer> {
    private final int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true); // accessOrder = true
        this.capacity = capacity;
    }

    public int get(int key) {
        return getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        super.put(key, value);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}

// Option 2: Custom doubly linked list + HashMap (demonstrates understanding)
class LRUCacheCustom {
    private static class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int cap;
    private final Map<Integer, Node> cache = new HashMap<>();
    private final Node head = new Node(0, 0); // dummy head (most recent)
    private final Node tail = new Node(0, 0); // dummy tail (least recent)

    LRUCacheCustom(int capacity) {
        this.cap = capacity;
        head.next = tail;
        tail.prev = head;
    }

    private void remove(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertFront(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        remove(node);
        insertFront(node);
        return node.val;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node != null) {
            remove(node);
            node.val = value;
            insertFront(node);
        } else {
            node = new Node(key, value);
            cache.put(key, node);
            insertFront(node);
            if (cache.size() > cap) {
                Node lru = tail.prev;
                remove(lru);
                cache.remove(lru.key);
            }
        }
    }
}
```

**Common Pitfalls:**
- Forgetting to store the key in the node (needed for eviction to delete from the map)
- Not using dummy head/tail nodes (leads to null-check edge cases)
- Forgetting to update the map on put when key already exists
- With `LinkedHashMap`, you must pass `accessOrder = true` to the constructor, not just `true`

> **Key Insight:** LRU Cache = HashMap + Doubly Linked List. The map gives O(1) access, the list gives O(1) ordering. Java's `LinkedHashMap` with `accessOrder=true` handles this automatically.

**NeetCode Relevance:** Linked List (LRU Cache design problem).

---

## Progress Checklist

Use this to track which data structures you've studied and implemented:

- [ ] Arrays / ArrayList
- [ ] Strings
- [ ] Linked Lists
- [ ] Stacks
- [ ] Queues
- [ ] Hash Maps / Hash Sets
- [ ] Binary Trees
- [ ] Binary Search Trees (TreeMap/TreeSet)
- [ ] Heaps / Priority Queues
- [ ] Tries
- [ ] Graphs
- [ ] Union-Find
- [ ] Monotonic Stack / Queue
- [ ] Segment Trees / Fenwick Trees
- [ ] LRU Cache

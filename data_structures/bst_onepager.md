# Binary Search Trees (BST) -- One-Pager

## Core Concept

A **Binary Search Tree (BST)** is a binary tree with a crucial ordering property: for every node, all values in the left subtree are strictly less than the node's value, and all values in the right subtree are strictly greater. This ordering enables efficient O(log n) search, insert, and delete operations on average -- but degrades to O(n) in the worst case when the tree becomes unbalanced (skewed).

**The BST Invariant:** For any node with value `x`:
- All descendants in the left subtree have values `< x`
- All descendants in the right subtree have values `> x`

This property applies recursively to every subtree. The key insight is that **inorder traversal of a BST always produces a sorted sequence**. This makes BSTs ideal for maintaining dynamic sorted data where you need fast lookup, insertion, and deletion.

In Java, `TreeMap<K, V>` and `TreeSet<E>` are backed by Red-Black trees and give O(log n) worst-case guarantees. For interview problems, you implement BST logic directly on `TreeNode`.

---

## Time Complexity Table

| Operation | Average Case | Worst Case | Notes |
|-----------|--------------|------------|-------|
| Search | O(log n) | O(n) | Worst when tree is skewed/degenerate |
| Insert | O(log n) | O(n) | Same as search -- find position |
| Delete | O(log n) | O(n) | Find node + restructure (may need successor) |
| Find min/max | O(log n) | O(n) | Go leftmost for min, rightmost for max |
| Inorder traversal | O(n) | O(n) | Visit every node, produces sorted output |
| Validate BST | O(n) | O(n) | Must check all nodes against bounds |
| Kth smallest | O(n) | O(n) | Inorder traversal |
| Lowest Common Ancestor | O(log n) | O(n) | Leverage BST ordering property |

**Why worst case is O(n):** If you insert values in sorted order (1, 2, 3, 4...), the BST degenerates into a linked list. This is why self-balancing BSTs (`TreeMap`, AVL, Red-Black) exist, maintaining O(log n) worst-case guarantees.

---

## Implementation Patterns

### 1. BST Node Definition

```java
public class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```

### 2. Search in BST

Leverage the ordering property to eliminate half the tree at each step.

```java
// Recursive
TreeNode searchBST(TreeNode root, int val) {
    if (root == null || root.val == val) return root;
    return val < root.val
        ? searchBST(root.left, val)
        : searchBST(root.right, val);
}

// Iterative (O(1) space, preferred for deep trees)
TreeNode searchBSTIterative(TreeNode root, int val) {
    while (root != null && root.val != val) {
        root = val < root.val ? root.left : root.right;
    }
    return root;
}
```

### 3. Insert into BST

Find the correct position and attach the new node.

```java
TreeNode insertIntoBST(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insertIntoBST(root.left, val);
    else                root.right = insertIntoBST(root.right, val);
    return root;
}
```

### 4. Delete from BST

The trickiest operation -- three cases to handle.

```java
TreeNode deleteNode(TreeNode root, int key) {
    if (root == null) return null;
    if (key < root.val) {
        root.left = deleteNode(root.left, key);
    } else if (key > root.val) {
        root.right = deleteNode(root.right, key);
    } else {
        // Found the node to delete
        if (root.left == null) return root.right;   // Case 1: no left child
        if (root.right == null) return root.left;   // Case 2: no right child
        // Case 3: two children -- replace with inorder successor (min of right subtree)
        TreeNode successor = findMin(root.right);
        root.val = successor.val;
        root.right = deleteNode(root.right, successor.val);
    }
    return root;
}

TreeNode findMin(TreeNode node) {
    while (node.left != null) node = node.left;
    return node;
}
```

### 5. Validate BST

**Common mistake:** Only checking immediate children. Must check ALL descendants.

```java
// Top-down approach with min/max bounds
boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val) &&
           validate(node.right, node.val, max);
}

// Alternative: Inorder traversal should produce strictly increasing values
boolean isValidBSTInorder(TreeNode root) {
    long[] prev = {Long.MIN_VALUE};
    return inorder(root, prev);
}

boolean inorder(TreeNode node, long[] prev) {
    if (node == null) return true;
    if (!inorder(node.left, prev)) return false;
    if (node.val <= prev[0]) return false;
    prev[0] = node.val;
    return inorder(node.right, prev);
}
```

### 6. Kth Smallest Element

Inorder traversal gives sorted order -- stop at the kth element.

```java
int kthSmallest(TreeNode root, int k) {
    int[] count = {0}, result = {0};
    inorderKth(root, k, count, result);
    return result[0];
}

void inorderKth(TreeNode node, int k, int[] count, int[] result) {
    if (node == null || count[0] >= k) return;
    inorderKth(node.left, k, count, result);
    if (++count[0] == k) { result[0] = node.val; return; }
    inorderKth(node.right, k, count, result);
}
```

### 7. Lowest Common Ancestor (BST)

Use the BST property to find where paths to p and q diverge.

```java
TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    // If both p and q are smaller, LCA is in left subtree
    if (p.val < root.val && q.val < root.val)
        return lowestCommonAncestor(root.left, p, q);
    // If both p and q are greater, LCA is in right subtree
    if (p.val > root.val && q.val > root.val)
        return lowestCommonAncestor(root.right, p, q);
    // Otherwise, root is the split point (LCA)
    return root;
}
```

### 8. Convert Sorted Array to BST

Build a balanced BST from sorted input.

```java
TreeNode sortedArrayToBST(int[] nums) {
    return build(nums, 0, nums.length - 1);
}

TreeNode build(int[] nums, int lo, int hi) {
    if (lo > hi) return null;
    int mid = lo + (hi - lo) / 2;
    TreeNode node = new TreeNode(nums[mid]);
    node.left = build(nums, lo, mid - 1);
    node.right = build(nums, mid + 1, hi);
    return node;
}
```

---

## Java's Built-in BST: TreeMap / TreeSet

Java provides balanced BST structures out of the box (Red-Black tree, O(log n) worst-case):

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(3, "three");
map.put(1, "one");
map.put(2, "two");

map.firstKey();         // 1 -- minimum key
map.lastKey();          // 3 -- maximum key
map.floorKey(2);        // 2 -- largest key <= 2
map.ceilingKey(2);      // 2 -- smallest key >= 2
map.lowerKey(2);        // 1 -- largest key strictly < 2
map.higherKey(2);       // 3 -- smallest key strictly > 2
map.headMap(2);         // submap with keys < 2
map.tailMap(2);         // submap with keys >= 2

TreeSet<Integer> set = new TreeSet<>();
set.add(3); set.add(1); set.add(2);
set.first();            // 1
set.last();             // 3
set.floor(2);           // 2
set.ceiling(2);         // 2
```

---

## When to Use

| Scenario | Use BST? | Alternative |
|----------|----------|-------------|
| Need sorted data with fast insert/delete | Yes (`TreeMap`/`TreeSet`) | Sorted list (O(n) insert) |
| Need O(log n) search in dynamic data | Yes | `HashMap` (O(1) but no order) |
| Range queries (e.g., all values between x and y) | Yes (`TreeMap.subMap()`) | Segment tree for static arrays |
| Find min/max dynamically | Yes | Heap (but doesn't support general search) |
| Fixed dataset, no updates | No | Sorted array + binary search |
| Need O(1) lookup by key | No | `HashMap` |

---

## Common Pitfalls

1. **Checking only immediate children for BST validity.** The constraint is about ALL descendants. Node 10 with left child 5 and left-left grandchild 12 violates the BST property.

2. **Forgetting strict inequality.** BST nodes must have `left < node < right`. Handle duplicates explicitly -- the standard BST invariant doesn't allow them.

3. **Integer overflow in validation.** Use `long` for the min/max bounds when `int` node values are at `Integer.MIN_VALUE` or `Integer.MAX_VALUE`.

4. **Delete with two children: wrong replacement.** Use inorder successor (smallest in right subtree) or inorder predecessor (largest in left subtree). Be consistent.

5. **Not using the BST property.** LCA in a BST is O(log n); in a general binary tree it requires O(n). Always look for ways to exploit the ordering.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Search / Insert / Delete | "BST", "search", "insert" | Search in BST, Insert into BST |
| Validation | "valid BST", "verify" | Validate Binary Search Tree |
| Kth Element | "kth smallest", "kth largest" | Kth Smallest Element in BST |
| LCA with BST property | "lowest common ancestor", "BST" | Lowest Common Ancestor of a BST |
| Range queries | "values between", "in range" | Range Sum of BST |
| Inorder = sorted | "sorted", "increasing order" | Recover BST, Increasing Order BST |

---

## Practice Problems

| #  | Problem                              | Difficulty | Key Pattern                    | LeetCode # |
|----|--------------------------------------|------------|--------------------------------|------------|
| 1  | Search in a Binary Search Tree       | Easy       | Basic BST search               | 700        |
| 2  | Insert into a Binary Search Tree     | Medium     | Recursive insertion            | 701        |
| 3  | Validate Binary Search Tree          | Medium     | Min/max bounds or inorder      | 98         |
| 4  | Kth Smallest Element in BST          | Medium     | Inorder traversal              | 230        |
| 5  | Lowest Common Ancestor of BST        | Medium     | Use BST ordering               | 235        |
| 6  | Delete Node in a BST                 | Medium     | Three cases (0, 1, 2 children) | 450        |
| 7  | Convert Sorted Array to BST          | Easy       | Binary divide and conquer      | 108        |

---

## Quick Reference Card

```
Define:     class TreeNode { int val; TreeNode left, right; }
Search:     if val < node.val: go left; else: go right
Insert:     Recursively find null position, attach new node
Delete:     0 children: remove; 1 child: replace with child; 2 children: replace with successor
Validate:   Check min < node.val < max recursively (use long for bounds)
Min:        Go leftmost (while node.left != null)
Max:        Go rightmost (while node.right != null)
Inorder:    Left -> Node -> Right (produces sorted output)
LCA:        Both < node: go left; both > node: go right; else: found

Java built-in: TreeMap / TreeSet (Red-Black tree, O(log n) worst-case)
               firstKey(), lastKey(), floorKey(), ceilingKey()
```

# Binary Search Trees (BST) -- One-Pager

## Core Concept

A **Binary Search Tree (BST)** is a binary tree with a crucial ordering property: for every node, all values in the left subtree are strictly less than the node's value, and all values in the right subtree are strictly greater. This ordering enables efficient O(log n) search, insert, and delete operations on average -- but degrades to O(n) in the worst case when the tree becomes unbalanced (skewed).

**The BST Invariant:** For any node with value `x`:
- All descendants in the left subtree have values `< x`
- All descendants in the right subtree have values `> x`

This property applies recursively to every subtree. The key insight is that **inorder traversal of a BST always produces a sorted sequence**. This makes BSTs ideal for maintaining dynamic sorted data where you need fast lookup, insertion, and deletion.

In Go, BSTs are not provided in the standard library. You'll implement them from scratch in interviews using the `TreeNode` struct. For production, consider using sorted slices with binary search, or third-party balanced BST libraries.

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
| Kth smallest | O(n) | O(n) | Inorder traversal (can optimize with augmented tree) |
| Lowest Common Ancestor | O(log n) | O(n) | Leverage BST ordering property |

**Space Complexity:**
- Recursive operations: O(h) where h = height. O(log n) balanced, O(n) skewed.
- Iterative with stack: O(h) for stack space.

**Why worst case is O(n):** If you insert values in sorted order (1, 2, 3, 4...), the BST degenerates into a linked list. This is why self-balancing BSTs (AVL, Red-Black) exist, maintaining O(log n) worst-case guarantees.

---

## Implementation Patterns

### 1. BST Node Definition

```go
type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}
```

### 2. Search in BST

Leverage the ordering property to eliminate half the tree at each step.

```go
// Recursive
func searchBST(root *TreeNode, val int) *TreeNode {
    if root == nil || root.Val == val {
        return root
    }
    if val < root.Val {
        return searchBST(root.Left, val)  // search left subtree
    }
    return searchBST(root.Right, val)     // search right subtree
}

// Iterative (more space-efficient)
func searchBSTIterative(root *TreeNode, val int) *TreeNode {
    for root != nil && root.Val != val {
        if val < root.Val {
            root = root.Left
        } else {
            root = root.Right
        }
    }
    return root
}
```

### 3. Insert into BST

Find the correct position and attach the new node.

```go
func insertIntoBST(root *TreeNode, val int) *TreeNode {
    if root == nil {
        return &TreeNode{Val: val}
    }
    if val < root.Val {
        root.Left = insertIntoBST(root.Left, val)
    } else {
        root.Right = insertIntoBST(root.Right, val)
    }
    return root
}
```

### 4. Delete from BST

The trickiest operation -- three cases to handle.

```go
func deleteNode(root *TreeNode, key int) *TreeNode {
    if root == nil {
        return nil
    }

    if key < root.Val {
        root.Left = deleteNode(root.Left, key)
    } else if key > root.Val {
        root.Right = deleteNode(root.Right, key)
    } else {
        // Found the node to delete

        // Case 1: Leaf node or one child
        if root.Left == nil {
            return root.Right
        }
        if root.Right == nil {
            return root.Left
        }

        // Case 2: Two children
        // Replace with inorder successor (smallest in right subtree)
        successor := findMin(root.Right)
        root.Val = successor.Val
        root.Right = deleteNode(root.Right, successor.Val)
    }
    return root
}

func findMin(node *TreeNode) *TreeNode {
    for node.Left != nil {
        node = node.Left
    }
    return node
}
```

### 5. Validate BST

**Common mistake:** Only checking immediate children. Must check ALL descendants.

```go
// Top-down approach with min/max bounds
func isValidBST(root *TreeNode) bool {
    return validate(root, math.MinInt64, math.MaxInt64)
}

func validate(node *TreeNode, min, max int64) bool {
    if node == nil {
        return true
    }
    // Node value must be strictly within bounds
    if int64(node.Val) <= min || int64(node.Val) >= max {
        return false
    }
    // Left subtree: all values must be < node.Val
    // Right subtree: all values must be > node.Val
    return validate(node.Left, min, int64(node.Val)) &&
           validate(node.Right, int64(node.Val), max)
}

// Alternative: Inorder traversal should produce sorted sequence
func isValidBSTInorder(root *TreeNode) bool {
    prev := math.MinInt64
    var inorder func(node *TreeNode) bool
    inorder = func(node *TreeNode) bool {
        if node == nil {
            return true
        }
        if !inorder(node.Left) {
            return false
        }
        if int64(node.Val) <= prev {
            return false
        }
        prev = int64(node.Val)
        return inorder(node.Right)
    }
    return inorder(root)
}
```

### 6. Kth Smallest Element

Inorder traversal gives sorted order -- stop at the kth element.

```go
func kthSmallest(root *TreeNode, k int) int {
    count := 0
    result := 0

    var inorder func(node *TreeNode)
    inorder = func(node *TreeNode) {
        if node == nil || count >= k {
            return
        }
        inorder(node.Left)
        count++
        if count == k {
            result = node.Val
            return
        }
        inorder(node.Right)
    }

    inorder(root)
    return result
}
```

### 7. Lowest Common Ancestor (BST)

Use the BST property to find where paths to p and q diverge.

```go
func lowestCommonAncestor(root, p, q *TreeNode) *TreeNode {
    // If both p and q are smaller, LCA is in left subtree
    if p.Val < root.Val && q.Val < root.Val {
        return lowestCommonAncestor(root.Left, p, q)
    }
    // If both p and q are greater, LCA is in right subtree
    if p.Val > root.Val && q.Val > root.Val {
        return lowestCommonAncestor(root.Right, p, q)
    }
    // Otherwise, root is the split point (LCA)
    return root
}
```

### 8. Convert Sorted Array to BST

Build a balanced BST from sorted input.

```go
func sortedArrayToBST(nums []int) *TreeNode {
    if len(nums) == 0 {
        return nil
    }
    mid := len(nums) / 2
    return &TreeNode{
        Val:   nums[mid],
        Left:  sortedArrayToBST(nums[:mid]),
        Right: sortedArrayToBST(nums[mid+1:]),
    }
}
```

---

## When to Use

| Scenario | Use BST? | Alternative |
|----------|----------|-------------|
| Need sorted data with fast insert/delete | Yes | Sorted slice (O(n) insert/delete) |
| Need O(log n) search in dynamic data | Yes | Hash map (O(1) but no order) |
| Range queries (e.g., all values between x and y) | Yes | Segment tree for static arrays |
| Find min/max dynamically | Yes | Heap (but doesn't support general search) |
| Fixed dataset, no updates | No | Sorted slice + binary search (simpler) |
| Need O(1) lookup by key | No | Hash map |
| Frequent insertions in sorted order | No | Self-balancing BST (AVL, Red-Black) |

---

## Common Pitfalls

1. **Checking only immediate children for BST validity.** The constraint is about ALL descendants. Node 10 with left child 5 and left-left grandchild 12 violates the BST property.

2. **Forgetting strict inequality.** BST nodes must have `left < node < right`, not `left <= node <= right`. Decide how to handle duplicates upfront.

3. **Delete with two children: choosing wrong replacement.** Use inorder successor (smallest in right subtree) or inorder predecessor (largest in left subtree). Be consistent.

4. **Not using the BST property.** Many BST problems can be solved more efficiently than general binary tree problems. For example, LCA in BST is O(log n), while LCA in general tree requires O(n).

5. **Integer overflow in validation.** When using `math.MinInt` and `math.MaxInt` as sentinels, use `int64` to avoid overflow with edge-case node values.

6. **Confusing inorder traversal position.** Inorder visits left, then node, then right. For BST, this produces sorted order. Preorder and postorder do NOT.

7. **Memory leaks in deletion.** In languages with manual memory management, deleted nodes must be freed. Go's GC handles this, but be aware in other contexts.

---

## Interview Relevance

BST problems directly test understanding of recursion and tree invariants.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Search / Insert / Delete | "BST", "search", "insert" | Search in BST, Insert into BST |
| Validation | "valid BST", "verify" | Validate Binary Search Tree |
| Kth Element | "kth smallest", "kth largest" | Kth Smallest Element in BST |
| LCA with BST property | "lowest common ancestor", "BST" | Lowest Common Ancestor of a BST |
| Range queries | "values between", "in range" | Range Sum of BST |
| Inorder = sorted | "sorted", "increasing order" | Recover BST, Increasing Order BST |

**Interview Insight:** When the problem says "BST", immediately think: (1) Can I use the ordering to prune search space? (2) Can I use inorder traversal for sorted access? (3) Is there a more efficient solution than the general tree version?

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Search in a Binary Search Tree | Easy | Basic BST search | 700 |
| 2 | Insert into a Binary Search Tree | Medium | Recursive insertion | 701 |
| 3 | Validate Binary Search Tree | Medium | Min/max bounds or inorder | 98 |
| 4 | Kth Smallest Element in BST | Medium | Inorder traversal | 230 |
| 5 | Lowest Common Ancestor of BST | Medium | Use BST ordering | 235 |
| 6 | Delete Node in a BST | Medium | Three cases (0, 1, 2 children) | 450 |
| 7 | Convert Sorted Array to BST | Easy | Binary divide and conquer | 108 |

Start with 1-2 to internalize the BST property. Problem 3 is a classic validation pattern. Problems 4-5 show how BST ordering simplifies solutions. Problem 6 tests comprehensive understanding.

---

## Balanced BST Variants (Conceptual)

While you won't implement these in interviews, know they exist:

- **AVL Tree:** Strictly balanced (height difference ≤ 1), guarantees O(log n) worst case. Requires rotations on insert/delete.
- **Red-Black Tree:** Loosely balanced (max height 2 * min height), fewer rotations, still O(log n). Used in C++ `std::map`.
- **Splay Tree:** Self-adjusting, moves frequently accessed nodes toward root. Amortized O(log n).
- **Treap:** Randomized BST using priorities, simple to implement.

**In Go:** No standard library BST. Use `sort.Search` on sorted slices for read-heavy workloads, or implement your own BST for dynamic insert/delete.

---

## Quick Reference Card

```
Define:     type TreeNode struct { Val int; Left, Right *TreeNode }
Search:     if val < node.Val { go left } else { go right }
Insert:     Recursively find nil position, attach new node
Delete:     0 children: remove; 1 child: replace with child; 2 children: replace with successor
Validate:   Check min < node.Val < max recursively
Min:        Go leftmost (while node.Left != nil)
Max:        Go rightmost (while node.Right != nil)
Inorder:    Left -> Node -> Right (produces sorted output)
LCA:        If both < node: go left; if both > node: go right; else: found
```

---

> **Key Insight:** The BST property is about ALL descendants, not just immediate children. Inorder traversal of a BST is always sorted. Use this to your advantage.

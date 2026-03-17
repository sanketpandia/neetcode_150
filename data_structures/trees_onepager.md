# Trees (Binary Trees) -- One-Pager

## Core Concept

A **binary tree** is a hierarchical data structure where each node has at most two children,
referred to as the **left** and **right** child. Unlike linear structures (arrays, linked lists),
trees model hierarchical relationships and enable efficient divide-and-conquer strategies.

**Key Terminology:**
- **Root:** The topmost node (no parent)
- **Leaf:** A node with no children (`Left == nil && Right == nil`)
- **Height:** Longest path from root to any leaf (root-only tree has height 1)
- **Depth:** Distance from the root to a given node (root has depth 0)
- **Subtree:** A node and all its descendants
- **Complete tree:** Every level full except possibly the last, which fills left-to-right
- **Full tree:** Every node has 0 or 2 children
- **Perfect tree:** All leaves at the same depth, all internal nodes have 2 children
- **Balanced tree:** Height difference between left and right subtrees is at most 1 (for every node)

**The fundamental insight for tree problems:** Most solutions follow a recursive pattern --
solve the problem for the left subtree, solve for the right subtree, then combine the results
at the current node. Think: "What information do I need from my children?"

---

## Time Complexity Table

| Operation              | General Binary Tree | Balanced BST  | Notes                          |
|------------------------|---------------------|---------------|--------------------------------|
| DFS traversal          | O(n)                | O(n)          | Visit every node once          |
| BFS traversal          | O(n)                | O(n)          | Visit every node once          |
| Search                 | O(n)                | O(log n)      | Must check all in general tree |
| Insert                 | O(n)                | O(log n)      | Find position first            |
| Delete                 | O(n)                | O(log n)      | Find + restructure             |
| Height                 | O(n)                | O(n)          | Recursive DFS                  |
| Count nodes            | O(n)                | O(n)*         | *O(log^2 n) for complete trees |
| Find min/max           | O(n)                | O(log n)      | BST: go leftmost/rightmost     |

**Space Complexity for Traversals:**
- DFS (recursive): O(h) where h = height of tree. O(n) worst case (skewed), O(log n) balanced.
- BFS (queue): O(w) where w = maximum width of tree. Up to O(n/2) = O(n) for complete tree.

---

## Implementation Patterns

### 1. Node Definition

```go
type TreeNode struct {
    Val   int
    Left  *TreeNode
    Right *TreeNode
}
```

### 2. DFS Traversals (Recursive)

The three traversals differ only in when you process the current node relative to its children.

```go
// Preorder: Root -> Left -> Right
// Use: Serialize/copy a tree, prefix expression evaluation
func preorder(root *TreeNode, result *[]int) {
    if root == nil {
        return
    }
    *result = append(*result, root.Val)  // process BEFORE children
    preorder(root.Left, result)
    preorder(root.Right, result)
}

// Inorder: Left -> Root -> Right
// Use: BST sorted output, expression trees
func inorder(root *TreeNode, result *[]int) {
    if root == nil {
        return
    }
    inorder(root.Left, result)
    *result = append(*result, root.Val)  // process BETWEEN children
    inorder(root.Right, result)
}

// Postorder: Left -> Right -> Root
// Use: Delete tree, evaluate expression, calculate size/height
func postorder(root *TreeNode, result *[]int) {
    if root == nil {
        return
    }
    postorder(root.Left, result)
    postorder(root.Right, result)
    *result = append(*result, root.Val)  // process AFTER children
}
```

### 3. BFS / Level-Order Traversal

Process nodes level by level using a queue.

```go
func levelOrder(root *TreeNode) [][]int {
    if root == nil {
        return nil
    }
    result := [][]int{}
    queue := []*TreeNode{root}

    for len(queue) > 0 {
        levelSize := len(queue)  // nodes in current level
        level := make([]int, 0, levelSize)

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

### 4. Maximum Depth (Height)

The classic recursive pattern: get info from children, combine.

```go
func maxDepth(root *TreeNode) int {
    if root == nil {
        return 0
    }
    leftDepth := maxDepth(root.Left)
    rightDepth := maxDepth(root.Right)
    if leftDepth > rightDepth {
        return leftDepth + 1
    }
    return rightDepth + 1
}
```

### 5. Invert Binary Tree

Swap left and right children at every node.

```go
func invertTree(root *TreeNode) *TreeNode {
    if root == nil {
        return nil
    }
    root.Left, root.Right = root.Right, root.Left
    invertTree(root.Left)
    invertTree(root.Right)
    return root
}
```

### 6. Diameter of Binary Tree

The diameter is the longest path between any two nodes (measured in edges). It may or may
not pass through the root. Track it as a side effect during height computation.

```go
func diameterOfBinaryTree(root *TreeNode) int {
    diameter := 0

    var height func(node *TreeNode) int
    height = func(node *TreeNode) int {
        if node == nil {
            return 0
        }
        left := height(node.Left)
        right := height(node.Right)

        // Update diameter: path through this node
        if left+right > diameter {
            diameter = left + right
        }

        // Return height for parent's use
        if left > right {
            return left + 1
        }
        return right + 1
    }

    height(root)
    return diameter
}
```

### 7. Check if Balanced

A tree is balanced if for every node, the height difference between left and right subtrees
is at most 1. Compute height bottom-up and return -1 as a sentinel for "unbalanced."

```go
func isBalanced(root *TreeNode) bool {
    return checkHeight(root) != -1
}

func checkHeight(root *TreeNode) int {
    if root == nil {
        return 0
    }
    left := checkHeight(root.Left)
    if left == -1 {
        return -1  // left subtree unbalanced
    }
    right := checkHeight(root.Right)
    if right == -1 {
        return -1  // right subtree unbalanced
    }
    diff := left - right
    if diff < -1 || diff > 1 {
        return -1  // this node is unbalanced
    }
    if left > right {
        return left + 1
    }
    return right + 1
}
```

### 8. Same Tree / Subtree Check

```go
func isSameTree(p, q *TreeNode) bool {
    if p == nil && q == nil {
        return true
    }
    if p == nil || q == nil {
        return false
    }
    return p.Val == q.Val &&
        isSameTree(p.Left, q.Left) &&
        isSameTree(p.Right, q.Right)
}

func isSubtree(root, subRoot *TreeNode) bool {
    if root == nil {
        return false
    }
    if isSameTree(root, subRoot) {
        return true
    }
    return isSubtree(root.Left, subRoot) || isSubtree(root.Right, subRoot)
}
```

### 9. Iterative DFS (Using a Stack)

When recursion depth might cause stack overflow, or when you need explicit control.

```go
// Iterative preorder
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

---

## DFS vs BFS: When to Use Which

| Criteria                         | DFS                           | BFS                           |
|----------------------------------|-------------------------------|-------------------------------|
| Need to explore all paths        | Yes (backtracking)            | Not ideal                     |
| Need shortest path (unweighted)  | No                            | Yes                           |
| Level-by-level processing        | Awkward                       | Natural                       |
| Tree height/depth problems       | Natural                       | Possible but less clean       |
| Space in balanced tree           | O(log n)                      | O(n) (widest level)           |
| Space in skewed tree             | O(n)                          | O(1)                          |
| Validate BST                     | Natural (inorder)             | Not typical                   |
| Serialize/deserialize            | Both work                     | Both work                     |

**Rule of thumb:** If the problem involves path properties, depth, or subtree comparison, use
DFS. If it involves level-based processing or shortest distance, use BFS.

---

## Common Recursive Patterns

Most tree problems follow one of these templates:

**Pattern 1: Return a value (bottom-up)**
```go
func solve(root *TreeNode) int {
    if root == nil { return baseCase }
    left := solve(root.Left)
    right := solve(root.Right)
    return combine(left, right, root.Val)
}
```
Examples: max depth, height, count nodes, is balanced.

**Pattern 2: Pass information down (top-down)**
```go
func solve(root *TreeNode, parentInfo int) {
    if root == nil { return }
    // use parentInfo with root.Val
    solve(root.Left, updatedInfo)
    solve(root.Right, updatedInfo)
}
```
Examples: path sum, validate BST with min/max bounds.

**Pattern 3: Track global state (side effect)**
```go
var globalResult int
func solve(root *TreeNode) int {
    if root == nil { return 0 }
    left := solve(root.Left)
    right := solve(root.Right)
    globalResult = max(globalResult, left + right + root.Val)  // side effect
    return max(left, right) + root.Val                         // return value
}
```
Examples: diameter, max path sum.

---

## When to Use

| Scenario                                        | Use Binary Tree? |
|-------------------------------------------------|------------------|
| Hierarchical data (org chart, file system)      | Yes              |
| Need efficient search/insert/delete             | Use BST variant  |
| Priority-based access                           | Use heap         |
| Need O(1) lookup by key                         | No -- use hash map |
| Sequential data                                 | No -- use array/list |
| Expression parsing                              | Yes (expression tree) |

---

## Common Pitfalls

1. **Forgetting the nil base case.** Every recursive tree function must handle `root == nil`.
   This is the termination condition.

2. **Confusing height vs depth.** Height is measured from the bottom (leaf = 0 or 1 depending
   on convention). Depth is measured from the top (root = 0). Different problems use different
   conventions -- read carefully.

3. **Returning wrong type from recursion.** If the function returns a value, make sure you
   use it. A common bug: calling `solve(root.Left)` without storing the result.

4. **Stack overflow on deep trees.** Recursive DFS uses O(h) call stack. For trees with
   depth 10^5+, use iterative DFS with an explicit stack.

5. **Modifying the tree unintentionally.** When problems ask you to check a property, do not
   modify node values or structure. Use separate variables.

6. **Ignoring the difference between DFS and BFS space complexity.** For a wide, balanced
   tree, DFS uses O(log n) space while BFS uses O(n). For a deep, narrow tree, it is reversed.

---

## Interview Relevance

Binary tree problems are a staple of coding interviews. Pattern mapping:

| Pattern                  | Signal Words                                      | Example Problems                   |
|--------------------------|---------------------------------------------------|------------------------------------|
| Recursive DFS            | "depth", "height", "subtree", "path"               | Max Depth, Same Tree, Path Sum     |
| Level-Order BFS          | "level", "width", "zigzag", "right side view"       | Level Order, Right Side View       |
| Bottom-Up Computation    | "diameter", "balanced", "height-based"              | Diameter, Is Balanced              |
| Top-Down Passing         | "path sum", "validate", "boundaries"                | Path Sum, Validate BST             |
| Tree Construction        | "build", "construct", "from traversal"              | Build from Preorder + Inorder      |
| Serialize/Deserialize    | "encode", "decode", "serialize"                     | Serialize and Deserialize          |

**Interview tip:** When you see a tree problem, immediately ask: "Can I solve this by
recursively solving for left and right subtrees?" For ~80% of tree problems, the answer is yes.

---

## Practice Problems

| #  | Problem                              | Difficulty | Key Pattern                    | LeetCode # |
|----|--------------------------------------|------------|--------------------------------|------------|
| 1  | Invert Binary Tree                   | Easy       | Recursive swap                 | 226        |
| 2  | Maximum Depth of Binary Tree         | Easy       | Bottom-up recursion            | 104        |
| 3  | Same Tree                            | Easy       | Parallel recursion             | 100        |
| 4  | Subtree of Another Tree              | Easy       | Same Tree as subroutine        | 572        |
| 5  | Diameter of Binary Tree              | Easy       | Height + global side effect    | 543        |
| 6  | Balanced Binary Tree                 | Easy       | Height with sentinel           | 110        |
| 7  | Binary Tree Level Order Traversal    | Medium     | BFS with queue                 | 102        |
| 8  | Binary Tree Right Side View          | Medium     | BFS last-in-level              | 199        |
| 9  | Binary Tree Maximum Path Sum         | Hard       | DFS + global max tracking      | 124        |

Start with 1-4 to build recursive intuition. Problems 5-6 introduce the "compute height
with side effects" pattern. Problems 7-8 cover BFS. Problem 9 is a harder variant of the
diameter pattern.

---

## Traversal Summary

```
Preorder  (Root, Left, Right):  [1, 2, 4, 5, 3, 6, 7]
Inorder   (Left, Root, Right):  [4, 2, 5, 1, 6, 3, 7]
Postorder (Left, Right, Root):  [4, 5, 2, 6, 7, 3, 1]
Level-order:                    [[1], [2, 3], [4, 5, 6, 7]]

        1
       / \
      2   3
     / \ / \
    4  5 6  7
```

## Quick Reference Card

```
Define:     type TreeNode struct { Val int; Left, Right *TreeNode }
Create:     node := &TreeNode{Val: 1, Left: leftChild, Right: rightChild}
Nil check:  if root == nil { return }
Height:     max(height(left), height(right)) + 1
Leaf:       root.Left == nil && root.Right == nil
DFS:        Recursion or explicit stack
BFS:        Queue with level-size loop
Preorder:   process -> left -> right
Inorder:    left -> process -> right
Postorder:  left -> right -> process
```

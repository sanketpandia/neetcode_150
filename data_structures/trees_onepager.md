# Trees (Binary Trees) -- One-Pager

## Core Concept

A **binary tree** is a hierarchical data structure where each node has at most two children, referred to as the **left** and **right** child. Unlike linear structures (arrays, linked lists), trees model hierarchical relationships and enable efficient divide-and-conquer strategies.

**Key Terminology:**
- **Root:** The topmost node (no parent)
- **Leaf:** A node with no children (`left == null && right == null`)
- **Height:** Longest path from root to any leaf (root-only tree has height 1)
- **Depth:** Distance from the root to a given node (root has depth 0)
- **Subtree:** A node and all its descendants
- **Complete tree:** Every level full except possibly the last, which fills left-to-right
- **Full tree:** Every node has 0 or 2 children
- **Perfect tree:** All leaves at the same depth, all internal nodes have 2 children
- **Balanced tree:** Height difference between left and right subtrees is at most 1 (for every node)

**The fundamental insight for tree problems:** Most solutions follow a recursive pattern -- solve the problem for the left subtree, solve for the right subtree, then combine the results at the current node. Think: "What information do I need from my children?"

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
| Count nodes            | O(n)                | O(n)*         | *O(log² n) for complete trees  |
| Find min/max           | O(n)                | O(log n)      | BST: go leftmost/rightmost     |

**Space Complexity for Traversals:**
- DFS (recursive): O(h) where h = height. O(n) worst case (skewed), O(log n) balanced.
- BFS (queue): O(w) where w = maximum width of tree. Up to O(n/2) = O(n) for complete tree.

---

## Implementation Patterns

### 1. Node Definition

```java
public class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}
```

### 2. DFS Traversals (Recursive)

The three traversals differ only in when you process the current node relative to its children.

```java
// Preorder: Root -> Left -> Right
// Use: Serialize/copy a tree, prefix expression evaluation
void preorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    result.add(root.val);           // process BEFORE children
    preorder(root.left, result);
    preorder(root.right, result);
}

// Inorder: Left -> Root -> Right
// Use: BST sorted output, expression trees
void inorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    inorder(root.left, result);
    result.add(root.val);           // process BETWEEN children
    inorder(root.right, result);
}

// Postorder: Left -> Right -> Root
// Use: Delete tree, evaluate expression, calculate size/height
void postorder(TreeNode root, List<Integer> result) {
    if (root == null) return;
    postorder(root.left, result);
    postorder(root.right, result);
    result.add(root.val);           // process AFTER children
}
```

### 3. BFS / Level-Order Traversal

Process nodes level by level using a queue (`ArrayDeque` is preferred over `LinkedList`).

```java
List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.offer(root);

    while (!queue.isEmpty()) {
        int levelSize = queue.size();  // nodes in current level
        List<Integer> level = new ArrayList<>();

        for (int i = 0; i < levelSize; i++) {
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

### 4. Maximum Depth (Height)

The classic recursive pattern: get info from children, combine.

```java
int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return Math.max(maxDepth(root.left), maxDepth(root.right)) + 1;
}
```

### 5. Invert Binary Tree

Swap left and right children at every node.

```java
TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    TreeNode tmp = root.left;
    root.left = root.right;
    root.right = tmp;
    invertTree(root.left);
    invertTree(root.right);
    return root;
}
```

### 6. Diameter of Binary Tree

The diameter is the longest path between any two nodes (in edges). It may or may not pass through the root. Track it as a side effect during height computation.

```java
int diameter = 0;

int diameterOfBinaryTree(TreeNode root) {
    diameter = 0;
    height(root);
    return diameter;
}

int height(TreeNode node) {
    if (node == null) return 0;
    int left = height(node.left);
    int right = height(node.right);
    diameter = Math.max(diameter, left + right);  // update global max
    return Math.max(left, right) + 1;
}
```

### 7. Check if Balanced

A tree is balanced if for every node, the height difference between left and right subtrees is at most 1. Compute height bottom-up and return -1 as a sentinel for "unbalanced."

```java
boolean isBalanced(TreeNode root) {
    return checkHeight(root) != -1;
}

int checkHeight(TreeNode root) {
    if (root == null) return 0;
    int left = checkHeight(root.left);
    if (left == -1) return -1;     // left subtree unbalanced
    int right = checkHeight(root.right);
    if (right == -1) return -1;    // right subtree unbalanced
    if (Math.abs(left - right) > 1) return -1;  // this node unbalanced
    return Math.max(left, right) + 1;
}
```

### 8. Same Tree / Subtree Check

```java
boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val &&
           isSameTree(p.left, q.left) &&
           isSameTree(p.right, q.right);
}

boolean isSubtree(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    if (isSameTree(root, subRoot)) return true;
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
}
```

### 9. Iterative DFS (Using a Stack)

When recursion depth might cause stack overflow, or when you need explicit control.

```java
// Iterative preorder using Deque as a stack
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

**Rule of thumb:** If the problem involves path properties, depth, or subtree comparison, use DFS. If it involves level-based processing or shortest distance, use BFS.

---

## Common Recursive Patterns

**Pattern 1: Return a value (bottom-up)**
```java
int solve(TreeNode root) {
    if (root == null) return baseCase;
    int left = solve(root.left);
    int right = solve(root.right);
    return combine(left, right, root.val);
}
```
Examples: max depth, height, count nodes, is balanced.

**Pattern 2: Pass information down (top-down)**
```java
void solve(TreeNode root, int parentInfo) {
    if (root == null) return;
    // use parentInfo with root.val
    solve(root.left, updatedInfo);
    solve(root.right, updatedInfo);
}
```
Examples: path sum, validate BST with min/max bounds.

**Pattern 3: Track global state (side effect)**
```java
int globalResult = 0;
int solve(TreeNode root) {
    if (root == null) return 0;
    int left = solve(root.left);
    int right = solve(root.right);
    globalResult = Math.max(globalResult, left + right + root.val);  // side effect
    return Math.max(left, right) + root.val;                         // return value
}
```
Examples: diameter, max path sum.

---

## Common Pitfalls

1. **Forgetting the null base case.** Every recursive tree function must handle `root == null`. This is the termination condition.

2. **Confusing height vs depth.** Height is measured from the bottom (leaf = 0 or 1 depending on convention). Depth is measured from the top (root = 0). Read problems carefully.

3. **Returning wrong type from recursion.** If the function returns a value, make sure you use it. A common bug: calling `solve(root.left)` without storing the result.

4. **Stack overflow on deep trees.** Recursive DFS uses O(h) call stack. For trees with depth 10^5+, use iterative DFS with an explicit `Deque`.

5. **Not using `ArrayDeque` for BFS.** Don't use `new LinkedList<>()` for queues -- `ArrayDeque` is faster. Both implement `Queue`.

---

## Interview Relevance

| Pattern                  | Signal Words                                      | Example Problems                   |
|--------------------------|---------------------------------------------------|------------------------------------|
| Recursive DFS            | "depth", "height", "subtree", "path"               | Max Depth, Same Tree, Path Sum     |
| Level-Order BFS          | "level", "width", "zigzag", "right side view"       | Level Order, Right Side View       |
| Bottom-Up Computation    | "diameter", "balanced", "height-based"              | Diameter, Is Balanced              |
| Top-Down Passing         | "path sum", "validate", "boundaries"                | Path Sum, Validate BST             |
| Tree Construction        | "build", "construct", "from traversal"              | Build from Preorder + Inorder      |
| Serialize/Deserialize    | "encode", "decode", "serialize"                     | Serialize and Deserialize          |

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
Define:     class TreeNode { int val; TreeNode left, right; TreeNode(int v){val=v;} }
Null check: if (root == null) return;
Height:     Math.max(height(left), height(right)) + 1
Leaf:       root.left == null && root.right == null
DFS:        Recursion or Deque as explicit stack
BFS:        Queue<TreeNode> q = new ArrayDeque<>(); q.offer(root); q.poll()
Preorder:   process -> left -> right
Inorder:    left -> process -> right
Postorder:  left -> right -> process
```

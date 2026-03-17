# Linked Lists -- One-Pager

## Core Concept

A **linked list** is a linear data structure where each element (node) contains a value and a
pointer to the next node. Unlike arrays, elements are NOT stored contiguously in memory --
each node is independently allocated and connected through pointers.

**Singly Linked List:** Each node points to the next node only. Traversal is one-directional.

**Doubly Linked List:** Each node points to both its next and previous nodes. Allows
bidirectional traversal but uses more memory per node.

The key trade-off: linked lists give O(1) insertion and deletion at known positions (no
shifting required), but sacrifice O(1) random access. You must traverse from the head to
reach any arbitrary node.

---

## Time Complexity Table

| Operation               | Singly Linked | Doubly Linked | Notes                              |
|-------------------------|---------------|---------------|------------------------------------|
| Access by index         | O(n)          | O(n)          | Must traverse from head            |
| Search by value         | O(n)          | O(n)          | Linear scan                        |
| Insert at head          | O(1)          | O(1)          | Update head pointer                |
| Insert at tail          | O(1)*         | O(1)*         | *Only with a tail pointer          |
| Insert after given node | O(1)          | O(1)          | Pointer manipulation only          |
| Delete head             | O(1)          | O(1)          | Update head pointer                |
| Delete given node       | O(n) / O(1)** | O(1)         | **Singly: need prev pointer        |
| Delete by value         | O(n)          | O(n)          | Must find the node first           |
| Get length              | O(n) / O(1)***| O(n) / O(1)***| ***O(1) if you track count         |

**Space Complexity:** O(n) for n nodes, plus O(1) extra per node for pointers.

---

## Implementation Patterns

### 1. Node Definition

```go
// Singly linked list (standard LeetCode definition)
type ListNode struct {
    Val  int
    Next *ListNode
}

// Doubly linked list
type DListNode struct {
    Val        int
    Prev, Next *DListNode
}
```

### 2. Dummy Head (Sentinel Node) Pattern

This is the single most important linked list technique. A dummy node before the real head
eliminates all edge cases around modifying or deleting the head node.

```go
func removeElements(head *ListNode, val int) *ListNode {
    dummy := &ListNode{Next: head}
    curr := dummy
    for curr.Next != nil {
        if curr.Next.Val == val {
            curr.Next = curr.Next.Next  // skip/delete
        } else {
            curr = curr.Next
        }
    }
    return dummy.Next  // new head
}
```

**When to use:** Any time the head might change -- deletion, merging, partitioning, reversing.

### 3. Reverse a Linked List (Iterative)

The most frequently tested linked list operation. Three-pointer approach: prev, curr, next.

```go
func reverseList(head *ListNode) *ListNode {
    var prev *ListNode
    curr := head
    for curr != nil {
        next := curr.Next   // save next before overwriting
        curr.Next = prev    // reverse the pointer
        prev = curr         // advance prev
        curr = next         // advance curr
    }
    return prev  // prev is the new head
}
```

### 4. Reverse a Linked List (Recursive)

```go
func reverseListRecursive(head *ListNode) *ListNode {
    if head == nil || head.Next == nil {
        return head
    }
    newHead := reverseListRecursive(head.Next)
    head.Next.Next = head  // point the next node back to us
    head.Next = nil        // remove the old forward pointer
    return newHead
}
```

### 5. Fast and Slow Pointers (Tortoise and Hare)

Two pointers moving at different speeds. Slow moves 1 step, fast moves 2 steps.

```go
// Find the middle node (returns first middle for even-length)
func findMiddle(head *ListNode) *ListNode {
    slow, fast := head, head
    for fast.Next != nil && fast.Next.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
    }
    return slow
}

// Detect a cycle
func hasCycle(head *ListNode) bool {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
        if slow == fast {
            return true  // cycle detected
        }
    }
    return false  // no cycle
}

// Find the start of the cycle (Floyd's algorithm)
func detectCycle(head *ListNode) *ListNode {
    slow, fast := head, head
    for fast != nil && fast.Next != nil {
        slow = slow.Next
        fast = fast.Next.Next
        if slow == fast {
            // Phase 2: find entry point
            slow = head
            for slow != fast {
                slow = slow.Next
                fast = fast.Next  // both move at speed 1 now
            }
            return slow
        }
    }
    return nil
}
```

### 6. Merge Two Sorted Lists

```go
func mergeTwoLists(l1, l2 *ListNode) *ListNode {
    dummy := &ListNode{}
    curr := dummy
    for l1 != nil && l2 != nil {
        if l1.Val <= l2.Val {
            curr.Next = l1
            l1 = l1.Next
        } else {
            curr.Next = l2
            l2 = l2.Next
        }
        curr = curr.Next
    }
    if l1 != nil {
        curr.Next = l1
    } else {
        curr.Next = l2
    }
    return dummy.Next
}
```

### 7. Remove Nth Node From End

Use two pointers with an n-node gap.

```go
func removeNthFromEnd(head *ListNode, n int) *ListNode {
    dummy := &ListNode{Next: head}
    slow, fast := dummy, dummy
    // Advance fast by n+1 steps
    for i := 0; i <= n; i++ {
        fast = fast.Next
    }
    // Move both until fast hits nil
    for fast != nil {
        slow = slow.Next
        fast = fast.Next
    }
    slow.Next = slow.Next.Next  // remove the nth from end
    return dummy.Next
}
```

---

## When to Use

| Scenario                                          | Use Linked List? |
|---------------------------------------------------|------------------|
| Frequent insert/delete at arbitrary positions      | Yes              |
| Need O(1) access by index                         | No -- use array  |
| Implement a stack (push/pop at one end)            | Yes, or use slice |
| Implement a queue (FIFO)                           | Yes (with tail pointer) |
| Build an LRU cache                                | Yes (doubly linked) |
| Unknown size, lots of insertions                   | Yes              |
| Need cache-friendly iteration                      | No -- use array  |

---

## Common Pitfalls

1. **Losing references.** When reversing or rearranging, always save `curr.Next` in a
   temporary variable BEFORE overwriting `curr.Next`. Once you overwrite, the old reference
   is gone.

2. **Nil pointer dereference.** Always check `node != nil` before accessing `node.Val` or
   `node.Next`. Handle empty lists (nil head) and single-node lists as base cases.

3. **Not using dummy nodes.** Without a dummy head, you need special-case logic whenever the
   head itself is being modified. The dummy node pattern eliminates this entirely.

4. **Off-by-one in fast/slow pointer conditions.** For finding the middle:
   - `for fast != nil && fast.Next != nil` -- slow lands on second middle (even length)
   - `for fast.Next != nil && fast.Next.Next != nil` -- slow lands on first middle

5. **Creating cycles accidentally.** When reordering nodes, make sure the last node's `Next`
   is set to `nil`. Forgetting this creates an infinite loop.

6. **Confusing node deletion.** In a singly linked list, to delete a node you need a pointer
   to the PREVIOUS node, not the node itself. The dummy head pattern helps here.

---

## Interview Relevance

Linked list problems test your ability to manipulate pointers carefully. The core patterns:

| Pattern              | Signal Words                                       | Example Problems                |
|----------------------|----------------------------------------------------|---------------------------------|
| Dummy Head           | "delete", "merge", "partition", head might change  | Remove Elements, Merge Lists    |
| Reverse              | "reverse", "palindrome", "reorder"                 | Reverse List, Palindrome Check  |
| Fast/Slow Pointers   | "middle", "cycle", "kth from end"                  | Middle Node, Cycle Detection    |
| Merge                | "sorted lists", "combine", "merge"                 | Merge Two/K Sorted Lists        |
| In-Place Rearrange   | "reorder", "rotate", "swap pairs"                  | Reorder List, Swap Nodes        |

**Interview tip:** Draw the pointer diagram on paper (or whiteboard). Trace through your
code with a 3-4 node example before coding. Most linked list bugs come from incorrect
pointer order, and a diagram makes them obvious.

---

## Practice Problems

| #  | Problem                          | Difficulty | Key Pattern                 | LeetCode # |
|----|----------------------------------|------------|-----------------------------|------------|
| 1  | Reverse Linked List              | Easy       | Three-pointer reversal      | 206        |
| 2  | Merge Two Sorted Lists           | Easy       | Dummy head + merge          | 21         |
| 3  | Linked List Cycle                | Easy       | Fast/slow pointers          | 141        |
| 4  | Remove Nth Node From End         | Medium     | Two pointers with gap       | 19         |
| 5  | Reorder List                     | Medium     | Find middle + reverse + merge | 143      |
| 6  | LRU Cache                       | Medium     | Doubly linked + hash map    | 146        |
| 7  | Merge K Sorted Lists             | Hard       | Divide and conquer / heap   | 23         |

Start with 1-3 to master the basic patterns, then 4-5 which combine multiple patterns.
Problem 6 (LRU Cache) is a classic design question that combines linked lists with hash maps.

---

## Quick Reference Card

```
Define:     type ListNode struct { Val int; Next *ListNode }
Create:     node := &ListNode{Val: 42}
Traverse:   for curr := head; curr != nil; curr = curr.Next { ... }
Dummy:      dummy := &ListNode{Next: head}; ...; return dummy.Next
Reverse:    prev, curr = nil, head; save next; curr.Next = prev; advance
Middle:     slow, fast := head, head; fast moves 2x
Cycle:      slow == fast after both start at head
Merge:      dummy + compare l1.Val vs l2.Val, advance smaller
Delete:     prev.Next = prev.Next.Next
```

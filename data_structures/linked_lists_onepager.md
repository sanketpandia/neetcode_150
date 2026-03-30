# Linked Lists -- One-Pager

## Core Concept

A **linked list** is a linear data structure where each element (node) contains a value and a pointer to the next node. Unlike arrays, elements are NOT stored contiguously in memory -- each node is independently allocated and connected through references.

**Singly Linked List:** Each node points to the next node only. Traversal is one-directional.

**Doubly Linked List:** Each node points to both its next and previous nodes. Allows bidirectional traversal but uses more memory per node.

The key trade-off: linked lists give O(1) insertion and deletion at known positions (no shifting required), but sacrifice O(1) random access. You must traverse from the head to reach any arbitrary node.

In Java, `java.util.LinkedList` exists but is rarely used in interviews -- you define your own `ListNode` class (same as LeetCode). `ArrayDeque` is preferred for stack/queue use cases.

---

## Time Complexity Table

| Operation               | Singly Linked | Doubly Linked | Notes                              |
|-------------------------|---------------|---------------|------------------------------------|
| Access by index         | O(n)          | O(n)          | Must traverse from head            |
| Search by value         | O(n)          | O(n)          | Linear scan                        |
| Insert at head          | O(1)          | O(1)          | Update head reference              |
| Insert at tail          | O(1)*         | O(1)*         | *Only with a tail reference        |
| Insert after given node | O(1)          | O(1)          | Pointer manipulation only          |
| Delete head             | O(1)          | O(1)          | Update head reference              |
| Delete given node       | O(n) / O(1)** | O(1)          | **Singly: need prev reference      |
| Delete by value         | O(n)          | O(n)          | Must find the node first           |
| Get length              | O(n) / O(1)***| O(n) / O(1)***| ***O(1) if you track count         |

**Space Complexity:** O(n) for n nodes, plus O(1) extra per node for references.

---

## Implementation Patterns

### 1. Node Definition

```java
// Singly linked list (standard LeetCode definition)
public class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
}

// Doubly linked list
public class DListNode {
    int val;
    DListNode prev, next;
    DListNode(int val) { this.val = val; }
}
```

### 2. Dummy Head (Sentinel Node) Pattern

This is the single most important linked list technique. A dummy node before the real head eliminates all edge cases around modifying or deleting the head node.

```java
ListNode removeElements(ListNode head, int val) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode curr = dummy;
    while (curr.next != null) {
        if (curr.next.val == val) {
            curr.next = curr.next.next;  // skip/delete
        } else {
            curr = curr.next;
        }
    }
    return dummy.next;  // new head
}
```

**When to use:** Any time the head might change -- deletion, merging, partitioning, reversing.

### 3. Reverse a Linked List (Iterative)

The most frequently tested linked list operation. Three-reference approach: prev, curr, next.

```java
ListNode reverseList(ListNode head) {
    ListNode prev = null;
    ListNode curr = head;
    while (curr != null) {
        ListNode next = curr.next;  // save next before overwriting
        curr.next = prev;           // reverse the pointer
        prev = curr;                // advance prev
        curr = next;                // advance curr
    }
    return prev;  // prev is the new head
}
```

### 4. Reverse a Linked List (Recursive)

```java
ListNode reverseListRecursive(ListNode head) {
    if (head == null || head.next == null) return head;
    ListNode newHead = reverseListRecursive(head.next);
    head.next.next = head;  // point the next node back to us
    head.next = null;       // remove the old forward pointer
    return newHead;
}
```

### 5. Fast and Slow Pointers (Tortoise and Hare)

Two pointers moving at different speeds. Slow moves 1 step, fast moves 2 steps.

```java
// Find the middle node (returns first middle for even-length)
ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

// Detect a cycle
boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;  // cycle detected
    }
    return false;
}

// Find the start of the cycle (Floyd's algorithm)
ListNode detectCycle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            slow = head;  // Phase 2: find entry point
            while (slow != fast) {
                slow = slow.next;
                fast = fast.next;  // both move at speed 1 now
            }
            return slow;
        }
    }
    return null;
}
```

### 6. Merge Two Sorted Lists

```java
ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode(0);
    ListNode curr = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            curr.next = l1;
            l1 = l1.next;
        } else {
            curr.next = l2;
            l2 = l2.next;
        }
        curr = curr.next;
    }
    curr.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

### 7. Remove Nth Node From End

Use two pointers with an n-node gap.

```java
ListNode removeNthFromEnd(ListNode head, int n) {
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode slow = dummy, fast = dummy;
    // Advance fast by n+1 steps
    for (int i = 0; i <= n; i++) fast = fast.next;
    // Move both until fast hits null
    while (fast != null) {
        slow = slow.next;
        fast = fast.next;
    }
    slow.next = slow.next.next;  // remove the nth from end
    return dummy.next;
}
```

---

## When to Use

| Scenario                                          | Use Linked List? |
|---------------------------------------------------|------------------|
| Frequent insert/delete at arbitrary positions      | Yes              |
| Need O(1) access by index                         | No -- use array  |
| Implement a stack or queue                         | Use `ArrayDeque` |
| Build an LRU cache                                | Yes (doubly linked) |
| Unknown size, lots of insertions at head           | Yes              |
| Need cache-friendly iteration                      | No -- use array  |

---

## Common Pitfalls

1. **Losing references.** When reversing or rearranging, always save `curr.next` in a temporary variable BEFORE overwriting `curr.next`. Once you overwrite, the old reference is gone.

2. **NullPointerException.** Always check `node != null` before accessing `node.val` or `node.next`. Handle empty lists (null head) and single-node lists as base cases.

3. **Not using dummy nodes.** Without a dummy head, you need special-case logic whenever the head itself is being modified. The dummy node pattern eliminates this entirely.

4. **Off-by-one in fast/slow pointer conditions.** For finding the middle:
   - `while (fast != null && fast.next != null)` -- slow lands on second middle (even length)
   - `while (fast.next != null && fast.next.next != null)` -- slow lands on first middle

5. **Creating cycles accidentally.** When reordering nodes, make sure the last node's `next` is set to `null`. Forgetting this creates an infinite loop.

6. **Confusing node deletion.** In a singly linked list, to delete a node you need a reference to the PREVIOUS node, not the node itself. The dummy head pattern helps here.

---

## Interview Relevance

| Pattern              | Signal Words                                       | Example Problems                |
|----------------------|----------------------------------------------------|---------------------------------|
| Dummy Head           | "delete", "merge", "partition", head might change  | Remove Elements, Merge Lists    |
| Reverse              | "reverse", "palindrome", "reorder"                 | Reverse List, Palindrome Check  |
| Fast/Slow Pointers   | "middle", "cycle", "kth from end"                  | Middle Node, Cycle Detection    |
| Merge                | "sorted lists", "combine", "merge"                 | Merge Two/K Sorted Lists        |
| In-Place Rearrange   | "reorder", "rotate", "swap pairs"                  | Reorder List, Swap Nodes        |

**Interview tip:** Draw the pointer diagram on paper (or whiteboard). Trace through your code with a 3-4 node example before coding. Most linked list bugs come from incorrect pointer order, and a diagram makes them obvious.

---

## Practice Problems

| #  | Problem                          | Difficulty | Key Pattern                 | LeetCode # |
|----|----------------------------------|------------|-----------------------------|------------|
| 1  | Reverse Linked List              | Easy       | Three-pointer reversal      | 206        |
| 2  | Merge Two Sorted Lists           | Easy       | Dummy head + merge          | 21         |
| 3  | Linked List Cycle                | Easy       | Fast/slow pointers          | 141        |
| 4  | Remove Nth Node From End         | Medium     | Two pointers with gap       | 19         |
| 5  | Reorder List                     | Medium     | Find middle + reverse + merge | 143      |
| 6  | LRU Cache                        | Medium     | Doubly linked + hash map    | 146        |
| 7  | Merge K Sorted Lists             | Hard       | Divide and conquer / heap   | 23         |

Start with 1-3 to master the basic patterns, then 4-5 which combine multiple patterns. Problem 6 (LRU Cache) is a classic design question.

---

## Quick Reference Card

```
Define:     class ListNode { int val; ListNode next; ListNode(int val){this.val=val;} }
Create:     ListNode node = new ListNode(42);
Traverse:   for (ListNode curr = head; curr != null; curr = curr.next) { ... }
Dummy:      ListNode dummy = new ListNode(0); dummy.next = head; ... return dummy.next;
Reverse:    prev=null, curr=head; save next; curr.next=prev; advance both
Middle:     slow, fast = head; fast moves 2x while fast.next != null && fast.next.next != null
Cycle:      slow == fast after both start at head
Merge:      dummy + compare l1.val vs l2.val, advance smaller
Delete:     prev.next = prev.next.next
```

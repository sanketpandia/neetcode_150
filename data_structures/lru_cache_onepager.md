# LRU Cache -- One-Pager

## Core Concept

An **LRU (Least Recently Used) Cache** is a data structure that stores a fixed number of key-value pairs and automatically evicts the least recently accessed item when the cache reaches capacity. It combines two structures to achieve O(1) time complexity for both get and put operations:

1. **Hash Map:** Provides O(1) key lookup
2. **Doubly Linked List:** Maintains access order, with most recent at the front and least recent at the back

**The LRU Invariant:**
- Every `get` or `put` operation moves the accessed key to the front (most recent)
- When capacity is exceeded, remove the node at the back (least recent)
- The hash map stores key → node pointer for O(1) access
- The doubly linked list maintains the ordering for O(1) move/remove operations

**Real-World Applications:**
- CPU caches (L1, L2, L3)
- Web browser caches
- Database query result caches
- Operating system page replacement
- CDN content caching

This is a classic **design problem** that tests understanding of data structures, pointer manipulation, and system design principles.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Get | O(1) | Hash map lookup + move to front |
| Put | O(1) | Hash map insert/update + list manipulation |
| Eviction | O(1) | Remove last node + delete from map |
| Initialize | O(1) | Create empty structures |

**Space Complexity:** O(capacity) -- stores at most `capacity` key-value pairs.

**Why O(1)?**
- Hash map gives O(1) key access
- Doubly linked list gives O(1) node removal and insertion at any position (with pointer)
- We store pointers in the map to avoid scanning the list

---

## Implementation Patterns

### 1. LRU Node Definition

```go
type LRUNode struct {
    Key, Val   int
    Prev, Next *LRUNode
}
```

**Critical:** Store both key and value in the node. The key is needed when evicting (to delete from the hash map).

### 2. LRU Cache Structure

```go
type LRUCache struct {
    capacity   int
    cache      map[int]*LRUNode  // key -> node
    head, tail *LRUNode          // dummy head and tail sentinels
}

func Constructor(capacity int) LRUCache {
    head := &LRUNode{}
    tail := &LRUNode{}
    head.Next = tail
    tail.Prev = head
    return LRUCache{
        capacity: capacity,
        cache:    make(map[int]*LRUNode),
        head:     head,
        tail:     tail,
    }
}
```

**Dummy Nodes:** Using dummy head and tail simplifies edge cases (no nil checks for first/last nodes).

### 3. Helper: Remove Node from List

```go
func (lru *LRUCache) remove(node *LRUNode) {
    node.Prev.Next = node.Next
    node.Next.Prev = node.Prev
}
```

**O(1) Removal:** Since we have pointers to Prev and Next, no scanning needed.

### 4. Helper: Insert Node at Front (After Head)

```go
func (lru *LRUCache) insertFront(node *LRUNode) {
    node.Next = lru.head.Next
    node.Prev = lru.head
    lru.head.Next.Prev = node
    lru.head.Next = node
}
```

**Most Recent Position:** The node right after the head dummy is the most recently used.

### 5. Get Operation

```go
func (lru *LRUCache) Get(key int) int {
    if node, ok := lru.cache[key]; ok {
        // Move to front (mark as recently used)
        lru.remove(node)
        lru.insertFront(node)
        return node.Val
    }
    return -1  // key not found
}
```

**Logic:**
1. Check if key exists in map
2. If yes, remove from current position and move to front
3. Return value
4. If no, return -1 (or sentinel value)

### 6. Put Operation

```go
func (lru *LRUCache) Put(key int, value int) {
    if node, ok := lru.cache[key]; ok {
        // Key exists: update value and move to front
        node.Val = value
        lru.remove(node)
        lru.insertFront(node)
        return
    }

    // Key doesn't exist: create new node
    node := &LRUNode{Key: key, Val: value}
    lru.cache[key] = node
    lru.insertFront(node)

    // Check capacity and evict if needed
    if len(lru.cache) > lru.capacity {
        // Remove least recently used (node before tail dummy)
        lru := lru.tail.Prev
        lru.remove(lru)
        delete(lru.cache, lru.Key)  // KEY IS NEEDED HERE
    }
}
```

**Logic:**
1. If key exists: update value, move to front
2. If new key: create node, add to map, insert at front
3. If over capacity: remove last node (before tail dummy) and delete from map

**Critical Detail:** When evicting, we need the key to delete from the hash map. This is why the node stores both key and value.

### 7. Complete Implementation

```go
type LRUNode struct {
    Key, Val   int
    Prev, Next *LRUNode
}

type LRUCache struct {
    capacity   int
    cache      map[int]*LRUNode
    head, tail *LRUNode
}

func Constructor(capacity int) LRUCache {
    head := &LRUNode{}
    tail := &LRUNode{}
    head.Next = tail
    tail.Prev = head
    return LRUCache{
        capacity: capacity,
        cache:    make(map[int]*LRUNode),
        head:     head,
        tail:     tail,
    }
}

func (lru *LRUCache) remove(node *LRUNode) {
    node.Prev.Next = node.Next
    node.Next.Prev = node.Prev
}

func (lru *LRUCache) insertFront(node *LRUNode) {
    node.Next = lru.head.Next
    node.Prev = lru.head
    lru.head.Next.Prev = node
    lru.head.Next = node
}

func (lru *LRUCache) Get(key int) int {
    if node, ok := lru.cache[key]; ok {
        lru.remove(node)
        lru.insertFront(node)
        return node.Val
    }
    return -1
}

func (lru *LRUCache) Put(key int, value int) {
    if node, ok := lru.cache[key]; ok {
        node.Val = value
        lru.remove(node)
        lru.insertFront(node)
        return
    }

    node := &LRUNode{Key: key, Val: value}
    lru.cache[key] = node
    lru.insertFront(node)

    if len(lru.cache) > lru.capacity {
        lruNode := lru.tail.Prev
        lru.remove(lruNode)
        delete(lru.cache, lruNode.Key)
    }
}
```

---

## Visualization

```
Initial state (capacity=3):
  head <-> tail

After Put(1, 1):
  head <-> [1:1] <-> tail
  cache: {1 -> node1}

After Put(2, 2):
  head <-> [2:2] <-> [1:1] <-> tail
  cache: {1 -> node1, 2 -> node2}

After Get(1):
  head <-> [1:1] <-> [2:2] <-> tail
  cache: {1 -> node1, 2 -> node2}

After Put(3, 3):
  head <-> [3:3] <-> [1:1] <-> [2:2] <-> tail
  cache: {1 -> node1, 2 -> node2, 3 -> node3}

After Put(4, 4) -- evicts key 2 (LRU):
  head <-> [4:4] <-> [3:3] <-> [1:1] <-> tail
  cache: {1 -> node1, 3 -> node3, 4 -> node4}
```

---

## When to Use

| Scenario | Use LRU Cache? |
|----------|----------------|
| Fixed-size cache with automatic eviction | Yes |
| Caching with access-based eviction | Yes |
| Web page/API response caching | Yes |
| Database query result caching | Yes |
| Need O(1) get and put | Yes |
| Need different eviction policy (e.g., LFU) | No -- use LFU cache |
| Unbounded cache size | No -- use hash map |
| Time-based expiration | No -- use TTL cache |

---

## Common Pitfalls

1. **Forgetting to store the key in the node.** When evicting, you need the key to delete from the hash map. Only storing the value is insufficient.

2. **Not using dummy head and tail nodes.** Without dummies, you need special cases for empty list, single node, etc. Dummies simplify all operations.

3. **Forgetting to update the map on put.** When updating an existing key, update `node.Val` but don't add a new entry to the map.

4. **Incorrect pointer updates.** Doubly linked list pointer manipulation is error-prone. Draw diagrams and test edge cases.

5. **Removing from list but not from map (or vice versa).** Always maintain consistency between the list and the map.

6. **Checking capacity incorrectly.** Check `len(cache) > capacity` after insertion, not before. The map size should never exceed capacity.

7. **Moving node to front on put when key exists.** Don't forget this step -- updating the value isn't enough; you must mark it as recently used.

8. **Off-by-one on eviction condition.** Evict when `len(cache) > capacity`, not `==`. This ensures the cache never exceeds capacity.

---

## Interview Relevance

LRU Cache is one of the most important design problems in coding interviews.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| LRU eviction policy | "LRU", "least recently used", "cache" | LRU Cache (LeetCode 146) |
| Other eviction policies | "LFU", "FIFO", "random" | LFU Cache (LeetCode 460) |
| Combined structures | "hash map + linked list", "O(1) operations" | LRU Cache, All O(1) Data Structure |

**Interview Insight:** This problem tests multiple skills simultaneously:
- Understanding of hash maps and linked lists
- Pointer manipulation
- Edge case handling (empty cache, capacity 1, etc.)
- System design thinking (caching strategies)

**Common Follow-Ups:**
- What if we want LFU (Least Frequently Used) instead?
- How would you handle multithreading?
- How would you implement time-based expiration (TTL)?
- What if the cache is distributed across multiple servers?

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | LRU Cache | Medium | Hash map + doubly linked list | 146 |
| 2 | LFU Cache | Hard | Hash map + doubly linked list + frequency | 460 |
| 3 | All O(1) Data Structure | Hard | Multiple hash maps + doubly linked list | 432 |
| 4 | Design In-Memory File System | Hard | Trie + file metadata | 588 (premium) |

**Focus on problem 1.** It's the canonical LRU Cache problem and appears frequently in interviews. Problems 2-3 are extensions with additional complexity.

---

## Variants and Extensions

**LFU Cache (Least Frequently Used):**
- Evict the least frequently accessed item
- Requires tracking access frequency per key
- Use hash map + multiple doubly linked lists (one per frequency level)

**TTL Cache (Time-To-Live):**
- Evict items after a fixed time period
- Requires storing timestamp with each entry
- Use hash map + min-heap (or timer-based cleanup)

**FIFO Cache (First-In-First-Out):**
- Evict the oldest inserted item regardless of access
- Simpler than LRU: hash map + queue

**Random Eviction:**
- Evict a random item
- Simpler but less cache-friendly: hash map + random selection

---

## System Design Considerations

**Thread Safety:**
- Use mutex/locks around get and put operations
- Consider read-write locks for better concurrency

**Distributed Caching:**
- Consistent hashing for key distribution
- Replication for fault tolerance
- Cache invalidation strategies

**Monitoring:**
- Hit rate (successful gets / total gets)
- Eviction rate
- Average response time

---

## Quick Reference Card

```
Structure:   HashMap + Doubly Linked List + Dummy Head/Tail
Node:        struct { Key, Val int; Prev, Next *Node }
Get:         1. Lookup in map
             2. Move to front (recently used)
             3. Return value or -1
Put:         1. If exists: update value, move to front
             2. If new: add to map, insert at front
             3. If over capacity: remove tail.Prev, delete from map

Remove:      node.Prev.Next = node.Next
             node.Next.Prev = node.Prev
InsertFront: node.Next = head.Next
             node.Prev = head
             head.Next.Prev = node
             head.Next = node

CRITICAL:    Store key in node (needed for eviction)
             Use dummy head/tail (simplifies edge cases)

Time:        O(1) for get and put
Space:       O(capacity)
```

---

> **Key Insight:** LRU Cache = HashMap + Doubly Linked List. The map gives O(1) access, the list gives O(1) ordering. Store the key in each node (needed for eviction). Use dummy head/tail to avoid nil checks. This is a classic design problem that every candidate should master.

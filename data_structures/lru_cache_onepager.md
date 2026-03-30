# LRU Cache -- One-Pager

## Core Concept

An **LRU (Least Recently Used) Cache** is a data structure that stores a fixed number of key-value pairs and automatically evicts the least recently accessed item when the cache reaches capacity. It combines two structures to achieve O(1) time complexity for both get and put operations:

1. **HashMap:** Provides O(1) key lookup
2. **Doubly Linked List:** Maintains access order, with most recent at the front and least recent at the back

**The LRU Invariant:**
- Every `get` or `put` operation moves the accessed key to the front (most recent)
- When capacity is exceeded, remove the node at the back (least recent)
- The map stores `key → node reference` for O(1) access
- The doubly linked list maintains the ordering for O(1) move/remove operations

**Java Shortcut:** `LinkedHashMap` with `accessOrder=true` and a size-limiting `removeEldestEntry` provides built-in LRU behavior -- useful for quick implementations. But interviews typically expect you to implement from scratch.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Get | O(1) | HashMap lookup + move to front |
| Put | O(1) | HashMap insert/update + list manipulation |
| Eviction | O(1) | Remove last node + delete from map |

**Space Complexity:** O(capacity).

**Why O(1)?**
- HashMap gives O(1) key access
- Doubly linked list gives O(1) node removal and insertion at any position (with pointer)
- We store node references in the map to avoid scanning the list

---

## Implementation Patterns

### 1. LRU Node Definition

```java
class LRUNode {
    int key, val;
    LRUNode prev, next;
    LRUNode(int key, int val) { this.key = key; this.val = val; }
}
```

**Critical:** Store both key and value in the node. The key is needed when evicting (to delete from the HashMap).

### 2. LRU Cache Structure with Dummy Sentinel Nodes

```java
class LRUCache {
    int capacity;
    Map<Integer, LRUNode> cache;
    LRUNode head, tail;  // dummy sentinel nodes

    LRUCache(int capacity) {
        this.capacity = capacity;
        cache = new HashMap<>();
        head = new LRUNode(0, 0);  // dummy head (most recent side)
        tail = new LRUNode(0, 0);  // dummy tail (least recent side)
        head.next = tail;
        tail.prev = head;
    }
}
```

**Dummy Nodes:** Using dummy head and tail simplifies edge cases -- no null checks needed for first/last nodes.

### 3. Helper: Remove Node from List

```java
private void remove(LRUNode node) {
    node.prev.next = node.next;
    node.next.prev = node.prev;
}
```

**O(1) Removal:** Since we have references to `prev` and `next`, no scanning needed.

### 4. Helper: Insert Node at Front (After Head)

```java
private void insertFront(LRUNode node) {
    node.next = head.next;
    node.prev = head;
    head.next.prev = node;
    head.next = node;
}
```

**Most Recent Position:** The node right after the head dummy is the most recently used.

### 5. Get Operation

```java
public int get(int key) {
    if (!cache.containsKey(key)) return -1;
    LRUNode node = cache.get(key);
    remove(node);         // remove from current position
    insertFront(node);    // move to front (mark as recently used)
    return node.val;
}
```

### 6. Put Operation

```java
public void put(int key, int value) {
    if (cache.containsKey(key)) {
        LRUNode node = cache.get(key);
        node.val = value;
        remove(node);
        insertFront(node);
        return;
    }
    LRUNode node = new LRUNode(key, value);
    cache.put(key, node);
    insertFront(node);

    if (cache.size() > capacity) {
        LRUNode lru = tail.prev;  // node before dummy tail = least recently used
        remove(lru);
        cache.remove(lru.key);   // KEY IS NEEDED HERE
    }
}
```

### 7. Complete Implementation

```java
class LRUCache {
    private int capacity;
    private Map<Integer, LRUNode> cache = new HashMap<>();
    private LRUNode head = new LRUNode(0, 0);
    private LRUNode tail = new LRUNode(0, 0);

    LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) return -1;
        LRUNode node = cache.get(key);
        remove(node); insertFront(node);
        return node.val;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            LRUNode node = cache.get(key);
            node.val = value;
            remove(node); insertFront(node);
            return;
        }
        LRUNode node = new LRUNode(key, value);
        cache.put(key, node);
        insertFront(node);
        if (cache.size() > capacity) {
            LRUNode lru = tail.prev;
            remove(lru);
            cache.remove(lru.key);
        }
    }

    private void remove(LRUNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insertFront(LRUNode node) {
        node.next = head.next; node.prev = head;
        head.next.prev = node; head.next = node;
    }
}
```

### 8. Java Shortcut: LinkedHashMap

For quick implementations (not full from-scratch):

```java
class LRUCache extends LinkedHashMap<Integer, Integer> {
    private int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // accessOrder = true
        this.capacity = capacity;
    }

    public int get(int key) { return super.getOrDefault(key, -1); }
    public void put(int key, int value) { super.put(key, value); }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
```

---

## Visualization

```
Initial state (capacity=3):
  head <-> tail

After put(1, 1):
  head <-> [1:1] <-> tail
  cache: {1 -> node1}

After put(2, 2):
  head <-> [2:2] <-> [1:1] <-> tail

After get(1):
  head <-> [1:1] <-> [2:2] <-> tail    (1 moved to front)

After put(3, 3):
  head <-> [3:3] <-> [1:1] <-> [2:2] <-> tail

After put(4, 4) -- evicts key 2 (LRU):
  head <-> [4:4] <-> [3:3] <-> [1:1] <-> tail
  cache: {1, 3, 4}    (key 2 evicted)
```

---

## Common Pitfalls

1. **Forgetting to store the key in the node.** When evicting, you need the key to delete from the HashMap. Only storing the value is insufficient.

2. **Not using dummy head and tail nodes.** Without dummies, you need special cases for empty list, single node, etc. Dummies simplify all operations.

3. **Forgetting to update the map on put.** When updating an existing key, update `node.val` but don't add a new entry to the map.

4. **Incorrect pointer update order.** Doubly linked list pointer manipulation is error-prone. Draw diagrams and trace through.

5. **Removing from list but not from map (or vice versa).** Always maintain consistency between the list and the map.

6. **Wrong eviction condition.** Evict when `cache.size() > capacity` AFTER insertion, not before.

7. **Moving node to front on `put` when key exists.** Don't forget -- updating the value isn't enough; you must mark it as recently used by moving to front.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| LRU eviction policy | "LRU", "least recently used", "cache" | LRU Cache (146) |
| Other eviction policies | "LFU", "FIFO", "random" | LFU Cache (460) |
| Combined structures | "hash map + linked list", "O(1) operations" | All O(1) Data Structure (432) |

**Common Follow-Ups:**
- What if we want LFU (Least Frequently Used) instead?
- How would you handle multithreading? (Use `Collections.synchronizedMap()` or `ConcurrentHashMap` + `ReentrantReadWriteLock`)
- How would you implement time-based expiration (TTL)?

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | LRU Cache | Medium | HashMap + doubly linked list | 146 |
| 2 | LFU Cache | Hard | HashMap + doubly linked list + frequency | 460 |
| 3 | All O(1) Data Structure | Hard | Multiple HashMaps + doubly linked list | 432 |

---

## Quick Reference Card

```
Structure:   HashMap<Integer, LRUNode> + Doubly Linked List + Dummy Head/Tail
Node:        class LRUNode { int key, val; LRUNode prev, next; }
Get:         1. Lookup in map
             2. Move to front (recently used)
             3. Return value or -1
Put:         1. If exists: update value, move to front
             2. If new: add to map, insert at front
             3. If over capacity: remove tail.prev, delete from map

Remove:      node.prev.next = node.next; node.next.prev = node.prev;
InsertFront: node.next = head.next; node.prev = head;
             head.next.prev = node; head.next = node;

CRITICAL:    Store key in node (needed for eviction)
             Use dummy head/tail (simplifies edge cases)

Java shortcut: LinkedHashMap(capacity, 0.75f, true) + removeEldestEntry override
Time:        O(1) for get and put
Space:       O(capacity)
```

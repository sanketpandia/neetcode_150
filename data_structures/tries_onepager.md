# Tries (Prefix Trees) -- One-Pager

## Core Concept

A **trie** (pronounced "try") is a tree-like data structure specialized for storing and searching strings. Each node represents a single character, and paths from the root to nodes form prefixes of stored strings. The key advantage: search, insert, and prefix queries all run in **O(m)** time, where m is the length of the word -- completely independent of how many words are stored.

**How It Works:**
- The root node is empty (represents the empty string)
- Each edge is labeled with a character
- Each node may have up to 26 children (for lowercase English) or 256 (for ASCII) or more (for Unicode)
- Nodes are marked with an `IsEnd` flag to indicate a complete word ends there

For example, storing "cat", "car", and "dog":
```
       (root)
      /      \
     c        d
     |        |
     a        o
    / \       |
   t   r      g
  (E) (E)    (E)
```
(E) = `IsEnd` flag set

**Why Tries Excel:** In a hash map, searching for all words with a given prefix requires checking every word. In a trie, you walk down the prefix path once, then collect all descendants. Tries are the go-to for autocomplete, spell checkers, and IP routing tables.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert word | O(m) | m = length of word; create nodes as needed |
| Search exact word | O(m) | Walk path, check `IsEnd` at final node |
| Search prefix | O(m) | Walk path, don't need `IsEnd` check |
| Delete word | O(m) | Mark `IsEnd` as false; optionally prune empty nodes |
| Autocomplete | O(m + k) | m = prefix length, k = total chars in results |
| Count words with prefix | O(m + n) | m = prefix length, n = nodes in subtree |
| Longest common prefix | O(m) | Walk until branching or end |

**Space Complexity:**
- Worst case: O(ALPHABET_SIZE * N * M) where N = number of words, M = average length
- Practical case: Much better due to prefix sharing
- Using `map[rune]*TrieNode` vs `[26]*TrieNode`: map is flexible but slower and uses more memory; fixed array is faster for known alphabets

---

## Implementation Patterns

### 1. Trie Node and Structure

```go
type TrieNode struct {
    Children map[rune]*TrieNode  // or [26]*TrieNode for lowercase ASCII
    IsEnd    bool                 // marks a complete word
}

type Trie struct {
    Root *TrieNode
}

func NewTrie() *Trie {
    return &Trie{Root: &TrieNode{Children: make(map[rune]*TrieNode)}}
}
```

**Array vs Map for Children:**
- `[26]*TrieNode`: Faster, fixed memory, only for lowercase a-z
- `map[rune]*TrieNode`: Flexible for any characters, sparse storage
- Choose based on constraints: interviews often allow assuming lowercase English

### 2. Insert Word

```go
func (t *Trie) Insert(word string) {
    node := t.Root
    for _, ch := range word {
        if _, ok := node.Children[ch]; !ok {
            node.Children[ch] = &TrieNode{Children: make(map[rune]*TrieNode)}
        }
        node = node.Children[ch]
    }
    node.IsEnd = true  // mark the end of the word
}
```

### 3. Search Exact Word

```go
func (t *Trie) Search(word string) bool {
    node := t.Root
    for _, ch := range word {
        if _, ok := node.Children[ch]; !ok {
            return false
        }
        node = node.Children[ch]
    }
    return node.IsEnd  // must be a complete word, not just a prefix
}
```

### 4. Search Prefix

Check if any word in the trie starts with the given prefix.

```go
func (t *Trie) StartsWith(prefix string) bool {
    node := t.Root
    for _, ch := range prefix {
        if _, ok := node.Children[ch]; !ok {
            return false
        }
        node = node.Children[ch]
    }
    return true  // found the prefix path
}
```

### 5. Delete Word

Mark `IsEnd` as false. Optionally prune nodes with no children.

```go
func (t *Trie) Delete(word string) bool {
    return deleteHelper(t.Root, word, 0)
}

func deleteHelper(node *TrieNode, word string, index int) bool {
    if index == len(word) {
        if !node.IsEnd {
            return false  // word not found
        }
        node.IsEnd = false
        // Return true if no children (can be deleted)
        return len(node.Children) == 0
    }

    ch := rune(word[index])
    child, ok := node.Children[ch]
    if !ok {
        return false  // word not found
    }

    shouldDeleteChild := deleteHelper(child, word, index+1)

    if shouldDeleteChild {
        delete(node.Children, ch)
        // Return true if current node is not end of another word and has no children
        return !node.IsEnd && len(node.Children) == 0
    }

    return false
}
```

### 6. Word Search with Wildcards

Support `.` as a wildcard matching any character.

```go
func (t *Trie) SearchWithWildcard(word string) bool {
    return searchHelper(t.Root, word, 0)
}

func searchHelper(node *TrieNode, word string, index int) bool {
    if index == len(word) {
        return node.IsEnd
    }

    ch := rune(word[index])
    if ch == '.' {
        // Try all possible children
        for _, child := range node.Children {
            if searchHelper(child, word, index+1) {
                return true
            }
        }
        return false
    } else {
        if child, ok := node.Children[ch]; ok {
            return searchHelper(child, word, index+1)
        }
        return false
    }
}
```

### 7. Autocomplete (All Words with Prefix)

Find the prefix node, then DFS to collect all words.

```go
func (t *Trie) Autocomplete(prefix string) []string {
    node := t.Root
    for _, ch := range prefix {
        if _, ok := node.Children[ch]; !ok {
            return nil  // prefix not found
        }
        node = node.Children[ch]
    }

    results := []string{}
    collectWords(node, prefix, &results)
    return results
}

func collectWords(node *TrieNode, currentWord string, results *[]string) {
    if node.IsEnd {
        *results = append(*results, currentWord)
    }
    for ch, child := range node.Children {
        collectWords(child, currentWord+string(ch), results)
    }
}
```

### 8. Word Search II (Backtracking with Trie)

Use a trie to store the dictionary, then backtrack on the grid.

```go
func findWords(board [][]byte, words []string) []string {
    // Build trie from words
    trie := NewTrie()
    for _, word := range words {
        trie.Insert(word)
    }

    results := make(map[string]bool)
    m, n := len(board), len(board[0])

    var backtrack func(i, j int, node *TrieNode, path string)
    backtrack = func(i, j int, node *TrieNode, path string) {
        if i < 0 || i >= m || j < 0 || j >= n {
            return
        }
        ch := rune(board[i][j])
        if ch == '#' || node.Children[ch] == nil {
            return
        }

        node = node.Children[ch]
        path += string(ch)

        if node.IsEnd {
            results[path] = true
            // Don't return; continue to find longer words
        }

        // Mark as visited
        board[i][j] = '#'
        backtrack(i+1, j, node, path)
        backtrack(i-1, j, node, path)
        backtrack(i, j+1, node, path)
        backtrack(i, j-1, node, path)
        board[i][j] = byte(ch)  // unmark
    }

    for i := 0; i < m; i++ {
        for j := 0; j < n; j++ {
            backtrack(i, j, trie.Root, "")
        }
    }

    result := []string{}
    for word := range results {
        result = append(result, word)
    }
    return result
}
```

---

## When to Use

| Scenario | Use Trie? | Alternative |
|----------|-----------|-------------|
| Autocomplete / prefix search | Yes | Hash map (inefficient for prefix queries) |
| Dictionary with prefix queries | Yes | Sorted array + binary search (less efficient) |
| Spell checker | Yes | - |
| IP routing (longest prefix match) | Yes | - |
| Word search in grid | Yes | Brute force DFS with hash set (slower) |
| Fixed set of words, no prefix queries | No | Hash set (simpler, faster lookup) |
| Substring search (not prefix) | No | Use suffix tree or suffix array |
| Single word lookup | No | Hash map (O(1) vs O(m)) |

---

## Common Pitfalls

1. **Forgetting `IsEnd` flag.** "app" and "apple" are different words. Without `IsEnd`, you can't distinguish whether a path is a prefix or a complete word.

2. **Using `[26]*TrieNode` with non-lowercase input.** If the input has uppercase or special characters, this array indexing breaks. Use `map[rune]*TrieNode` for flexibility.

3. **Not initializing children map.** When creating a new node, you must initialize `Children: make(map[rune]*TrieNode)`. Forgetting this causes nil map writes.

4. **Memory explosion with sparse tries.** If you store unrelated words (e.g., "a", "zzz"), a fixed array wastes memory. Use a map for sparse data.

5. **Confusing search vs prefix search.** `Search("app")` on trie containing "apple" should return false (not a complete word). `StartsWith("app")` returns true.

6. **Not handling empty string.** Decide if empty string is valid. If yes, check `root.IsEnd`.

7. **Infinite loops in backtracking.** In Word Search II, mark cells as visited (`board[i][j] = '#'`) and unmark after recursion. Forgetting to unmark causes bugs.

---

## Interview Relevance

Tries are less common than arrays or trees but appear in high-value problems.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Basic trie operations | "implement trie", "prefix", "insert/search" | Implement Trie (Prefix Tree) |
| Wildcard search | "wildcard", "regex", "pattern matching" | Design Add and Search Words Data Structure |
| Word search in grid | "word search", "grid", "dictionary" | Word Search II |
| Autocomplete | "autocomplete", "suggestions", "prefix" | Design Search Autocomplete System |
| Longest prefix | "longest common prefix", "shared prefix" | Longest Common Prefix |
| Replace words | "replace", "root", "dictionary" | Replace Words |

**Interview Insight:** If the problem mentions "prefix", immediately consider a trie. Tries turn O(n * m) string comparisons into O(m) path walks.

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Implement Trie (Prefix Tree) | Medium | Basic insert/search/startsWith | 208 |
| 2 | Design Add and Search Words | Medium | Wildcard search with `.` | 211 |
| 3 | Word Search II | Hard | Backtracking + trie | 212 |
| 4 | Longest Word in Dictionary | Medium | Build trie, DFS for longest | 720 |
| 5 | Replace Words | Medium | Trie to find shortest root | 648 |
| 6 | Implement Magic Dictionary | Medium | Trie with single-char mismatch | 676 |

Start with problem 1 to master basic operations. Problem 2 adds wildcards. Problem 3 is the classic trie + backtracking combo, testing both trie and DFS skills.

---

## Array vs Map Implementation

```go
// Map-based (flexible, slower, sparse-friendly)
type TrieNode struct {
    Children map[rune]*TrieNode
    IsEnd    bool
}

// Array-based (fast, fixed, lowercase a-z only)
type TrieNode struct {
    Children [26]*TrieNode
    IsEnd    bool
}

// Convert char to index: ch - 'a'
// Example:
if node.Children[ch-'a'] == nil {
    node.Children[ch-'a'] = &TrieNode{}
}
node = node.Children[ch-'a']
```

**When to use which:**
- Map: Unicode support, sparse data, unknown alphabet size
- Array: Maximum performance, lowercase English only

---

## Quick Reference Card

```
Define:     type TrieNode struct { Children map[rune]*TrieNode; IsEnd bool }
Create:     trie := &Trie{Root: &TrieNode{Children: make(map[rune]*TrieNode)}}
Insert:     Walk path, create nodes as needed, set IsEnd=true at end
Search:     Walk path, check IsEnd at final node
Prefix:     Walk path, return true if path exists (ignore IsEnd)
Delete:     Set IsEnd=false, optionally prune childless nodes
Wildcard:   DFS with '.' matching any character

Space:      O(ALPHABET_SIZE * N * M) worst case, better with shared prefixes
Time:       O(m) for all operations (m = word length)
```

---

> **Key Insight:** Tries are the ultimate prefix-search structure. The cost is O(m) regardless of dictionary size. When you see "prefix", "autocomplete", or "word search with dictionary", think trie. Don't forget the `IsEnd` flag to distinguish prefixes from complete words.

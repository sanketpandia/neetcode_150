# Tries (Prefix Trees) -- One-Pager

## Core Concept

A **trie** (pronounced "try") is a tree-like data structure specialized for storing and searching strings. Each node represents a single character, and paths from the root to nodes form prefixes of stored strings. The key advantage: search, insert, and prefix queries all run in **O(m)** time, where m is the length of the word -- completely independent of how many words are stored.

**How It Works:**
- The root node is empty (represents the empty string)
- Each edge is labeled with a character
- Each node may have up to 26 children (for lowercase English) or more for other alphabets
- Nodes are marked with an `isEnd` flag to indicate a complete word ends there

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
(E) = `isEnd` flag set

**Why Tries Excel:** In a hash map, searching for all words with a given prefix requires checking every word. In a trie, you walk down the prefix path once, then collect all descendants. Tries are the go-to for autocomplete, spell checkers, and IP routing.

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Insert word | O(m) | m = length of word; create nodes as needed |
| Search exact word | O(m) | Walk path, check `isEnd` at final node |
| Search prefix | O(m) | Walk path, don't need `isEnd` check |
| Delete word | O(m) | Mark `isEnd` as false; optionally prune empty nodes |
| Autocomplete | O(m + k) | m = prefix length, k = total chars in results |
| Count words with prefix | O(m + n) | n = nodes in subtree |
| Longest common prefix | O(m) | Walk until branching or end |

**Space Complexity:**
- Worst case: O(ALPHABET_SIZE × N × M) where N = number of words, M = average length
- Practical case: Much better due to prefix sharing
- `TrieNode[]` (array): faster, less memory for known alphabets
- `Map<Character, TrieNode>`: flexible, sparse-friendly

---

## Implementation Patterns

### 1. Trie Node and Structure

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];  // for lowercase a-z
    boolean isEnd = false;
}

class Trie {
    private TrieNode root = new TrieNode();
}
```

**Array vs Map for children:**
- `TrieNode[26]`: faster, fixed memory, only for lowercase a-z
- `Map<Character, TrieNode>`: flexible for any characters, sparse storage
- Choose based on constraints: interviews often allow assuming lowercase English

### 2. Insert Word

```java
void insert(String word) {
    TrieNode node = root;
    for (char c : word.toCharArray()) {
        int idx = c - 'a';
        if (node.children[idx] == null) {
            node.children[idx] = new TrieNode();
        }
        node = node.children[idx];
    }
    node.isEnd = true;  // mark the end of the word
}
```

### 3. Search Exact Word

```java
boolean search(String word) {
    TrieNode node = root;
    for (char c : word.toCharArray()) {
        int idx = c - 'a';
        if (node.children[idx] == null) return false;
        node = node.children[idx];
    }
    return node.isEnd;  // must be a complete word, not just a prefix
}
```

### 4. Search Prefix

Check if any word in the trie starts with the given prefix.

```java
boolean startsWith(String prefix) {
    TrieNode node = root;
    for (char c : prefix.toCharArray()) {
        int idx = c - 'a';
        if (node.children[idx] == null) return false;
        node = node.children[idx];
    }
    return true;  // found the prefix path
}
```

### 5. Complete Trie Implementation (LeetCode 208)

```java
class Trie {
    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) node.children[i] = new TrieNode();
            node = node.children[i];
        }
        node.isEnd = true;
    }

    public boolean search(String word) {
        TrieNode node = find(word);
        return node != null && node.isEnd;
    }

    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }

    private TrieNode find(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int i = c - 'a';
            if (node.children[i] == null) return null;
            node = node.children[i];
        }
        return node;
    }
}
```

### 6. Word Search with Wildcards

Support `.` as a wildcard matching any character (LeetCode 211).

```java
boolean searchWithWildcard(String word) {
    return searchHelper(root, word, 0);
}

boolean searchHelper(TrieNode node, String word, int index) {
    if (index == word.length()) return node.isEnd;
    char c = word.charAt(index);
    if (c == '.') {
        // Try all possible children
        for (TrieNode child : node.children) {
            if (child != null && searchHelper(child, word, index + 1)) return true;
        }
        return false;
    } else {
        TrieNode child = node.children[c - 'a'];
        return child != null && searchHelper(child, word, index + 1);
    }
}
```

### 7. Autocomplete (All Words with Prefix)

Find the prefix node, then DFS to collect all words.

```java
List<String> autocomplete(String prefix) {
    TrieNode node = root;
    for (char c : prefix.toCharArray()) {
        int i = c - 'a';
        if (node.children[i] == null) return new ArrayList<>();
        node = node.children[i];
    }
    List<String> results = new ArrayList<>();
    collectWords(node, new StringBuilder(prefix), results);
    return results;
}

void collectWords(TrieNode node, StringBuilder current, List<String> results) {
    if (node.isEnd) results.add(current.toString());
    for (int i = 0; i < 26; i++) {
        if (node.children[i] != null) {
            current.append((char) ('a' + i));
            collectWords(node.children[i], current, results);
            current.deleteCharAt(current.length() - 1);  // backtrack
        }
    }
}
```

### 8. Word Search II (Backtracking with Trie)

Use a trie to store the dictionary, then backtrack on the grid.

```java
List<String> findWords(char[][] board, String[] words) {
    Trie trie = new Trie();
    for (String word : words) trie.insert(word);

    Set<String> found = new HashSet<>();
    int m = board.length, n = board[0].length;

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            backtrack(board, i, j, trie.root, new StringBuilder(), found);
        }
    }
    return new ArrayList<>(found);
}

void backtrack(char[][] board, int i, int j, TrieNode node, StringBuilder path, Set<String> found) {
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return;
    char c = board[i][j];
    if (c == '#' || node.children[c - 'a'] == null) return;

    node = node.children[c - 'a'];
    path.append(c);
    if (node.isEnd) found.add(path.toString());

    board[i][j] = '#';  // mark as visited
    int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
    for (int[] d : dirs) backtrack(board, i + d[0], j + d[1], node, path, found);
    board[i][j] = c;    // unmark
    path.deleteCharAt(path.length() - 1);  // backtrack
}
```

---

## When to Use

| Scenario | Use Trie? | Alternative |
|----------|-----------|-------------|
| Autocomplete / prefix search | Yes | HashMap (inefficient for prefix queries) |
| Dictionary with prefix queries | Yes | Sorted array + binary search |
| Spell checker | Yes | -- |
| Word search in grid | Yes | Brute force DFS with hash set (slower) |
| Fixed set of words, no prefix queries | No | HashSet (simpler, faster lookup) |
| Substring search (not prefix) | No | Use suffix tree or KMP |
| Single word lookup | No | HashMap (O(1) vs O(m)) |

---

## Common Pitfalls

1. **Forgetting `isEnd` flag.** "app" and "apple" are different words. Without `isEnd`, you can't distinguish whether a path is a prefix or a complete word.

2. **Using `TrieNode[26]` with non-lowercase input.** If the input has uppercase or special characters, `c - 'a'` gives wrong or negative indices. Use `Map<Character, TrieNode>` for flexibility.

3. **Not backtracking `StringBuilder`.** In autocomplete or Word Search II, remember to remove the last character before returning from recursion (`sb.deleteCharAt(sb.length() - 1)`).

4. **Confusing `search` vs `startsWith`.** `search("app")` on a trie containing only "apple" returns `false` (not a complete word). `startsWith("app")` returns `true`.

5. **Not handling empty string.** Decide if empty string is valid. If yes, check `root.isEnd`.

6. **Forgetting to unmark visited cells.** In Word Search II, mark cells as visited (`'#'`) and unmark after recursion. Forgetting to unmark causes missing valid paths.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Basic trie operations | "implement trie", "prefix", "insert/search" | Implement Trie (Prefix Tree) |
| Wildcard search | "wildcard", "regex", "pattern matching" | Design Add and Search Words Data Structure |
| Word search in grid | "word search", "grid", "dictionary" | Word Search II |
| Autocomplete | "autocomplete", "suggestions", "prefix" | Design Search Autocomplete System |
| Replace words | "replace", "root", "dictionary" | Replace Words |

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Implement Trie (Prefix Tree) | Medium | Basic insert/search/startsWith | 208 |
| 2 | Design Add and Search Words | Medium | Wildcard search with `.` | 211 |
| 3 | Word Search II | Hard | Backtracking + trie | 212 |
| 4 | Longest Word in Dictionary | Medium | Build trie, DFS for longest | 720 |
| 5 | Replace Words | Medium | Trie to find shortest root | 648 |

Start with problem 1 to master basic operations. Problem 2 adds wildcards. Problem 3 is the classic trie + backtracking combo.

---

## Quick Reference Card

```
Node:       class TrieNode { TrieNode[] children = new TrieNode[26]; boolean isEnd; }
Create:     TrieNode root = new TrieNode();
Insert:     Walk path using c - 'a' index, create nodes as needed, set isEnd=true
Search:     Walk path, return node.isEnd at final node
Prefix:     Walk path, return true if path exists (ignore isEnd)
Wildcard:   DFS with '.' trying all non-null children
Autocomplete: Walk to prefix node, DFS with StringBuilder (backtrack on return)

Space:      O(ALPHABET_SIZE * N * M) worst, better with shared prefixes
Time:       O(m) for all operations (m = word length)
```

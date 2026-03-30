# Monotonic Stack / Queue -- One-Pager

## Core Concept

A **monotonic stack** maintains elements in strictly increasing or decreasing order. When a new element violates this order, elements are popped from the stack until the invariant is restored. A **monotonic deque** (double-ended queue) extends this to support operations at both ends, commonly used for sliding window problems.

**Why Monotonic Structures?** Many problems ask: "For each element, what is the next (or previous) greater/smaller element?" A naive approach scans backwards for each element (O(n²)). A monotonic stack solves this in O(n) total time because each element is pushed and popped at most once.

In Java, use `Deque<Integer> stack = new ArrayDeque<>()` for both stacks and deques. `ArrayDeque` is faster than `LinkedList` for this purpose.

**Common Patterns:**
- **Next Greater Element:** Monotonic decreasing stack (pop when current > stack top)
- **Previous Greater Element:** Monotonic decreasing stack (process right-to-left)
- **Sliding Window Max/Min:** Monotonic deque (maintain max/min at front)
- **Largest Rectangle in Histogram:** Monotonic increasing stack (track heights)

---

## Time Complexity Table

| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Push to stack/deque | O(1) amortized | May pop multiple elements, but total pops ≤ n |
| Pop from stack/deque | O(1) | Standard stack/deque operation |
| Overall for n elements | O(n) | Each element pushed once, popped once |
| Sliding window max/min | O(n) total | O(1) amortized per window |

**Space Complexity:** O(n) in worst case (all elements on stack/deque).

**Why O(n) total?** Each element can be pushed at most once (n pushes total) and popped at most once (n pops total). Therefore, total operations = O(n).

---

## Implementation Patterns

### 1. Next Greater Element (to the right)

For each element, find the next element to its right that is greater.

```java
int[] nextGreaterElements(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);              // default: no greater element
    Deque<Integer> stack = new ArrayDeque<>();  // store indices, monotonic decreasing by value

    for (int i = 0; i < n; i++) {
        // Pop elements smaller than current (they found their answer)
        while (!stack.isEmpty() && nums[i] > nums[stack.peek()]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}
```

**Pattern:** Monotonic decreasing stack. When current element is larger, it's the "next greater" for all smaller elements in the stack.

### 2. Next Greater Element (circular array)

Handle circular arrays by processing twice (or using modulo).

```java
int[] nextGreaterElementsCircular(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();

    // Process array twice to handle circular nature
    for (int i = 0; i < 2 * n; i++) {
        int idx = i % n;
        while (!stack.isEmpty() && nums[idx] > nums[stack.peek()]) {
            result[stack.pop()] = nums[idx];
        }
        if (i < n) stack.push(idx);  // only push in first pass
    }
    return result;
}
```

### 3. Daily Temperatures (Days Until Warmer)

Classic next greater element variant -- store indices, compute distance.

```java
int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>();  // indices, monotonic decreasing by temperature

    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
            int idx = stack.pop();
            result[idx] = i - idx;  // days until warmer
        }
        stack.push(i);
    }
    return result;
}
```

### 4. Largest Rectangle in Histogram

Use monotonic increasing stack to track heights.

```java
int largestRectangleArea(int[] heights) {
    Deque<Integer> stack = new ArrayDeque<>();  // indices, monotonic increasing by height
    int maxArea = 0;
    int n = heights.length;

    for (int i = 0; i <= n; i++) {
        int h = (i == n) ? 0 : heights[i];  // sentinel 0 at end to flush stack
        while (!stack.isEmpty() && h < heights[stack.peek()]) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        stack.push(i);
    }
    return maxArea;
}
```

**Insight:** When we pop a height, it can extend as a rectangle from the previous smaller height to the current smaller height.

### 5. Sliding Window Maximum (Monotonic Deque)

Maintain max at front of deque, remove elements outside window from front, remove smaller elements from back.

```java
int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();  // indices, monotonic decreasing by value

    for (int i = 0; i < n; i++) {
        // Remove indices outside current window from front
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        // Remove smaller elements from back (they can never be max)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        deque.offerLast(i);

        // Start recording results once we have a full window
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    return result;
}
```

**Pattern:** Deque front has the maximum. Back maintains decreasing order.

### 6. Trapping Rain Water (Monotonic Stack)

Calculate trapped water by finding boundaries.

```java
int trap(int[] height) {
    Deque<Integer> stack = new ArrayDeque<>();  // indices, monotonic decreasing by height
    int water = 0;

    for (int i = 0; i < height.length; i++) {
        while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
            int top = stack.pop();
            if (stack.isEmpty()) break;  // no left boundary
            int left = stack.peek();
            int width = i - left - 1;
            int boundedHeight = Math.min(height[left], height[i]) - height[top];
            water += width * boundedHeight;
        }
        stack.push(i);
    }
    return water;
}
```

---

## ArrayDeque as Stack vs Deque

`ArrayDeque<Integer>` serves both roles in Java:

```java
// As a stack (LIFO)
Deque<Integer> stack = new ArrayDeque<>();
stack.push(val);           // addFirst
stack.pop();               // removeFirst
stack.peek();              // peekFirst

// As a deque (both ends)
Deque<Integer> deque = new ArrayDeque<>();
deque.offerFirst(val);     // add to front
deque.offerLast(val);      // add to back
deque.pollFirst();         // remove from front
deque.pollLast();          // remove from back
deque.peekFirst();         // view front
deque.peekLast();          // view back
```

**Note:** `push`/`pop`/`peek` on `ArrayDeque` operate on the front (head), making it LIFO (stack). Use `offerLast`/`pollLast` explicitly when you need the back.

---

## When to Use

| Scenario | Use Monotonic Stack/Deque? |
|----------|---------------------------|
| Next/previous greater/smaller element | Yes (stack) |
| Sliding window max/min | Yes (deque) |
| Histogram problems (largest rectangle) | Yes (stack) |
| Stock span problem | Yes (stack) |
| Trapping rain water | Yes (stack, or two pointers) |
| Range queries with dynamic updates | No -- use segment tree |
| Static range max/min | No -- use sparse table |

---

## Common Pitfalls

1. **Wrong monotonic direction.** For "next greater", use monotonic decreasing (pop smaller). For "next smaller", use monotonic increasing (pop larger).

2. **Storing values instead of indices.** Indices are more versatile -- you can compute distances, access original values, and handle duplicates.

3. **Forgetting to initialize result.** Set default values (e.g., `-1` for "not found") before processing with `Arrays.fill()`.

4. **Off-by-one in sliding window.** Start recording results at `i >= k-1`, not `i >= k`.

5. **Incorrect width calculation in histogram.** Width is `i - previous_smaller - 1`, not just `i - stack_top`.

6. **Mixing `push`/`pop` with `offerLast`/`pollFirst` on the same deque.** Be explicit: `push` = `addFirst`, `pop` = `removeFirst`. When using as a deque, always use the directional methods.

7. **Thinking it's O(n²).** The nested while loop is deceptive -- each element is pushed and popped exactly once, making it O(n) total.

---

## Interview Relevance

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Next greater/smaller | "next larger", "first greater", "next warmer" | Next Greater Element, Daily Temperatures |
| Sliding window max/min | "sliding window", "maximum in window" | Sliding Window Maximum |
| Histogram/rectangle | "largest rectangle", "histogram", "max area" | Largest Rectangle in Histogram |
| Trapping water | "trapped water", "elevation map" | Trapping Rain Water |
| Stock span | "span", "consecutive days" | Online Stock Span |

---

## Practice Problems

| # | Problem | Difficulty | Key Pattern | LeetCode # |
|---|---------|------------|-------------|------------|
| 1 | Next Greater Element I | Easy | Basic monotonic stack | 496 |
| 2 | Daily Temperatures | Medium | Monotonic decreasing stack | 739 |
| 3 | Largest Rectangle in Histogram | Hard | Monotonic increasing stack | 84 |
| 4 | Sliding Window Maximum | Hard | Monotonic deque | 239 |
| 5 | Trapping Rain Water | Hard | Monotonic stack (or two pointers) | 42 |
| 6 | Sum of Subarray Minimums | Medium | Contribution of each element | 907 |
| 7 | Online Stock Span | Medium | Monotonic decreasing stack | 901 |

---

## Quick Reference Card

```
Next Greater:
  Deque<Integer> stack = new ArrayDeque<>();  // indices, monotonic decreasing
  for (int i = 0; i < n; i++) {
    while (!stack.isEmpty() && nums[i] > nums[stack.peek()])
      result[stack.pop()] = nums[i];
    stack.push(i);
  }

Sliding Window Max:
  Deque<Integer> deque = new ArrayDeque<>();  // indices, monotonic decreasing
  for (int i = 0; i < n; i++) {
    while (!deque.isEmpty() && deque.peekFirst() <= i - k) deque.pollFirst();
    while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) deque.pollLast();
    deque.offerLast(i);
    if (i >= k - 1) result[i - k + 1] = nums[deque.peekFirst()];
  }

Time: O(n) amortized -- each element pushed and popped exactly once
Space: O(n) worst case
```

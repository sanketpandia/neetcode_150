# Monotonic Stack / Queue -- One-Pager

## Core Concept

A **monotonic stack** maintains elements in strictly increasing or decreasing order. When a new element violates this order, elements are popped from the stack until the invariant is restored. A **monotonic deque** (double-ended queue) extends this to support operations at both ends, commonly used for sliding window problems.

**Why Monotonic Structures?** Many problems ask: "For each element, what is the next (or previous) greater/smaller element?" A naive approach scans backwards for each element (O(n²)). A monotonic stack solves this in O(n) total time because each element is pushed and popped at most once.

**Key Insight:** The amortized O(1) per element comes from the observation that across all operations, each element enters and leaves the stack exactly once. Even though a single push might pop multiple elements, the total pops across all pushes is bounded by n.

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

**Why O(n) total?** Consider processing n elements. Each element can be pushed at most once (n pushes total) and popped at most once (n pops total). Therefore, total operations = O(n).

---

## Implementation Patterns

### 1. Next Greater Element (to the right)

For each element, find the next element to its right that is greater.

```go
func nextGreaterElements(nums []int) []int {
    n := len(nums)
    result := make([]int, n)
    for i := range result {
        result[i] = -1  // default: no greater element
    }
    stack := []int{}  // store indices, monotonic decreasing by value

    for i := 0; i < n; i++ {
        // Pop elements smaller than current (they found their answer)
        for len(stack) > 0 && nums[i] > nums[stack[len(stack)-1]] {
            idx := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[idx] = nums[i]
        }
        stack = append(stack, i)
    }

    return result
}
```

**Pattern:** Monotonic decreasing stack. When current element is larger, it's the "next greater" for all smaller elements in the stack.

### 2. Next Greater Element (circular array)

Handle circular arrays by processing twice (or using modulo).

```go
func nextGreaterElementsCircular(nums []int) []int {
    n := len(nums)
    result := make([]int, n)
    for i := range result {
        result[i] = -1
    }
    stack := []int{}

    // Process array twice to handle circular nature
    for i := 0; i < 2*n; i++ {
        idx := i % n
        for len(stack) > 0 && nums[idx] > nums[stack[len(stack)-1]] {
            top := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[top] = nums[idx]
        }
        // Only push in first pass to avoid duplicate processing
        if i < n {
            stack = append(stack, idx)
        }
    }

    return result
}
```

### 3. Previous Greater Element

Process right-to-left or use separate stack.

```go
func previousGreaterElements(nums []int) []int {
    n := len(nums)
    result := make([]int, n)
    for i := range result {
        result[i] = -1
    }
    stack := []int{}  // monotonic decreasing

    // Process right to left
    for i := n - 1; i >= 0; i-- {
        for len(stack) > 0 && nums[i] > nums[stack[len(stack)-1]] {
            idx := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[idx] = nums[i]
        }
        stack = append(stack, i)
    }

    return result
}
```

### 4. Daily Temperatures (Days Until Warmer)

Classic next greater element variant.

```go
func dailyTemperatures(temperatures []int) []int {
    n := len(temperatures)
    result := make([]int, n)
    stack := []int{}  // indices, monotonic decreasing by temperature

    for i := 0; i < n; i++ {
        for len(stack) > 0 && temperatures[i] > temperatures[stack[len(stack)-1]] {
            idx := stack[len(stack)-1]
            stack = stack[:len(stack)-1]
            result[idx] = i - idx  // days until warmer
        }
        stack = append(stack, i)
    }

    return result
}
```

### 5. Largest Rectangle in Histogram

Use monotonic increasing stack to track heights.

```go
func largestRectangleArea(heights []int) int {
    stack := []int{}  // indices, monotonic increasing by height
    maxArea := 0
    heights = append(heights, 0)  // sentinel to flush stack at end

    for i := 0; i < len(heights); i++ {
        // Pop taller bars and calculate area
        for len(stack) > 0 && heights[i] < heights[stack[len(stack)-1]] {
            h := heights[stack[len(stack)-1]]
            stack = stack[:len(stack)-1]

            // Width: from element after previous stack top to current (exclusive)
            width := i
            if len(stack) > 0 {
                width = i - stack[len(stack)-1] - 1
            }

            area := h * width
            if area > maxArea {
                maxArea = area
            }
        }
        stack = append(stack, i)
    }

    return maxArea
}
```

**Insight:** When we pop a height, it can extend as a rectangle from the previous smaller height to the current smaller height.

### 6. Sliding Window Maximum (Monotonic Deque)

Maintain max at front of deque, remove elements outside window from front, remove smaller elements from back.

```go
func maxSlidingWindow(nums []int, k int) []int {
    deque := []int{}  // indices, monotonic decreasing by value
    result := []int{}

    for i := 0; i < len(nums); i++ {
        // Remove indices outside current window from front
        for len(deque) > 0 && deque[0] <= i-k {
            deque = deque[1:]
        }

        // Remove smaller elements from back (they can never be max)
        for len(deque) > 0 && nums[deque[len(deque)-1]] < nums[i] {
            deque = deque[:len(deque)-1]
        }

        deque = append(deque, i)

        // Start recording results once we have a full window
        if i >= k-1 {
            result = append(result, nums[deque[0]])
        }
    }

    return result
}
```

**Pattern:** Deque front has the maximum. Back maintains decreasing order.

### 7. Trapping Rain Water (Monotonic Stack)

Calculate trapped water by finding boundaries.

```go
func trap(height []int) int {
    stack := []int{}  // indices, monotonic decreasing by height
    water := 0

    for i := 0; i < len(height); i++ {
        // Found a higher or equal bar: calculate trapped water
        for len(stack) > 0 && height[i] > height[stack[len(stack)-1]] {
            top := stack[len(stack)-1]
            stack = stack[:len(stack)-1]

            if len(stack) == 0 {
                break  // no left boundary
            }

            left := stack[len(stack)-1]
            width := i - left - 1
            boundedHeight := min(height[left], height[i]) - height[top]
            water += width * boundedHeight
        }
        stack = append(stack, i)
    }

    return water
}

func min(a, b int) int {
    if a < b {
        return a
    }
    return b
}
```

### 8. Sum of Subarray Minimums

Use monotonic stack to find contribution of each element as minimum.

```go
func sumSubarrayMins(arr []int) int {
    mod := int(1e9 + 7)
    n := len(arr)
    stack := []int{}

    // For each element, find how many subarrays it is the minimum of
    left := make([]int, n)   // distance to previous smaller element
    right := make([]int, n)  // distance to next smaller element

    // Calculate left distances
    for i := 0; i < n; i++ {
        for len(stack) > 0 && arr[stack[len(stack)-1]] > arr[i] {
            stack = stack[:len(stack)-1]
        }
        if len(stack) == 0 {
            left[i] = i + 1  // no smaller element to the left
        } else {
            left[i] = i - stack[len(stack)-1]
        }
        stack = append(stack, i)
    }

    // Calculate right distances
    stack = []int{}
    for i := n - 1; i >= 0; i-- {
        for len(stack) > 0 && arr[stack[len(stack)-1]] >= arr[i] {
            stack = stack[:len(stack)-1]
        }
        if len(stack) == 0 {
            right[i] = n - i
        } else {
            right[i] = stack[len(stack)-1] - i
        }
        stack = append(stack, i)
    }

    // Calculate sum
    result := 0
    for i := 0; i < n; i++ {
        result = (result + arr[i]*left[i]*right[i]) % mod
    }

    return result
}
```

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
| Static range max/min | No -- use sparse table or RMQ |

---

## Common Pitfalls

1. **Wrong monotonic direction.** For "next greater", use monotonic decreasing (pop smaller). For "next smaller", use monotonic increasing (pop larger). Confusing these breaks the logic.

2. **Storing values instead of indices.** Indices are more versatile -- you can compute distances, access original values, and avoid duplicates issues.

3. **Forgetting to initialize result.** Set default values (e.g., -1 for "not found") before processing.

4. **Off-by-one in sliding window.** Start recording results at `i >= k-1`, not `i >= k`.

5. **Not handling edge cases.** Empty input, k=1, k=n all need testing.

6. **Incorrect width calculation in histogram.** Width is `i - previous_smaller - 1`, not just `i - stack_top`.

7. **Mixing front/back operations on deque.** Be clear: remove from front for out-of-window, remove from back for maintaining order.

8. **Thinking it's O(n²).** The nested loop is deceptive -- each element is pushed and popped exactly once, making it O(n) total.

---

## Interview Relevance

Monotonic stacks are a powerful pattern that separates candidates who recognize it from those who don't.

| Pattern | Signal Words | Example Problems |
|---------|--------------|------------------|
| Next greater/smaller | "next larger", "first greater", "next warmer" | Next Greater Element, Daily Temperatures |
| Sliding window max/min | "sliding window", "maximum in window" | Sliding Window Maximum |
| Histogram/rectangle | "largest rectangle", "histogram", "max area" | Largest Rectangle in Histogram, Maximal Rectangle |
| Trapping water | "trapped water", "elevation map" | Trapping Rain Water |
| Stock span | "span", "consecutive days" | Online Stock Span |
| Subarray min/max sums | "sum of subarray minimums" | Sum of Subarray Minimums |

**Interview Insight:** If the problem asks for "next greater/smaller" or "sliding window max/min", immediately think monotonic stack/deque. The O(n²) brute force is obvious; the O(n) monotonic solution is what interviewers want to see.

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

Start with 1-2 to understand the core pattern. Problem 3 is a classic that every candidate should know. Problems 4-5 are hard but become manageable with the monotonic pattern.

---

## Stack vs Deque Choice

**Use Stack:**
- Processing elements in one direction
- Need next/previous greater/smaller
- Example: Daily Temperatures, Histogram

**Use Deque:**
- Sliding window problems
- Need to remove from both ends
- Example: Sliding Window Maximum

**Go Implementation Note:**
- Go doesn't have a built-in deque
- Use slice with append/slicing: `deque = deque[1:]` for pop front, `deque = deque[:len(deque)-1]` for pop back
- For high performance, consider ring buffer or `container/list`

---

## Quick Reference Card

```
Next Greater:
  stack := []int{}  // indices, monotonic decreasing
  for i := range nums {
    while len(stack) > 0 && nums[i] > nums[stack[top]] {
      result[stack.pop()] = nums[i]
    }
    stack.push(i)
  }

Sliding Window Max:
  deque := []int{}  // indices, monotonic decreasing
  for i := range nums {
    // Remove out-of-window from front
    while deque[0] <= i-k { deque.pop_front() }
    // Remove smaller from back
    while nums[deque[back]] < nums[i] { deque.pop_back() }
    deque.push_back(i)
    if i >= k-1 { result = deque[0] }
  }

Histogram:
  stack := []int{}  // indices, monotonic increasing
  for i := range heights {
    while heights[i] < heights[stack[top]] {
      h := heights[stack.pop()]
      width := i - stack[top] - 1
      area := h * width
    }
    stack.push(i)
  }

Time: O(n) amortized
Space: O(n) worst case
```

---

> **Key Insight:** Monotonic stacks and deques turn O(n²) nested scans into O(n) single passes. Each element is pushed and popped exactly once. When you see "next greater", "previous smaller", or "sliding window max", immediately think monotonic. The pattern is counterintuitive but powerful.

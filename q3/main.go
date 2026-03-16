package main

import (
	"fmt"
)

func twoSum(nums []int, target int) []int {

	seen := make(map[int]int)
	for i, v := range nums {
		fmt.Printf("\nIdx: %d, Num:%d", i, v)
		if idx1, ok := seen[target-v]; ok {
			return []int{idx1, i}
		}
		seen[v] = i
	}
	return []int{}
}

func main() {

	t1 := []int{3, 4, 5, 6}
	out := twoSum(t1, 7)
	fmt.Printf("\n======\n%d", out)

}

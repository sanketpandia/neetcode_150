package main

import (
	"fmt"
)

func main() {
	fmt.Printf("\nK most frequent elements problem\n========\n")

	samp1 := []int{1, 2, 7, 9, 2, 3, 7, 3, 7, 9, 7, 2, 7, 3}
	res := topKFrequent(samp1, 2)

	fmt.Printf("Result: %v", res)

}

func topKFrequent(nums []int, k int) []int {
	// K  most frequent elements.
	// Make a frequency map
	// Insertion sort the data
	res := make([]int, len(nums))
	freqMap := make(map[int]int)

	for _, v := range nums {
		freqMap[v]++
	}

	fmt.Printf("FrequencyMap: %v\n", freqMap)

	res = mapInsSort(freqMap)
	fmt.Printf("Sorted resultSet: %v\n", res)
	return res[:k]
}

func mapInsSort(data map[int]int) []int {
	keys := len(data)
	res := make([]int, keys)
	for k, v := range data {
		fmt.Printf("Searching for key: %d & val: %d\n", k, v)
		for i, x := range res {
			// Fetch key for comparison
			if cnt, ok := data[x]; ok {
				if v > cnt {
					fmt.Printf("Value sorted at Position: %d & val: %d & arr: %v\n", i, k, res)
					insertInSlice(res, k, i)
					break
				}
			} else {
				res[i] = k
				break
			}
		}
	}
	return res
}

func insertInSlice(arr []int, x int, pos int) []int {
	// Without size change
	// Not expecting overflows
	l := len(arr) - 1

	if l < 0 {
		return arr
	}
	for i := l; i > pos; i-- {
		arr[i] = arr[i-1]

	}
	arr[pos] = x

	return arr
}

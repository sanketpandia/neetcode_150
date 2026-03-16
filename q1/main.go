package main

func hasDuplicate(nums []int) bool {

	//Convert to a map
	values := make(map[int]bool)

	for _, num := range nums {

		if _, ok := values[num]; ok {
			return true
		}
		values[num] = true
	}
	return false
}

func main() {
	// Test cases
	nums1 := []int{1, 2, 3, 1}
	nums2 := []int{1, 2, 3, 4}
	nums3 := []int{1, 1, 1, 3, 3, 4, 3, 2, 4, 2}

	println("Test 1:", hasDuplicate(nums1))
	println("Test 2:", hasDuplicate(nums2))
	println("Test 3:", hasDuplicate(nums3))
}

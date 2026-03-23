package main

import "fmt"

func main() {

	arr1 := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 7, 5, 7, 6}
	arr2 := []int{-1, 4, 7, 2, 0, 5, 7, 9, 4}

	fmt.Println("Insertion Sort")
	fmt.Println("Before:", arr1)
	InsertionSort(arr1)
	fmt.Println("After: ", arr1)

	fmt.Println("Before:", arr2)
	InsertionSort(arr2)
	fmt.Println("After: ", arr2)
	arr1 = []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 7, 5, 7, 6}
	arr2 = []int{-1, 4, 7, 2, 0, 5, 7, 9, 4}

	fmt.Println("Selection Sort")
	fmt.Println("Before:", arr1)
	InsertionSort(arr1)
	fmt.Println("After: ", arr1)

	fmt.Println("Before:", arr2)
	InsertionSort(arr2)
	fmt.Println("After: ", arr2)

}

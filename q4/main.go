package main

import (
	"fmt"
	"sort"
)

func groupAnagrams(strs []string) [][]string {

	ret := [][]string{}
	brkup := make(map[[26]int][]string)
	for _, v := range strs {

		strCount := [26]int{}
		for _, key := range v {
			// Build count
			idx := key - 'a'
			strCount[idx]++
		}

		brkup[strCount] = append(brkup[strCount], v)
	}

	for _, v := range brkup {
		ret = append(ret, v)
	}
	return ret
}

func anagramMapMethod(strs []string) [][]string {
	ret := [][]string{}

	resMap := make(map[string][]string)
	for _, v := range strs {
		sorted := sortString(v)
		resMap[sorted] = append(resMap[sorted], v)
	}

	fmt.Printf("\nResult Map\n==========\n%v\n", resMap)

	for _, val := range resMap {
		ret = append(ret, val)
	}

	return ret
}

func sortString(str string) string {
	r := []rune(str)
	sort.Slice(r, func(i, j int) bool {
		return r[i] < r[j]
	})
	return string(r)
}

func main() {
	fmt.Printf("Checking for anagrams")

	strs1 := []string{"act", "pots", "tops", "cat", "stop", "hat"}
	fmt.Printf("Original:\n%v\n", strs1)
	ag := groupAnagrams(strs1)
	fmt.Printf("Anagrams:\n%v\n", ag)
	fmt.Printf("\nNew Method:\n============\n")

	ag = anagramMapMethod(strs1)
	fmt.Printf("Anagrams:\n%v\n", ag)
}

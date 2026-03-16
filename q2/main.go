package main

import "strings"

func isAnagram(s string, t string) bool {
	if len(s) != len(t) {
		return false
	}

	s = strings.ToLower(s)
	t = strings.ToLower(t)

	cnt1 := countMap(s)

	//single map approach
	for _, c := range t {
		cnt1[c]--

		if val := cnt1[c]; val < 0 {
			return false
		}
	}
	return true
}

func countMap(s1 string) map[rune]int {

	cnt1 := make(map[rune]int)
	for _, c := range s1 {
		if _, ok := cnt1[c]; !ok {
			cnt1[c] = 1
		} else {
			cnt1[c]++
		}
	}

	return cnt1
}

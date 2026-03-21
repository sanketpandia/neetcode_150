package datastructures

import (
	"fmt"
)

type DSString struct {
	len int
	str []rune
}

func NewString() *DSString {
	arrPtr := []rune{}
	return &DSString{
		len: 0,
		str: arrPtr,
	}
}

func (s *DSString) Append(c rune) {
	fmt.Printf("\nAppending to string")
	str := s.str
	str = append(str, c)
	s.len = s.len + 1
}

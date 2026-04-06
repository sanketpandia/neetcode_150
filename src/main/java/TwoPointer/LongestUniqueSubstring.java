package TwoPointer;

import java.util.HashMap;
import java.util.Map;

public class LongestUniqueSubstring {
    public static void main(String[] args){

    }

    public int lengthOfLongestSubstring(String s) {
        int maxLen = 0;
        int startIdx = 0;
        int left = 0;
        Map<Character, Integer> seen = new HashMap<>();
        for(int right = 0; right < s.length() ; right++){
            char c = s.charAt(right);

            if(seen.containsKey(c) && seen.get(c) >= left) {
                left = seen.get(c) + 1;
            }
            seen.put(c, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }
}

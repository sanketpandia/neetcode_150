package arrays;

import java.util.HashMap;
import java.util.Map;

public class ValidAnagrams {
    public boolean isAnagram(String s, String t) {
        if(s.length() != t.length()) return false;
        Map<Character, Integer> cMap = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            char x = s.charAt(i);
            int cnt = 0;
            if (cMap.containsKey(x)) {
                cnt = cMap.get(x);
            }
            cMap.put(x, ++cnt);
        }

        for (int i = 0; i < s.length(); i++) {
            char x = t.charAt(i);
            int cnt;
            if (cMap.containsKey(x)) {
                cnt = cMap.get(x);
                if(cnt == 0) {
                    return false;
                }
            } else {
                return false;
            }
            cMap.put(x, --cnt);
        }
        return true;
    }

    public static void main(String[] args) {
        ValidAnagrams vd = new ValidAnagrams();

        System.out.println(vd.isAnagram("racecar", "carrace"));
        System.out.println(vd.isAnagram("jar", "jam"));
    }
}

package arrays;

import java.util.*;

public class GroupAnagrams {
    public List<List<String>> groupAnagrams(String[] strs) {
        List<List<String>> anagrams = new ArrayList<>();
        Map<String, List<Integer>> seen = new HashMap<>();

        // New approach
        for(int i = 0; i< strs.length; i++){
            char[] x = strs[i].toCharArray();
            Arrays.sort(x);
            String key = new String(x);
            List<Integer> indexes = seen.getOrDefault(key, null);
            if(indexes == null){
                indexes = new ArrayList<>();
            }
            indexes.add(i);
            seen.put(key, indexes);
        }


        for(Map.Entry<String, List<Integer>> entry : seen.entrySet()) {
            List<String> ags = new ArrayList<>();
            List<Integer> kys = entry.getValue();
            for (Integer ky : kys) {
                ags.add(strs[ky]);
            }
            anagrams.add(ags);
        }

        return anagrams;
        

//        ValidAnagrams va = new ValidAnagrams();
//
//        List<String> elements = new ArrayList<>(Arrays.asList(strs));
//        while(!elements.isEmpty()){
//            boolean matched = false;
//            String element = elements.getFirst();
//            for(List<String> ags : anagrams){
//                if(va.isAnagram(ags.getFirst(), element)){
//                    matched = true;
//                    ags.add(element);
//                    elements.removeFirst();
//                    break;
//                }
//            }
//
//            if(!matched) {
//                List<String> a1 = new ArrayList<>(){};
//                a1.add(element);
//                anagrams.add(a1);
//                elements.removeFirst();
//            }
//        }
//        return anagrams;
    }

    public static void main(String[] args){
        GroupAnagrams ga = new GroupAnagrams();

        System.out.println(ga.groupAnagrams(new String[]{"act","pots","tops","cat","stop","hat"}));
        System.out.println(ga.groupAnagrams(new String[]{"z"}));
        System.out.println(ga.groupAnagrams(new String[]{}));
    }
}

package arrays;

import javax.swing.tree.TreeNode;
import java.util.*;

public class KFrequentElements {
    public int[] topKFrequent(int[] nums, int k){
        int[] ret = new int[k];

        Map<Integer, Integer> seen = new HashMap<>();
        for (int element: nums) seen.put(element, seen.getOrDefault(element, 0) + 1);

        List<Integer>[] frequencies = new List[nums.length+1];

        for (Integer key: seen.keySet()){
            int freq = seen.getOrDefault(key, 0);
            if(frequencies[freq] == null){
                frequencies[freq] = new ArrayList<Integer>() ;
            }

            frequencies[freq].add(key);

        }
        int cntr = 0;
        for (int i = frequencies.length - 1; k > 0 && i >=0; i--){
            if(frequencies[i] != null && !frequencies[i].isEmpty()) {
                List<Integer> elements = frequencies[i];
                for (Integer element : elements) {
                    ret[cntr] = element;
                    cntr++;
                    k--;
                }
            }
        }
        return ret;
    }

    public static void main(String[] args){
        KFrequentElements fe = new KFrequentElements();
        System.out.println(Arrays.toString(fe.topKFrequent(new int[]{9,9,9,2,7,7,7,7,2,2,4,5,3}, 3)));
        System.out.println(Arrays.toString(fe.topKFrequent(new int[]{1,2,2,3,3,3}, 2)));
    }
}

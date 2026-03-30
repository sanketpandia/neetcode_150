package arrays;

import java.util.HashSet;
import java.util.Set;

public class ContainsDuplicate {

    public boolean hasDuplicate(int[] nums) {
        Set<Integer> valSet = new HashSet<Integer>();
        for (int num: nums ){
            if(valSet.contains(num)){
                return true;
            }
            valSet.add(num);
        }
        return false;
    }

    public static void main(String[] args){
        ContainsDuplicate cd = new ContainsDuplicate();

        System.out.println(cd.hasDuplicate(new int[]{1,2,3,4}));
        System.out.println(cd.hasDuplicate(new int[]{1,2,3,3}));
    }
}

package TwoPointer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreeSum {
    public static void main(String[] args){
        ThreeSum ts = new ThreeSum();
        ts.threeSum(new int[]{-1,0,1,2,-1,-4});
    }

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> ls = new ArrayList<>();
        Arrays.sort(nums);
        for(int i =0; i < nums.length - 2; i++){
        if (i > 0 && nums[i] == nums[i - 1]) continue;
            int left, right, cur;
            left = i + 1;
            right = nums.length - 1;
            cur = nums[i];
            while(left < right){
                int lval = nums[left];
                int rval = nums[right];
                if((cur + lval + rval) > 0) right--;
                else if((cur + lval + rval) < 0) left++;
                else {
                    List<Integer> triplet = new ArrayList<>(3){};
                    triplet.add(cur);
                    triplet.add(lval);
                    triplet.add(rval);
                    ls.add(triplet);
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) left++;
                    while (left < right && nums[right] == nums[right + 1]) right--;
                }
            }
        }
        return ls;
    }
}

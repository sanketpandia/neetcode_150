package TwoPointer;

public class TwoSum {
    public static void main(String[] args){

    }

    public int[] twoSum(int[] numbers, int target) {
        int left, right;
        left = 0;
        right = numbers.length - 1;
        while (left < right) {
            int twosum = numbers[left] + numbers[right];
            if (target - twosum == 0) return new int[]{left + 1, right + 1};
            else if (twosum > target) right--;
            else left++;
        }
        return new int[]{2,3};
    }
}

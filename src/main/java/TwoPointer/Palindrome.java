package TwoPointer;

public class Palindrome {
    public static void main(String[] args){

    }

    public boolean isPalindrome(String s) {
        int left, right;
        s = s.replaceAll("[^a-zA-Z0-9]", "");
        s = s.trim().toLowerCase().replaceAll(" ", "");
        left = 0;
        right = s.length() - 1;

        while(left <= right ){
            if(s.charAt(left) != s.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }
}

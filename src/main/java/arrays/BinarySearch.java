package arrays;

import java.util.Arrays;

import static java.util.Arrays.sort;

public class BinarySearch {
    public static void main(String[] args){
        BinarySearch bin = new BinarySearch();
        int[] data = {1,2,4,45,7,6,3,7,5};
        sort(data);
        System.out.println(bin.binarySearch(data, 45));
        int[] data1= {1,56,9,3,7,43,4, 5};
        Arrays.sort(data);
        System.out.println(bin.binarySearch(data1, 7));

    }
    int binarySearch(int[] data, int needle){
        System.out.println(Arrays.toString(data));
        return search(data, needle, 0, data.length - 1);
    }

    int search(int[] data, int needle, int lo, int hi){
        if(lo > hi) return -1;
        int mid = lo +  (hi - lo) / 2;

        if(data[mid] == needle) return mid;
        if(data[mid] > needle) return search(data, needle, lo, mid - 1);
        if(data[mid] < needle) return search(data, needle, mid + 1, hi);
        return -1;
    }
}

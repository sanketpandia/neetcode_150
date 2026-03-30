package arrays;

import java.util.ArrayList;
import java.util.List;

public class EncodeDecode {

    static String SEPARATOR = "&sp";

    public String encode(List<String> strs) {
        StringBuilder x = new StringBuilder(0);
        for (String str : strs){
            int l = str.length();
            x.append(l);
            x.append(SEPARATOR);
            x.append(str);
        }
        return x.toString();
    }

    public List<String> decode(String str) {
        int sepLen = SEPARATOR.length();
        char[] chars = str.toCharArray();
        List<String> strs = new ArrayList<>();
        for (int i =0; i < chars.length - 4; i ++){
            int nextIndex = str.indexOf(SEPARATOR, i);
            if(nextIndex == -1) break;
            int len = Integer.parseInt(String.valueOf(chars[nextIndex - 1]));
            if(len == 0){
                strs.add("");
            } else {
            strs.add(str.substring(nextIndex + sepLen, nextIndex + sepLen + len ));
            }
            i = nextIndex + sepLen + len;
        }
        return strs;
    }

    public static void main(String[] args){
        EncodeDecode ecd = new EncodeDecode();
        List<String> lis = new ArrayList<>();
        lis.add("");
        lis.add("");
        String enc = ecd.encode(lis);
        System.out.println(ecd.decode(enc));
    }
}

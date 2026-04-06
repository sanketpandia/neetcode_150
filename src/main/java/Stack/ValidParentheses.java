package Stack;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ValidParentheses {
    public boolean isValid(String s){
        Map<Character, Character> openBr = new HashMap<>();
        openBr.put('{', '}');
        openBr.put('[', ']');
        openBr.put('(', ')');

        Map<Character, Character> closeBr = new HashMap<>();
        closeBr.put( '}', '{');
        closeBr.put(']','[' );
        closeBr.put(')', '(' );

        char[] open = new char[]{'{', '(', '['};
        char[] close = new char[]{'}', ')', ']'};

        Stack<Character> stck = new Stack<>();

        for(int i = 0; i< s.length() ; i++){
            if(openBr.containsKey(s.charAt(i))){
                stck.push(s.charAt(i));
                continue;
            }

            if(closeBr.containsKey(s.charAt(i))){
                if (stck.isEmpty() || stck.peek() != closeBr.get(s.charAt(i)))return false;
                else stck.pop();
            }
        }

        return stck.isEmpty();
    }
}

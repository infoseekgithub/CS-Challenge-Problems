// Copyright (c) 2020 https://github.com/infoseekgithub. All rights reserved.


import java.util.*;

public class SpecialBinaryString {
    
    
    static String getLargestString(String str) {
        int pos = 0;
        List<String> list = new ArrayList<>();
        String tmpStr;
        
        // The given string could include a combination of a few substrings
        // that are speical binary strings     
        while (pos < str.length()) {
            tmpStr = getLargestStringHelper(str, pos);
	    list.add(tmpStr);
            pos += tmpStr.length();
        }
        
        Collections.sort(list, Collections.reverseOrder());
        StringBuilder sbd = new StringBuilder();
        
        for (String str_item: list) {
            sbd.append(str_item);
        }
        return sbd.toString();
    }


    static String getLargestStringHelper(String str, int pos) {
        //pos: starting position

        if (pos == str.length()) {
            // Fail to find a matching '0' for the begining '1' to make a
            // special binary string
            throw new IllegalArgumentException("Given string " +
               str + " is not a special binary string");	    
        }
        
        if (str.charAt(pos) == '0') {
            return "0";
        }
                
        StringBuilder sbd = new StringBuilder();
        sbd.append("1");
        pos++;
        
        String tmpStr = "";
        List<String> list = new ArrayList<>();
        
        while (true) {
            tmpStr = getLargestStringHelper(str, pos);
            if (tmpStr.equals("0")) {
                break;
            }
            list.add(tmpStr);
            pos += tmpStr.length();
        }
        
        Collections.sort(list, Collections.reverseOrder());
        for (String str_item : list) {
            sbd.append(str_item);
        }
        
        sbd.append(tmpStr);
        return sbd.toString();
    }
    
    
    public static void main(String[] args) {
        System.out.println("-- Positive Tests: ---"); 
        String str1 = "10";
        System.out.println(getLargestString(str1));
      
        String str2 = "101100";
        System.out.println(getLargestString(str2));
        String str3 = "110010";
        System.out.println(getLargestString(str3));

        String str4 = "11011000";
        System.out.println(getLargestString(str4));
	System.out.println();

	// Negative tests
        System.out.println("-- Negative Tests: ---"); 
	try {
           String str5 = "1101100";
           System.out.println(getLargestString(str5));
        } catch (Exception e) {
           e.printStackTrace();
        }

        try {
           String str6 = "110110011";
           System.out.println(getLargestString(str6));
        } catch (Exception e) {
           e.printStackTrace();
        }
    }
    
}    

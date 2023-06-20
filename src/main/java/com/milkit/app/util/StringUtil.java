package com.milkit.app.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtil {

	public static String leftPad(String srcStr, String padStr, int totalLength) {
	    if (srcStr.length() >= totalLength) {
	        return srcStr;
	    }
	    StringBuilder strBuilder = new StringBuilder();
	    while (strBuilder.length() < totalLength - srcStr.length()) {
	        strBuilder.append(padStr);
	    }
	    strBuilder.append(srcStr);
	 
	    return strBuilder.toString();
	}

	public static String toJsonString(Object obj) throws JsonProcessingException {
	    return (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}

	public static String removeMinusChar(String str) {
        return remove(str, '-');
	}
	
	public static String remove(String str, char remove) {
        if (isEmpty(str) || str.indexOf(remove) == -1) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[pos++] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
	}

	public static boolean isCheckByteSize(String sourceStr, int checkSize) {
        if (isEmpty(sourceStr)) { return true; }
 
        int en = 0;
        int ko = 0;
        int etc = 0;
 
        char[] txtChar = sourceStr.toCharArray();
        for (char c : txtChar) {
            if (c >= 'A' && c <= 'z') {
                en++;
            } else if (c >= '\uAC00' && c <= '\uD7A3') {
                ko = ko + 3;
            } else {
                etc++;
            }
        }
		int txtByte = en + ko + etc;

        return txtByte <= checkSize;
    }

	public static boolean isValidEmail(String email) {
		boolean err = false; 
		String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; 
		Pattern p = Pattern.compile(regex); 
		Matcher m = p.matcher(email); 

		if(m.matches()) { 
			err = true; 
		}

		return err; 
	}
}

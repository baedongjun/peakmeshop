package com.peakmeshop.util;

import java.util.Base64;

public class Base64Utils {

    public static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeFromString(String encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
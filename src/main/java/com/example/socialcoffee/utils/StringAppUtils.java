package com.example.socialcoffee.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringAppUtils {
    public static String removeNewLineCharacter(String str) {
        return str.replaceAll("\\n",
                              "");
    }

    public static String replaceNewLineToBrDiv(String str) {
        return str.replaceAll("\\n",
                              "<br/>");
    }

    public static boolean isEmpty(String str) {
        return !org.springframework.util.StringUtils.hasLength(str);
    }
}

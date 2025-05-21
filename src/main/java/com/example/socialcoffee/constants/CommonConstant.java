package com.example.socialcoffee.constants;


import org.apache.commons.lang3.math.NumberUtils;

public interface CommonConstant {
    String TOKEN_PREFIX = "TOKEN_";
    String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";
    String CONTENT_TYPE = "application/json";
    String AT_SIGN = "@";
    Long ADMIN_INDEX = NumberUtils.LONG_ZERO;
    String USER_PROMPT = "These are the system filters: %s, extract matching filters from the prompt and return only a valid JSON object. Do not include any additional text, characters or explanation. Return the JSON only: ";
    String USER_ID = "User-Id";

    Long MAX_SPONSORED_SHOP = 3L;
}

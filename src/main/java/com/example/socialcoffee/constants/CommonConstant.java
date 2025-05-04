package com.example.socialcoffee.constants;


import com.example.socialcoffee.enums.Privacy;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;

public interface CommonConstant {
    String TOKEN_PREFIX = "TOKEN_";
    String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";
    String CONTENT_TYPE = "application/json";
    String AT_SIGN = "@";
    Long ADMIN_INDEX = NumberUtils.LONG_ZERO;
    List<String> DEFAULT_PRIVACY = List.of(Privacy.PUBLIC.getValue(), Privacy.FOLLOWER.getValue());
    String COFFEE_SHOP_DESCRIPTION_PROMPT = "Generate description for a coffee shop with these features (limit 150 words):";
    String USER_PREFERENCE_DESCRIPTION_PROMPT = "Generate description for a coffee shop preference with these features (limit 150 words):";
}

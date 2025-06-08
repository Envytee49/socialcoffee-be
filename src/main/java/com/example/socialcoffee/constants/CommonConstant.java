package com.example.socialcoffee.constants;


import org.apache.commons.lang3.math.NumberUtils;

public interface CommonConstant {
    String TOKEN_PREFIX = "TOKEN_";

    String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";

    String CONTENT_TYPE = "application/json";

    String AT_SIGN = "@";

    Long ADMIN_INDEX = NumberUtils.LONG_ZERO;

    String USER_PROMPT = "These are the system filters: %s, extract matching filters from the prompt, each value must be matched only within its defined category, do not assign a value to a category it does not belong to (for example 'Acoustic' belongs to ambiances not entertainments). Do not include any additional text, characters or explanation. Return the JSON only: ";

    String USER_ID = "User-Id";

    Long MAX_SPONSORED_SHOP = 3L;

    String LATITUDE = "lat";

    String LONGITUDE = "lng";

    String MAY_LIKE = "may_like:";

    String PREFERENCE = "preference:";

    String SIMILAR_PLACE = "similar_place:";

    String YOU_FOLLOW = "you_follow:";
}

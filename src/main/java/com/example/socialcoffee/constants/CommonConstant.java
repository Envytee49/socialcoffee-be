package com.example.socialcoffee.constants;


import org.apache.commons.lang3.math.NumberUtils;

public interface CommonConstant {
    String TOKEN_PREFIX = "TOKEN_";

    String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";

    String CONTENT_TYPE = "application/json";

    Long ADMIN_INDEX = NumberUtils.LONG_ZERO;

    Long MAX_SPONSORED_SHOP = 3L;

    String LATITUDE = "lat";

    String LONGITUDE = "lng";

    String MAY_LIKE = "may_like:";

    String PREFERENCE = "preference:";

    String SIMILAR_PLACE = "similar_place:";

    String YOU_FOLLOW = "you_follow:";

}

package com.example.socialcoffee.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleUserInfo {
    private String sub;

    private String name;

    private String givenName;

    private String familyName;

    private String picture;

    private String email;

    private boolean emailVerified;
}

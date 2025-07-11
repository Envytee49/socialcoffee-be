package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum MetaData {
    INTERNAL_SERVER_ERROR(500,
                          "Internal Server Error"),
    NO_RESOURCE_FOUND(404,
                      "No Resource Found"),
    NO_CONTENT(204,
               "No content"),
    BAD_REQUEST(400,
                "Bad Request"),
    SUCCESS(200,
            "Success"),
    SERVICE_UNAVAILABLE(503,
                        "Service Unavailable"),
    UNAUTHORIZED(401,
                 "Unauthorized"),
    PARAMETERS_MISSING(1000,
                       "Parameters Missing"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(1001,
                                  "HTTP Media Type Not Supported"),
    MISSING_SERVLET_REQUEST_PART(1002,
                                 "Missing Servlet Request Part"),
    MULTIPART_IS_NULL(1003,
                      "Multipart Is Null"),
    ONLY_NUMBER(1004,
                "Only Number"),
    REQUEST_BODY_INVALID(1005,
                         "Request Body Invalid"),
    ACTION_NOT_ALLOWED(1006,
                       "Action Not Allowed"),
    METHOD_NOT_ALLOWED(1007,
                       "Method Not Allowed"),
    MISSING_REQUEST_HEADER(1008,
                           "Missing Request Header"),
    NOT_FOUND(1009,
              "Not Found"),
    ILLEGAL_ARGUMENT(1010,
                     "Illegal Argument: %s"),
    NOT_REGISTERED(1011,
                   "You are not registered in the system"),
    ALREADY_REGISTER(1012,
                     "You already registerd with this account"),
    FACEBOOK_ERROR(1013,
                   "Facebook Service Is Currently Down"),
    GOOGLE_ERROR(1014,
                 "Google Service Is Currently Down"),
    CONTENT_AND_FILE_MISSING(4758,
                             "Content and files are missing"),
    EXCEED_MAX_LENGTH_COMMENT_POST(4095,
                                   "The comment of post exceed %s characters"),
    FILE_EXTENSION_NOT_ACCEPTED(4964,
                                "File extension not accepted"),
    PASSWORD_INVALID(4660,
                     "Password must contain at least 8 characters, at least one lowercase letter, one uppercase letter, one numeric digit, and one special character"),
    PASSWORD_MISSING(4764,
                     "Password is missing"),
    PASSWORD_INCORRECT(4090,
                       "Login password is incorrect"),
    PASSWORD_ALREADY_USED(4091,
                          "The new password is the same as one of your three previous passwords. Please enter a new ones"),
    YOU_DO_NOT_HAVE_RIGHTS(4019,
                           "You do not have rights to do this action"),
    ALREADY_FOLLOWING(4020,
                      "You already followed this user"),
    NOT_FOLLOWING(4021,
                  "You are not following this user"),
    INVALID_REACTION(4022,
                     "Invalid reaction"),
    INVALID_PRIVACY(4023,
                    "Invalid privacy"),
    INVALID_CREDENTIALS(4024,
                        "Invalid credentials"),
    USERNAME_MISSING(4025,
                     "Username missing"),
    WRONG_DATE_TIME_FORMAT(4026,
                           "Wrong date time format"),
    INVALID_PARAMETERS(4027,
                       "Invalid parameters: %s"),
    EXCEED_MAX_SPONSORED_SHOP(4028,
                              "Max sponsored shop can only be %s"),
    PASSWORD_IS_NOT_THE_SAME(4029,
                             "Password and confirm password are not the identical"), USERNAME_EXISTED(4030,"Username existed"
                                                                                   ), DISPLAY_NAME_EXISTED(4031,"Display name existed"
                                                                                                           );


    private final Integer metaCode;
    private final String message;

    MetaData(Integer metaCode,
             String message) {
        this.metaCode = metaCode;
        this.message = message;
    }

}


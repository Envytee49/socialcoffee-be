package com.example.socialcoffee.enums;

import lombok.Getter;

@Getter
public enum MetaData {
    INTERNAL_SERVER_ERROR(500, "Interal Server Error"),
    BAD_REQUEST(400, "Bad Request"),
    SUCCESS(200, "Success"),

    PARAMETERS_MISSING(1000, "Parameters Missing"),
    HTTP_MEDIA_TYPE_NOT_SUPPORTED(1001, "HTTP Media Type Not Supported"),
    MISSING_SERVLET_REQUEST_PART(1002, "Missing Servlet Request Part"),
    MULTIPART_IS_NULL(1003, "Multipart Is Null"),
    ONLY_NUMBER(1004, "Only Number"),
    REQUEST_BODY_INVALID(1005, "Request Body Invalid"),
    ACTION_NOT_ALLOWED(1006, "Action Not Allowed"),
    METHOD_NOT_ALLOWED(1007, "Method Not Allowed"),
    MISSING_REQUEST_HEADER(1008, "Missing Request Header"),
    NOT_FOUND(1009, "Not Found" ),
    ILLEGAL_ARGUMENT(1010,"Illegal Argument: %s" ),
    NOT_REGISTERED(1011, "You are not registered in the system"),
    ALREADY_REGISTER(1012,"You already registerd with this account");

    private final Integer metaCode;
    private final String message;

    MetaData(Integer metaCode, String message) {
        this.metaCode = metaCode;
        this.message = message;
    }

}


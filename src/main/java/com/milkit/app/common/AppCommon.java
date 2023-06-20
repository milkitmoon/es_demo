package com.milkit.app.common;

public class AppCommon {
	
    public static final String JWT_SECRET_KEY = "jwt_demo";
    public static final long JWT_EXPIRATION_TIME = 190960000000L;              //테스트를 위해 Token Expire 시간을 충분히 늘려줌
    public static final long JWT_REFRESH_EXPIRATION_TIME = 190960000000L;
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_HEADER_STRING = "Authorization";
    public static final String JWT_REFRESH_HEADER_STRING = "Refresh";
}

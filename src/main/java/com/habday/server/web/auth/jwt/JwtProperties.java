package com.habday.server.web.auth.jwt;

public interface JwtProperties {

    String SECRET = "habday"; //우리 서버만 알고 있는 비밀값
    int AccessToken_TIME =  20000; // (1/1000초)
    int RefreshToken_TIME = 2000000 ;//* 12 * 24;
    String HEADER_STRING = "accessToken";
}
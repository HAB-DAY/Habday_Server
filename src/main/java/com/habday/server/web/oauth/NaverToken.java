package com.habday.server.web.oauth;

import lombok.Data;

@Data
public class NaverToken {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private int expires_in;

    private String error;
    private String error_description;
}

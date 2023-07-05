package com.habday.server.web.oauth.provider.profile;

import lombok.Data;

@Data
public class NaverProfile {

    public String resultcode;
    public String message;
    public Response response;

    @Data
    public class Response {
        public String id;
        public String email; //연락처 이메일 주소
        public String name; //회원이름
        public String profile_image; //사용자 프로필 사진 URL

    }
}

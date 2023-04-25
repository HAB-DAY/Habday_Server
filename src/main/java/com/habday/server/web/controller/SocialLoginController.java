package com.habday.server.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.habday.server.domain.member.Member;
import com.habday.server.web.auth.jwt.JwtService;
import com.habday.server.web.auth.jwt.JwtToken;
import com.habday.server.web.oauth.NaverService;
import com.habday.server.web.oauth.NaverToken;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SocialLoginController {

    private final NaverService naverService;
    private final JwtService jwtService;

    /**
     * JWT를 이용한 네이버 로그인
     */

    @GetMapping("/api/oauth/token/naver")
    public Map<String, String> NaverLogin(@RequestParam("code") String code) {

        NaverToken oauthToken = naverService.getAccessToken(code);

        Member saveMember = naverService.saveMember(oauthToken.getAccess_token());

        JwtToken jwtToken = jwtService.joinJwtToken(saveMember.getName());

        return jwtService.successLoginResponse(jwtToken);
    }
    @GetMapping("/login/oauth2/code/naver")
    public String NaverCode(@RequestParam("code") String code) {
        return "네이버 로그인 인증완료, code: "  + code;
    }




    /**
     * refresh token 재발급
     * @return
     */
    @GetMapping("/refresh/{userId}")
    public Map<String,String> refreshToken(@PathVariable("userId") String userid, @RequestHeader("refreshToken") String refreshToken,
                                           HttpServletResponse response) throws JsonProcessingException {

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        JwtToken jwtToken = jwtService.validRefreshToken(userid, refreshToken);
        Map<String, String> jsonResponse = jwtService.recreateTokenResponse(jwtToken);

        return jsonResponse;
    }
}

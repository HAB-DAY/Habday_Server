package com.habday.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.habday.server.classes.Common;
import com.habday.server.config.S3Uploader;
import com.habday.server.domain.fundingItem.FundingItem;
import com.habday.server.domain.member.Member;
import com.habday.server.dto.CommonResponse;
import com.habday.server.dto.MemberProfileRequestDto;
import com.habday.server.dto.MemberProfileResponse;
import com.habday.server.dto.req.CreateFundingItemRequestDto;
import com.habday.server.dto.res.CreateFundingItemResponseDto;
import com.habday.server.dto.res.fund.ShowFundingContentResponseDto;
import com.habday.server.exception.CustomException;
import com.habday.server.service.MemberService;
import com.habday.server.service.NaverService;
import com.habday.server.web.auth.jwt.JwtToken;
import com.habday.server.web.auth.jwt.service.JwtService;
import com.habday.server.web.oauth.provider.Token.NaverToken;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.habday.server.constants.ExceptionCode.NO_MEMBER_ID;
import static com.habday.server.constants.SuccessCode.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/v1")
public class MemberController extends Common {
    private final MemberService memberService;
    private final NaverService naverService;
    private final JwtService jwtService;


    @PutMapping("/save/memberProfile/{memberId}")
    public ResponseEntity<MemberProfileResponse> saveMemberProfile(@PathVariable("memberId") Long memberId, @RequestBody MemberProfileRequestDto request) {
        memberService.updateMemberProfile(memberId, request);
        return MemberProfileResponse.newResponse(VERIFY_MEMBER_PROFILE_SUCCESS);
    }

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

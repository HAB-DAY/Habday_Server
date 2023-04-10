package com.habday.server.web.oauth;

import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import com.habday.server.web.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NaverService {

    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    private final String client_id = "UAusWFQ9IPtJbSu2FD8R";
    private final String client_secret = "LjS2Cr6Edg";
    private final String redirect_uri = "http://localhost:9000/login/oauth2/code/naver";
    private final String accessTokenUri = "https://nid.naver.com/oauth2.0/token";
    private final String UserInfoUri = "https://openapi.naver.com/v1/nid/me";

    /**
     * 카카오로 부터 엑세스 토큰을 받는 함수
     */
    public NaverToken getAccessToken(String code) {

        //요청 param (body)
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id",client_id );
        params.add("redirect_uri",redirect_uri);
        params.add("code", code);
        params.add("client_secret", client_secret);


        //request
        WebClient wc = WebClient.create(accessTokenUri);
        String response = wc.post()
                .uri(accessTokenUri)
                .body(BodyInserters.fromFormData(params))
                .header("Content-type","application/x-www-form-urlencoded;charset=utf-8" ) //요청 헤더
                .retrieve()
                .bodyToMono(String.class)
                .block();

        //json형태로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        NaverToken naverToken =null;

        try {
            naverToken = objectMapper.readValue(response, NaverToken.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return naverToken;
    }

    /**
     * 사용자 정보 가져오기
     */
    public NaverProfile findProfile(String token) {

        //Http 요청
        WebClient wc = WebClient.create(UserInfoUri);
        String response = wc.get()
                .uri(UserInfoUri)
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/xml;charset=utf-8")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        NaverProfile naverProfile = null;

        try {
            naverProfile = objectMapper.readValue(response, NaverProfile.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("===========NaverProfile=================");
        System.out.println(naverProfile);
        return naverProfile;
    }

    /**
     * 카카오 로그인 사용자 강제 회원가입
     */
    @Transactional
    public Member saveMember(String access_token) {
        NaverProfile profile = findProfile(access_token); //사용자 정보 받아오기
        Member member = memberRepository.findByUserid(profile.response.getId());

        //처음이용자 강제 회원가입
        if(member ==null) {
            member = Member.builder()
                    .name(profile.response.getId())
                    .password(null) //필요없으니 일단 아무거도 안넣음. 원하는데로 넣으면 됌
                    .nickName(profile.response.getNickname())
                    .profileImg(profile.response.profile_image)
                    .email(profile.response.email)
                    .roles("USER")
                    .createTime(LocalDateTime.now())
                    .provider("Naver")
                    .build();

            memberRepository.save(member);
        }

        return member;
    }
}

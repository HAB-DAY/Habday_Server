package com.habday.server.config.auth;

import com.habday.server.config.auth.dto.OAuthAttributes;
import com.habday.server.config.auth.dto.SessionMember;
import com.habday.server.domain.member.Member;
import com.habday.server.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /*OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakao_account.get("email");
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = (String) properties.get("nickname");

        if(memberRepository.findByEmail(email)) {

        }*/

        OAuth2UserService <OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        // Oauth2 정보를 가져옴
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // 현재 로그인 중인 서비스를 구분하는 코드, 네이버인지, 카카오인지 구분하기 위함
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 진행시 키가 되는 필드값으로 구글만 지원
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        httpSession.setAttribute("member", new SessionMember(member));

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())), attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    // 카카오 사용자 정보가 업데이트 되었을 때를 대비하여 아래의 메소드 구현
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getEmail(), attributes.getProfileImg()))
                .orElse(attributes.toEntity());

        return memberRepository.save(member);
    }
}

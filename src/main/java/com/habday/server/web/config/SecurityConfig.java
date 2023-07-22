package com.habday.server.web.config;

import com.habday.server.domain.member.MemberRepository;
import com.habday.server.web.auth.jwt.JwtService;
import com.habday.server.web.auth.jwt.filter.JwtAuthenticationFilter;
import com.habday.server.web.auth.jwt.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity //시큐리티 활성화 -> 기본 스프링 필터 체인에 등록
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorConfig config;
    private final MemberRepository memberRepository;
    private final JwtService jwtService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                // 유저관련 (소셜로그인)
                .antMatchers("/login/oauth2/code/naver", "/api/oauth/token/naver", "/funding/showFundingContent/**", "/funding/showConfirmation/**", "/verifyIamport/noneauthpay/unschedule/**");

    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다!(login시 세션을 검증하는 필터를 사용하지 않겠다)
                .and()
                .formLogin().disable() //formLogin(form)방식 사용 안함 , json방식으로 전달
                .httpBasic().disable() //Bearer 방식 사용 -> header 에 authentication 에 토큰을 넣어 전달하는 방식

                .apply(new MyCustomDsl()) //커스텀 필터 등록
                .and()

                .authorizeRequests() //인증, 권한 api 설정
                .antMatchers("/funding/**").hasAuthority("USER")
                //.antMatchers("/api/v1/manager/**").hasAuthority("MANAGER")
                //.antMatchers("/api/v1/admin/**").hasAuthority("ADMIN")
                .anyRequest().permitAll()

                .and()
                .build();

    }

    //jwt 커스텀 필터 등록
    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {

        @Override
        public void configure(HttpSecurity http)  {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(config.corsFilter()) //스프링 시큐리티 필터내에 cors 관련 필터가 있음!! 그래서 제공해주는 필터 객체를 생성후 HttpSecurity에 등록!
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtService)) //AuthenticationManger가 있어야 된다.(파라미터로)
                    .addFilter(new JwtAuthorizationFilter(authenticationManager, memberRepository, jwtService))
                    ;//.requestMatchers().antMatchers();
        }
    }
}

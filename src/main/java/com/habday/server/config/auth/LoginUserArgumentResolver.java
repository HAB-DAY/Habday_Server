package com.habday.server.config.auth;

import com.habday.server.config.auth.dto.SessionMember;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final HttpSession httpSession;

    // 파라미터에 @LoginUser 어노테이션이 붙어있고, 파라미터 클래스 타입이 SessionMember.class인 경우 true 반환
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getMethodAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionMember.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    // 파라미터에 전달할 객체를 생성함, 여기에서는 세션 객체를 가져옴
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("member");
    }
}

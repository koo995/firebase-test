package test.firebase.auth;


import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import test.firebase.member.Member;

@Slf4j
@RequiredArgsConstructor
public class LoginMemberArgResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        log.info("resolveArgument 실행");
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        FirebaseToken decodedToken = (FirebaseToken) request.getAttribute("decodedToken");
        return authService.JoinAndLogin(decodedToken);
    }
}

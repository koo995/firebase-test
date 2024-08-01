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
import test.firebase.member.MemberRepository;

@Slf4j
@RequiredArgsConstructor
public class LoginMemberArgResolver implements HandlerMethodArgumentResolver {

//    private final AuthService authService;
    private final MemberRepository memberRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasMemberType;
    }

//    @Override
//    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        log.info("resolveArgument 실행");
//        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//        FirebaseToken decodedToken = (FirebaseToken) request.getAttribute("decodedToken");
//        return authService.JoinAndLogin(decodedToken);
//    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        FirebaseToken decodedToken = (FirebaseToken) request.getAttribute("decodedToken");
        String uid = decodedToken.getUid();
        Thread.sleep(1000);
        Member member = memberRepository.findByUid(uid)
                .orElse(Member.builder()
                        .username(decodedToken.getName())
                        .email(decodedToken.getEmail())
                        .uid(uid)
                        .build());
        // 여기서 member을 영속화하는 것이 좋은 방법일까?
        memberRepository.save(member);
        return member;
    }
}

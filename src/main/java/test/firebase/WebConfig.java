package test.firebase;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import test.firebase.auth.AuthService;
import test.firebase.auth.FirebaseTokenInterceptor;
import test.firebase.auth.LoginMemberArgResolver;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthService authService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new FirebaseTokenInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/error-page/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgResolver(authService));
    }
}

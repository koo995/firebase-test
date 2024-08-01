package test.firebase.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class FirebaseTokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("preHandle 실행");
        FirebaseToken decodedToken;
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No Token or Invalid Token.");
        }
        String token = header.split(" ")[1];
        // verify IdToken
        try{
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("Invalid Token.");
        }
        log.info("인증 완료");
        request.setAttribute("decodedToken", decodedToken);
        return true;
    }
}

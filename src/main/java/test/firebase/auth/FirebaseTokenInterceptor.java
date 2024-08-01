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
        String token = getAuthorizationToken(request);
        FirebaseToken decodedToken = decodeToken(token);
        log.info("인증 완료");
        request.setAttribute("decodedToken", decodedToken);
        return true;
    }

    private static FirebaseToken decodeToken(String token) {
        FirebaseToken decodedToken;
        try{
            decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("Invalid Token.");
        }
        return decodedToken;
    }

    private static String getAuthorizationToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("No Token or Invalid Token.");
        }
        return header.split(" ")[1];
    }
}

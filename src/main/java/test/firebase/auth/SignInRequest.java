package test.firebase.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignInRequest {
    private String email;
    private String password;
    private Boolean returnSecureToken;

    @Builder
    public SignInRequest(String email, String password, Boolean returnSecureToken) {
        this.email = email;
        this.password = password;
        this.returnSecureToken = returnSecureToken;
    }
}

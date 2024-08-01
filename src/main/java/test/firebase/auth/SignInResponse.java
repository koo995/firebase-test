package test.firebase.auth;

import lombok.Getter;

@Getter
public class SignInResponse {

    private String kind;
    private String localId;
    private String email;
    private String displayName;
    private String idToken;
    private String registered;
    private String refreshToken;
    private String expiresIn;
}

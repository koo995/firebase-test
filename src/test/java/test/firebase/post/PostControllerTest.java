package test.firebase.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import test.firebase.auth.SignInRequest;
import test.firebase.auth.SignInResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private static String uid;
    private static String idToken;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${firebase.credential.resource-path}")
    private String keyPath;

    private final String firebaseSignInUrl = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword";

    @BeforeEach
    void createUserAndSignIn() throws Exception {
        String testEmail = "gunhong951@gmail.com";
        String testPassword = "123456";
        String username = "testName";
        UserRecord user = createFirebaseUserAccount(testEmail, testPassword, username);
        uid = user.getUid();
        String apiKey = getApiKey();
        SignInResponse signInResponse = signInFirebase(testEmail, testPassword, apiKey);
        idToken = signInResponse.getIdToken();
    }

    private SignInResponse signInFirebase(String testEmail, String testPassword, String apiKey) {
        RestTemplate restTemplate = new RestTemplate();
        SignInRequest signInRequest = SignInRequest.builder()
                .email(testEmail)
                .password(testPassword)
                .returnSecureToken(true)
                .build();
        URI uri = UriComponentsBuilder
                .fromHttpUrl(firebaseSignInUrl)
                .queryParam("key", apiKey)
                .build().toUri();
        SignInResponse signInResponse = restTemplate.postForObject(uri, signInRequest, SignInResponse.class);
        return signInResponse;
    }

    private String getApiKey() throws IOException {
        HashMap map = objectMapper.readValue(new ClassPathResource(keyPath).getInputStream(), HashMap.class);
        return (String) map.get("web_api_key");
    }

    private static UserRecord createFirebaseUserAccount(String testEmail, String testPassword, String username) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(testEmail)
                .setPassword(testPassword)
                .setDisplayName(username);
        return FirebaseAuth.getInstance().createUser(request);
    }

    @AfterEach
    void delete() throws FirebaseAuthException {
        FirebaseAuth.getInstance().deleteUser(uid);
        uid = "";
        idToken = "";
    }

    @DisplayName("동시처리 테스트")
    @Test
    void test() throws Exception {
        // given
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");

        CountDownLatch latch = new CountDownLatch(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/post/new"))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(post)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + idToken)
                .build();


        Runnable task2 = () -> {
            try(HttpClient client = HttpClient.newBuilder().build()) {
                client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        };

        // when
        Runnable task = () -> {
            try {
                mockMvc.perform(post("/post/new")
                                .content(objectMapper.writeValueAsString(post))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + idToken)
                        );
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                latch.countDown();
            }
        };

        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();

        latch.await();
    }

}
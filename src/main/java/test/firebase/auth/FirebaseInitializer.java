package test.firebase.auth;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseInitializer {

    @Value("${firebase.credential.resource-path}")
    private String keyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing Firebase.");
        InputStream inputStream = new ClassPathResource(keyPath).getInputStream();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();
        return FirebaseApp.initializeApp(options);
    }
}
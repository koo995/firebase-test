package test.firebase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class FirebaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirebaseApplication.class, args);
    }

}

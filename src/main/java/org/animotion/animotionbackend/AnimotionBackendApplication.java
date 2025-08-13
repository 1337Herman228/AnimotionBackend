package org.animotion.animotionbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AnimotionBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimotionBackendApplication.class, args);
    }

}

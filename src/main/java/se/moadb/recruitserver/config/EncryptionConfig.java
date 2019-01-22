package se.moadb.recruitserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class EncryptionConfig {

    /**
     * We use BCrypt to encode passwords
     * @return
     */
    @Bean
    public BCryptPasswordEncoder passWordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

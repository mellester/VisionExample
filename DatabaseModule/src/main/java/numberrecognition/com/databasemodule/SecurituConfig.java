package numberrecognition.com.databasemodule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurituConfig {
    @Bean
    public RestAuthenticationEntryPoint getRAEP() {
        return new RestAuthenticationEntryPoint();
    }
}

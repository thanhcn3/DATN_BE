package  com.datn.datn_be;

import jakarta.persistence.Cacheable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Cacheable
@EnableScheduling
@EnableAsync
public class DatnBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatnBeApplication.class, args);
    }

}

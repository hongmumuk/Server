package hongmumuk.hongmumuk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HongmumukApplication {

    public static void main(String[] args) {
        SpringApplication.run(HongmumukApplication.class, args);
            System.out.println("Hongmumuk Application Started");
    }

}

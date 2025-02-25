package hongmumuk.hongmumuk;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {@Server(url = "https://hongmumuk.shop", description = "Default Server url")})
@EnableScheduling
@SpringBootApplication
public class HongmumukApplication {

    public static void main(String[] args) {
        SpringApplication.run(HongmumukApplication.class, args);
            System.out.println("Hongmumuk Application Started");
    }

}

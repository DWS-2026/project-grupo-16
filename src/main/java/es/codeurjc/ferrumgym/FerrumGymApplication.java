package es.codeurjc.ferrumgym;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import es.codeurjc.ferrumgym.service.FileService;

@SpringBootApplication
public class FerrumGymApplication {

	public static void main(String[] args) {
		SpringApplication.run(FerrumGymApplication.class, args);
	}

	@Bean
    CommandLineRunner init(FileService fileService) {
        return args -> {
            fileService.init();
        };
    }
}

package main;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import main.model.User;
import main.repository.UserRepository;

@SpringBootApplication
public class YoruApplication {
	
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(YoruApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initData(UserRepository userRepository) {
		return args -> {
			
			User us1 = new User("Yoru",encoder.encode("123"), "franbl98@gmail.com", "", null);
			us1.setNumeroPublicaciones(0);
			us1.setNumeroSeguidores(0);
			us1.setNumeroSeguidos(0);
			us1.setAboutMe("Cuenta oficila de Yoru. Conecta con tus amigos.");
			userRepository.save(us1);
		};
	}
	

	
	
	
	
}

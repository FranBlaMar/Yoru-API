package main;


import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import main.model.Hobbie;
import main.model.User;
import main.repository.HobbieRepository;
import main.repository.UserRepository;

@SpringBootApplication
public class YoruApplication {
	
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(YoruApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initData(UserRepository userRepository, HobbieRepository hobbieRepository) {
		return args -> {
			
			User us1 = new User("Yoru",encoder.encode("123"), "franbl98@gmail.com", "", null, "ADMIN");
			us1.setNumeroPublicaciones(0);
			us1.setNumeroSeguidores(0);
			us1.setNumeroSeguidos(0);
			us1.setAboutMe("Cuenta oficila de Yoru. Conecta con tus amigos.");
			User us2 = new User("David",encoder.encode("123"), "franbl984@gmail.com", "", null, "USER");
			us2.setNumeroPublicaciones(0);
			us2.setNumeroSeguidores(0);
			us2.setNumeroSeguidos(0);
			us2.setAboutMe("cuenta personal de David, encantado!");
			userRepository.save(us1);
			userRepository.save(us2);
			
			Hobbie h1 = new Hobbie("DEPORTES");
			Hobbie h2 = new Hobbie("MUSICA");
			Hobbie h3 = new Hobbie("ARTE");
			Hobbie h4 = new Hobbie("VIAJES");
			Hobbie h5 = new Hobbie("VIDEOJUEGOS");
			Hobbie h6 = new Hobbie("LIBROS");
			Hobbie h7 = new Hobbie("PELICULAS");
			Hobbie h8 = new Hobbie("FOTOGRAFIA");
			Hobbie h9 = new Hobbie("MANUALIDADES");
			Hobbie h10 = new Hobbie("BAILE");
			
			hobbieRepository.saveAll(Arrays.asList(h1,h2,h3,h4,h5,h6,h7,h8,h9,h10));
		};
	}
	

	
	
	
	
}

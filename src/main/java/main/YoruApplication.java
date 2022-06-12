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

@SpringBootApplication
public class YoruApplication {
	
	@Autowired
	private PasswordEncoder encoder;

	public static void main(String[] args) {
		SpringApplication.run(YoruApplication.class, args);
	}
	
	@Bean
	CommandLineRunner initData(HobbieRepository hobbieRepository) {
		return args -> {
			
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

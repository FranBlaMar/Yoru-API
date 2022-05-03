package main.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import main.model.Publicacion;
import main.model.User;

@Component
public class PublicationService {

	@Autowired
	private UserService userService;
	
	
	
	public Publicacion subirPubli(MultipartFile file, String titulo, String user) {
		User us = this.userService.findById(user);
		try {
			return new Publicacion(titulo,file.getBytes(), us);
		} catch (IOException e) {
			return null;
		}
	}
	
}

package main.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import main.model.Publicacion;
import main.service.PublicationService;

@RestController
public class PublicacionController {

	@Autowired
	private PublicationService publicationService;
	
	
	/**
	 * Petición para crear publicaciones
	 * @param file Imagen de la publicacion
	 * @param titulo  Titulo de la publicacion
	 * @param user  Autor de la publicacion
	 * @return La publicacion creada
	 */
	@PostMapping("/publicacion")
	public Publicacion uploadFile(@RequestParam MultipartFile file, @RequestParam String titulo) {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
			System.out.println(file.getContentType().equals("image/jpeg"));
			return null;
		}
	    try {
	      Publicacion  newPublic =this.publicationService.subirPubli(file,titulo,user);
	      return newPublic;
	    } catch (Exception e) {
	    	System.out.println("Errorr");
	      return null;
	    }
	}
	
	/**
	 * Petición para obtener todas las publicaciones de la base de datos
	 * @return Lista con todas las publicaciones de la base de datos
	 */
	@GetMapping("/publicaciones")
	public List<Publicacion> getPublicaciones(){
		return this.publicationService.findAll();
	}

	/**
	 * Petición para obtener todas las publicaciones de un usuario
	 * @param email Email del usuario
	 * @return Lista con todas las publicaciones de un usuario
	 */
	@GetMapping("/publicaciones/{user}")
	public List<Publicacion> getPublicacionesByUser(@PathVariable  String user){
		return this.publicationService.findByUser(user);
	}
	
}

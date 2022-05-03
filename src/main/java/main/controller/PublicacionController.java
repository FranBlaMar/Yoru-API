package main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import main.model.Publicacion;
import main.service.PublicationService;

@RestController
public class PublicacionController {

	@Autowired
	private PublicationService publicationService;
	
	@PostMapping("/publicacion")
	public Publicacion uploadFile(@RequestParam MultipartFile file, @RequestParam String titulo, @RequestParam String user) {
		String message = "";
		System.out.println(file);
		System.out.println(file.getContentType());
		System.out.println(titulo);
		System.out.println(user);
		
		if(file.getContentType() != "image/jpeg" && file.getContentType() != "image/png") {
			return null;
		}
	    try {
	      Publicacion  newPublic =this.publicationService.subirPubli(file,titulo,user);
	      message = "El archivo "+file.getOriginalFilename()+" se ha subido correctamente";
	      return newPublic;
	    } catch (Exception e) {
	      message = "Error al subir el archivo: " + file.getOriginalFilename();
	      return null;
	    }
	}

	
}

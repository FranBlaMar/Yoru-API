package main.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import main.exception.ErrorManager;
import main.exception.FileUploadException;
import main.exception.FormatErrorException;
import main.exception.UserNotFoundEmailException;
import main.model.Publicacion;
import main.service.PublicationService;
import main.service.UserService;

@RestController
public class PublicacionController {

	@Autowired
	private PublicationService publicationService;
	@Autowired
	private UserService userService;
	
	/**
	 * Petición para crear publicaciones
	 * @param file Imagen de la publicacion
	 * @param titulo  Titulo de la publicacion
	 * @param user  Autor de la publicacion
	 * @return La publicacion creada
	 */
	@PostMapping("/publicacion")
	public ResponseEntity<Publicacion> uploadFile(@RequestParam MultipartFile file, @RequestParam String titulo) {
		String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
			throw new FormatErrorException();
		}
	    try {
	      Publicacion  newPublic =this.publicationService.subirPubli(file,titulo,user);
	      return ResponseEntity.status(HttpStatus.CREATED).body(newPublic);
	    } catch (Exception e) {
	    	throw new FileUploadException();
	    }
	}
	
	
	@PutMapping("/publicacion")
	public ResponseEntity<Publicacion> editarPublicacion(@RequestBody Publicacion publi) {
		return ResponseEntity.status(HttpStatus.CREATED).body(this.publicationService.editarPublicacion(publi));
	}
	
	/**
	 * Petición para obtener todas las publicaciones de la base de datos
	 * @return Lista con todas las publicaciones de la base de datos
	 */
	@GetMapping("/publicacion")
	public ResponseEntity<List<Publicacion>> getPublicaciones(){
		return ResponseEntity.status(HttpStatus.OK).body(this.publicationService.findAll());
	}

	/**
	 * Petición para obtener todas las publicaciones de un usuario
	 * @param email Email del usuario
	 * @return Lista con todas las publicaciones de un usuario
	 */
	@GetMapping("/publicaciones/{user}")
	public ResponseEntity<List<Publicacion>> getPublicacionesByUser(@PathVariable  String user){
		if(this.userService.findById(user) == null) {
			throw new UserNotFoundEmailException();
		}
		return ResponseEntity.status(HttpStatus.OK).body(this.publicationService.findByUser(user));
	}
	
	
	
    /**
	 * Metodo handler de exception de error de formato
	 * @param ex excepción lanzada
	 * @return Excepción modificada por nosotros
	 */
    @ExceptionHandler(FormatErrorException.class)
	public ResponseEntity<ErrorManager> handleErrorFormato(FormatErrorException ex) {
		ErrorManager apiError = new ErrorManager();
		apiError.setRequestStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
		apiError.setDate(LocalDateTime.now());
		apiError.setErrorMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiError);
	}
    
    /**
   	 * Metodo handler de exception de error al subir la imagen
   	 * @param ex excepción lanzada
   	 * @return Excepción modificada por nosotros
   	 */
       @ExceptionHandler(FileUploadException.class)
   	public ResponseEntity<ErrorManager> handleFileUploadException(FileUploadException ex) {
   		ErrorManager apiError = new ErrorManager();
   		apiError.setRequestStatus(HttpStatus.BAD_REQUEST);
   		apiError.setDate(LocalDateTime.now());
   		apiError.setErrorMessage(ex.getMessage());
   		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
   	}
    
}

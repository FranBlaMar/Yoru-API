package main.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import main.model.Publicacion;
import main.model.User;
import main.repository.PublicacionRepository;

@Component
public class PublicationService {

	@Autowired
	private UserService userService;
	@Autowired
	private PublicacionRepository repositorio;
	
	
	/**
	 * Método para crear un publicación y subirla a la base de datos
	 * @param file Imagen a guardar
	 * @param titulo  Titulo de la imagen
	 * @param user Usuario que ha creado la publicacion
	 * @return Publicacion creada
	 */
	public Publicacion subirPubli(MultipartFile file, String titulo, String user) {
		User us = this.userService.findById(user);
		Publicacion publi = null;
		try {
			publi = new Publicacion(titulo,file.getBytes(), us);
			us.anadirPublicacion(publi);
			us.setNumeroPublicaciones(us.getNumeroPublicaciones()+1);
			this.userService.saveUser(us);

		} catch (IOException e1) {
			return null;
		}
		return publi;
	}
	
	/**
	 * Método para obtener todas las publicaciones de la base de datos
	 * @return Lista con todas las publicaciones de la base de datos
	 */
	public List<Publicacion> findAll(){
		return this.repositorio.findAll();
	}
	
	/**
	 * Método para obtener todas las publicaciones de un usuario
	 * @param email Email del usuario
	 * @return Lista con todas las publicaciones
	 */
	public List<Publicacion> findByUser(String email){
		User us = this.userService.findById(email);
		return this.repositorio.findByAutor(us);
	}
	
	/**
	 * Método para editar una publicación
	 * @param publi publicacion editada
	 * @return Publicacion editada
	 */
	public Publicacion editarPublicacion(Publicacion publi) {
		return this.repositorio.save(publi);
	}
	
	/**
	 * Método para obtener una publicacion por su id
	 * @param id id de la publicacion
	 * @return la publicacion buscada
	 */
	public Publicacion findById(Long id) {
		return this.repositorio.findById(id).orElse(null);
	}
}

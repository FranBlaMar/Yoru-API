package main.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import main.model.Publicacion;
import main.model.User;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
	
	public List<Publicacion> findByAutor(User us);
	
	@Transactional
	@Modifying //Para indicar que la query va a modificar la base de datos
	@Query(value= "DELETE FROM yoru.usuario_publicaciones_gustadas WHERE publicaciones_gustadas_id_publicacion = ?1", nativeQuery = true)
	public void deletePublicacionEnPublicacionesGustadas(Long idPublicacion);
}
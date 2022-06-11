package main.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import main.model.Publicacion;
import main.model.User;


public interface UserRepository extends JpaRepository<User, String> {
	
	public List<User> findByUserName(String userName);
	
	@Query("SELECT p FROM Publicacion p, User u JOIN u.seguidos s WHERE u.email =:idUsuario AND s.email = p.autor.email ORDER BY p.fechaPublicacion")
	public List<Publicacion> findAllPublicacionesSeguidos(@Param("idUsuario")String idUsuario, Pageable pageable);
	
	@Query("SELECT u FROM User u JOIN u.hobbie h WHERE h.hobbie =:hobbie")
	public List<User> findUserByHobbie(@Param("hobbie")String hobbie, Pageable pageable);

}
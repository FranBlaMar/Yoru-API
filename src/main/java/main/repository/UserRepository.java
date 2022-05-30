package main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import main.model.Publicacion;
import main.model.User;


public interface UserRepository extends JpaRepository<User, String> {
	
	public List<User> findByUserName(String userName);
	
	@Query("SELECT p FROM Publicacion p, User u JOIN u.seguidos s WHERE u.email = :idUsuario AND s.email = p.autor.email")
	public List<Publicacion> findAllPublicacionesSeguidos(String idUsuario);
}
package main.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import main.model.Publicacion;
import main.model.User;

public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
	
	public List<Publicacion> findByAutor(User us);
}
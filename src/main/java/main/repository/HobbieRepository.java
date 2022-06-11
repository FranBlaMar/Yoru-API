package main.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import main.model.Hobbie;

public interface HobbieRepository extends JpaRepository<Hobbie, Long> {
	
	@Query("SELECT h FROM Hobbie h WHERE h.hobbie =:hobbie")
	public List<Hobbie> findHobbieByHobbie(String hobbie);
	
}
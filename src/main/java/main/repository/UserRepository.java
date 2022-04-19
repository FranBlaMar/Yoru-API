package main.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import main.model.User;


public interface UserRepository extends JpaRepository<User, String> {
	
	public List<User> findByUserName(String userName);
	
}
package main.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


import main.model.User;
import main.repository.UserRepository;


/**
 * Servicio donde gestionamos la lógica de negocio de usuarios
 * @author usuario
 *
 */
@Component
public class UserService implements UserDetailsService {

    @Autowired 
    private UserRepository repository;

   
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> userRes = repository.findById(userName);
        if(userRes.isEmpty())
            throw new UsernameNotFoundException("No se pudo encontrar un usuario con el nombre de usuario " + userName);
        User user = userRes.get();
        return new org.springframework.security.core.userdetails.User(
                userName,
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
    
    /**
     * Método para obtener un usuario mediante su correo electronico
     * @param Email El correo del usuario buscado
     * @return El usuario obtenido o null
     */
    public User findById(String email) {
    	return this.repository.findById(email).orElse(null);	
    }
    
    
    /**
     * Método para obtener usuario por su nombre de usuario
     * @return usuario encontrado
     */
    public List<User> findByUserName(String userName){
    	return this.repository.findByUserName(userName);
    }
    
    /**
     * Método para guardar un usuario en la base de datos
     * @param newUser Usuario que vamos a guardar
     * @return El usuario guardado
     */
    public User saveUser(User newUser) {
    	return this.repository.save(newUser);
    }
    
    /**
     * Método para comprobar que no existe un correo electronico en la base de datos
     * @param email correo a comprobar
     * @return true o false dependiendo de si está disponible el correo
     */
    public boolean comprobarCorreo(String email) {
    	User user= this.repository.findById(email).orElse(null);

    	if(user != null) {
    		return false;
    	}
    	else {
    		return true;
    	}
    }
}
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

import main.model.Publicacion;
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
    @Autowired
    private PublicationService servicio;
   
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
    
    /**
     * Método para editar un usuario 
     * @param user usuario nuevo
     * @return usuario ya editado
     */
    public User editarUsuario(User user) {
    	User userAntiguo = this.repository.findById(user.getEmail()).orElse(null);
    	user.setPassword(userAntiguo.getPassword());
    	user.setPublicaciones(userAntiguo.getPublicaciones());
    	return this.repository.save(user);
    }
    
    /**
     * Método para cuando un usuario siga a otro este se añada a la lista de seguidores del otro usuario y el otro usuario
     * se añada a la lista de seguidos del usuario que ha empezado a seguir al otro.
     * @param seguidor Usuario que ha empezado a seguir a un usuario
     * @param seguido El usuario al que el primer usario ha empezado a seguir
     * @return El usuario que ha empezado a seguir
     */
    public User seguirUsuario(String seguidor, String seguido) {
    	User usuarioSeguidor = this.repository.findById(seguidor).orElse(null);
    	User usuarioSeguido = this.repository.findById(seguido).orElse(null);
    	
    	usuarioSeguidor.anadirSeguido(usuarioSeguido);
    	usuarioSeguidor.setNumeroSeguidos(usuarioSeguidor.getSeguidos().size());
    	
    	usuarioSeguido.anadirSeguidor(usuarioSeguidor);
    	usuarioSeguido.setNumeroSeguidores(usuarioSeguido.getSeguidores().size());
    	
    	this.repository.save(usuarioSeguido);
    	return this.repository.save(usuarioSeguidor);
    }
    
    /**
     * Método para dejar de seguir a un usuario y borrar a ambos usuarios de la lista de seguidores y seguidos
     * @param seguidor El usuario que ha dejado de seguir al otro
     * @param seguido El usuario al que han dejado de seguir
     * @return El usuario que ha dejado de seguir al otro
     */
    public User dejarDeSeguirUsuario(String seguidor, String seguido) {
    	User usuarioSeguidor = this.repository.findById(seguidor).orElse(null);
    	User usuarioSeguido = this.repository.findById(seguido).orElse(null);
    	
    	List<User> listaSeguidos = usuarioSeguidor.getSeguidos();
    	listaSeguidos.remove(usuarioSeguido);
    	usuarioSeguidor.setSeguidos(listaSeguidos);
    	usuarioSeguidor.setNumeroSeguidos(usuarioSeguidor.getSeguidos().size());
    	
    	List<User> listaSeguidores = usuarioSeguido.getSeguidores();
    	listaSeguidores.remove(usuarioSeguidor);
    	usuarioSeguido.setSeguidores(listaSeguidores);
    	usuarioSeguido.setNumeroSeguidores(usuarioSeguido.getSeguidores().size());
    	
    	this.repository.save(usuarioSeguido);
    	return this.repository.save(usuarioSeguidor);
    }
    
    /**
     * Método para obtener todos los usuarios a los que sigue el usuario logueado
     * @return Lista de usuarios seguidos
     */
    public List<User> findAll(String usuario){
    	User user = this.repository.findById(usuario).orElse(null);
    	
    	return user.getSeguidos();
    }
    
    /**
     * Método para encontrar un usuario seguido
     * @param usuarioLogueado usuario logueado
     * @param usuarioSeguido usuario que estmos buscando en seguidos
     * @return Usuario seguido o null
     */
    public User findSeguido(String usuarioLogueado, String usuarioSeguido) {
    	User user = this.repository.findById(usuarioLogueado).orElse(null);
    	User userSeguido = this.repository.findById(usuarioSeguido).orElse(null);
    	User result = null;
    	
    	if(user.getSeguidos().contains(userSeguido)) {
    		result = userSeguido;
    	}

    	return result;

    }
    
    /**
     * Método para obtener las publicaciones gustadas de un usuario
     * @param usuario usuario logueado
     * @return Lista de publicaciones gustadas
     */
    public List<Publicacion> getPublicacionesgustadas(String usuario){
    	User user = findById(usuario);
    	return user.getPublicacionesGustadas();
    }
    
    
    /**
     * Método para añadir una publicacion a la lista de publicaciones gustadas de un usuario
     * @param usuario usuario que le ha gustado la publicacion
     * @param publicacion publicacion a la que el usuario ha dado like
     * @return La publicacion a la que han dado like
     */
    public Publicacion anadirPublicacionGustada(String usuario, Long publicacion) {
    	User user = findById(usuario);
    	Publicacion publi = this.servicio.findById(publicacion);
    	publi.setLikes(publi.getLikes()+1);
    	
    	user.anadirPublicacionGustada(publi);
    	this.repository.save(user);
    	return publi;
    }
    
    /**
     * Método para borrar una publicacion de la lista de megustas de un usuario
     * @param usuario Usuario al que ha dejado de gustarle la publicacion
     * @param publicacion Publicacion que ha dejado de gustar
     * @return La publicacion que ha dejado de gustar
     */
    public Publicacion eliminarPublicacionGustada(String usuario, Long publicacion) {
    	User user = findById(usuario);
    	Publicacion publi = this.servicio.findById(publicacion);
    	publi.setLikes(publi.getLikes()-1);
    	user.getPublicacionesGustadas().remove(publi);
    	this.repository.save(user);
    	return publi;
    }
}
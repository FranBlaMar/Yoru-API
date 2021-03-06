package main.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import main.exception.ErrorManager;
import main.exception.ExistingEmailException;
import main.exception.ExistingUserException;
import main.exception.FormatErrorException;
import main.exception.HobbieNotFoundException;
import main.exception.PublicacionNotFoundException;
import main.exception.UserNotFoundEmailException;
import main.exception.UserNotFoundPasswordException;
import main.model.Hobbie;
import main.model.Publicacion;
import main.model.User;
import main.model.UserLogin;
import main.security.UtilJWT;
import main.service.EmailSenderService;
import main.service.UserService;

/**
 * Controller donde gestionar las peticiones a la clase usuario
 * @author usuario
 *
 */


@RestController
public class UserController {
	
	 	
	    @Autowired 
	    private UtilJWT jwtUtil;
	    
	    @Autowired 
	    private AuthenticationManager authManager;
	    
	    @Autowired 
	    private PasswordEncoder passwordEncoder;
	    
	    @Autowired
	    private UserService service;

	    @Autowired
	    private EmailSenderService serviceEmail;
	    
	    
	    
	    /**
	     * Metodo para registrar un usuario
	     * @param user Usuario que se desea a??adir
	     * @return Token jwt generado
	     */
	    @PostMapping("/auth/register")
	    public Map<String, Object> registerHandler(@RequestParam String userName,@RequestParam String password, @RequestParam String email, @RequestParam("file") MultipartFile file) throws FormatErrorException, ExistingUserException{
	    	User verificar = this.service.findById(email);
	    	if(verificar != null) {
	    		throw new ExistingUserException();
	    	}	
	    	if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
				throw new FormatErrorException();
			}
	        String encodedPass = passwordEncoder.encode(password);
	        User usuarioNuevo = null;
			try {
				usuarioNuevo = new User(userName, encodedPass ,email, "", file.getBytes(), "USER");
			} catch (IOException e) {
				throw new FormatErrorException();
			}
	        usuarioNuevo = this.service.saveUser(usuarioNuevo);
	        String token = jwtUtil.generateToken(usuarioNuevo.getEmail());
	        return Collections.singletonMap("jwt_token", token);
	    }

	    /**
	     * Metodo para hacer login y generar el token del user
	     * @param userLogin usuario introducido en el formulario
	     * @return Token jwt generado
	     */
	    @PostMapping("/auth/login")
	    public Map<String, Object> loginHandler(@RequestBody UserLogin userLogin){
	        try {
	            UsernamePasswordAuthenticationToken authInputToken =
	            new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword());
	            authManager.authenticate(authInputToken);

	            String token = jwtUtil.generateToken(userLogin.getEmail());

	            return Collections.singletonMap("jwt_token", token);
	        }catch (AuthenticationException authExc){
	        	if(this.service.findById(userLogin.getEmail()) != null) {
	        		throw new UserNotFoundPasswordException();
	        	}
	        	else {
	        		 throw new UserNotFoundEmailException();
		        }
	        }
	           
	    }
	    
	    
	    
	    /**
	     * Metodo para enviar un codigo de verificaci??n al correo electronico
	     * @param email Email del destinatario
	     * @return Codigo de verirficacion generado
	     * @throws MessagingException
	     */
	    @PostMapping("/auth/verification")
	    public ResponseEntity<Integer> emailVerify(@RequestBody String email) throws MessagingException{

	    	if(this.service.comprobarCorreo(email)) {
				return ResponseEntity.status(HttpStatus.CREATED).body(this.serviceEmail.sendVerificationEmail(email));
	    	}
	    	else {
	    		throw new ExistingEmailException();
	    	}
	    }
	    
	    
	    /**
		 * Metodo handler de exception de usuario no encontrado
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(UserNotFoundPasswordException.class)
		public ResponseEntity<ErrorManager> handleUsuarioNoEncontradoPassword(UserNotFoundPasswordException ex) {
			ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de tama??o de imagen
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(MaxUploadSizeExceededException.class)
	    public ResponseEntity<ErrorManager> handleFileSizeLimitExceeded(MaxUploadSizeExceededException ex) {
	    	ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage("La imagen ha superado el l??mite de tama??o permitido");
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiError);
	    }
	    
	    /**
		 * Metodo handler de exception de usuario no encontrado
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(UserNotFoundEmailException.class)
		public ResponseEntity<ErrorManager> handleUsuarioNoEncontrado(UserNotFoundEmailException ex) {
			ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de publicacion no encontrada
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(PublicacionNotFoundException.class)
		public ResponseEntity<ErrorManager> handlePublicacionNoEncontrada(PublicacionNotFoundException ex) {
			ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de usuario ya existente
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(ExistingUserException.class)
		public ResponseEntity<ErrorManager> handleUsuarioExistente(ExistingUserException ex) {
	    	ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.BAD_REQUEST);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de hobbie no encontrado
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(HobbieNotFoundException.class)
		public ResponseEntity<ErrorManager> handleHobbieNotFound(HobbieNotFoundException ex) {
	    	ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de correo ya existente
		 * @param ex excepci??n lanzada
		 * @return Excepci??n modificada por nosotros
		 */
	    @ExceptionHandler(ExistingEmailException.class)
		public ResponseEntity<ErrorManager> handleCorreoExistente(ExistingEmailException ex) {
	    	ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.BAD_REQUEST);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
		}
	    
	    /**
	     * Metodo para obtener un usuario mediante el token de autenticaci??n
	     * @return Usuario al que pertenece el token de la cabecera de la petici??n
	     */
	    @GetMapping("/user")
	    public ResponseEntity<User> getUser() throws UserNotFoundEmailException{ 	
	        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        User us = this.service.findById(email);
	        if(us == null) {
	        	throw new UserNotFoundEmailException();
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(this.service.findById(email));
	    }
	    
	    
	    
	    /**
	     * Metodo para obtener un usuario por email, para comprobaci??n reactiva del formulario de registro
	     * @param userName nombre de usuario del usuario
	     * @return Usuario encontrado o null
	     * @throws UsuarioNotFoundException
	     */
	    @GetMapping("/user/{userName}")
	    public ResponseEntity<List<User>> getUserPorUserName(@PathVariable String userName) throws UserNotFoundEmailException{ 	
	        return ResponseEntity.status(HttpStatus.OK).body(this.service.findByUserName(userName));
	    }
	    
	    /**
	     * M??todo para editar un usuario
	     * @param usuario usuario editado
	     * @return el usuario editado
	     */
	    @PutMapping("/user")
	    public ResponseEntity<User> putUser(@RequestBody User usuario) {
	    	System.out.println(usuario.getHobbie());
	    	 return ResponseEntity.status(HttpStatus.CREATED).body(this.service.editarUsuario(usuario));
	    }
	    
	    
	    /**
	     * M??todo que devuelve un usuario al que el usuario logueado sigue, mediante su email
	     * @param comprobarSeguido Usuario que querems obtener
	     * @return Usuario encontrado
	     */
	    @GetMapping("/user/follower/{usuarioComprobar}")
	    public ResponseEntity<User> comprobarSiEsSeguidor(@PathVariable String usuarioComprobar){
	    	 String usuarioLogueado = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	 return ResponseEntity.status(HttpStatus.OK).body(this.service.findSeguido(usuarioLogueado, usuarioComprobar));
	    }
	    
	    /**
	     * M??todo para obtener todos los usuarios seguidos por el usuario logueado
	     * @return Lista de usuarios
	     */
	    @GetMapping("/user/follower")
	    public ResponseEntity<List<User>> findAll(){
	    	 String usuario = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();	 
	    	 return ResponseEntity.status(HttpStatus.OK).body(this.service.findAll(usuario));
	    }
	    
	    /**
	     * M??todo para seguir a un usuario
	     * @param seguidor Usuario que ha empezado a seguir al otro
	     * @param seguido El usuario al que el primer user ha empezado a seguir
	     * @return El usuario que ha empezado a seguir
	     */ 
	    @PostMapping("/user/follower")
	    public ResponseEntity<User> followUser(@RequestParam String seguido){
	    	 String seguidor = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	 return ResponseEntity.status(HttpStatus.CREATED).body(this.service.seguirUsuario(seguidor, seguido));
	    }
	    
	    /**
	     * M??todo para dejar de seguir a un usuario
	     * @param seguidor Usuario que ha dejado de seguir al otro
	     * @param seguido El usuario al que han dejado de seguir
	     * @return Usuario que ha dejado de seguir al otro
	     */ 
	    @DeleteMapping("/user/follower/{seguido}")
	    public ResponseEntity<User> unfollowUser(@PathVariable String seguido){
	    	 String seguidor = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	 return ResponseEntity.status(HttpStatus.OK).body(this.service.dejarDeSeguirUsuario(seguidor, seguido));
	    }
	    
	    
	    /**
	     * M??todo para a??adir una publicacion a la lista de publicaciones gustadas de un usuario
	     * @param publicacion id de lapublicacion a la que el usuario ha dado like
	     * @return La publicacion a la que han dado like
	     */
	    @PostMapping("/user/publicacionesGustadas")
	    public ResponseEntity<Publicacion> addPublicacionGustada(@RequestParam String publicacion) {
	    	String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	Long publi = Long.valueOf(publicacion); 
	    	Publicacion result = this.service.anadirPublicacionGustada(user, publi);
	    	if(result == null) {
	    		 throw new PublicacionNotFoundException();
	    	}
	    	return ResponseEntity.status(HttpStatus.CREATED).body(result);
	    }
	    
	    
	    /**
	     * M??todo para obtener las publicaciones que le han gustado a un usuario
	     * @return Lista de publicaciones
	     */
	    @GetMapping("/user/publicacionesGustadas")
	    public ResponseEntity<List<Publicacion>> getPublicacionesgustadas(){
	    	 String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	 return ResponseEntity.status(HttpStatus.OK).body(this.service.getPublicacionesgustadas(user));
	    }
	    
	    /**
	     * M??todo para borrar una publicacion de la lista de megustas de un usuario
	     * @param publicacion Publicacion que ha dejado de gustar
	     * @return La publicacion que ha dejado de gustar
	     */
	   @DeleteMapping("/user/publicacionesGustadas/{publicacion}")
	   public ResponseEntity<Publicacion> deletePublicacionGustada(@PathVariable String publicacion){
	    	 String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    	 Long publi = Long.valueOf(publicacion);
	    	 Publicacion result = this.service.eliminarPublicacionGustada(user, publi);
	    	 if(result == null) {
	    		 throw new PublicacionNotFoundException();
	    	 }
	    	 return ResponseEntity.status(HttpStatus.OK).body(result);
	   }
	   
	   /**
	    * M??todo para editar la foto de perfil de un usuario
	    * @param file foto de perfil
	    * @return usuario editado
	    */
	   @PutMapping("/user/fotoPerfil")
	   public ResponseEntity<User> editarfotoPerfil(@RequestParam("file") MultipartFile file){
		   String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		   if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
				throw new FormatErrorException();
			}
		   User us = this.service.findById(user);
	        if(us == null) {
	        	throw new UserNotFoundEmailException();
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(this.service.cambiarFotoPerfil(us, file));
	   }
	   
	   /**
	    * M??todo para obtener las publicaciones de los usuarios a los que sigue el user logueado
	    * @return lista de publicaciones
	    */
	   @GetMapping("/user/follower/publicacion/{offSet}")
	   public ResponseEntity<List<Publicacion>> getAllPublicacionesSeguidos(@PathVariable Integer offSet){
		   String user = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		   User us = this.service.findById(user);
	        if(us == null) {
	        	throw new UserNotFoundEmailException();
	        }
	        return ResponseEntity.status(HttpStatus.OK).body(this.service.obtenerPublicacionesSeguidos(user, offSet));
	   }
	   
	   /**
	    * M??todo para obtener usuarios mediante su hobbie
	    * @param hobbie hobbie del usuario
	    * @param offSet Pagination
	    * @return Lista de usuarios
	    */
	   @GetMapping("/user/{hobbie}/{offSet}")
	   public ResponseEntity<List<User>> getUsersByHobbie(@PathVariable String hobbie, @PathVariable Integer offSet){
		   if(this.service.buscarHobbiePorNombre(hobbie.toUpperCase()) == null) {
			   throw new HobbieNotFoundException();
		   }
	       return ResponseEntity.status(HttpStatus.OK).body(this.service.buscarUsersPorHobbie(hobbie.toUpperCase(), offSet));
	   }
	   
	   /**
	    * M??todo para obtener todos los hobbies
	    * @return lista de hobbies
	    */
	   @GetMapping("/hobbie")
	   public ResponseEntity<List<Hobbie>> getAllHobbies(){
	       return ResponseEntity.status(HttpStatus.OK).body(this.service.findAllHobbies());
	   }
	   
}

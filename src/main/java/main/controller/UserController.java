package main.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import main.exception.ErrorManager;
import main.exception.ExistingEmailException;
import main.exception.ExistingUserException;
import main.exception.UserNotFoundException;
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
	     * @param user Usuario que se desea añadir
	     * @return Token jwt generado
	     */
	    @PostMapping("/auth/register")
	    public Map<String, Object> registerHandler(@RequestBody User user) throws ExistingUserException{
	    	System.out.println(user);
	    	User verificar = this.service.findById(user.getEmail());
	    	if(verificar != null) {
	    		throw new ExistingUserException();
	    	}
	        String encodedPass = passwordEncoder.encode(user.getPassword());
	        user.setPassword(encodedPass);
	        user = this.service.saveUser(user);
	        String token = jwtUtil.generateToken(user.getEmail());
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
	            throw new UserNotFoundException();
	        }
	    }
	    
	    
	    /**
	     * Metodo para enviar un codigo de verificación al correo electronico
	     * @param email Email del destinatario
	     * @return Codigo de verirficacion generado
	     * @throws MessagingException
	     */
	    @PostMapping("auth/verification")
	    public int emailVerify(@RequestBody String email) throws MessagingException{

	    	if(this.service.comprobarCorreo(email)) {
				return this.serviceEmail.sendVerificationEmail(email);
	    	}
	    	else {
	    		throw new ExistingEmailException();
	    	}
	    }
	    
	    
	    /**
		 * Metodo handler de exception de usuario no encontrado
		 * @param ex excepción lanzada
		 * @return Excepción modificada por nosotros
		 */
	    @ExceptionHandler(UserNotFoundException.class)
		public ResponseEntity<ErrorManager> handleUsuarioNoEncontrado(UserNotFoundException ex) {
			ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de usuario ya existente
		 * @param ex excepción lanzada
		 * @return Excepción modificada por nosotros
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
		 * Metodo handler de exception de correo ya existente
		 * @param ex excepción lanzada
		 * @return Excepción modificada por nosotros
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
	     * Metodo para obtener un usuario mediante el token de autenticación
	     * @return Usuario al que pertenece el token de la cabecera de la petición
	     */
	    @GetMapping("/user")
	    public User getUser(){ 	
	        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        return this.service.findById(email);
	    }
	    
	    /**
	     * Metodo para obtener un usuario por email, para comprobación reactiva del formulario de registro
	     * @param userName nombre de usuario del usuario
	     * @return Usuario encontrado o null
	     * @throws UsuarioNotFoundException
	     */
	    @GetMapping("/auth/{userName}")
	    public List<User> getUserPorUserName(@PathVariable String userName) throws UserNotFoundException{ 	
	        List<User> result = this.service.findByUserName(userName);
	        if(result.isEmpty()) {
	        	 return null;
	        }
	        return result;
	    }
	    
}

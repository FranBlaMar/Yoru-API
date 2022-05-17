package main.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import main.exception.ErrorManager;
import main.exception.ExistingEmailException;
import main.exception.ExistingUserException;
import main.exception.FormatErrorException;
import main.exception.UserNotFoundEmailException;
import main.exception.UserNotFoundPasswordException;
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
	    public Map<String, Object> registerHandler(@RequestParam String userName,@RequestParam String password, @RequestParam String email, @RequestParam("file") MultipartFile file) throws FormatErrorException, ExistingUserException{
	    	User verificar = this.service.findById(email);
	    	System.out.println(file);
	    	if(verificar != null) {
	    		throw new ExistingUserException();
	    	}	
	    	if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
				throw new FormatErrorException();
			}
	        String encodedPass = passwordEncoder.encode(password);
	        User usuarioNuevo = null;
			try {
				usuarioNuevo = new User(userName, encodedPass ,email, "", file.getBytes());
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
	    @ExceptionHandler(UserNotFoundPasswordException.class)
		public ResponseEntity<ErrorManager> handleUsuarioNoEncontradoPassword(UserNotFoundPasswordException ex) {
			ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.NOT_FOUND);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage(ex.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
		}
	    
	    /**
		 * Metodo handler de exception de tamaño de imagen
		 * @param ex excepción lanzada
		 * @return Excepción modificada por nosotros
		 */
	    @ExceptionHandler(MaxUploadSizeExceededException.class)
	    public ResponseEntity<ErrorManager> handleFileSizeLimitExceeded(MaxUploadSizeExceededException ex) {
	    	ErrorManager apiError = new ErrorManager();
			apiError.setRequestStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
			apiError.setDate(LocalDateTime.now());
			apiError.setErrorMessage("La imagen ha superado el límite de tamaño permitido");
			return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiError);
	    }
	    
	    /**
		 * Metodo handler de exception de usuario no encontrado
		 * @param ex excepción lanzada
		 * @return Excepción modificada por nosotros
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
	    public User getUser() throws UserNotFoundEmailException{ 	
	        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	        User us = this.service.findById(email);
	        if(us == null) {
	        	throw new UserNotFoundEmailException();
	        }
	        return this.service.findById(email);
	    }
	    
	    
	    
	    /**
	     * Metodo para obtener un usuario por email, para comprobación reactiva del formulario de registro
	     * @param userName nombre de usuario del usuario
	     * @return Usuario encontrado o null
	     * @throws UsuarioNotFoundException
	     */
	    @GetMapping("/user/{userName}")
	    public List<User> getUserPorUserName(@PathVariable String userName) throws UserNotFoundEmailException{ 	
	        List<User> result = this.service.findByUserName(userName);
	        if(result.isEmpty()) {
	        	throw new UserNotFoundEmailException();
	        }
	        return result;
	    }
	    
}

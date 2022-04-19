package main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Clase usuariologin para pasar uan clase usuarioLogin al formulario de login y comprobar los credenciales
 * @author usuario
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserLogin {
	private String Email;
	private String password;
}

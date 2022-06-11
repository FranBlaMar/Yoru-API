package main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HobbieNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6734027569391630482L;
	
	public HobbieNotFoundException() {
		super("El hobbie buscado no está incluido aún en nuestra web");
	}
}
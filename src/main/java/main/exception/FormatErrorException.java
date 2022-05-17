package main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.NOT_FOUND)
public class FormatErrorException extends RuntimeException {

	private static final long serialVersionUID = -6734027569391630482L;
	
	public FormatErrorException() {
		super("Formato o tamaño de imagen no válido");
	}
}
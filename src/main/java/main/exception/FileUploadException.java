package main.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileUploadException extends RuntimeException {

	private static final long serialVersionUID = -6734027569391630482L;
	
	public FileUploadException() {
		super("Se ha producido un error inesperado al intentar almacenar la imagen");
	}
}
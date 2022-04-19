package main.exception;




public class ExistingUserException extends RuntimeException {

	private static final long serialVersionUID = -6734027569391630482L;
	
	public ExistingUserException() {
		super("Ya existe un usuario con ese nombre de usuario");
	}
}

package main.exception;



public class ExistingEmailException extends RuntimeException  {
	private static final long serialVersionUID = -6734027569391630482L;
	
	public ExistingEmailException() {
		super("Ya existe un usuario con ese email");
	}
}

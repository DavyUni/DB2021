package exceptions;

public class IllegalUpdateException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalUpdateException() {
		super("You cannot perform that update query");

	}

	public IllegalUpdateException(String string) {
		super(string);
	}

}

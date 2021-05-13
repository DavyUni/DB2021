package exceptions;

public class IllegalQueryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalQueryException() {
		super("You cannot perform that query");

	}

	public IllegalQueryException(String string) {
		super(string);
	}

}

package exceptions;

public class IllegalInsertException extends IllegalQueryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalInsertException() {
		super("You cannot perform that insert query");

	}

	public IllegalInsertException(String string) {
		super(string);
	}

}

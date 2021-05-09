package exceptions;

/**
 * 
 * @author Iker, Davy and Juyoung
 *
 */
public class UnsuccessfulUpdate extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsuccessfulUpdate() {
		super("The update failed!");
	}

	public UnsuccessfulUpdate(String s) {
		super(s);
	}

}

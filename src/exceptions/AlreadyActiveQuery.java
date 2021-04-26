package exceptions;
/**
 * 
 * @author Iker, Davy and Juyoung
 *
 */
public class AlreadyActiveQuery extends Exception {
	
	private static final long serialVersionUID = 1L;

	public AlreadyActiveQuery() {
		super("There is already an active query, before performing a new one you will need to terminate the last one");
	}
	
	public AlreadyActiveQuery(String s) {
		super(s);
	}
	
}

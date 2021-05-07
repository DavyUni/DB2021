package exceptions;
/**
 * 
 * @author Iker, Davy and Juyoung
 *
 */
public class NoQueryToClose extends Exception {

	private static final long serialVersionUID = 1L;
	
	public NoQueryToClose() {
		super("There is no active query, you can't close anything");
	}
	
	public NoQueryToClose(String s) {
		super(s);
	}
	
}

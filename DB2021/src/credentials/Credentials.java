package credentials;

/**
 * Container class for the credentials, the credentials are encripted using a
 * very basic encripting algorithm
 * 
 * @author Iker, Davy and Juyoung
 *
 */

public class Credentials {
	private static final String USER = ">=@F05";
	private static final String PASSWORD = "CUcWjggc^[.02";
	private static final String DATABASE = "^]`f05";

	public static String getUser() {

		char[] temp = USER.toCharArray();
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

	public static String getPassword() {

		char[] temp = PASSWORD.toCharArray();
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

	public static String getDatabase() {

		char[] temp = DATABASE.toCharArray();
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

}

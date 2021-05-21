package credentials;

/**
 * Container class for the credentials, the credentials are encripted using a
 * very basic encripting algorithm
 * 
 * @author Iker, Davy and Juyoung
 *
 */

public class Credentials {
	/**
	 * Davy's encrytpted user
	 */
	private static final String USER_D = ">=@F05";
	/**
	 * Davy's encrytpted password
	 */
	private static final String PASSWORD_D = "CUcWjggc^[.02";
	/**
	 * Davy's encrytpted database
	 */
	private static final String DATABASE_D = "^]`f05";
	/**
	 * Iker's encrytpted user
	 */
	private static final String USER_I = ">=@F/8";
	/**
	 * Iker's encrytpted password
	 */
	private static final String PASSWORD_I = "`hf\\ehcq";
	/**
	 * Iker's encrytpted database
	 */
	private static final String DATABASE_I = "^]`f/8";
	/**
	 * Juyoung's encrytpted user
	 */
	private static final String USER_J = ">=@F.8";
	/**
	 * Juyoung's encrytpted password
	 */
	private static final String PASSWORD_J = ">=@F.8";
	/**
	 * Juyoung's encrytpted database
	 */
	private static final String DATABASE_J = "^]`f.8";

	/**
	 * method that decrypts the user
	 * 
	 * @param j int that represents the user: 1-Davy, 2-Iker and 3-Juyoung
	 * @return the decrypted user
	 */
	public static String getUser(int j) {

		char[] temp = null;
		if (j == 1) {
			temp = USER_D.toCharArray();
		} else if (j == 2) {
			temp = USER_I.toCharArray();
		} else if (j == 3) {
			temp = USER_J.toCharArray();
		} else {
			return null;
		}
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

	/**
	 * method that decrypts the password
	 * 
	 * @param j int that represents the user: 1-Davy, 2-Iker and 3-Juyoung
	 * @return the decrypted password
	 */
	public static String getPassword(int j) {

		char[] temp = null;
		if (j == 1) {
			temp = PASSWORD_D.toCharArray();
		} else if (j == 2) {
			temp = PASSWORD_I.toCharArray();
		} else if (j == 3) {
			temp = PASSWORD_J.toCharArray();
		} else {
			return null;
		}
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

	/**
	 * method that decrypts the database
	 * 
	 * @param j int that represents the user: 1-Davy, 2-Iker and 3-Juyoung
	 * @return the decrypted database
	 */
	public static String getDatabase(int j) {

		char[] temp = null;
		if (j == 1) {
			temp = DATABASE_D.toCharArray();
		} else if (j == 2) {
			temp = DATABASE_I.toCharArray();
		} else if (j == 3) {
			temp = DATABASE_J.toCharArray();
		} else {
			return null;
		}
		String sol = "";

		for (int i = 0; i < temp.length; i++) {
			sol = sol + (char) (temp[i] + temp.length - i);
		}
		return sol;
	}

}

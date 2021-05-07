package main;

import java.sql.SQLException;
import java.util.Scanner;

import credentials.Credentials;
import managing.QueryConsole;

/**
 * 
 * @author Iker, Davy, Juyoung
 *
 */
public class Menu {

	private static QueryConsole qmaker;

	private static String s = "--------------------------------------------------------------\n"
			+ "|                      Select a choice:                      |\n"
			+ "|------------------------------------------------------------|\n"
			+ "| type 1 if you want to access to an standard account and DB |\n"
			+ "| type 2 to introduce your own account and DataBase          |\n"
			+ "| type anything else to terminate the execution              |\n"
			+ "--------------------------------------------------------------\n";
	private static String selMenu = "--------------------------------------------------------------\n"
			+ "|                      Select a choice:                      |\n"
			+ "|------------------------------------------------------------|\n"
			+ "|   Iker's queries section:                                  |\n"
			+ "|                                                            |\n"
			+ "|   Davy's queries section:                                  |\n"
			+ "|                                                            |\n"
			+ "|   Juyoung's queries section:                               |\n"
			+ "|                                                            |\n"
			+ "| type 16 to change the working database                     |\n"
			+ "|                                                            |\n"
			+ "| type anything else to terminate the execution              |\n"
			+ "--------------------------------------------------------------\n";

	public static void main(String[] args) {

		Scanner sn = new Scanner(System.in);
		String input;

		boolean stay = setQmaker(sn);

		while (stay) {

			printNicely(selMenu);

			input = sn.nextLine();

			switch (input) {

			case "16":
				stay = setQmaker(sn);
				break;

			default:
				stay = false;
				printNicely("Goodbye!!\n");
				break;
			}

		}

		sn.close();

	}

	private static void standardWay(Scanner sc) {

		boolean exit = false;
		while (!exit) {
			try {
				qmaker = new QueryConsole(Credentials.getUser(), Credentials.getPassword(), Credentials.getDatabase());
				exit = true;
			} catch (SQLException throwables) {
				printNicely(
						"The connection failed, check it and be sure you have the vpn connected.\nPress anything to reconect:\n");

				sc = new Scanner(System.in);
				sc.nextLine();
			}
		}

	}

	private static void other(Scanner sn) {

		printNicely("Insert the username of your mysql account:\n");
		String user = sn.nextLine();

		printNicely("Now, insert your password of the same mysql account:\n");
		String pwd = sn.nextLine();

		printNicely("Before going on we remind you to connect the vpn");
		try {
			Thread.sleep(1000);
			System.out.print(".");

			Thread.sleep(1000);
			System.out.print(".");

			Thread.sleep(1000);
			System.out.println(".");

		} catch (InterruptedException e) {

		}

		printNicely(
				"Okay, let's go on! Introduce the name of the database you will access (usually the name is similar to the user's name):\n");
		String db = sn.nextLine();

		printNicely("Creating query console...\n");

		boolean exit = false;
		while (!exit) {
			try {
				qmaker = new QueryConsole(user, pwd, db);
				exit = true;
			} catch (SQLException throwables) {
				printNicely("Something failed! Try it again\n");

				printNicely("Insert the username of your mysql account:\n");
				user = sn.nextLine();

				printNicely("Now, insert your password of the same mysql account:\n");
				pwd = sn.nextLine();

				printNicely("Introduce now the name of the database you will access:\n");
				db = sn.nextLine();
			}

		}

		printNicely("Created!\n");

	}

	public static boolean setQmaker(Scanner sn) {
		printNicely(s);

		boolean stay = true;
		String input = sn.nextLine();

		switch (input) {
		case "1":
			standardWay(sn);
			break;

		case "2":
			other(sn);
			break;

		default:
			printNicely("Goodbye!!\n");
			stay = false;
			break;

		}

		return stay;

	}

	public static void printNicely(String s) {
		for (int i = 0; i < s.length(); i++) {

			System.out.print(s.charAt(i));
			try {
				Thread.sleep(7);
			} catch (InterruptedException e) {
				//
			}

		}
	}

}

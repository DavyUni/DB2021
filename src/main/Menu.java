package main;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import credentials.Credentials;
import exceptions.AlreadyActiveQuery;
import exceptions.IllegalUpdateException;
import exceptions.NoQueryToClose;
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
			+ "|   type 1 to: Retrieve the employee info, trip and guide of |\n"
			+ "|   all the employees that have been on a trip where the     |\n"
			+ "|   guide either speaks 2 or more languages or has gone to   |\n"
			+ "|   all trips.                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 2 to: Retrieve the employee info of all the         |\n"
			+ "|   employees that have been in more different cities than   |\n"
			+ "|   all the employees that work in more than one department. |\n"
			+ "|                                                            |\n"
			+ "|   type 3 to: Retrieve the restaurant name, city and the    |\n"
			+ "|   average value of its dishes of all the restaurants that  |\n"
			+ "|   are in the same city as the departments that only have   |\n"
			+ "|   workers of one single sex.                               |\n"
			+ "|                                                            |\n"
			+ "|   type 4 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 5 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   Davy's queries section:                                  |\n"
			+ "|                                                            |\n"
			+ "|   type 6 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 7 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 8 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 9 to:                                               |\n"
			+ "|                                                            |\n"
			+ "|   type 10 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|   Juyoung's queries section:                               |\n"
			+ "|                                                            |\n"
			+ "|   type 11 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|   type 12 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|   type 13 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|   type 14 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|   type 15 to:                                              |\n"
			+ "|                                                            |\n"
			+ "|------------------------------------------------------------|\n"
			+ "|   type 16 to: change the working database                  |\n"
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

			case "1":
				processQ1();
				break;

			case "2":
				processQ2();
				break;

			case "3":
				processQ3();
				break;

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

	private static void processQ1() {
		String query = "SELECT e.Ssn, e.Lname, e.Fname, htc.TripTo, htc.DepartureDate, tri.GuideId, tou.guidename FROM employee as e INNER join employee_customer as ec on ec.Emp_id=e.Ssn INNER JOIN hotel_trip_customer as htc ON htc.CustomerId=ec.Cust_Id INNER JOIN trip as tri ON tri.TripTo=htc.TripTo and tri.DepartureDate=htc.DepartureDate INNER JOIN tourguide as tou ON tou.GuideId=tri.GuideId WHERE tri.GuideId IN (SELECT t2.GuideId FROM tourguide as t2 INNER JOIN languages as l ON l.GuideId=t2.GuideId GROUP BY t2.GuideId HAVING COUNT(*)>=2 UNION SELECT t3.GuideId FROM trip as t3 WHERE NOT EXISTS(SELECT * FROM trip as t4 WHERE NOT EXISTS( SELECT * FROM tourguide as t5 WHERE t3.GuideId=t5.GuideId and t4.TripTo=t3.TripTo and t4.DepartureDate=t3.DepartureDate)))";

		try {
			ResultSet rs = qmaker.performQuery(query);

			int ssn, guideId;
			String lName, fName, tripTo, guidename;
			Date depDate;

			try {
				while (rs.next()) {

					// Retrieve by column name
					ssn = rs.getInt("e.Ssn");
					lName = rs.getString("e.Lname");
					fName = rs.getString("e.Fname");
					tripTo = rs.getString("htc.TripTo");
					depDate = rs.getDate("htc.DepartureDate");
					guideId = rs.getInt("tri.GuideId");
					guidename = rs.getString("tou.guidename");

					// Display values
					System.out.println("Ssn: " + ssn + "	| Last name: " + lName + "	| First name: " + fName
							+ "	| Trip to: " + tripTo + "	| Departure date: " + depDate + "	| GuideId: " + guideId
							+ "	| Guide name: " + guidename);

				}
				System.out.println("Query finished!");
			} catch (SQLException e) {

				System.err.println("Something failed retrieving the data");
			} finally {

				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}

		} catch (IllegalUpdateException | AlreadyActiveQuery e) {
			// cannot happen
		}

	}

	private static void processQ2() {
		String query = "SELECT e.Ssn, e.Lname, e.Fname, COUNT(DISTINCT htc.TripTo) as CitiesVisited "
				+ "FROM employee as e LEFT JOIN works_on AS w ON w.Essn=e.Ssn LEFT JOIN employee_customer as ec ON ec.Emp_id=e.Ssn LEFT JOIN hotel_trip_customer as htc ON htc.CustomerId=ec.Cust_Id "
				+ "GROUP BY e.Ssn HAVING COUNT(DISTINCT htc.TripTo) >= ALL( " + "SELECT COUNT(DISTINCT htc1.TripTo) "
				+ "FROM employee as e1 LEFT JOIN works_on as w1 ON w1.Essn=e1.Ssn LEFT JOIN employee_customer as ec1 ON ec1.Emp_id=e1.Ssn LEFT JOIN hotel_trip_customer as htc1 ON htc1.CustomerId=ec1.Cust_Id "
				+ "GROUP BY e1.Ssn HAVING COUNT(DISTINCT w1.Pno)>1)";

		try {
			ResultSet rs = qmaker.performQuery(query);

			int ssn, cityVisit;
			String lName, fName;

			try {
				while (rs.next()) {

					// Retrieve by column name
					ssn = rs.getInt("e.Ssn");
					lName = rs.getString("e.Lname");
					fName = rs.getString("e.Fname");
					cityVisit = rs.getInt("CitiesVisited");

					// Display values
					System.out.println("Ssn: " + ssn + "	| Last name: " + lName + "	| First name: " + fName
							+ "	| Cities visited: " + cityVisit);

				}
				System.out.println("Query finished!");
			} catch (SQLException e) {

				System.err.println("Something failed retrieving the data");
			} finally {

				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}

		} catch (IllegalUpdateException | AlreadyActiveQuery e) {
			// cannot happen
		}

	}

	private static void processQ3() {
		String query = "SELECT r.restaurname,r.city,AVG(s.price) as theprice "
				+ "FROM restaurant as r INNER JOIN serves as s ON s.restaurname=r.restaurname " + "WHERE r.city IN ( "
				+ "SELECT dl.Dlocation" + " FROM employee as e INNER JOIN dept_locations as dl ON dl.Dnumber=e.Dno"
				+ " WHERE NOT EXISTS(" + " SELECT *"
				+ " FROM employee as e2 INNER JOIN dept_locations as dl2 ON  dl2.Dnumber=e2.Dno"
				+ " WHERE e.Sex='F' and dl.Dnumber=dl2.Dnumber" + ")"

				+ " UNION"

				+ " SELECT dl3.Dlocation"
				+ " FROM employee as e3 INNER JOIN dept_locations as dl3 ON dl3.Dnumber=e3.Dno" + " WHERE NOT EXISTS("
				+ " SELECT *" + " FROM employee as e4 INNER JOIN dept_locations as dl4 ON  dl4.Dnumber=e4.Dno"
				+ " WHERE e3.Sex='M' and dl3.Dnumber=dl4.Dnumber" + ")" + ")" + " GROUP BY r.restaurname";

		try {
			ResultSet rs = qmaker.performQuery(query);

			double thePrice;
			String rName, rCity;

			try {
				while (rs.next()) {

					// Retrieve by column name
					thePrice = rs.getDouble("theprice");
					rName = rs.getString("r.restaurname");
					rCity = rs.getString("r.city");

					// Display values
					System.out.println("Restaurant name: " + rName + "	| Location: " + rCity
							+ "	| Average dish price: " + thePrice);

				}
				System.out.println("Query finished!");
			} catch (SQLException e) {

				System.err.println("Something failed retrieving the data");
			} finally {

				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}

		} catch (IllegalUpdateException | AlreadyActiveQuery e) {
			// cannot happen
		}

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
		;

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
			if (s.charAt(i) != ' ')
				try {
					Thread.sleep(4);
				} catch (InterruptedException e) {
					//
				}

		}
	}

}

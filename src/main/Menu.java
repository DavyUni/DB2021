package main;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Scanner;

import credentials.Credentials;
import exceptions.AlreadyActiveQuery;
import exceptions.IllegalQueryException;
import exceptions.IllegalUpdateException;
import exceptions.NoQueryToClose;
import managing.QueryConsole;

/**
 * Class to perform specific queries
 * 
 * @author Iker, Davy, Juyoung
 *
 */
public class Menu {
	/**
	 * parameter of the object to perform queries
	 */
	private static QueryConsole qmaker;
	/**
	 * string of the menu for setting the query maker
	 */
	private static String s = "--------------------------------------------------------------\n"
			+ "|                      Select a choice:                      |\n"
			+ "|------------------------------------------------------------|\n"
			+ "| type 1 if you want to access to an standard account and DB |\n"
			+ "| type 2 to introduce your own account and DataBase          |\n"
			+ "| type anything else to terminate the execution              |\n"
			+ "--------------------------------------------------------------\n";
	/**
	 * string of the menu to select a query
	 */
	private static String selMenu = "--------------------------------------------------------------\n"
			+ "|                      Select a choice:                      |\n"
			+ "|------------------------------------------------------------|\n"
			+ "|   Iker's queries section:                                  |\n"
			+ "|                                                            |\n"
			+ "|   type 1 to: Retrieve the employee info, trip and guide    |\n"
			+ "|   of all the employees that have been on a trip where the  |\n"
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
			+ "|   type 4 to: Insert into the database some new works_on    |\n"
			+ "|   relationships. Be careful and introduce existing social  |\n"
			+ "|   security numbers and proyect numbers because they are    |\n"
			+ "|   foreign keys.                                            |\n"
			+ "|                                                            |\n"
			+ "|   type 5 to: Update the social security number of some     |\n"
			+ "|   employees. Remember that these updates will have side    |\n"
			+ "|   effects in other tables that use this number as foreign  |\n"
			+ "|   key.                                                     |\n"
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

	/**
	 * main method to perform the menu
	 * 
	 * @param args
	 */
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

			case "4":
				processQ4(sn);
				break;

			case "5":
				processQ5(sn);
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
		if (qmaker != null)
			qmaker.endConnection();
		sn.close();

	}

	/**
	 * method for the query of choice 1
	 */
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

	/**
	 * method for the query of choice 2
	 */
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

	/**
	 * method for the query of choice 3
	 */
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

	/**
	 * method for the query of choice 4
	 */
	private static void processQ4(Scanner sc) {

		String insert = "INSERT INTO `works_on` (`Essn`, `Pno`, `Hours`) VALUES (?, ?, ?)";
		boolean exit = false;
		int n = 0;
		LinkedList<Object> list = new LinkedList<Object>();
		while (!exit) {
			printNicely("Write down the number of works_on tuples you want to insert: \n");
			try {
				n = Integer.parseInt(sc.nextLine());
				exit = true;
			} catch (RuntimeException e) {
				printNicely("Please, write down an integer number\n");
			}
		}
		exit = false;
		int ssn = 12345;
		int pno = 5;
		double hours = 0;

		for (int i = 1; i <= n; i++) {
			printNicely("tuple number " + i + ":\n");

			while (!exit) {
				printNicely("Write down social security number of tuple " + i + ": \n");
				try {
					ssn = Integer.parseInt(sc.nextLine());
					exit = true;
				} catch (RuntimeException e) {
					printNicely("Please, write down an integer number\n");
				}
			}
			list.add(i * 3 - 3, ssn);
			exit = false;
			while (!exit) {
				printNicely("Write down project number of tuple " + i + ": \n");
				try {
					pno = Integer.parseInt(sc.nextLine());
					exit = true;
				} catch (RuntimeException e) {
					printNicely("Please, write down an integer number\n");
				}
			}
			list.add(i * 3 - 2, pno);
			exit = false;
			while (!exit) {
				printNicely("Write down hours of work of tuple " + i + ": \n");
				try {
					hours = Double.parseDouble(sc.nextLine());
					exit = true;
				} catch (RuntimeException e) {
					printNicely("Please, write down a float number\n");
				}
			}
			exit = false;
			list.add(i * 3 - 1, hours);

		}

		try {

			printNicely("This is the state of works_on table before the insertions:\n");
			ResultSet rs = qmaker.performQuery("Select * from works_on");
			int essn;
			int pnm;
			float hour;
			try {
				while (rs.next()) {

					// Retrieve by column name
					hour = rs.getFloat("Hours");
					essn = rs.getInt("Essn");
					pnm = rs.getInt("Pno");

					// Display values
					System.out.println("Social security number: " + essn + "	| Project number: " + pnm
							+ "	| Hours of work: " + hour);

				}

			} catch (SQLException e) { // should not happen
			} finally {

				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) { // Should not happen

				}

			}

			if (n == 1) {

				System.out.println(qmaker.insertTypeQuery(insert, list) + "/1 successful query");

			} else {
				System.out.println(qmaker.insertTypeQuery(insert, list, n) + "/" + n + " successful queries");

			}

			try {
				qmaker.endQuery();
			} catch (NoQueryToClose e1) {
				// should not happen
			}

			printNicely("This is the state of works_on table after the insertions:\n");
			rs = qmaker.performQuery("Select * from works_on");

			try {
				while (rs.next()) {

					// Retrieve by column name
					hour = rs.getFloat("Hours");
					essn = rs.getInt("Essn");
					pnm = rs.getInt("Pno");

					// Display values
					System.out.println("Social security number: " + essn + "	| Project number: " + pnm
							+ "	| Hours of work: " + hour);

				}

			} catch (SQLException e) {
				// should not happen
			} finally {

				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}

		} catch (IllegalQueryException e) {
			System.err.println(e.getMessage());
		} catch (AlreadyActiveQuery e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				qmaker.endQuery();
			} catch (NoQueryToClose e) {
				// not important
			}
		}
	}

	/**
	 * method for the query of choice 5
	 */
	private static void processQ5(Scanner sc) {

		String update = "Update employee set Ssn=? Where Ssn=?";

		boolean exit = false;
		int n = 0;
		LinkedList<Object> list = new LinkedList<Object>();
		LinkedList<Object> temp1 = new LinkedList<Object>();
		LinkedList<Object> temp2 = new LinkedList<Object>();

		while (!exit) {
			printNicely("Write down the number of social security number updates you want to do: \n");
			try {
				n = Integer.parseInt(sc.nextLine());
				exit = true;
			} catch (RuntimeException e) {
				printNicely("Please, write down an integer number\n");
			}
		}
		exit = false;
		int ssn1 = 0;
		int ssn2 = 0;

		for (int i = 1; i <= n; i++) {
			printNicely("tuple number " + i + ":\n");

			while (!exit) {
				printNicely("Write down social security number of tuple " + i + ": \n");
				try {
					ssn1 = Integer.parseInt(sc.nextLine());
					exit = true;
				} catch (RuntimeException e) {
					printNicely("Please, write down an integer number\n");
				}
			}

			exit = false;
			while (!exit) {
				printNicely("Write down the new social security number of tuple " + i + ": \n");
				try {
					ssn2 = Integer.parseInt(sc.nextLine());
					exit = true;
				} catch (RuntimeException e) {
					printNicely("Please, write down an integer number\n");
				}
			}
			list.add(i * 2 - 2, ssn2);
			temp2.add(ssn1);
			list.add(i * 2 - 1, ssn1);
			temp1.add(ssn2);
			exit = false;

		}
		try {

			printNicely("These are all the tuples where this social security number appears before the update(s):\n");

			// employee_customer, employee, dependent, works_on, (department)
			// rs.next;rs.previous

			String q1, q2, q3, q4, q5;

			q1 = "select * from employee_customer where emp_id=?";
			q2 = "select * from employee where ssn=?";
			q3 = "select * from dependent where essn=?";
			q4 = "select * from works_on where essn=?";
			q5 = "select * from department where mgr_ssn=?";

			ResultSet rs = null;

			LinkedList<Object> temp3;
			try {

				System.out.println("Table employee_customer for the updated tuples before the updation:");
				for (Object o : temp2) {

					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q1, temp3);
					// goes back

					int ssn, cid;
					while (rs.next()) {

						ssn = rs.getInt("emp_id");
						cid = rs.getInt("cust_id");

						// Display values
						System.out.println("Social security number: " + ssn + "	| Customer id: " + cid);
					}

					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}

				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table employee for the updated tuples before the updation:");
				for (Object o : temp2) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q2, temp3);
					// goes back
					String fName, minit, lName, address, sex;
					int ssn, sussn, dno;
					float salary;
					Date bdate;
					while (rs.next()) {

						fName = rs.getString("fname");
						minit = rs.getString("minit");
						lName = rs.getString("lname");
						ssn = rs.getInt("ssn");
						bdate = rs.getDate("bdate");
						address = rs.getString("address");
						sex = rs.getString("sex");
						salary = rs.getFloat("salary");
						sussn = rs.getInt("super_ssn");
						dno = rs.getInt("dno");

						// Display values
						System.out.println("First name: " + fName + "	| Minit: " + minit + "	| Last name: " + lName
								+ "	| Social security number: " + ssn + "	| Birth date: " + bdate + "	| Address: "
								+ address + "	| Sex: " + sex + "	| Salary: " + salary
								+ "	| Superior's social security number: " + sussn + "	| Department number: " + dno);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table dependent for the updated tuples before the updation:");
				for (Object o : temp2) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q3, temp3);
					// goes back
					String dName, relation, sex;
					int ssn;
					Date bdate;
					while (rs.next()) {

						dName = rs.getString("dependent_name");
						ssn = rs.getInt("essn");
						bdate = rs.getDate("bdate");
						relation = rs.getString("relationship");
						sex = rs.getString("sex");

						// Display values
						System.out.println("Employee social security number: " + ssn + "	| Dependent name: " + dName
								+ "	| Sex: " + sex + "	| Birth date: " + bdate + "	| Relationship: " + relation);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table works_on for the updated tuples before the updation:");
				for (Object o : temp2) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q4, temp3);

					int ssn, pno;
					float hours;
					while (rs.next()) {

						pno = rs.getInt("pno");
						ssn = rs.getInt("essn");
						hours = rs.getFloat("hours");

						// Display values
						System.out.println("Social security number: " + ssn + "	| Project number: " + pno
								+ "	| Hours of work: " + hours);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table department for the updated tuples before the updation:");
				for (Object o : temp2) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q5, temp3);
					// goes back
					String dname;
					int ssn, dno;
					Date start;
					while (rs.next()) {

						dno = rs.getInt("dnumber");
						ssn = rs.getInt("mgr_ssn");
						dname = rs.getString("dname");
						start = rs.getDate("mgr_start_date");

						// Display values
						System.out.println("Department name: " + dname + "	| Department number: " + dno
								+ "Social security number of department's boss: " + ssn + "	| Boss' start date: "
								+ start);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			} // end of showing first state

			if (n == 1) {

				System.out.println(qmaker.updateTypeQuery(update, list) + "/1 successful update");

			} else {
				System.out.println(qmaker.updateTypeQuery(update, list, n) + "/" + n + " successful updates");

			}

			try {
				qmaker.endQuery();
			} catch (NoQueryToClose e1) {
				// should not happen
			}

			printNicely(
					"These are all the tuples where this social security number appears after the update(s) (In case of failed updation, this will print the tuples corresponding to the ssn that matches\n"
							+ "with the new ssn you wanted to update to someone):\n");

			try {

				System.out.println("Table employee_customer for the updated tuples after the updation:");
				for (Object o : temp1) {

					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q1, temp3);
					// goes back

					int ssn, cid;
					while (rs.next()) {

						ssn = rs.getInt("emp_id");
						cid = rs.getInt("cust_id");

						// Display values
						System.out.println("Social security number: " + ssn + "	| Customer id: " + cid);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}

			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table employee for the updated tuples after the updation:");
				for (Object o : temp1) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q2, temp3);
					// goes back
					String fName, minit, lName, address, sex;
					int ssn, sussn, dno;
					float salary;
					Date bdate;
					while (rs.next()) {

						fName = rs.getString("fname");
						minit = rs.getString("minit");
						lName = rs.getString("lname");
						ssn = rs.getInt("ssn");
						bdate = rs.getDate("bdate");
						address = rs.getString("address");
						sex = rs.getString("sex");
						salary = rs.getFloat("salary");
						sussn = rs.getInt("super_ssn");
						dno = rs.getInt("dno");

						// Display values
						System.out.println("First name: " + fName + "	| Minit: " + minit + "	| Last name: " + lName
								+ "	| Social security number: " + ssn + "	| Birth date: " + bdate + "	| Address: "
								+ address + "	| Sex: " + sex + "	| Salary: " + salary
								+ "	| Superior's social security number: " + sussn + "	| Department number: " + dno);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table dependent for the updated tuples after the updation:");
				for (Object o : temp1) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q3, temp3);
					// goes back
					String dName, relation, sex;
					int ssn;
					Date bdate;
					while (rs.next()) {

						dName = rs.getString("dependent_name");
						ssn = rs.getInt("essn");
						bdate = rs.getDate("bdate");
						relation = rs.getString("relationship");
						sex = rs.getString("sex");

						// Display values
						System.out.println("Employee social security number: " + ssn + "	| Dependent name: " + dName
								+ "	| Sex: " + sex + "	| Birth date: " + bdate + "	| Relationship: " + relation);

					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table works_on for the updated tuples after the updation:");
				for (Object o : temp1) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q4, temp3);
					// goes back

					int ssn, pno;
					float hours;
					while (rs.next()) {

						pno = rs.getInt("pno");
						ssn = rs.getInt("essn");
						hours = rs.getFloat("hours");

						// Display values
						System.out.println("Social security number: " + ssn + "	| Project number: " + pno
								+ "	| Hours of work: " + hours);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

			try {
				System.out.println("Table department for the updated tuples after the updation:");
				for (Object o : temp1) {
					temp3 = new LinkedList<Object>();
					temp3.add(o);
					rs = qmaker.performQuery(q5, temp3);
					// goes back
					String dname;
					int ssn, dno;
					Date start;
					while (rs.next()) {

						dno = rs.getInt("dnumber");
						ssn = rs.getInt("mgr_ssn");
						dname = rs.getString("dname");
						start = rs.getDate("mgr_start_date");

						// Display values
						System.out.println("Department name: " + dname + "	| Department number: " + dno
								+ "Social security number of department's boss: " + ssn + "	| Boss' start date: "
								+ start);
					}
					try {
						qmaker.endQuery();
					} catch (NoQueryToClose e) {
						// nomatter
					}
				}
			} catch (SQLException e) {
				System.err.println("Fatal error with a query");
			} finally {
				System.out.println();
				try {
					qmaker.endQuery();
				} catch (NoQueryToClose e) {
					// don't care
				}
			}

		} catch (IllegalQueryException e) {
			System.err.println(e.getMessage());
		} catch (AlreadyActiveQuery e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				qmaker.endQuery();
			} catch (NoQueryToClose e) {
				// not important
			}
		}
	}

	/**
	 * method to set the query maker with the normal account
	 * 
	 * @param sc scanner
	 */
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

	/**
	 * method to introduce our own database
	 * 
	 * @param sn scanner
	 */
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

	/**
	 * method for the menu to set the query maker
	 * 
	 * @param sn scanner
	 * @return true if the qmaker was settled
	 */
	public static boolean setQmaker(Scanner sn) {
		printNicely(s);

		if (qmaker != null)
			qmaker.endConnection();

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

	/**
	 * method to print strings like in an old-school videogame
	 * 
	 * @param s the string to print
	 */
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

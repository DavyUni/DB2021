package managing;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import exceptions.AlreadyActiveQuery;
import exceptions.IllegalUpdateException;
import exceptions.NoQueryToClose;
import exceptions.UnsuccessfulUpdate;

/**
 * Class created to retrieve the queries from the database
 * 
 * @authors Iker, Davy, Juyoung
 *
 */
public class QueryConsole {

	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:3306/";

	// Database credentials

	/**
	 * Parameter for the user of the mysql server
	 */
	private String user;
	/**
	 * Parameter for the password of the mysql user
	 */
	private String pass;
	/**
	 * Parameter that represents the name of the database to work with
	 */
	private String dBase;

	/**
	 * Temporal parameter of the database connector
	 */
	private Connection conn = null;
	/**
	 * Temporal parameter of the query statement
	 */
	private Statement stmt = null;
	/**
	 * Temporal parameter of the query statement
	 */
	private PreparedStatement prepStmt = null;
	/**
	 * Temporal parameter of the result set
	 */
	private ResultSet rs = null;
	/**
	 * Parameter for the number of current active queries. NOTE: It cannot be more
	 * than one
	 */
	private int numQueries;

	// Constructor:

	public QueryConsole(String usr, String pw, String dataBase) throws SQLException {
		super();
		this.user = usr;
		this.pass = pw;
		this.dBase = dataBase;
		numQueries = 0;

		// STEP 2: Register JDBC driver
		try {
			Class.forName(JDBC_DRIVER);

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL + dBase, user, pass);

			conn.setAutoCommit(false);

		} catch (ClassNotFoundException e) {
			// Won't happen!
		}
	}

	// Methods:

	// TODO: Method for closing the connection

	/**
	 * Method to perform a query
	 * 
	 * @param query the sql code to perform a query
	 * @return the set of the query
	 * @throws AlreadyActiveQuery if there already is an active query
	 */
	public ResultSet performQuery(String query) throws AlreadyActiveQuery, IllegalUpdateException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (query.split(" ")[0].equalsIgnoreCase("update"))
			throw new IllegalUpdateException();

		try {
			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Something wrong with the query... Aborting.");
			try {
				endQuery();
			} catch (NoQueryToClose e1) {
				// Impossible
			}
		}

		// STEP 5: Extract data from result set
		/*
		 * while(rs.next()){ //Retrieve by column name int ssn = rs.getInt("e.Ssn");
		 * String fName = rs.getString("e.Fname");
		 * 
		 * //Display values System.out.print("Ssn: " + ssn);
		 * System.out.print(", First Name: " + fName); System.out.println(""); }
		 */

		return rs;

	}

	/**
	 * Method to perform a query using parameters
	 * 
	 * @param query the sql code for the query
	 * @param list  of elements to introduce in the query
	 * @return the set of the query
	 * @throws AlreadyActiveQuery if there is a not closed query
	 */
	public ResultSet performQuery(String query, List<Object> list) throws AlreadyActiveQuery, IllegalUpdateException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (query.split(" ")[0].equalsIgnoreCase("update"))
			throw new IllegalUpdateException();

		try {

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			prepStmt = conn.prepareStatement(query);
			Iterator<Object> it = list.iterator();
			Object o;
			for (int i = 1; it.hasNext(); i++) {
				o = it.next();
				if (o instanceof String) {
					prepStmt.setString(i, (String) o);
				} else if (o instanceof Integer) {
					prepStmt.setInt(i, (int) o);
				} else {
					prepStmt.setObject(i, o);
					System.out.println(
							o.getClass() + " has been introduced with setObject method, check its correctness");
				}
			}

			rs = prepStmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Something wrong with the query... Aborting.");
			try {
				endQuery();
			} catch (NoQueryToClose e1) {
				// Impossible
			}
		}

		// STEP 5: Extract data from result set
		/*
		 * while(rs.next()){ //Retrieve by column name int ssn = rs.getInt("e.Ssn");
		 * String fName = rs.getString("e.Fname");
		 * 
		 * //Display values System.out.print("Ssn: " + ssn);
		 * System.out.print(", First Name: " + fName); System.out.println(""); }
		 */

		return rs;

	}

	public void updateTypeQuery(String update) throws AlreadyActiveQuery, UnsuccessfulUpdate {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}
		Savepoint backup = null;
		// savepoint
		try {
			backup = conn.setSavepoint();

			System.out.println("Creating update statement...");
			stmt = conn.createStatement();
			rs = stmt.executeQuery(update);

			// commit or rollback
			conn.commit();
		} catch (SQLException e) {
			// savepoint
			try {
				endQuery();
			} catch (NoQueryToClose e2) {
				// Will not happen
			}
			try {
				conn.rollback(backup);
				System.err.println(
						"There has been an irreparable problem with the update, the state of the database has been rolled back!");
			} catch (SQLException e1) {
				System.err.println("The rollback has failed!");
			}

		}

	}

	public int updateTypeQuery(String update, List<Object> list, int numTuples)
			throws AlreadyActiveQuery, UnsuccessfulUpdate, IllegalUpdateException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (list.size() % numTuples != 0) {
			throw new IllegalUpdateException(
					"You need to give a list with a number of elements compatible with the number of tuples");
		}

		int params = list.size() / numTuples;
		Savepoint backup = null;
		// savepoint

		try {
			prepStmt = conn.prepareStatement(update);
		} catch (SQLException e1) {
			return -1;
		}

		// STEP 4: Execute the queries
		int success = 0;

		Iterator<Object> it = list.iterator();
		Object o;
		for (int i = 1; i < numTuples + 1; i++) {
			System.out.println("Preparing update query " + i + "...");
			try {
				backup = conn.setSavepoint();
				for (int j = 1; j < params + 1; j++) {// no need to use it.hasNext() because we know the size of the
														// list
					o = it.next();
					if (o instanceof String) {

						prepStmt.setString(j, (String) o);

					} else if (o instanceof Integer) {
						prepStmt.setInt(j, (int) o);
					} else {
						prepStmt.setObject(j, o);
						System.out.println(
								o.getClass() + " has been introduced with setObject method, check its correctness");
					}
				}

				rs = prepStmt.executeQuery();

				conn.commit();
				success++;
			} catch (SQLException e) {
				System.err.println("update " + i + " has failed, rollback");
				try {
					endQuery();
				} catch (NoQueryToClose e2) {
					// Will not happen
				}
				try {
					conn.rollback(backup);
				} catch (SQLException e1) {
					System.err.println("The rollback has failed!");
				}
			}
		}

		return success;
		/*
		 * 
		 * for (int j = 1; it.hasNext(); j++) { o = it.next(); if (o instanceof String)
		 * { prepStmt.setString(j, (String) o); } else if (o instanceof Integer) {
		 * prepStmt.setInt(j, (int) o); } else { prepStmt.setObject(j, o);
		 * System.out.println(o.getClass() +
		 * " has been introduced with setObject method, check its correctness"); } }
		 */

		// commit or rollback

	}

	public void endQuery() throws NoQueryToClose {

		if (numQueries == 1)
			numQueries--;
		else
			throw new NoQueryToClose();

		try { // Close current resultSet and corresponding

			if (rs != null)
				rs.close();

			if (stmt != null)
				stmt.close();

			if (prepStmt != null)
				prepStmt.close();

		} catch (SQLException e) {
			System.out.println("Failed to close");
		}
	}

	public void endConnection() {

		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Failed to close");
			}
	}
}

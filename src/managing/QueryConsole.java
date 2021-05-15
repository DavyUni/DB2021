package managing;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import exceptions.AlreadyActiveQuery;
import exceptions.IllegalInsertException;
import exceptions.IllegalQueryException;
import exceptions.IllegalUpdateException;
import exceptions.NoQueryToClose;

/**
 * Class created to retrieve the queries from the database
 * 
 * @authors Iker, Davy, Juyoung
 *
 */
public class QueryConsole {

	// JDBC driver name and database URL
	/**
	 * Parameter of the driver class' name
	 */
	private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

	/**
	 * Parameter of the url of the database
	 */
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

	/**
	 * Constructor of the object
	 * 
	 * @param usr      - User of the database
	 * @param pw       - Password of the database
	 * @param dataBase - The name of the database
	 * @throws SQLException when the connection cannot be stablished
	 */
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

	/**
	 * Method to perform a query
	 * 
	 * @param query the sql code to perform a query
	 * @return the set of the query
	 * @throws AlreadyActiveQuery    if there already is an active query
	 * @throws IllegalQueryException when a query of a non-allowed type is performed
	 */
	public ResultSet performQuery(String query) throws AlreadyActiveQuery, IllegalQueryException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (query.split(" ")[0].equalsIgnoreCase("update")) {
			IllegalQueryException e = new IllegalUpdateException();
			throw e;
		} else if (query.split(" ")[0].equalsIgnoreCase("insert")) {
			IllegalQueryException e = new IllegalInsertException();
			throw e;
		} else if (!query.split(" ")[0].equalsIgnoreCase("select")) {
			throw new IllegalQueryException();
		}

		try {
			// STEP 4: Execute a query
			// System.out.println("Creating statement...");
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
	 * @throws AlreadyActiveQuery    if there is a not closed query
	 * @throws IllegalQueryException when a query of a non-allowed type is performed
	 */
	public ResultSet performQuery(String query, List<Object> list) throws AlreadyActiveQuery, IllegalQueryException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (query.split(" ")[0].equalsIgnoreCase("update")) {
			IllegalQueryException e = new IllegalUpdateException();
			throw e;
		} else if (query.split(" ")[0].equalsIgnoreCase("insert")) {
			IllegalQueryException e = new IllegalInsertException();
			throw e;
		} else if (!query.split(" ")[0].equalsIgnoreCase("select")) {
			throw new IllegalQueryException();
		}

		try {

			// STEP 4: Execute a query
			// System.out.println("Creating statement...");
			prepStmt = conn.prepareStatement(query);
			Iterator<Object> it = list.iterator();
			Object o;
			for (int i = 1; it.hasNext(); i++) {
				o = it.next();
				if (o instanceof String) {
					prepStmt.setString(i, (String) o);
				} else if (o instanceof Integer) {
					prepStmt.setInt(i, (int) o);
				} else if (o instanceof Float) {
					prepStmt.setFloat(i, (float) o);
				} else if (o instanceof Date) {
					prepStmt.setDate(i, (Date) o);
				} else {
					prepStmt.setObject(i, o);
					System.out.println(
							o.getClass() + " has been introduced with setObject method, check its correctness");
				}
			}
			// System.out.println(prepStmt);
			rs = prepStmt.executeQuery();
		} catch (SQLException e) {
			System.out.println("Something wrong with the query... Aborting.");
			try {
				endQuery();
			} catch (NoQueryToClose e1) {
				// Impossible
			}
		}

		return rs;

	}

	/**
	 * Method to update a single tuple in the database
	 * 
	 * @param update query to execute
	 * @param list   of atributes to update in the query
	 * @throws AlreadyActiveQuery    when there is a not closed query
	 * @throws IllegalQueryException when a non-allowed query is being executed
	 */
	public int updateTypeQuery(String update, List<Object> list) throws AlreadyActiveQuery, IllegalQueryException {
		return updateTypeQuery(update, list, 1);
	}

	/**
	 * Method to execute several update queries
	 * 
	 * @param update     query to perform
	 * @param list       of parameters to fulfill the update queries
	 * @param numTuples, number of different updates performed
	 * @return the number of successful updates, its optimal number is numTuples
	 * @throws AlreadyActiveQuery when there is a not closed query
	 */
	public int updateTypeQuery(String update, List<Object> list, int numTuples)
			throws AlreadyActiveQuery, IllegalQueryException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (update.split(" ")[0].equalsIgnoreCase("select")) {
			throw new IllegalQueryException();
		} else if (update.split(" ")[0].equalsIgnoreCase("insert")) {
			IllegalQueryException e = new IllegalInsertException();
			throw e;
		} else if (!update.split(" ")[0].equalsIgnoreCase("update")) {
			throw new IllegalQueryException();
		}

		if (list.size() % numTuples != 0) {
			IllegalQueryException e = new IllegalUpdateException(
					"You need to give a list with a number of elements compatible with the number of tuples");
			throw e;
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
					} else if (o instanceof Double) {
						prepStmt.setDouble(j, (double) o);
					} else if (o instanceof Date) {
						prepStmt.setDate(j, (Date) o);
					} else {
						prepStmt.setObject(j, o);
						System.out.println(
								o.getClass() + " has been introduced with setObject method, check its correctness");
					}
				}

				// System.out.println(prepStmt);

				prepStmt.execute();

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

	}

	/**
	 * Method to insert a single tuple into the database
	 * 
	 * @param insert query to execute
	 * @param list   of atributes to insert in the query
	 * @throws AlreadyActiveQuery    when there is a not closed query
	 * @throws IllegalQueryException when a non-allowed query is being executed
	 */
	public int insertTypeQuery(String insert, List<Object> list) throws AlreadyActiveQuery, IllegalQueryException {
		return insertTypeQuery(insert, list, 1);
	}

	/**
	 * Method to insert several tuples into the database
	 * 
	 * @param insert    query to execute
	 * @param list      of atributes to insert in the query
	 * @param numTuples is the number of tuples to insert
	 * @throws AlreadyActiveQuery    when there is a not closed query
	 * @throws IllegalQueryException when a non-allowed query is being executed
	 */
	public int insertTypeQuery(String insert, List<Object> list, int numTuples)
			throws AlreadyActiveQuery, IllegalQueryException {

		if (numQueries == 0) {
			numQueries++;
		} else {
			throw new AlreadyActiveQuery();
		}

		if (insert.split(" ")[0].equalsIgnoreCase("select")) {
			throw new IllegalQueryException();
		} else if (insert.split(" ")[0].equalsIgnoreCase("update")) {
			IllegalQueryException e = new IllegalUpdateException();
			throw e;
		} else if (!insert.split(" ")[0].equalsIgnoreCase("insert")) {
			throw new IllegalQueryException();
		}

		if (list.size() % numTuples != 0) {
			IllegalQueryException e = new IllegalInsertException(
					"You need to give a list with a number of elements compatible with the number of tuples");
			throw e;
		}

		int params = list.size() / numTuples;
		Savepoint backup = null;
		// savepoint

		try {
			prepStmt = conn.prepareStatement(insert);
		} catch (SQLException e1) {
			return -1;
		}

		// STEP 4: Execute the queries
		int success = 0;

		Iterator<Object> it = list.iterator();
		Object o;
		for (int i = 1; i < numTuples + 1; i++) {
			System.out.println("Preparing insert query " + i + "...");
			try {
				backup = conn.setSavepoint();
				for (int j = 1; j < params + 1; j++) {// no need to use it.hasNext() because we know the size of the
														// list
					o = it.next();
					if (o instanceof String) {

						prepStmt.setString(j, (String) o);

					} else if (o instanceof Integer) {
						prepStmt.setInt(j, (int) o);
					} else if (o instanceof Double) {
						prepStmt.setDouble(j, (double) o);
					} else if (o instanceof Date) {
						prepStmt.setDate(j, (Date) o);
					} else {
						prepStmt.setObject(j, o);
						System.out.println(
								o.getClass() + " has been introduced with setObject method, check its correctness");
					}
				}
				prepStmt.execute();
				conn.commit();
				success++;
			} catch (SQLException e) {
				System.err.println("insert " + i + " has failed, rollback");
				System.out.println(e.getMessage());
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

	}

	/**
	 * Method to close the previous activated query
	 * 
	 * @throws NoQueryToClose if there is no previously not closed query
	 */
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

	/**
	 * Method to close a connection to the database
	 */
	public void endConnection() {

		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println("Failed to close");
			}
	}
}

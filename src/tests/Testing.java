package tests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import exceptions.AlreadyActiveQuery;
import exceptions.NoQueryToClose;
import managing.QueryConsole;

public class Testing {

	public static void main(String[] args) {

		QueryConsole qc = new QueryConsole("DBDI26", "Panasonic_123", "dbdi26");

		ResultSet rs;

		try {

			rs = qc.performQuery("SELECT Ssn FROM employee");

			try {

				int ssn;
				while (rs.next()) {

					// Retrieve by column name
					ssn = rs.getInt("Ssn");

					// Display values
					System.out.println("Ssn: " + ssn);

				}

			} catch (SQLException e) {

				System.out.println("Something failed gettin the values");

			} finally {

				try {
					qc.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}
		} catch (AlreadyActiveQuery e1) {
			System.out.println(e1.getMessage());
		}
		System.out.println("\n\n\n new query:");
		LinkedList<Object> lk = new LinkedList<Object>();
		lk.add(888665555);
		lk.add(987654321);

		try {
			rs = qc.performQuery("SELECT Ssn FROM employee WHERE Ssn=? or Ssn=?", lk);

			int ssn;
			try {
				while (rs.next()) {

					// Retrieve by column name
					ssn = rs.getInt("Ssn");

					// Display values
					System.out.println("Ssn: " + ssn);

				}
			} catch (SQLException e) {
				System.out.println("Something failed gettin the values");
			} finally {

				try {
					qc.endQuery();
				} catch (NoQueryToClose e) {
					// Should not happen
				}

			}

		} catch (AlreadyActiveQuery e) {
			// will not happen
		}

	}

}

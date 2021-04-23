package main;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Testing {

	public static void main(String[] args) {
		QueryConsole qc = new QueryConsole("DBDI26", "Panasonic_123", "dbdi26" );
		
		ResultSet rs = qc.performQuery("SELECT Ssn FROM employee");

		try {
			System.out.println("Entrado try");
			int ssn;
			while(rs.next()){
				System.out.println("Entrado while");
			     //Retrieve by column name
			      ssn  = rs.getInt("Ssn");
			     
			     
			     //Display values
			     System.out.println("Ssn: " + ssn);
			     
			  }
		} catch (Exception e) {
			System.out.println("FFFF");
		}
		finally {
			qc.endQuery();
		}
	}

}

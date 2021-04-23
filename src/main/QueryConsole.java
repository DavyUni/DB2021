package main;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryConsole {
	
	// JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    private static final String DB_URL = "jdbc:mysql://dif-mysql.ehu.es:3306/";

    // Database credentials
    private String user;
    private String pass;
    private String dBase;
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs=null;
    
    private int numQueries;
    
    //Constructor:
    
    public QueryConsole(String usr, String pw, String dataBase) {
    	super();
    	this.user= usr;
    	this.pass= pw;
    	this.dBase= dataBase;
    	numQueries=0;
    }
    
    
    
    //Methods:
		    
	public ResultSet performQuery(String query) {
		
		if(numQueries==0) {
			numQueries++;
		}else {
			// TODO throw new illegalException();
		}
		
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL+dBase,user,pass);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		      rs = stmt.executeQuery(query);
		}
		catch(Exception e) {
			System.out.println("Something wrong with the query... Aborting.");
			endQuery();
		}
		      
		      //STEP 5: Extract data from result set
		      /*while(rs.next()){
		         //Retrieve by column name
		         int ssn  = rs.getInt("e.Ssn");
		         String fName = rs.getString("e.Fname");
		         
		         //Display values
		         System.out.print("Ssn: " + ssn);
		         System.out.print(", First Name: " + fName);
		         System.out.println("");
		    	}*/
		  
		   return rs;

	}

	
	public void endQuery() {
		
		if(numQueries==1)	
			numQueries--;
		else // TODO throw new noQuery (asegurar no hacer nullcheck)
		
		try {// TODO nullcheck de todos
			rs.close();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.out.println("F");
		}
	}
	
}

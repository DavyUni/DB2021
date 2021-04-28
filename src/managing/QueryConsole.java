package managing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import exceptions.AlreadyActiveQuery;
import exceptions.NoQueryToClose;

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
    private PreparedStatement prepStmt =null;
    /**
     * Temporal parameter of the result set
     */
    private ResultSet rs=null;
    /**
     * Parameter for the number of current active queries. NOTE: It cannot be more than one
     */
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
		    
    /**
     * Method to perform a query
     * 
     * @param query the sql code to perform a query
     * @return the set of the query
     * @throws AlreadyActiveQuery if there already is an active query
     */
	public ResultSet performQuery(String query) throws AlreadyActiveQuery{
		
		if(numQueries==0) {
			numQueries++;
		}else {
			throw new AlreadyActiveQuery();
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
		catch(SQLException | ClassNotFoundException e) {
			System.out.println("Something wrong with the query... Aborting.");
			try {
				endQuery();
			} catch (NoQueryToClose e1) {
				//Impossible
			}
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

	/**
	 * Method to perform a query using parameters
	 * @param query the sql code for the query
	 * @param list of elements to introduce in the query
	 * @return the set of the query
	 * @throws AlreadyActiveQuery if there is a not closed query
	 */
	public ResultSet performQueryWithParams(String query, List<Object> list) throws AlreadyActiveQuery{
		
		if(numQueries==0) {
			numQueries++;
		}else {
			throw new AlreadyActiveQuery();
		}
		
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName(JDBC_DRIVER);

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL+dBase,user,pass);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      prepStmt=conn.prepareStatement(query);
		      Iterator<Object> it=list.iterator();
		      Object o;
		      for(int i=1;it.hasNext();i++) {
		    	  o=it.next();
		    	  if(o instanceof String) {
		    		  prepStmt.setString(i, (String)o);
		    	  }else if (o instanceof Integer) {
		    		  prepStmt.setInt(i, (int)o);
		    	  }else {
		    		  prepStmt.setObject(i, o);
		    		  System.out.println(o.getClass()+" has been introduced with setObject method, check its correctness");
		    	  }
		      }
		      
		      rs = prepStmt.executeQuery();
		}
		catch(SQLException | ClassNotFoundException e) {
			System.out.println("Something wrong with the query... Aborting.");
			try {
				endQuery();
			} catch (NoQueryToClose e1) {
				//Impossible
			}
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

	
	
	public void endQuery() throws NoQueryToClose{
		
		if(numQueries==1)	
			numQueries--;
		else throw new NoQueryToClose();
		
		try {
			
			if(rs!=null)	rs.close();
			
			if(stmt!=null)	stmt.close();
			
			if(prepStmt!=null)	prepStmt.close();
			
			if(conn!=null)	conn.close();
				
		} catch (SQLException e) {
			System.out.println("Failed to close");
		}
	}
	
}

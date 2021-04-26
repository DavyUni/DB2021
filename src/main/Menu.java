package main;

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
	
	public static void main(String[] args) {
		
		String s="--------------------------------------------------------------\n"+
				 "|                      Select a choice:                      |\n"+
				 "|------------------------------------------------------------|\n"+
				 "| type 1 if you want to access to an standard account and DB |\n"+
				 "| type 2 to introduce your own account and DataBase          |\n"+
				 "| type anything else to terminate the execution              |\n"+
				 "--------------------------------------------------------------\n";
		String selMenu="--------------------------------------------------------------\n"+
					   "|                      Select a choice:                      |\n"+
					   "|------------------------------------------------------------|\n"+
					   "|   Iker's queries section:                                  |\n"+
					   "|                                                            |\n"+
					   "|   Davy's queries section:                                  |\n"+
					   "|                                                            |\n"+
					   "|   Juyoung's queries section:                               |\n"+
					   "|                                                            |\n"+
					   "| type anything else to terminate the execution              |\n"+
					   "--------------------------------------------------------------\n";
		
		System.out.println(s);
		
		Scanner sn=new Scanner(System.in);
		String input=sn.nextLine();
		
		boolean stay=true;
		
		switch (input) {
			case "1":
				standardWay();
				break;
				
			case "2":
				other(sn);
				break;
				
			default:
				System.out.println("Goodbye!!");
				stay=false;
				break;
		
		}
		
		while(stay) {
			
			System.out.println(selMenu);
			
			input=sn.nextLine();
			
			switch (input) {
				
				
				default:
					stay=false;
					System.out.println("Goodbye!!");
					break;
			}
			
			
		}
		
		sn.close();
		
		
	}

	private static void standardWay() {
		
		qmaker = new QueryConsole(Credentials.USER, Credentials.PASSWORD, Credentials.DATABASE);
		
	}

	private static void other(Scanner sn) {
		
		
		System.out.println("Insert the username of your mysql account:");
		String user=sn.nextLine();
		
		System.out.println("Now, insert your password of the same mysql account:");
		String pwd=sn.nextLine();
		
		System.out.print("Before going on we remind you to connect the vpn");
		try {
			Thread.sleep(1000);
			System.out.print(".");
			
			Thread.sleep(1000);
			System.out.print(".");
			
			Thread.sleep(1000);
			System.out.println(".");
			
		} catch (InterruptedException e) {
			
		}
		
		System.out.println("Okay, let's go on! Introduce the name of the database you will access (usually the name is similar to the user's name):");
		String db=sn.nextLine();
		
		System.out.println("Creating query console...");
		
		qmaker = new QueryConsole(user, pwd, db);
		
		System.out.println("Created!");
		
		
		
	}

}

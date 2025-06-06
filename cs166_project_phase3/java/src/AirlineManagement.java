 import java.sql.DriverManager;
 import java.sql.Connection;
 import java.sql.Statement;
 import java.sql.ResultSet;
 import java.sql.ResultSetMetaData;
 import java.sql.SQLException;
 import java.io.File;
 import java.io.FileReader;
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.util.List;
 import java.util.ArrayList;
 import java.lang.Math;
 import java.util.LinkedList;
 
 
 
 /**
  * This class defines a simple embedded SQL utility class that is designed to
  * work with PostgreSQL JDBC drivers.
  *
  */
 public class AirlineManagement {
 
    public static List<String[]> users = new LinkedList<>();
 
    static {
       users.add(new String[]{"admin", "adminpass", "Management"});
       users.add(new String[]{"customer1", "custpass", "Customer"});
       users.add(new String[]{"pilot1", "pilotpass", "Pilot"});
       users.add(new String[]{"tech1", "techpass", "Technician"});
    }
 
    // reference to physical database connection.
    private Connection _connection = null;
 
    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));
 
    /**
     * Creates a new instance of AirlineManagement
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {
 
       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");
 
          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end AirlineManagement
 
    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the update instruction
       stmt.executeUpdate (sql);
 
       // close the instruction
       stmt.close ();
    }//end executeUpdate
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult(String query) throws SQLException {
      Statement stmt = this._connection.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;
  
      // iterates through the result set and outputs them to standard out.
      boolean outputHeader = true;
      while (rs.next()) {
          if (outputHeader) {
              for (int i = 1; i <= numCol; i++) {
                  System.out.printf("%-20s", rsmd.getColumnName(i));  // Left-align, width 20
              }
              System.out.println();
              outputHeader = false;
          }
          for (int i = 1; i <= numCol; ++i) {
              System.out.printf("%-20s", rs.getString(i));  // Match header format
          }
          System.out.println();
          ++rowCount;
      }
      stmt.close();
      return rowCount;
  }
  
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();
 
       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);
 
       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;      // ...existing code...
 
       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
         List<String> record = new ArrayList<String>();
       for (int i=1; i<=numCol; ++i)
          record.add(rs.getString (i));
         result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult
 
 
 
 
    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();
 
        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);
 
        int rowCount = 0;
 
        // iterates through the result set and count nuber of results.
        while (rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }//end executeQuery
 
    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
       Statement stmt = this._connection.createStatement ();
 
       ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
       if (rs.next())
          return rs.getInt(1);
       return -1;
    }//end getCurrSeqVal
 
    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup
 
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
  public static void main (String[] args) {
    if (args.length != 3) {
       System.err.println (
          "Usage: " +
          "java [-classpath <classpath>] " +
          AirlineManagement.class.getName () +
          " <dbname> <port> <user>");
       return;
    }//end if
 
    Greeting();
    AirlineManagement esql = null;
    try{
       // use postgres JDBC driver.
       Class.forName ("org.postgresql.Driver").newInstance ();
       // instantiate the AirlineManagement object and creates a physical
       // connection.
       String dbname = args[0];
       String dbport = args[1];
       String user = args[2];
       esql = new AirlineManagement (dbname, dbport, user, "");
 
       boolean keepon = true;
       while(keepon) {
          // These are sample SQL statements
          System.out.println("MAIN MENU");
          System.out.println("---------");
          System.out.println("1. Create user");
          System.out.println("2. Log in");
          System.out.println("9. < EXIT");
          String[] authorisedUser = null;
          switch (readChoice()){
             case 1: CreateUser(esql); break;
             case 2: authorisedUser = LogIn(esql); break;
             case 9: keepon = false; break;
             default : System.out.println("Unrecognized choice!"); break;
          }//end switch
          if  (authorisedUser != null) {
             String login = authorisedUser[0];
             String role = authorisedUser[1]; 
             boolean usermenu = true;
             while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
 
                //**the following functionalities should only be able to be used by Management**
                if (role.equalsIgnoreCase("Management")) {
                   System.out.println("1. View Flights");
                   System.out.println("2. View Flight Seats");
                   System.out.println("3. View Flight Status");
                   System.out.println("4. View Flights of the day");  
                   System.out.println("5. View Full Order ID History");
                   System.out.println("6. View Reservations");
                   System.out.println("7. View Plane Information");
                   System.out.println("8. View Repairs by Person");
                   System.out.println("9. View Repairs by Plane");
                   System.out.println("10.View Flight Statistics");
                }
                //**the following functionalities should only be able to be used by customers**
                if (role.equalsIgnoreCase("Customer")) {
                   System.out.println("11. Search Flights");
                   System.out.println("12. Get Ticket Cost ");
                   System.out.println("13. Get Airplane Type ");
                   System.out.println("14. Make Reservation");
                }
                //**the following functionalities should only be able to be used by Pilots**
                if (role.equalsIgnoreCase("Pilot")) {
                   System.out.println("15. Maintenace Request");
                }
                //**the following functionalities should only be able to be used by Technicians**
                if (role.equalsIgnoreCase("Technician")) {
                   System.out.println("16. View Repairs Performed");
                   System.out.println("17. View Maintenance Requests");
                   System.out.println("18. Add Repair");
                }
                System.out.println("20. Log out");
 
                int choice = readChoice();
                switch (choice){
                   case 1: case 2: case 3: case 4: case 5: case 6: case 7: case 8: case 9: case 10:
                      if (role.equalsIgnoreCase("Management")) {
                         switch (choice) {
                            case 1: feature1(esql); break;
                            case 2: feature2(esql); break;
                            case 3: feature3(esql); break;
                            case 4: feature4(esql); break;
                            case 5: feature5(esql); break;
                            case 6: feature6(esql); break;
                            case 7: feature7(esql); break;
                            case 8: feature8(esql); break;
                            case 9: feature9(esql); break;
                            case 10: feature10(esql); break;
                         }
                      } else {
                         System.out.println("Access denied: Management only.");
                      }
                      break;
                   case 11: case 12: case 13: case 14:
                      if (role.equalsIgnoreCase("Customer")) {
                         switch (choice) {
                            case 11: feature11(esql); break;
                            case 12: feature12(esql); break;
                            case 13: feature13(esql); break;
                            case 14: feature14(esql); break;
                         }
                      } else {
                         System.out.println("Access denied: Customers only.");
                      }
                      break;
                   case 15:
                      if (role.equalsIgnoreCase("Pilot")) {
                         feature15(esql);
                      } else {
                         System.out.println("Access denied: Pilots only.");
                      }
                      break;
                   case 16: case 17: case 18:
                      if (role.equalsIgnoreCase("Technician")) {
                         switch (choice) {
                            case 16: feature16(esql); break;
                            case 17: feature17(esql); break;
                            case 18: feature18(esql); break;
                         }
                      } else {
                         System.out.println("Access denied: Technicians only.");
                      }
                      break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
             } // end usermenu while
          } // end if authorisedUser
       } // end keepon while
    } catch(Exception e) {
       System.err.println (e.getMessage ());
    } finally {
       // make sure to cleanup the created table and close the connection.
       try {
          if(esql != null) {
             System.out.print("Disconnecting from database...");
             esql.cleanup ();
             System.out.println("Done\n\nBye !");
          }//end if
       } catch (Exception e) {
          // ignored.
       }//end catch
    } // end finally
 } // end main
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting
 
    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("Please make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice
 
    /*
     * Creates a new user
     **/
    public static void CreateUser(AirlineManagement esql){
    try {
       System.out.print("Enter login: ");
       String login = in.readLine();
 
       System.out.print("Enter password: ");
       String password = in.readLine();
 
       System.out.print("Enter full name: ");
       String name = in.readLine();
 
       System.out.print("Enter role (Customer, Management, Pilot, Technician): ");
       String role = in.readLine();
 
       // Add to in-memory list
       users.add(new String[]{login, password, role});
       System.out.println("User created successfully!");
 
    } catch (Exception e) {
       System.err.println("Error creating user: " + e.getMessage());
    }
 }//end CreateUser
 
 
    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static String[] LogIn(AirlineManagement esql){
      try {
         System.out.print("Enter login: ");
         String login = in.readLine();
   
         System.out.print("Enter password: ");
         String password = in.readLine();
   
         for (String[] user : users) {
            if (user[0].equals(login) && user[1].equals(password)) {
               System.out.println("Login successful! Welcome " + login + " (" + user[2] + ")");
               return new String[]{login, user[2]};
            }
         }
         System.out.println("Invalid login or password. Try again.");
         return null;
         } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            return null;
         }
   }//end LogIn
 
 
 // Rest of the functions definition go in here
 
    public static void feature1(AirlineManagement esql) {
       try {
       System.out.print("Enter flight number: ");
       String flightNumber = in.readLine();
 
       String query = String.format(
          "SELECT DayOfWeek, DepartureTime, ArrivalTime FROM Schedule WHERE FlightNumber = '%s';",
          flightNumber);
 
       esql.executeQueryAndPrintResult(query);
       } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
       }
    }//end feature1
 
    public static void feature2(AirlineManagement esql) {
       try {
       System.out.print("Enter flight number: ");
       String flightNumber = in.readLine();
       System.out.print("Enter flight date (M/D/YY): ");
       String date = in.readLine();
 
       String query = String.format(
          "SELECT SeatsTotal - SeatsSold AS SeatsAvailable, SeatsSold " +
          "FROM FlightInstance WHERE FlightNumber = '%s' AND FlightDate = '%s';",
          flightNumber, date);
 
       esql.executeQueryAndPrintResult(query);
          } catch (Exception e)
     {
       System.err.println("Error: " + e.getMessage());
     }
    }//end feature2
 
    public static void feature3(AirlineManagement esql) {
      try {
         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine();

         System.out.print("Enter flight date (M/D/YY): ");
         String date = in.readLine();

         String query = String.format(
               "SELECT DepartedOnTime, ArrivedOnTime " +
               "FROM FlightInstance WHERE FlightNumber = '%s' AND FlightDate = '%s';",
               flightNumber, date);

         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
               System.out.println("No flight found with that number and date.");
               return;
         }

         String departed = result.get(0).get(0).equals("t") ? "Yes" : "No";
         String arrived = result.get(0).get(1).equals("t") ? "Yes" : "No";

         System.out.println("Departed on time: " + departed);
         System.out.println("Arrived on time: " + arrived);

      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
}//end feature3
 
    public static void feature4(AirlineManagement esql) {
       try {
       System.out.print("Enter date (M-D-YY): ");
       String date = in.readLine();
 
       String query = String.format(
          "SELECT FlightNumber, FlightDate, DepartureCity, ArrivalCity " +
          "FROM FlightInstance JOIN Flight USING (FlightNumber) " +
          "WHERE FlightDate = '%s';",
          date);
 
       esql.executeQueryAndPrintResult(query);
       } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
       }
    }//end feature4
 
    public static void feature5(AirlineManagement esql) {
      try {
         System.out.print("Enter flight number: ");
         String flightNumber = in.readLine();

         System.out.print("Enter flight date (M/D/YY): ");
         String date = in.readLine();

         String query = String.format(
            "SELECT C.FirstName || ' ' || C.LastName AS Name, R.Status " +
            "FROM Reservation R " +
            "JOIN Customer C ON R.CustomerID = C.CustomerID " +
            "JOIN FlightInstance FI ON R.FlightInstanceID = FI.FlightInstanceID " +
            "WHERE FI.FlightNumber = '%s' AND FI.FlightDate = '%s';",
            flightNumber, date);

         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
               System.out.println("No passengers found for that flight and date.");
               return;
         }

         List<String> reserved = new ArrayList<>();
         List<String> waitlisted = new ArrayList<>();
         List<String> flew = new ArrayList<>();

         for (List<String> row : result) {
               String name = row.get(0);
               String status = row.get(1).toLowerCase();

               switch (status) {
                  case "reserved":
                     reserved.add(name);
                     break;
                  case "waitlist":
                     waitlisted.add(name);
                     break;
                  case "flown":
                     flew.add(name);
                     break;
                  default:
                     reserved.add(name); // fallback
                     break;
               }
         }

         System.out.println("\n--- Passengers with Reservations ---");
         if (reserved.isEmpty()) System.out.println("None");
         else reserved.forEach(name -> System.out.println("- " + name));

         System.out.println("\n--- Passengers on Waiting List ---");
         if (waitlisted.isEmpty()) System.out.println("None");
         else waitlisted.forEach(name -> System.out.println("- " + name));

         System.out.println("\n--- Passengers Who Actually Flew ---");
         if (flew.isEmpty()) System.out.println("None");
         else flew.forEach(name -> System.out.println("- " + name));

      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
}
//end feature5
 
    public static void feature6(AirlineManagement esql) {
      try {
         System.out.print("Enter reservation ID: ");
         String resID = in.readLine();

         String query = String.format(
            "SELECT FirstName, LastName, Gender, DOB, Address, Phone, Zip " +
            "FROM Reservation R JOIN Customer C ON R.CustomerID = C.CustomerID " +
            "WHERE R.ReservationID = '%s';",
            resID);

         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty()) {
            System.out.println("No reservation found with that ID.");
            return;
         }

         List<String> row = result.get(0);  // There should only be one result

         System.out.println("\n--- Customer Details ---");
         System.out.println("Name           : " + row.get(0) + " " + row.get(1));
         System.out.println("Gender         : " + row.get(2));
         System.out.println("Date of Birth  : " + row.get(3));
         System.out.println("Address        : " + row.get(4));
         System.out.println("Phone          : " + row.get(5));
         System.out.println("ZIP Code       : " + row.get(6));

      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
}//end feature6
 
    public static void feature7(AirlineManagement esql) {
        try {
        System.out.print("Enter Plane ID: ");
        String planeID = in.readLine();

        String query = String.format(
            "SELECT Make, Model, (EXTRACT(YEAR FROM CURRENT_DATE) - Year) AS Age, LastRepairDate " +
            "FROM Plane WHERE PlaneID = '%s';",
            planeID);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.isEmpty()) {
            System.out.println("No plane found with that ID.");
            return;
        }

        List<String> row = result.get(0);  // Expecting one record

        System.out.println("\n--- Plane Details ---");
        System.out.println("Make           : " + row.get(0));
        System.out.println("Model          : " + row.get(1));
        System.out.println("Age (Years)    : " + row.get(2));
        System.out.println("Last Repaired  : " + row.get(3));

      } catch (Exception e) {
         System.err.println("Error: " + e.getMessage());
      }
}//end feature7
 
    public static void feature8(AirlineManagement esql) {
        try {
       System.out.print("Enter Technician ID: ");
       String techID = in.readLine();
 
       String query = String.format("SELECT * FROM Repair WHERE TechnicianID = '%s';", techID);
 
       esql.executeQueryAndPrintResult(query);
       } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
       }
    }//end feature8
 
    public static void feature9(AirlineManagement esql) {
        try {
       System.out.print("Enter Plane ID: ");
       String planeID = in.readLine();
       System.out.print("Enter start date (YYYY-MM-DD): ");
       String start = in.readLine();
       System.out.print("Enter end date (YYYY-MM-DD): ");
       String end = in.readLine();
 
       String query = String.format(
          "SELECT RepairDate, RepairCode FROM Repair " +
          "WHERE PlaneID = '%s' AND RepairDate BETWEEN '%s' AND '%s';",
          planeID, start, end);
 
       esql.executeQueryAndPrintResult(query);
       } catch (Exception e) {
          System.err.println("Error: " + e.getMessage());
       }  
    }//end feature9
 
    public static void feature10(AirlineManagement esql) {
    try {
        System.out.print("Enter flight number: ");
        String flightNumber = in.readLine();

        System.out.print("Enter start date (M/D/YY): ");
        String start = in.readLine();

        System.out.print("Enter end date (M/D/YY): ");
        String end = in.readLine();

        String query = String.format(
            "SELECT COUNT(*) AS TotalDays, " +
            "SUM(CASE WHEN DepartedOnTime THEN 1 ELSE 0 END) AS DepartedOnTimeCount, " +
            "SUM(CASE WHEN ArrivedOnTime THEN 1 ELSE 0 END) AS ArrivedOnTimeCount, " +
            "SUM(SeatsSold) AS TotalTicketsSold, " +
            "SUM(SeatsTotal - SeatsSold) AS TotalTicketsUnsold " +
            "FROM FlightInstance " +
            "WHERE FlightNumber = '%s' AND FlightDate BETWEEN '%s' AND '%s';",
            flightNumber, start, end);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.isEmpty()) {
            System.out.println("No data found for that flight and date range.");
            return;
        }

        List<String> row = result.get(0); // Expecting one row

        System.out.println("\n--- Flight Statistics ---");
        System.out.println("Flight Number        : " + flightNumber);
        System.out.println("Date Range           : " + start + " to " + end);
        System.out.println("Total Days Operated  : " + row.get(0));
        System.out.println("Departed On Time     : " + row.get(1) + " day(s)");
        System.out.println("Arrived On Time      : " + row.get(2) + " day(s)");
        System.out.println("Total Tickets Sold   : " + row.get(3));
        System.out.println("Total Tickets Unsold : " + row.get(4));

    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    }
}//end feature10
 
    public static void feature11(AirlineManagement esql) {
        try {
       System.out.print("Enter departure city: ");
       String departure = in.readLine();
 
       System.out.print("Enter arrival city: ");
       String arrival = in.readLine();
 
       String query = String.format(
          "SELECT * FROM Flight WHERE DepartureCity = '%s' AND ArrivalCity = '%s';",
          departure, arrival
       );
 
       esql.executeQueryAndPrintResult(query);
       } catch (Exception e) {
          System.err.println("Error searching flights: " + e.getMessage());
       }
    }//end feature11
 
    public static void feature12(AirlineManagement esql) {
    try {
        System.out.print("Enter flight number: ");
        String flightNum = in.readLine();

        System.out.print("Enter flight date (M/D/YY): ");
        String date = in.readLine();

        String query = String.format(
            "SELECT ticketCost FROM FlightInstance WHERE FlightNumber = '%s' AND FlightDate = '%s';",
            flightNum, date);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.isEmpty()) {
            System.out.println("No ticket found for that flight and date.");
        } else {
            System.out.println("Ticket cost: $" + result.get(0).get(0));
        }
    } catch (Exception e) {
        System.err.println("Error getting ticket cost: " + e.getMessage());
    }
}//end feature12
 
    public static void feature13(AirlineManagement esql) {
    try {
        System.out.print("Enter flight number: ");
        String flightNum = in.readLine();

        String query = String.format(
            "SELECT Make, Model " +
            "FROM Plane P " +
            "JOIN Flight F ON F.PlaneID = P.PlaneID " +
            "WHERE F.FlightNumber = '%s';",
            flightNum);

        List<List<String>> result = esql.executeQueryAndReturnResult(query);

        if (result.isEmpty()) {
            System.out.println("No plane found for the given flight number.");
        } else {
            String make = result.get(0).get(0);
            String model = result.get(0).get(1);
            System.out.println("Plane Make: " + make);
            System.out.println("Plane Model: " + model);
        }
    } catch (Exception e) {
        System.err.println("Error retrieving plane make and model: " + e.getMessage());
    }
}
//end feature13
 
    public static void feature14(AirlineManagement esql) {
      try {
         System.out.print("Enter your Customer ID: ");
         String customerID = in.readLine();

         System.out.print("Enter flight number: ");
         String flightNum = in.readLine();

         System.out.print("Enter flight date (YYYY-MM-DD): ");
         String flightDate = in.readLine();

         System.out.print("Enter number of tickets: ");
         int numTickets = Integer.parseInt(in.readLine());

         String checkQuery = String.format(
               "SELECT FI.FlightInstanceID, FI.SeatsTotal, FI.SeatsSold " +
               "FROM FlightInstance FI " +
               "WHERE FI.FlightNumber = '%s' AND FI.FlightDate = '%s';",
               flightNum, flightDate);

         List<List<String>> result = esql.executeQueryAndReturnResult(checkQuery);

         if (result.isEmpty()) {
               System.out.println("No flight instance found for that flight and date.");
               return;
         }

         int flightInstanceID = Integer.parseInt(result.get(0).get(0));
         int totalSeats = Integer.parseInt(result.get(0).get(1));
         int seatsSold = Integer.parseInt(result.get(0).get(2));

         int seatsAvailable = totalSeats - seatsSold;

         int reserved = 0, waitlisted = 0;
         for (int i = 0; i < numTickets; i++) {
               String status;
               if (seatsAvailable > 0) {
                  status = "reserved";
                  seatsAvailable--;
                  reserved++;
               } else {
                  status = "waitlist";
                  waitlisted++;
               }
               String reservationID = "R" + System.currentTimeMillis() + "_" + (int)(Math.random()*10000);
               String insertQuery = String.format(
               "INSERT INTO Reservation (ReservationID, CustomerID, FlightInstanceID, Status) " +
               "VALUES ('%s', %s, %d, '%s');",
               reservationID, customerID, flightInstanceID, status);
               esql.executeUpdate(insertQuery);
         }

         if (reserved > 0) {
               String updateQuery = String.format(
                  "UPDATE FlightInstance SET SeatsSold = SeatsSold + %d WHERE FlightInstanceID = %d;",
                  reserved, flightInstanceID);
               esql.executeUpdate(updateQuery);
         }

         System.out.println("Reservation complete: " + reserved + " reserved, " + waitlisted + " waitlisted.");

      } catch (Exception e) {
         System.err.println("Error making reservation: " + e.getMessage());
      }
}//end feature14
 
    public static void feature15(AirlineManagement esql) {
        try {
       System.out.print("Enter plane ID: ");
       String planeID = in.readLine();
 
       System.out.print("Enter maintenance issue: ");
       String issue = in.readLine();
 
       String query = String.format(
          "INSERT INTO MaintenanceRequests (planeID, requestDate, description) " +
          "VALUES ('%s', CURRENT_DATE, '%s');",
          planeID, issue);
 
       esql.executeUpdate(query);
       System.out.println("Maintenance request submitted!");
       } catch (Exception e) {
          System.err.println("Error submitting request: " + e.getMessage());
       }
    }//end feature15
 
    public static void feature16(AirlineManagement esql) {
    try {
        System.out.print("Enter Plane ID: ");
        String planeID = in.readLine();

        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = in.readLine();

        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = in.readLine();

        String query = String.format(
            "SELECT RepairDate, RepairCode FROM Repair " +
            "WHERE PlaneID = '%s' AND RepairDate BETWEEN '%s' AND '%s' " +
            "ORDER BY RepairDate;",
            planeID, startDate, endDate);

        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println("Error retrieving repairs: " + e.getMessage());
    }
}//end feature16
 
    public static void feature17(AirlineManagement esql) {
    try {
        System.out.print("Enter Pilot ID: ");
        String pilotID = in.readLine();

        String query = String.format(
            "SELECT RepairCode FROM MaintenanceRequest WHERE PilotID = '%s';",
            pilotID);

        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println("Error fetching maintenance requests: " + e.getMessage());
    }
}//end feature17
 
    public static void feature18(AirlineManagement esql) {
    try {
        System.out.print("Enter plane ID: ");
        String planeID = in.readLine();

        System.out.print("Enter technician ID: ");
        String techID = in.readLine();

        System.out.print("Enter repair code: ");
        String repairCode = in.readLine();

        // Generate a random RepairID (e.g., using current time and random number)
        int repairID = (int)(System.currentTimeMillis() % 1000000) + (int)(Math.random() * 10000);

        String query = String.format(
            "INSERT INTO Repair (RepairID, PlaneID, RepairCode, RepairDate, TechnicianID) " +
            "VALUES (%d, '%s', '%s', CURRENT_DATE, '%s');",
            repairID, planeID, repairCode, techID);

        esql.executeUpdate(query);
        System.out.println("Repair added successfully!");
    } catch (Exception e) {
        System.err.println("Error adding repair: " + e.getMessage());
    }  
}//end feature18//end feature18
 
 } //end AirlineManagement
import java.sql.*;

public class DockerConnectMySQL {
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://10.0.10.3:3306/zadanie";

   static final String USER = "mnowosad";
   static final String PASS = "password";
   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      Class.forName("com.mysql.jdbc.Driver");

      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL,USER,PASS);

      stmt = conn.createStatement();
      String sql;
      sql = "CREATE TABLE MyGuests (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50))";

ResultSet rs = stmt.executeQuery(sql);

sql = "INSERT INTO MyGuests (firstname, lastname, email) VALUES ('Mateusz', 'Nowosad', 'asd@asd.pl')";

rs = stmt.executeQuery(sql);

sql = "INSERT INTO MyGuests (firstname, lastname, email) VALUES ('Mateusz1', 'Nowosad1', 'asd1@asd.pl')";

rs = stmt.executeQuery(sql);

sql = "INSERT INTO MyGuests (firstname, lastname, email) VALUES ('Mateusz2', 'Nowosad2', 'asd2@asd.pl')";

rs = stmt.executeQuery(sql);
	   
	   sql = "SELECT id, firstname, lastname, email FROM MyGuests";
rs = stmt.executeQuery(sql);

      while(rs.next()){
         int id  = rs.getInt("id");
         String first = rs.getString("firstname");
         String last = rs.getString("lastname");
		 String address = rs.getString("email");

         System.out.println("ID: " + id);
         System.out.println(", First: " + first);
         System.out.println(", Last: " + last);
		 System.out.println(", Email Address: " + address);
      }
      rs.close();
      stmt.close();
      conn.close();
   }catch(SQLException se){
      se.printStackTrace();
   }catch(Exception e){
      e.printStackTrace();
   }finally{
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }
   }
 }
}

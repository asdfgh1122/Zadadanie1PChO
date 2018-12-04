import java.util.Scanner;
import java.sql.*;

/**
 *
 * @author Mateusz Nowosad
 */
public class DockerMySQLTest {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://10.0.10.3:3306/zadanie";

    static final String USER = "mnowosad";
    static final String PASS = "password";

    static Connection connectToDatabase() {
        for (;;) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                return conn;
            } catch (SQLException se) {
                System.out.println(se);
            }
        }
    }

    static String databaseInit(Connection conn, Statement stmt) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet metaData = meta.getTables(null, null, "%", null);
            while (metaData.next()) {
                System.out.println("Czysczenie bazy, usuwam table " + metaData.getString(3));
                stmt.executeUpdate("DROP TABLE " + metaData.getString(3));
            }
            stmt.executeUpdate("CREATE TABLE users (id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,firstname VARCHAR(30) NOT NULL,lastname VARCHAR(30) NOT NULL,email VARCHAR(50))");
            stmt.executeUpdate("INSERT INTO users (firstname, lastname, email) VALUES ('Mateusz', 'Nowosad', 'asd@asd.pl'),('Mateusz1', 'Nowosad1', 'asd1@asd.pl'),('Mateusz2', 'Nowosad2', 'asd2@asd.pl')");
            System.out.println("Inicjalizacja tabeli zakonczona!");

            String workingTable;
            meta = conn.getMetaData();
            metaData = meta.getTables(null, null, "%", null);
            System.out.println("Dostepne tabele:  ");
            if (metaData.next()) {
                workingTable = metaData.getString(3);
                System.out.println(workingTable);
                return workingTable;
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
        return "ERROR";
    }

    static void processQuery(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) {
                        System.out.print("  ");
                    }
                    String columnValue = rs.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
                }
                System.out.println("");
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
    }

    static void insertQuery(Statement stmt, String workingTable) {
        Scanner odczyt = new Scanner(System.in);
        String sql = "INSERT INTO users (";
        String sqlValues = " VALUES (";
        try {
            ResultSet rs = stmt.executeQuery("select * from " + workingTable);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) {
                    System.out.print(rsmd.getColumnName(i) + ": ");
                    if (i != columnsNumber) {
                        sql += rsmd.getColumnName(i) + ",";
                        sqlValues += "'" + odczyt.nextLine() + "',";
                    } else {
                        sql += rsmd.getColumnName(i) + ")";
                        sqlValues += "'" + odczyt.nextLine() + "');";
                    }
                }
            }
            stmt.executeUpdate(sql + sqlValues);
        } catch (SQLException se) {
            System.out.println(se);
        }
    }

    static void runDangerousQuery(Statement stmt) {
        Scanner odczyt = new Scanner(System.in);
        try {
            if (stmt.execute(odczyt.nextLine())) {
                ResultSet dangerRS = stmt.getResultSet();
                processQuery(dangerRS);
            } else {
                System.out.println("Zapytanie zwórciło int albo void");
            }
        } catch (SQLException se) {
            System.out.println(se);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Laczenie z baza...");
            conn = connectToDatabase();
            System.out.println("Polaczono!");
            stmt = conn.createStatement();

            String workingTable = databaseInit(conn, stmt);
            if (workingTable.equals("ERROR")) {
                System.out.println("Coś poszło nie tak przy inicjalizacji bazy danych, zamykam program...");
            } else {
                String control;
                Scanner odczyt = new Scanner(System.in);
                System.out.println("Kontrola funkcji \"0\" - odczyt danych, \"1\" - Zapis danych , \"2\" - Wpisywanie zapytań SQL, \"3\" - Zakończenie programu");
                boolean finish = true;
                while (finish) {
                    System.out.print("Podaj kod kontrolny: ");
                    control = odczyt.nextLine();

                    switch (control) {
                        case "0":
                            System.out.println("Wszystkie zwrotki");
                            ResultSet rs = stmt.executeQuery("select * from " + workingTable);
                            processQuery(rs);
                            break;
                        case "1":
                            String sql = "INSERT INTO users (firstname, lastname, email) VALUES ";
                            System.out.println("Wypelnij dane");
                            insertQuery(stmt, workingTable);
                            System.out.println("Zapisano!");
                            break;
                        case "2":
                            System.out.print("Wpisz zapytanie SQL: ");
                            runDangerousQuery(stmt);
                            break;
                        case "3":
                            System.out.println("Zamykam...");
                            finish = false;
                            break;
                        default:
                            System.out.println("Bledny kod kontrolny");
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException se) {
            System.out.println(se);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }
        }
    }

}

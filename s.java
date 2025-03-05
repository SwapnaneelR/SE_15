import java.util.*;
import java.sql.*;

class Main {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
	String url = "jdbc:postgresql://pg-1af627cc-sray.i.aivencloud.com/defaultdb?ssl=require&user=avnadmin&password=AVNS_j11_bVbkRFF0fOJDpUa";
        //String url = "jdbc:postgresql://64.227.168.242:28134/defaultdb?sslmode=require";
        String username = "avnadmin";
        String password = "AVNS_j11_bVbkRFF0fOJDpUa";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found. Include it in your library path!");
            e.printStackTrace();
            return;
        }

        System.out.println("Enter Your role.\nPress 1 for teacher, 2 for student");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            System.out.println("Enter the subject you teach:\n1. OOPS\n2. DBMS\n3. COA\n4. DSA");
            int subjectChoice = sc.nextInt();
            sc.nextLine();

            while (true) {
                System.out.println("Enter 1 to update marks, 0 to exit");
                int ch = sc.nextInt();
                if (ch == 0) {
                    break;
                }

                System.out.println("Enter the student's roll number to update marks:");
                int rollNo = sc.nextInt();
                System.out.println("Enter the marks for the subject:");
                int marks = sc.nextInt();
                sc.nextLine();

                String updateQuery = "";
                switch (subjectChoice) {
                    case 1:
                        updateQuery = "UPDATE MARKS SET oops_marks = ? WHERE roll_no = ?";
                        break;
                    case 2:
                        updateQuery = "UPDATE MARKS SET dbms_marks = ? WHERE roll_no = ?";
                        break;
                    case 3:
                        updateQuery = "UPDATE MARKS SET coa_marks = ? WHERE roll_no = ?";
                        break;
                    case 4:
                        updateQuery = "UPDATE MARKS SET dsa_marks = ? WHERE roll_no = ?";
                        break;
                    default:
                        System.out.println("Invalid choice, try again.");
                        continue;
                }

                try (Connection conn = DriverManager.getConnection(url,username,password);
                     PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

                    stmt.setInt(1, marks);
                    stmt.setInt(2, rollNo);

                    int rowsUpdated = stmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("Marks updated successfully!");
                    } else {
                        System.out.println("No student found with this roll number.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } else if (choice == 2) {
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 Statement stmt = conn.createStatement()) {
                
                String query = "SELECT name, roll_no, oops_marks, dbms_marks, coa_marks, dsa_marks, " +
                               "(oops_marks + dbms_marks + coa_marks + dsa_marks) AS total_marks " +
                               "FROM MARKS ORDER BY total_marks DESC;";
                
                ResultSet rs = stmt.executeQuery(query);
                if (!rs.isBeforeFirst()) {
                    System.out.println("No students found. Please enter your details.");
                    System.out.println("Enter your name:");
                    String name = sc.nextLine();
                    System.out.println("Enter your roll number:");
                    int rollNo = sc.nextInt();
                    sc.nextLine();
                    
                    String insertQuery = "INSERT INTO MARKS (name, roll_no, oops_marks, dbms_marks, coa_marks, dsa_marks) VALUES (?, ?, 0, 0, 0, 0)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, name);
                        insertStmt.setInt(2, rollNo);
                        int rowsInserted = insertStmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println("Student added successfully.");
                        }
                    }
                } else {
                    System.out.println("Student Name | Roll No | OOPS Marks | DBMS Marks | COA Marks | DSA Marks | Total Marks");
                    while (rs.next()) {
                        String name = rs.getString("name");
                        int rollNo = rs.getInt("roll_no");
                        int oopsMarks = rs.getInt("oops_marks");
                        int dbmsMarks = rs.getInt("dbms_marks");
                        int coaMarks = rs.getInt("coa_marks");
                        int dsaMarks = rs.getInt("dsa_marks");
                        int totalMarks = rs.getInt("total_marks");
                        
                        System.out.println(name + " | " + rollNo + " | " + oopsMarks + " | " + dbmsMarks +
                                           " | " + coaMarks + " | " + dsaMarks + " | " + totalMarks);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }
}


import java.util.*;
import java.sql.*;

class Main {
    private static final String URL = "jdbc:postgresql://64.227.168.242:28134/defaultdb?sslmode=require";
    private static final String USERNAME = "avnadmin";
    private static final String PASSWORD = "AVNS_j11_bVbkRFF0fOJDpUa";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            Class.forName("org.postgresql.Driver");
            setupDatabase();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        System.out.println(
                "  \n ROLES AVAILABLE: Admin, Teacher, Student \n ADMIN CAN ADD NEW STUDENTS TO THE DATABASE \n TEACHER CAN UPDATE MARKS OF STUDENTS OF THEIR PERTICULAR SUBJECT \n STUDENT CAN VIEW THE MARKS OF ALL STUDENTS \n");
        while (true) {
            System.out.println("Select your role: 1. Admin 2. Teacher 3. Student 4. Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    handleAdmin(sc);
                    break;
                case 2:
                    handleTeacher(sc);
                    break;
                case 3:
                    handleStudent();
                    break;
                case 4:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement()) {

            String createTableQuery = "CREATE TABLE IF NOT EXISTS MARKS (" +
                    "name VARCHAR(100) NOT NULL, " +
                    "roll_no INT PRIMARY KEY, " +
                    "oops_marks INT DEFAULT 0, " +
                    "dbms_marks INT DEFAULT 0, " +
                    "coa_marks INT DEFAULT 0, " +
                    "dsa_marks INT DEFAULT 0);";
            stmt.executeUpdate(createTableQuery);
        } catch (SQLException e) {
            System.out.println("Error setting up database: " + e.getMessage());
        }
    }

    private static void handleAdmin(Scanner sc) {
        System.out.println("Enter student name:");
        String name = sc.nextLine();
        System.out.println("Enter roll number:");
        int rollNo = sc.nextInt();

        String insertQuery = "INSERT INTO MARKS (name, roll_no) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, rollNo);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private static void handleTeacher(Scanner sc) {
        System.out.println("Select subject: 1. OOPS 2. DBMS 3. COA 4. DSA");
        int subjectChoice = sc.nextInt();
        sc.nextLine();

        String columnName = "";
        switch (subjectChoice) {
            case 1:
                columnName = "oops_marks";
                break;
            case 2:
                columnName = "dbms_marks";
                break;
            case 3:
                columnName = "coa_marks";
                break;
            case 4:
                columnName = "dsa_marks";
                break;
            default:
                System.out.println("Invalid subject.");
                return;
        }

        System.out.println("Enter roll number:");
        int rollNo = sc.nextInt();
        System.out.println("Enter new marks:");
        int marks = sc.nextInt();

        String updateQuery = "UPDATE MARKS SET " + columnName + " = ? WHERE roll_no = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setInt(1, marks);
            pstmt.setInt(2, rollNo);
            pstmt.executeUpdate();
            System.out.println("Marks updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating marks: " + e.getMessage());
        }
    }

    private static void handleStudent() {
        String query = "SELECT *, (oops_marks + dbms_marks + coa_marks + dsa_marks) AS total FROM MARKS ORDER BY total DESC";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("Name | Roll No | OOPS | DBMS | COA | DSA | Total");
            while (rs.next()) {
                System.out.println(rs.getString("name") + " | " + rs.getInt("roll_no") + " | " +
                        rs.getInt("oops_marks") + " | " + rs.getInt("dbms_marks") + " | " +
                        rs.getInt("coa_marks") + " | " + rs.getInt("dsa_marks") + " | " +
                        rs.getInt("total"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }
    }
}

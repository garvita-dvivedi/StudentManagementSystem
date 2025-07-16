import java.sql.*;
import java.util.Scanner;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.*;

public class studentSystem {

    static final String url = "jdbc:mysql://localhost:3306/student_db";
    static final String user = "root";
    static final String password = "g12345";
    static Scanner sc = new Scanner(System.in); // we have used static here bcz we want to get input wrt the prev op , if static was not used here each method have been performed interdependently

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");

            while (true) {
                System.out.println("\n===== Poornima Student Management System =====");
                System.out.println("1. View All Student Records");
                System.out.println("2. Add New Student");
                System.out.println("3. Update Student Details");
                System.out.println("4. Delete Student");
                System.out.println("5. Search Student");
                System.out.println("6. Validate Email or Phone");
                System.out.println("7. View Department-wise Stats");
                System.out.println("8. Exit");
                System.out.print("Choose an option: ");
                // it takes input as string and convert it to integer bcz sometimes you may skip the line while giving input
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1 -> viewAll(conn);
                    case 2 -> addStudent(conn);
                    case 3 -> updateStudent(conn);
                    case 4 -> deleteStudent(conn);
                    case 5 -> searchStudent(conn);
                    case 6 -> validateField(conn);
                    case 7 -> showStats(conn);
                    case 8 -> {
                        System.out.println("Exiting... Thank you!");
                        conn.close();
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public static void showMenuBasedOnRole(Connection conn, String role) {
        Set<Integer> allowedOptions = switch (role.toLowerCase()) {
            case "student" -> Set.of(1, 5, 8);
            case "teacher", "proctor" -> Set.of(1, 2, 3, 5, 6, 7, 8);
            case "hod", "principal", "director", "vice_principal" -> Set.of(1, 2, 3, 4, 5, 6, 7, 8);
            default -> Set.of(8); // unknown roles only see Exit
        };

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Poornima Student Management System =====");
            for (int i = 1; i <= 8; i++) {
                if (allowedOptions.contains(i)) {
                    System.out.println(getMenuText(i));
                }
            }

            System.out.print("Choose an option: ");
            int choice = Integer.parseInt(sc.nextLine());

            if (!allowedOptions.contains(choice)) {
                System.out.println("🚫 Access denied for this option.");
                continue;
            }

            switch (choice) {
                case 1 -> viewAll(conn);
                case 2 -> addStudent(conn);
                case 3 -> updateStudent(conn);
                case 4 -> deleteStudent(conn);
                case 5 -> searchStudent(conn);
                case 6 -> validateField(conn);
                case 7 -> showStats(conn);
                case 8 -> {
                    System.out.println("Exiting...");
                    return;
                }
            }
        }
    }

    private static String getMenuText(int i) {
        return switch (i) {
            case 1 -> "1. View All Student Records";
            case 2 -> "2. Add New Student";
            case 3 -> "3. Update Student Details";
            case 4 -> "4. Delete Student";
            case 5 -> "5. Search Student";
            case 6 -> "6. Validate Email or Phone";
            case 7 -> "7. View Department-wise Stats";
            case 8 -> "8. Exit";
            default -> "";
        };
    }


    public static void viewAll(Connection conn) {
        try {
            String query = "SELECT * FROM StudentsRecords";
            Statement stmt = conn.createStatement();  //This object helps send SQL commands (like queries) to the database
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--- Student Records ---");
            while (rs.next()) {
                printStudent(rs);
            }

        } catch (Exception e) {
            System.out.println("Failed to retrieve records: " + e.getMessage());
        }
    }

    public static void addStudent(Connection conn) {
        try {
            System.out.println("\n--- Add New Student ---");

            System.out.print("Enroll ID: ");
            String enrollId = sc.nextLine();

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Department: ");
            String dept = sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                System.out.println("Invalid email format.");
                return;
            }

            System.out.print("Phone: ");
            String phone = sc.nextLine();
            if (!phone.matches("\\d{10}")) {
                System.out.println("Invalid phone number.");
                return;
            }

            System.out.print("Gender (Male/Female/Other): ");
            String gender = sc.nextLine();

            System.out.print("DOB (yyyy-mm-dd): ");
            String dob = sc.nextLine();

            System.out.print("Blood Group: ");
            String bg = sc.nextLine();

            System.out.print("Address: ");
            String address = sc.nextLine();

            System.out.print("City: ");
            String city = sc.nextLine();

            System.out.print("State: ");
            String state = sc.nextLine();
            // Prevents SQL Injection attacks.
            //Automatically handles data formatting and escaping (e.g., strings, dates).
            //Makes your code easier to maintain.
            String query = "INSERT INTO StudentsRecords (Enroll_id, Name, Department, Email, Phone, Gender, Dob, blood_group, Address, City, State) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            // You're saying: Hey MySQL, I’ve got a query with blanks. Let’s prepare it.
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, enrollId);
            pstmt.setString(2, name);
            pstmt.setString(3, dept);
            pstmt.setString(4, email);
            pstmt.setString(5, phone);
            pstmt.setString(6, gender);
            pstmt.setString(7, dob);
            pstmt.setString(8, bg);
            pstmt.setString(9, address);
            pstmt.setString(10, city);
            pstmt.setString(11, state);

            pstmt.executeUpdate();
            System.out.println("Student added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public static void updateStudent(Connection conn) {
        try {
            System.out.print("Enter Enroll ID to update: ");
            String enrollId = sc.nextLine();

            System.out.println("Choose field to update:");
            System.out.println("1. Email");
            System.out.println("2. Phone");
            int choice = Integer.parseInt(sc.nextLine());

            String field = "";
            String newValue = "";

            switch (choice) {
                case 1 -> {
                    field = "Email";
                    System.out.print("New Email: ");
                    newValue = sc.nextLine();
                    if (!newValue.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                        System.out.println("Invalid email.");
                        return;
                    }
                }
                case 2 -> {
                    field = "Phone";
                    System.out.print("New Phone: ");
                    newValue = sc.nextLine();
                    if (!newValue.matches("\\d{10}")) {
                        System.out.println("Invalid phone.");
                        return;
                    }
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }

            String query = "UPDATE StudentsRecords SET " + field + " = ? WHERE Enroll_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, newValue);
            pstmt.setString(2, enrollId);

            int updated = pstmt.executeUpdate();
            if (updated > 0)
                System.out.println("Student updated.");
            else
                System.out.println("Enroll ID not found.");

        } catch (Exception e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    public static void deleteStudent(Connection conn) {
        try {
            System.out.print("Enter Enroll ID to delete: ");
            String enrollId = sc.nextLine();

            String query = "DELETE FROM StudentsRecords WHERE Enroll_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, enrollId);

            int deleted = pstmt.executeUpdate();
            if (deleted > 0)
                System.out.println("Student deleted.");
            else
                System.out.println("Enroll ID not found.");

        } catch (Exception e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    public static void searchStudent(Connection conn) {
        try {
            System.out.print("Enter Enroll ID or Name to search: ");
            String input = sc.nextLine();

            String query = "SELECT * FROM StudentsRecords WHERE Enroll_id = ? OR Name LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, input);
            pstmt.setString(2, "%" + input + "%");

            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No matching student found.");
                return;
            }

            System.out.println("\n--- Search Results ---");
            while (rs.next()) {
                printStudent(rs);
            }

        } catch (Exception e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    public static void validateField(Connection conn) {
        try {
            System.out.println("\nCheck for invalid:");
            System.out.println("1. Email");
            System.out.println("2. Phone");
            System.out.print("Choose: ");
            int choice = Integer.parseInt(sc.nextLine());

            String fieldToCheck = "";
            boolean foundInvalid = false;

            switch (choice) {
                case 1:
                    fieldToCheck = "Email";
                    break;
                case 2:
                    fieldToCheck = "Phone";
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }

            String query = "SELECT * FROM StudentsRecords";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--- Invalid " + fieldToCheck + " Records ---");

            while (rs.next()) {
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                boolean isInvalid = false;

                if (fieldToCheck.equals("Email") && !email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                    isInvalid = true;
                } else if (fieldToCheck.equals("Phone") && !phone.matches("\\d{10}")) {
                    isInvalid = true;
                }

                if (isInvalid) {
                    foundInvalid = true;
                    printStudent(rs);
                    System.out.println("   → Invalid " + fieldToCheck + ": " + (fieldToCheck.equals("Email") ? email : phone));
                    System.out.println();
                }
            }

            if (!foundInvalid) {
                System.out.println("All " + fieldToCheck + " entries are valid!");
            }

        } catch (Exception e) {
            System.out.println("Error checking invalid records: " + e.getMessage());
        }
    }


    public static void showStats(Connection conn) {
        try {
            String query = "SELECT Department, COUNT(*) as count FROM StudentsRecords GROUP BY Department";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("\n--- Department Stats ---");
            while (rs.next()) {
                System.out.println(rs.getString("Department") + ": " + rs.getInt("count") + " students");
            }

        } catch (Exception e) {
            System.out.println("Stats error: " + e.getMessage());
        }
    }

    public static void printStudent(ResultSet rs) throws SQLException {
        System.out.println(
                rs.getString("Enroll_id") + " | " +
                        rs.getString("Name") + " | " +
                        rs.getString("Department") + " | " +
                        rs.getString("Email") + " | " +
                        rs.getString("Phone") + " | " +
                        rs.getString("Gender") + " | " +
                        rs.getDate("Dob") + " | " +
                        rs.getString("blood_group") + " | " +
                        rs.getString("City") + " | " +
                        rs.getString("State")
        );
    }
}


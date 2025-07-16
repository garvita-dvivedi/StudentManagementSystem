import java.sql.*;
import java.util.*;

public class LoginManager2 {

    static final String url = "jdbc:mysql://localhost:3306/student_db";
    static final String user = "root";
    static final String password = "g12345";
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            System.out.println("===== Welcome to Poornima Student Management System =====");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.print("Enter choice: ");
            int option = Integer.parseInt(sc.nextLine());

            if (option == 1) {
                handleLogin(conn);
            } else if (option == 2) {
                SignUpHandler.handleSignup(conn);
            } else {
                System.out.println("Invalid choice.");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void handleLogin(Connection conn) throws Exception {
        System.out.println("Login with User ID & Password:");
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();

        System.out.print("Enter Password: ");
        String pass = sc.nextLine();

        String authQuery = "SELECT * FROM Users WHERE user_id = ? AND password = ?";
        PreparedStatement authStmt = conn.prepareStatement(authQuery);
        authStmt.setString(1, userId);
        authStmt.setString(2, pass);
        ResultSet authRs = authStmt.executeQuery();

        if (!authRs.next()) {
            System.out.println("Invalid credentials. Access denied.");
            return;
        }

        // Fetch role(s)
        String roleQuery = "SELECT role FROM Access WHERE user_id = ?";
        PreparedStatement roleStmt = conn.prepareStatement(roleQuery);
        roleStmt.setString(1, userId);
        ResultSet roleRs = roleStmt.executeQuery();

        List<String> roles = new ArrayList<>();
        while (roleRs.next()) {
            roles.add(roleRs.getString("role"));
        }

        if (roles.isEmpty()) {
            System.out.println("No roles assigned to this user.");
            return;
        }

        String selectedRole;
        if (roles.size() == 1) {
            selectedRole = roles.get(0);
            System.out.println("Logged in as: " + selectedRole);
        } else {
            System.out.println("Multiple roles found. Select one:");
            for (int i = 0; i < roles.size(); i++) {
                System.out.println((i + 1) + ". " + roles.get(i));
            }
            int choice = Integer.parseInt(sc.nextLine());
            selectedRole = roles.get(choice - 1);
            System.out.println("Logged in as: " + selectedRole);
        }

        // Launch studentSystem
        studentSystem.main(new String[]{userId, selectedRole});
    }
}

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class SignUpHandler {

    public static void handleSignup(Connection conn) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Enter your Email: ");
            String email = sc.nextLine().trim();

            // Step 1: Check if user already exists
            String checkQuery = "SELECT * FROM Users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet checkRs = checkStmt.executeQuery();
            if (checkRs.next()) {
                System.out.println("User already exists with this email. Please login instead.");
                return;
            }

            // Step 2: Send and verify OTP
            boolean verified = OTPVerifier.sendAndVerifyOTP(conn, email);
            if (!verified) {
                logSignup(conn, email, "FAILED", "INVALID");
                return;
            }

            // Step 3: Collect user details
            System.out.print("Enter your full name: ");
            String name = sc.nextLine().trim();

            System.out.print("Choose a User ID: ");
            String userId = sc.nextLine().trim();

            System.out.print("Set a password: ");
            String password = sc.nextLine().trim();

            System.out.print("Assign a role (student/teacher/hod/director): ");
            String role = sc.nextLine().trim().toLowerCase();

            // Step 4: Insert into Users
            String insertUser = "INSERT INTO Users (user_id, name, password, email) VALUES (?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(insertUser);
            userStmt.setString(1, userId);
            userStmt.setString(2, name);
            userStmt.setString(3, password);
            userStmt.setString(4, email);
            userStmt.executeUpdate();

            // Step 5: Insert into Access
            String insertRole = "INSERT INTO Access (user_id, role) VALUES (?, ?)";
            PreparedStatement roleStmt = conn.prepareStatement(insertRole);
            roleStmt.setString(1, userId);
            roleStmt.setString(2, role);
            roleStmt.executeUpdate();

            // Step 6: Log the signup
            logSignup(conn, email, "SUCCESS", "VERIFIED");

            System.out.println("Signup completed. You can now login with your credentials.");

        } catch (Exception e) {
            System.out.println("Error during signup: " + e.getMessage());
        }
    }

    private static void logSignup(Connection conn, String email, String status, String otpEntered) {
        try {
            String logQuery = "INSERT INTO SignupLogs (email, otp_sent_time, otp_entered, status) VALUES (?, ?, ?, ?)";
            PreparedStatement logStmt = conn.prepareStatement(logQuery);
            logStmt.setString(1, email);
            logStmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            logStmt.setString(3, otpEntered);
            logStmt.setString(4, status);
            logStmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("Could not log signup attempt: " + e.getMessage());
        }
    }
}

import java.sql.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class OTPVerifier {

    private static final Map<String, OTPRecord> otpStore = new HashMap<>();

    // Updated method signature to accept email only
    public static boolean sendAndVerifyOTP(Connection conn, String email) {
        try {
            // Generate OTP
            String otp = String.valueOf(100000 + new Random().nextInt(900000));
            LocalDateTime otpSentTime = LocalDateTime.now();

            // Save to in-memory store
            otpStore.put(email, new OTPRecord(otp, otpSentTime));

            // Send OTP
            if (!sendEmail(email, otp)) {
                System.out.println("Failed to send OTP.");
                return false;
            }

            System.out.println("OTP sent to: " + email);
            System.out.print("Enter OTP: ");
            Scanner sc = new Scanner(System.in);
            String enteredOtp = sc.nextLine().trim();

            // Check OTP validity
            OTPRecord record = otpStore.get(email);
            if (record == null) {
                System.out.println("OTP not found or expired.");
                return false;
            }

            // Check if expired (valid for 2 minutes)
            if (Duration.between(record.timestamp, LocalDateTime.now()).toMinutes() >= 2) {
                System.out.println("OTP expired.");
                return false;
            }

            if (record.otp.equals(enteredOtp)) {
                System.out.println("OTP Verified!");
                return true;
            } else {
                System.out.println("Incorrect OTP.");
                return false;
            }

        } catch (Exception e) {
            System.out.println("OTP verification failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean sendEmail(String to, String otp) {
        final String from = "dvivedigarvita@gmail.com";
        final String username = "dvivedigarvita@gmail.com";
        final String password = "16 digit code"; // App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Poornima Student Management System - OTP Verification");
            message.setText("Your OTP for signup is: " + otp + "\n(This OTP is valid for 2 minutes)");

            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            System.out.println("Email Error: " + e.getMessage());
            return false;
        }
    }

    // Helper class to store OTP and timestamp
    private static class OTPRecord {
        String otp;
        LocalDateTime timestamp;

        OTPRecord(String otp, LocalDateTime timestamp) {
            this.otp = otp;
            this.timestamp = timestamp;
        }
    }
}

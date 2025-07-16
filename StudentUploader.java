import java.sql.*;
import java.util.List;

public class StudentUploader extends Thread {
    private List<List<String>> batch;

    public StudentUploader(List<List<String>> batch) {
        this.batch = batch;
    }

    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "g12345";

        String query = "INSERT INTO StudentsRecords (Enroll_id, Name, Department, Email, Phone, Gender, Dob, blood_group, Address, City, State) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (List<String> row : batch) {
                for (int i = 0; i < 11; i++) {
                    pstmt.setString(i + 1, row.get(i));
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Student batch inserted by " + Thread.currentThread().getName());
            // studentSystem.main(new String[0]); to connect to menu dashboard

        } catch (SQLException e) {
            System.out.println("Error in StudentUploader: " + e.getMessage());
        }
    }
}

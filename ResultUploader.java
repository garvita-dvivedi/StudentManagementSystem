import java.sql.*;
import java.util.List;

public class ResultUploader extends Thread {
    private List<List<String>> batch;

    public ResultUploader(List<List<String>> batch) {
        this.batch = batch;
    }

    @Override
    public void run() {
        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "g12345";

        String query = "INSERT INTO StudentResults (id, name, dept, result) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            for (List<String> row : batch) {
                for (int i = 0; i < 4; i++) {
                    pstmt.setString(i + 1, row.get(i));
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("Result batch inserted by " + Thread.currentThread().getName());

        } catch (SQLException e) {
            System.out.println("Error in ResultUploader: " + e.getMessage());
        }
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;

public class jdbcConnect {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "g12345";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);

            String query = "SELECT * FROM StudentsRecords";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("----- Student Records -----");
            while (rs.next()) {
                String enrollId = rs.getString("Enroll_id");
                String name = rs.getString("Name");
                String dept = rs.getString("Department");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String gender = rs.getString("Gender");
                Date dob = rs.getDate("Dob");
                String bloodGroup = rs.getString("blood_group");
                String address = rs.getString("Address");
                String city = rs.getString("City");
                String state = rs.getString("State");

                System.out.println(
                        enrollId + " | " + name + " | " + dept + " | " + email + " | " + phone +
                                " | " + gender + " | " + dob + " | " + bloodGroup + " | " +
                                city + " | " + state
                );
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

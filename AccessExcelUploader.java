import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class AccessExcelUploader {

    public static void main(String[] args) {
        String filePath = "Access.xlsx";

        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "g12345";

        // Define staff-only roles
        Set<String> staffOnlyRoles = Set.of("teacher", "proctor", "hod", "principal", "director", "vice_principal");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String insertQuery = "INSERT INTO Access (user_id, role) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);

            int batchSize = 100;
            int count = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String userId = row.getCell(0).getStringCellValue();
                String role = row.getCell(1).getStringCellValue();

                // Rule: if user_id looks like student, reject staff roles
                if (userId.toLowerCase().startsWith("stu") && staffOnlyRoles.contains(role.toLowerCase())) {
                    System.out.println("Invalid role: Student '" + userId + "' cannot be assigned '" + role + "'");
                    continue;
                }

                pstmt.setString(1, userId);
                pstmt.setString(2, role);
                pstmt.addBatch();

                if (++count % batchSize == 0) {
                    pstmt.executeBatch();
                }
            }

            pstmt.executeBatch(); // Final batch
            System.out.println("Access roles uploaded successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

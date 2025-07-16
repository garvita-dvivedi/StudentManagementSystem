import java.io.FileInputStream;
import java.sql.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UserExcelUploader {

    public static void main(String[] args) {
        String filePath = "User.xlsx";

        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "g12345";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            String insertQuery = "INSERT INTO Users (user_id, name, password, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);

            int batchSize = 100;
            int count = 0;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header

                String userId = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(1));
                String pass = getCellValue(row.getCell(2));
                String email = getCellValue(row.getCell(3));

                pstmt.setString(1, userId);
                pstmt.setString(2, name);
                pstmt.setString(3, pass);
                pstmt.setString(4, email);
                pstmt.addBatch();

                if (++count % batchSize == 0) {
                    pstmt.executeBatch();
                }
            }

            pstmt.executeBatch(); // Final batch
            System.out.println("Users uploaded successfully !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }
}

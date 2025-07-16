import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.util.*;

public class ExcelUploaderStudents {

    public static void main(String[] args) throws Exception {
        List<List<String>> studentData = readExcel("StudentsRecords.xlsx");
        List<List<String>> resultData = readExcel("StudentResults.xlsx");

        int batchSize = 100;
        List<Thread> threads = new ArrayList<>();

        // Split student data into batches and start StudentUploader threads
        for (int i = 1; i < studentData.size(); i += batchSize) {
            List<List<String>> batch = studentData.subList(i, Math.min(i + batchSize, studentData.size()));
            Thread t = new StudentUploader(batch);
            t.start();
            threads.add(t);
        }

        // Split result data into batches and start ResultUploader threads
        for (int i = 1; i < resultData.size(); i += batchSize) {
            List<List<String>> batch = resultData.subList(i, Math.min(i + batchSize, resultData.size()));
            Thread t = new ResultUploader(batch);
            t.start();
            threads.add(t);
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("All Excel data uploaded using batching + threading!");
    }

    public static List<List<String>> readExcel(String filePath) throws Exception {
        List<List<String>> data = new ArrayList<>();
        FileInputStream fis = new FileInputStream(filePath);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                rowData.add(cell.toString().trim());
            }
            data.add(rowData);
        }

        wb.close();
        return data;
    }
}

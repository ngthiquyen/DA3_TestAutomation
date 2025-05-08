package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelLogger {
    private static final String LOG_FILE = "src/test/java/resources/LoginResult.xlsx";
    private static Workbook workbook;
    private static Sheet sheet;
    private static int currentRow = 1;

    static {
        try {
            File file = new File(LOG_FILE);
            if (file.exists()) {
                // Nếu file đã tồn tại → mở ra để tiếp tục ghi
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                sheet = workbook.getSheetAt(0);
                currentRow = sheet.getLastRowNum() + 1;
                fis.close();
            } else {
                // Nếu chưa có → tạo file mới và thêm header
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("Log");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Thời gian");
                header.createCell(1).setCellValue("Username");
                header.createCell(2).setCellValue("Password");
                header.createCell(3).setCellValue("Kết quả");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logResult(String username, String password, String result) {
        // Ghi một dòng kết quả test vào Excel
        Row row = sheet.createRow(currentRow++);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        row.createCell(0).setCellValue(LocalDateTime.now().format(formatter));
        row.createCell(1).setCellValue(username);
        row.createCell(2).setCellValue(password);
        row.createCell(3).setCellValue(result);

        try (FileOutputStream fos = new FileOutputStream(LOG_FILE)) {
            workbook.write(fos);
        } catch (IOException e) {
            System.out.println("Không thể ghi log vào Excel: " + e.getMessage());
        }
    }

    public static void openLogFile() {
        // Mở file Excel sau khi test xong (chỉ chạy được trên Windows)
        try {
            File file = new File(LOG_FILE);
            if (file.exists()) {
                Runtime.getRuntime().exec("cmd /c start excel \"" + file.getAbsolutePath() + "\"");
            }
        } catch (IOException e) {
            System.out.println("Không thể mở file Excel: " + e.getMessage());
        }
    }
}

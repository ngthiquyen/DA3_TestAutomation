package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelLogger {
    private static final String LOG_FILE = "src/test/java/resources/TestResults.xlsx";
    private static Workbook workbook;
    private static File logFile;

    static {
        try {
            logFile = new File(LOG_FILE);
            if (logFile.exists()) {
                FileInputStream fis = new FileInputStream(logFile);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể khởi tạo workbook cho ExcelLogger.");
        }
    }

    public static void logResult(String sheetName, String col1, String col2, String col3) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Thời gian");
                header.createCell(1).setCellValue("Thông tin 1");
                header.createCell(2).setCellValue("Thông tin 2");
                header.createCell(3).setCellValue("Kết quả");
            }

            int currentRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(currentRow);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            row.createCell(0).setCellValue(LocalDateTime.now().format(formatter));
            row.createCell(1).setCellValue(col1);
            row.createCell(2).setCellValue(col2);
            row.createCell(3).setCellValue(col3);

            FileOutputStream fos = new FileOutputStream(logFile);
            workbook.write(fos);
            fos.close();

        } catch (IOException e) {
            System.out.println("Không thể ghi log vào Excel: " + e.getMessage());
        }
    }

    public static void openLogFile() {
        try {
            if (logFile.exists()) {
                Runtime.getRuntime().exec("cmd /c start excel \"" + logFile.getAbsolutePath() + "\"");
            }
        } catch (IOException e) {
            System.out.println("Không thể mở file Excel: " + e.getMessage());
        }
    }
}

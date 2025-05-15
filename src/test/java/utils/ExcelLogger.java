package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExcelLogger {
    private static final String LOG_FILE = "src/test/java/resources/Test_Results.xlsx";
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

    /**
     * Hàm dùng cho LoginTest – ghi log gồm 6 cột cố định
     */
    public static void logResult(String sheetName, String col1, String col2,
                                 String actualResult, String expectedResult, String status) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Thời gian");
                header.createCell(1).setCellValue("Thông tin 1");
                header.createCell(2).setCellValue("Thông tin 2");
                header.createCell(3).setCellValue("Kết quả thực tế");
                header.createCell(4).setCellValue("Kết quả mong muốn");
                header.createCell(5).setCellValue("Trạng thái");
            }

            int currentRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(currentRow);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            row.createCell(0).setCellValue(LocalDateTime.now().format(formatter));
            row.createCell(1).setCellValue(col1);
            row.createCell(2).setCellValue(col2);
            row.createCell(3).setCellValue(actualResult);
            row.createCell(4).setCellValue(expectedResult);
            row.createCell(5).setCellValue(status);

            save();
        } catch (Exception e) {
            System.out.println("Không thể ghi log vào Excel: " + e.getMessage());
        }
    }

    /**
     * Hàm dùng cho các test như SearchTest – ghi log với tiêu đề và giá trị tùy chỉnh
     */
    public static void logCustomRow(String sheetName, String[] headers, String[] values) {
        try {
            Sheet sheet = workbook.getSheet(sheetName);
            boolean isNewSheet = false;

            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
                isNewSheet = true;
            }

            if (isNewSheet) {
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                }
            }

            int currentRow = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(currentRow);

            for (int i = 0; i < values.length; i++) {
                row.createCell(i).setCellValue(values[i]);
            }

            save();
        } catch (Exception e) {
            System.out.println("Không thể ghi log tuỳ chỉnh: " + e.getMessage());
        }
    }

    private static void save() throws IOException {
        try (FileOutputStream fos = new FileOutputStream(logFile)) {
            workbook.write(fos);
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

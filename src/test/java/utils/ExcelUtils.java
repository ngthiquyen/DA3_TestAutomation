package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static List<String[]> readExcelData(String filePath, String sheetName) {
        List<String[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Không tìm thấy sheet: " + sheetName);
            }

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề nếu có

                String[] rowData = new String[row.getLastCellNum()];
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData[i] = cell.toString().trim();
                }
                data.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
}

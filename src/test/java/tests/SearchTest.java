package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import utils.ExcelUtils;
import utils.ExcelLogger;
import utils.ExtendReportsManager;

import java.util.List;


 //Kiểm thử chức năng tìm kiếm sản phẩm từ file Excel Data.xlsx, sheet 2

public class SearchTest extends BaseTest {
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testSearchWithExcel() {
        List<String[]> keywords = ExcelUtils.readExcelData(
                "src/test/java/resources/Data.xlsx", "Sheet2");

        for (String[] data : keywords) {
            String keyword = data.length > 0 ? data[0] : "";
            ExtentTest test = extent.createTest("Tìm kiếm: " + keyword);

            try {
                // Truy cập trang tìm kiếm
                driver.get("https://dipsoul.vn/search");

                // Tìm ô nhập và nhập từ khóa
                WebElement searchInput = driver.findElement(By.name("query"));
                searchInput.clear();
                searchInput.sendKeys(keyword);

                // Gửi biểu mẫu
                searchInput.submit();

                Thread.sleep(3000); // Chờ kết quả tải về

                List<WebElement> results = driver.findElements(By.cssSelector(".product-card"));

                if (results.size() > 0) {
                    test.pass("Tìm thấy " + results.size() + " sản phẩm với từ khóa: " + keyword);
             //       ExcelLogger.logResult("Sheet2", keyword, "", "Tìm thấy sản phẩm");
                } else {
                    test.fail("Không tìm thấy sản phẩm với từ khóa: " + keyword);
               //     ExcelLogger.logResult("Sheet2", keyword, "", "Không tìm thấy sản phẩm");
                }

            } catch (Exception e) {
                test.fail("Lỗi khi tìm kiếm: " + e.getMessage());
             //   ExcelLogger.logResult("Sheet2", keyword, "", "Lỗi - \" + e.getMessage()");

            }
        }

        extent.flush();
        System.out.println("Đã hoàn thành kiểm thử tìm kiếm. Xem file Excel để kiểm tra log.");
        ExcelLogger.openLogFile();
    }
}

package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.SearchPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;

import java.time.Duration;
import java.util.List;

public class SearchTest extends BaseTest {
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testSearchWithExcel() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/java/resources/Data_Test.xlsx", "Search");

        for (String[] data : testData) {
            String testCaseID = data.length > 0 ? data[0].trim() : "";
            String keyword = data.length > 1 ? data[1].trim() : "";
            String expectedResult = data.length > 2 ? data[2].trim() : "";

            ExtentTest test = extent.createTest("Tìm kiếm sản phẩm: " + keyword);
            System.out.println("Đang kiểm tra tìm kiếm với từ khóa: " + keyword);

            driver.get("https://dipsoul.vn"); // hoặc trang cụ thể có tìm kiếm
            SearchPage searchPage = new SearchPage(driver);

            String actualResult = "";

            try {
                // Trường hợp người dùng không nhập gì mà bấm tìm
                if (keyword.isEmpty()) {
                    searchPage.searchProduct("");  // Gửi input rỗng
                    actualResult = "Vui lòng điền vào trường này.";
                    test.fail("Không nhập từ khóa tìm kiếm");
                } else {
                    searchPage.searchProduct(keyword);

                    // Chờ phần tử kết quả xuất hiện (tùy chỉnh selector nếu cần)
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    WebElement resultArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchResult")));

                    actualResult = resultArea.getText().trim();

                    if (actualResult.contains("Không tìm thấy bất kỳ kết quả nào với từ khóa trên.") || actualResult.isEmpty()) {
                        actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
                        test.fail("Không tìm thấy kết quả với từ khóa: " + keyword);
                    } else {
                        test.pass("Tìm thấy kết quả: " + actualResult);
                    }
                }
            } catch (TimeoutException te) {
                actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
                test.fail("Timeout: Không tìm thấy sản phẩm.");
            } catch (Exception ex) {
                actualResult = "Lỗi không xác định: " + ex.getMessage();
                test.fail("Lỗi không xác định xảy ra: " + ex.getMessage());
            }

            // So sánh kết quả
            String status = actualResult.equalsIgnoreCase(expectedResult) ? "Pass" : "Fail";

            // Ghi log Excel
            String[] headers = {"TestCaseID", "Keyword", "Expected", "Actual", "Status"};
            String[] values = {testCaseID, keyword, expectedResult, actualResult, status};
            ExcelLogger.logCustomRow("Search", headers, values);
        }

        extent.flush();
        System.out.println("Đã hoàn tất kiểm thử tìm kiếm.");
        ExcelLogger.openLogFile();
    }
}

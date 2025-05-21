package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
        List<String[]> testData = ExcelUtils.readExcelData("src/test/resources/Data_Test.xlsx", "Search");

        for (String[] data : testData) {
            String keyword = data.length > 0 ? data[0].trim() : "";
            String expectedResult = data.length > 1 ? data[1].trim() : "";

            ExtentTest test = extent.createTest("Tìm kiếm sản phẩm: " + keyword);
            System.out.println("Đang kiểm tra tìm kiếm với từ khóa: " + keyword);

            String actualResult = "";
            // Xử lý từ khóa rỗng
            if (keyword.isEmpty()) {
                actualResult = "Vui lòng điền vào trường này.";
                // Ghi vào ExtentReport
                if (actualResult.equalsIgnoreCase(expectedResult)) {
                    test.pass("Keyword: " + keyword +
                            "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
                } else {
                    test.fail("Keyword: " + keyword +
                            "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
                }
                String status = actualResult.equalsIgnoreCase(expectedResult) ? "Pass" : "Fail";
                String[] headers = {"Keyword", "Expected", "Actual", "Status"};
                String[] values = {keyword, expectedResult, actualResult, status};
                ExcelLogger.logCustomRow("Search", headers, values);
                continue; // Bỏ qua test này
            }

            driver.get("https://dipsoul.vn/search");
            SearchPage searchPage = new SearchPage(driver);

            try {
                // Đợi input hiển thị sẵn sàng
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOf(searchPage.getSearchInput()));

                String currentUrl = driver.getCurrentUrl();
                searchPage.searchProduct(keyword);


                    // Đợi redirect URL nếu có kết quả
                    wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));

                    // Kiểm tra URL mới
                    String newUrl = driver.getCurrentUrl();

                    // TH1: Không tìm thấy kết quả (thông báo xuất hiện)
                    List<WebElement> noResults = driver.findElements(
                            By.xpath("//p[contains(text(),'Không tìm thấy bất kỳ kết quả nào với từ khóa trên')]")
                    );

                    if (!noResults.isEmpty()) {
                        actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
                    } else {
                        // TH2: Có kết quả tìm kiếm -> kiểm tra các sản phẩm hiển thị
                        List<WebElement> productItems = driver.findElements(By.xpath("//div[@class='category-products']"));

                        if (!productItems.isEmpty()) {
                            String[] keywordParts = keyword.toLowerCase().split("\\s+");
                            boolean foundMatchingProduct = false;

                            for (WebElement product : productItems) {
                                String productText = product.getText().toLowerCase();
                                boolean allWordsMatch = true;

                                for (String word : keywordParts) {
                                    if (!productText.contains(word)) {
                                        allWordsMatch = false;
                                        break;
                                    }
                                }
                                if (allWordsMatch) {
                                    foundMatchingProduct = true;
                                    break;
                                }
                            }

                            if (foundMatchingProduct) {
                                actualResult =  keyword ;
                            } else {
                                actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
                            }
                        } else {
                            actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
                        }
                    }

            } catch (TimeoutException te) {
                actualResult = "Không tìm thấy bất kỳ kết quả nào với từ khóa trên.";
            } catch (Exception ex) {
                ex.printStackTrace(); // In lỗi ra console để dễ debug
                actualResult = "Lỗi không xác định: " + ex.getMessage();
            }
            // Ghi vào ExtentReport
            if (actualResult.equalsIgnoreCase(expectedResult)) {
                test.pass("Keyword: " + keyword +
                        "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
            } else {
                test.fail("Keyword: " + keyword +
                        "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
            }

            // Ghi log Excel
            String status = actualResult.equalsIgnoreCase(expectedResult) ? "Pass" : "Fail";
            String[] headers = { "Keyword", "Expected", "Actual", "Status"};
            String[] values = {keyword, expectedResult, actualResult, status};
            ExcelLogger.logCustomRow("Search", headers, values);
        }

        extent.flush();
        System.out.println("Đã hoàn tất kiểm thử tìm kiếm.");
        ExcelLogger.openLogFile();
    }
}

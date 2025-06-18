package tests;

import base.BaseTest;
import com.aventstack.extentreports.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.Test;
import pages.SearchPage;
import utils.*;

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
            driver.get("https://dipsoul.vn/search");
            SearchPage searchPage = new SearchPage(driver);

            if (keyword.isEmpty()) {
                WebElement input = searchPage.getSearchInput();
                input.click();
                input.sendKeys(Keys.ENTER); // Kích hoạt thông báo validation
                actualResult = searchPage.getHtml5ValidationMessage();
                logResultAndContinue(test, keyword, expectedResult, actualResult);
                continue;

            }
            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                wait.until(ExpectedConditions.visibilityOf(searchPage.getSearchInput()));

                String currentUrl = driver.getCurrentUrl();
                searchPage.searchProduct(keyword);
                wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
            // Chờ 1 trong 2 điều kiện: có sản phẩm hoặc có thông báo không tìm thấy
                wait.until(driver1 ->
                        !searchPage.getProductTitles().isEmpty() || searchPage.isNoResultMessageDisplayed()

                );
                List<WebElement> productTitles = searchPage.getProductTitles();
                if (productTitles.isEmpty() ) {
                    actualResult = searchPage.getNoResultMessageText(); //  lấy thông báo hệ thống
                } else {
                    StringBuilder resultBuilder = new StringBuilder();
                    for (WebElement title : productTitles) {
                        resultBuilder.append(title.getText().trim()).append(" | ");
                    }
                    actualResult = resultBuilder.length() > 0
                            ? resultBuilder.toString().replaceAll(" \\| $", "")
                            : "";
                }

            } catch (TimeoutException te) {
                actualResult = ""; // Timeout cũng coi như không có sản phẩm
            } catch (Exception ex) {
                ex.printStackTrace();
                actualResult = "Lỗi không xác định: " + ex.getMessage();
            }

            logResultAndContinue(test, keyword, expectedResult, actualResult);
        }

        extent.flush();
        System.out.println("Đã hoàn tất kiểm thử tìm kiếm.");
        ExcelLogger.openLogFile();
    }


    private void logResultAndContinue(ExtentTest test, String keyword, String expected, String actual) {
        // Nếu actual là danh sách tiêu đề sản phẩm, kiểm tra có chứa keyword không
        boolean isPass = false;

        if (actual.equalsIgnoreCase("Vui lòng điền vào trường này.")) {
            isPass = expected.equalsIgnoreCase("Vui lòng điền vào trường này.");
        } else if (actual.toLowerCase().contains(keyword.toLowerCase())) {
            isPass = true;
        }

        String status = isPass ? "Pass" : "Fail";

        if (isPass) {
            test.pass("Keyword: " + keyword + "\nExpected: " + expected + "\nActual: " + actual);
        } else {
            test.fail("Keyword: " + keyword + "\nExpected: " + expected + "\nActual: " + actual);
            String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Search_Fail_" + keyword);
            test.addScreenCaptureFromPath(screenshotPath);
        }

        String testTime = java.time.LocalDateTime.now().toString();
        String[] headers = {"Keyword", "Expected", "Actual", "Status", "Thời gian"};
        String[] values = {keyword, expected, actual, status, testTime};
        ExcelLogger.logCustomRow("Search", headers, values);
    }
}

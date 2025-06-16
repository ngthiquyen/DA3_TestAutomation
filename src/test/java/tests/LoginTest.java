package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;
import utils.ScreenshotUtils;

import java.time.Duration;
import java.util.List;

public class LoginTest extends BaseTest {
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testLoginWithExcel() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/resources/Data_Test.xlsx", "Login");

        for (String[] data : testData) {
            String username = data.length > 0 ? data[0].trim() : "";
            String password = data.length > 1 ? data[1].trim() : "";
            String expectedResult = data.length > 2 ? data[2].trim() : "";

            System.out.println("Đang kiểm tra: username = [" + username + "], password = [" + password + "]");

            ExtentTest test = extent.createTest("Kiểm tra đăng nhập: " + username);

            driver.get("https://dipsoul.vn/account/login");
            LoginPage loginPage = new LoginPage(driver);

            loginPage.getEmailInput().clear();
            loginPage.getPasswordInput().clear();

            if (!username.isEmpty()) loginPage.getEmailInput().sendKeys(username);
            if (!password.isEmpty()) loginPage.getPasswordInput().sendKeys(password);

            loginPage.getLoginButton().click();

            String actualResult = "";

            try {
                // Nếu thiếu username hoặc password, lấy thông báo HTML5
                if (username.isEmpty() || password.isEmpty()) {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    WebElement fieldToCheck = username.isEmpty() ? loginPage.getEmailInput() : loginPage.getPasswordInput();
                    actualResult = (String) js.executeScript("return arguments[0].validationMessage;", fieldToCheck);
                } else {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                    // Trường hợp đăng nhập thành công
                    WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(loginPage.getWelcomeTextLocator()));
                    actualResult = welcomeText.getText().trim();
                }
            } catch (TimeoutException e) {
                try {
                    // Trường hợp đăng nhập thất bại
                    WebDriverWait waitError = new WebDriverWait(driver, Duration.ofSeconds(3));
                    WebElement errorMsg = waitError.until(ExpectedConditions.visibilityOfElementLocated(loginPage.getLoginErrorMessageLocator()));
                    actualResult = errorMsg.getText().trim();
                } catch (Exception ex) {
                    actualResult = "Không xác định được lỗi từ hệ thống.";
                    test.fail(actualResult);
                }
            }


            // Ghi báo cáo Extent
            if (actualResult.equalsIgnoreCase(expectedResult)) {
                test.pass("\n Username: " + username +
                        "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
            } else {
                String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Login_Fail_" + username);
                test.fail(" \n Username: " + username +
                                "\n Expected: " + expectedResult + "\n Actual: " + actualResult)
                        .addScreenCaptureFromPath(screenshotPath);
            }

            // Ghi log Excel
            String status = actualResult.equalsIgnoreCase(expectedResult) ? "Pass" : "Fail";
            ExcelLogger.logResult("Login", username, password, actualResult, expectedResult, status);
        }

        extent.flush();
        System.out.println("Đã chạy xong tất cả trường hợp. Mở file Excel để xem log.");
        ExcelLogger.openLogFile();
    }
}

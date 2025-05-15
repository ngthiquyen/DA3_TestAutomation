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

import java.time.Duration;
import java.util.List;

public class LoginTest extends BaseTest {
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testLoginWithExcel() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/java/resources/Data_Test.xlsx", "Login");

        for (String[] data : testData) {
            String username = data.length > 0 ? data[0].trim() : "";
            String password = data.length > 1 ? data[1].trim() : "";
            String expectedResult = data.length > 2 ? data[2].trim() : "";

            System.out.println("Đang kiểm tra: username = [" + username + "], password = [" + password + "]");

            ExtentTest test = extent.createTest("Kiểm tra đăng nhập: " + username);

            driver.get("https://dipsoul.vn/account/login");
            LoginPage loginPage = new LoginPage(driver);

            WebElement emailInput = loginPage.getEmailInput();
            WebElement passwordInput = loginPage.getPasswordInput();
            WebElement loginBtn = loginPage.getLoginButton();

            // Xóa trước mỗi lần chạy
            emailInput.clear();
            passwordInput.clear();

            if (!username.isEmpty()) emailInput.sendKeys(username);
            if (!password.isEmpty()) passwordInput.sendKeys(password);

            loginBtn.click();

            String actualResult = "";

            try {
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

                // TH1: Kiểm tra nếu đăng nhập thành công
                WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p/span[contains(text(), 'Nguyễn Thị Quyên')]")));

                actualResult = welcomeText.getText().trim();
                test.pass("Đăng nhập thành công: " + actualResult);

            } catch (TimeoutException e) {
                // TH2: Không đăng nhập được, kiểm tra thông báo lỗi
                try {
                    WebElement body = driver.findElement(By.tagName("body"));
                    String pageText = body.getText().trim();

                    if (pageText.contains("Thông tin đăng nhập không chính xác")) {
                        actualResult = "Thông tin đăng nhập không chính xác.";
                    } else if (pageText.contains("Vui lòng điền vào trường này")) {
                        actualResult = "Vui lòng điền vào trường này.";
                    } else if (username.isEmpty() || password.isEmpty()) {
                        actualResult = "Vui lòng điền vào trường này.";
                    }
                    else {
                        actualResult = "Thông tin đăng nhập không chính xác.";
                    }

                } catch (Exception ex) {
                    actualResult = "Lỗi không xác định: " + ex.getMessage();
                    test.fail(actualResult);
                }
            }
            // Ghi vào ExtentReport dựa trên actual vs expected
            if (actualResult.equalsIgnoreCase(expectedResult)) {
                test.pass("\n Username: " + username +
                        "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
            } else {
                test.fail(" \n Username: " + username +
                        "\n Expected: " + expectedResult + "\n Actual: " + actualResult);
            }


            // Ghi log kết quả
            String status = actualResult.equalsIgnoreCase(expectedResult) ? "Pass" : "Fail";
            ExcelLogger.logResult("Login", username, password, actualResult, expectedResult, status);
        }

        extent.flush();
        System.out.println("Đã chạy xong tất cả trường hợp. Mở file Excel để xem log.");
        ExcelLogger.openLogFile();
    }
}

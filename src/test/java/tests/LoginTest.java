package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
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
        List<String[]> testData = ExcelUtils.readExcelData(
                "src/test/java/resources/Data.xlsx", "Sheet1");

        for (String[] data : testData) {
            String username = data.length > 0 ? data[0] : "";
            String password = data.length > 1 ? data[1] : "";

            System.out.println("Testing: username = [" + username + "], password = [" + password + "]");

            ExtentTest test = extent.createTest("Test login: " + username);

            driver.get("https://dipsoul.vn/account/login");
            LoginPage loginPage = new LoginPage(driver);

            WebElement emailInput = loginPage.getEmailInput();
            WebElement passwordInput = loginPage.getPasswordInput();
            WebElement loginBtn = loginPage.getLoginButton();

            // Trường hợp để trống username hoặc password
            if (username.isEmpty() || password.isEmpty()) {
                if (!username.isEmpty()) {
                    emailInput.sendKeys(username);
                }
                if (!password.isEmpty()) {
                    passwordInput.sendKeys(password);
                }

                loginBtn.click();

                JavascriptExecutor js = (JavascriptExecutor) driver;
                boolean isEmailValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", emailInput);
                boolean isPasswordValid = (Boolean) js.executeScript("return arguments[0].checkValidity();", passwordInput);

                if (!isEmailValid || !isPasswordValid) {
                    String errorMessage = "";
                    if (!isEmailValid) errorMessage += "Vui lòng điền vào trường này. ";
                    if (!isPasswordValid) errorMessage += "Vui lòng điền vào trường này.";

                    System.out.println("Thông báo lỗi : " + errorMessage);
                    ExcelLogger.logResult("Login", username, password, "Lỗi - " + errorMessage);
                    test.fail("Lỗi - " + errorMessage);
                } else {
                    System.out.println("Không hiển thị lỗi khi để trống.");
                    ExcelLogger.logResult("Login", username, password, "Không có cảnh báo khi để trống");
                    test.warning("Không có cảnh báo khi để trống");
                }
                continue;
            }

            // Trường hợp có đủ dữ liệu
            try {
                loginPage.login(username, password);
                test.info("Đã nhập username và password");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p/span[contains(text(), 'Nguyễn Thị Quyên')]")));

                String actualText = welcomeText.getText().trim();
                Assert.assertEquals(actualText, "Nguyễn Thị Quyên", "Tên hiển thị không đúng sau khi đăng nhập");

                System.out.println("Đăng nhập thành công với tài khoản: " + username);
                ExcelLogger.logResult("Login", username, password, "Đăng nhập thành công");
                test.pass("Đăng nhập thành công");

            } catch (TimeoutException e) {
                System.out.println("Sai thông tin đăng nhập: " + username);
                ExcelLogger.logResult("Login", username, password, "Sai thông tin đăng nhập");
                test.fail("Sai thông tin đăng nhập");

            } catch (Exception e) {
                System.out.println("Lỗi hệ thống với tài khoản: " + username);
                ExcelLogger.logResult("Login", username, password, "Lỗi: " + e.getMessage());
                test.fail("Lỗi bất ngờ: " + e.getMessage());
            }
        }

        extent.flush();
        System.out.println("Đã chạy xong tất cả trường hợp. Mở file Excel để xem log.");
        ExcelLogger.openLogFile();
    }
}

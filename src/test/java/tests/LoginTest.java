package tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import java.time.Duration;
import java.util.List;
import org.testng.Assert;
import utils.ExtendReportsManager;

public class LoginTest {
    private ExtendReportsManager ExtentReportManager;
    ExtentReports extent = ExtentReportManager.getReportInstance();
    @Test
    public void testLoginWithExcel() {
        List<String[]> testData = ExcelUtils.readExcelData(
                "D:\\DA3_TestAutomation\\src\\test\\java\\resources\\DataLogin.xlsx",
                "Sheet1");

        for (String[] data : testData) {
            String username = data.length > 0 ? data[0] : "";
            String password = data.length > 1 ? data[1] : "";

            System.out.println("Testing: username = [" + username + "], password = [" + password + "]");

            WebDriver driver = null;
            ExtentTest test = extent.createTest("Test login: " + username);

            try {
                System.setProperty("webdriver.chrome.driver",
                        "C:\\Users\\Dell\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
                driver = new ChromeDriver();
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get("https://dipsoul.vn/account/login");

                LoginPage loginPage = new LoginPage(driver);
                loginPage.login(username, password);
                test.info("Đã nhập username và password");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//p/span[contains(text(), 'Nguyễn Thị Quyên')]")));
                String actualText = welcomeText.getText().trim();

                Assert.assertEquals(actualText, "Nguyễn Thị Quyên", "Tên hiển thị không đúng sau khi đăng nhập");

                System.out.println(" Đăng nhập thành công với tài khoản: " + username);
                ExcelLogger.logResult(username, password, "Đăng nhập thành công");
            } catch (TimeoutException e) {
                System.out.println(" Timeout: Không tìm thấy phần tử tên người dùng với tài khoản: " + username);
                ExcelLogger.logResult(username, password, "Timeout - Thông tin đăng nhập không chính xác");
            } catch (Exception e) {
                System.out.println("Lỗi khác với tài khoản: " + username);
                e.printStackTrace();
                ExcelLogger.logResult(username, password, "Lỗi - " + e.getMessage());
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        }

        extent.flush(); // Ghi báo cáo sau khi xong toàn bộ test
        // Sau khi chạy xong tất cả test
        System.out.println("Tất cả trường hợp đã được kiểm thử. Mở file Excel để xem log.");
        ExcelLogger.openLogFile();
    }
}

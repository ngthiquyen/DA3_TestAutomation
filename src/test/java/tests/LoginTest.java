package tests;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;
import utils.ExcelUtils;
import java.time.Duration;
import java.util.List;
import org.testng.Assert;

public class LoginTest {
    @Test
    public void testLoginWithExcel() {
            List<String[]> testData = ExcelUtils.readExcelData(
                    "D:\\DoAn3_NguyenThiQuyen_10122312\\Project3\\DoAn3\\src\\test\\java\\resources\\DataLogin.xlsx",
                    "Sheet1");

            for (String[] data : testData) {
                String username = data[0];
                String password = data[1];

                WebDriver driver = null;

                try {
                    System.setProperty("webdriver.chrome.driver",
                            "C:\\Users\\Dell\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
                    driver = new ChromeDriver();
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                    driver.get("https://dipsoul.vn/account/login");

                    LoginPage loginPage = new LoginPage(driver);
                    loginPage.login(username, password);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement welcomeText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//p/span[contains(text(), 'Nguyễn Thị Quyên')]")));
                    String actualText = welcomeText.getText().trim();

                    Assert.assertEquals(actualText, "Nguyễn Thị Quyên", "Tên hiển thị không đúng sau khi đăng nhập");

                    System.out.println("Đăng nhập thành công với tài khoản: " + username);
                } catch (TimeoutException e) {
                    System.out.println("Timeout: Không tìm thấy phần tử tên người dùng với tài khoản: " + username);
                } catch (Exception e) {
                    System.out.println("Lỗi khác với tài khoản: " + username);
                    e.printStackTrace();
                } finally {
                    if (driver != null) {
                        driver.quit(); // QUAN TRỌNG: đóng trình duyệt
                    }
                }
            }
        }

}

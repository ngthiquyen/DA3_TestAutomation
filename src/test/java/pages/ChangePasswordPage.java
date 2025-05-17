package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ChangePasswordPage {
    WebDriver driver;

    // Constructor
    public ChangePasswordPage(WebDriver driver) {
        this.driver = driver;
    }

    // Locator các thành phần
    private By currentPasswordInput = By.id("OldPass"); // Cập nhật nếu ID khác
    private By newPasswordInput = By.id("changePass");         // Cập nhật nếu ID khác
    private By confirmPasswordInput = By.id("confirmPass"); // Cập nhật nếu ID khác
    private By submitButton = By.xpath("//button[contains(text(),'Đặt lại mật khẩu')]");

    // Các thông báo lỗi
    private By errorMessages = By.xpath("//span[contains(text(),'Xác nhận mật khẩu không khớp')], //span[contains(text(),'Mật khẩu mới dài từ 6 đến 50 ký tự')] , //span[contains(text(),'Mật khẩu không đúng')]");
    private By successMessage = By.xpath("//span[contains(text(),'Đổi password thành công')]");

    // Các phương thức tương tác
    public void enterCurrentPassword(String currentPassword) {
        driver.findElement(currentPasswordInput).clear();
        driver.findElement(currentPasswordInput).sendKeys(currentPassword);
    }

    public void enterNewPassword(String newPassword) {
        driver.findElement(newPasswordInput).clear();
        driver.findElement(newPasswordInput).sendKeys(newPassword);
    }

    public void enterConfirmPassword(String confirmPassword) {
        driver.findElement(confirmPasswordInput).clear();
        driver.findElement(confirmPasswordInput).sendKeys(confirmPassword);
    }

    public void submitChange() {
        driver.findElement(submitButton).click();
    }


    public String getSuccessMessage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return success.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public String getAllErrorMessages() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            List<WebElement> errors = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(
                    "//span[contains(text(),'Xác nhận mật khẩu không khớp')]" +
                            " | //span[contains(text(),'Mật khẩu mới dài từ 6 đến 50 ký tự')]" +
                            " | //span[contains(text(),'Mật khẩu không đúng')]"
            )));

            StringBuilder errorText = new StringBuilder();
            for (WebElement error : errors) {
                String text = error.getText().trim();
                if (!text.isEmpty() && error.isDisplayed()) {
                    errorText.append(text).append(" | ");
                }
            }

            return errorText.toString().replaceAll("\\s*\\|\\s*$", ""); // loại bỏ dấu "|" cuối
        } catch (Exception e) {
            return "";
        }
        }
    }


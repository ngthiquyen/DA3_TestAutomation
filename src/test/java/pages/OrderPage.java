package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class OrderPage {

    private WebDriver driver;
    private WebDriverWait wait;

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void addProductToCart() throws InterruptedException {
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn.btn_base.normal_button.btn_add_cart.add_to_cart.btn-cart")));
        addToCartBtn.click();
        Thread.sleep(2000);
    }

    public void openCartAndCheckout() throws InterruptedException {
        driver.get("https://dipsoul.vn/cart");
        Thread.sleep(1000);
        WebElement checkoutBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//button[@id='btn-proceed-checkout'])[2]")));
        checkoutBtn.click();
        Thread.sleep(2000);
    }

    public void enterCustomerInfo(String email, String name, String phone) {
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("billingName")).sendKeys(name);
        driver.findElement(By.id("billingPhone")).sendKeys(phone);
    }

    public boolean selectProvince(String provinceName) {
        try {
            WebElement provinceDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//span[@id='select2-billingProvince-container'])")));
            provinceDropdown.click();
            WebElement provinceOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + provinceName + "')]")));
            provinceOption.click();
            return true;
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn tỉnh: " + e.getMessage());
            return false;
        }
    }

    public boolean selectDistrict(String districtName) {
        try {
            WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//span[@id='select2-billingDistrict-container'])")));
            districtDropdown.click();
            WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + districtName + "')]")));
            districtOption.click();
            return true;
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn quận/huyện: " + e.getMessage());
            return false;
        }
    }

    public boolean selectWard(String wardName) {
        try {
            WebElement wardDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("(//span[@id='select2-billingWard-container'])")));
            wardDropdown.click();
            WebElement wardOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + wardName + "')]")));
            wardOption.click();
            return true;
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn phường/xã: " + e.getMessage());
            return false;
        }
    }

    public void enterNote(String note) {
        driver.findElement(By.id("note")).sendKeys(note);
    }

    public boolean selectShippingMethod(String method) {
        try {
            WebElement shippingOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[contains(normalize-space(),'" + method + "')]")));
            shippingOption.click();
            return true;
        } catch (Exception e) {
            System.out.println("Không thể chọn phương thức vận chuyển: " + method + " - " + e.getMessage());
            return false;
        }
    }

    public boolean selectPaymentMethod(String method) {
        if (method == null || method.trim().isEmpty()) {
            System.out.println("Không có phương thức thanh toán được cung cấp.");
            return false;
        }
        try {
            WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//label[contains(.,'" + method + "')]")));
            WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[@for='paymentMethod-786631']")));
            if (!radio.isSelected()) {
                radio.click();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Không thể chọn phương thức thanh toán: " + method + " - " + e.getMessage());
            return false;
        }
    }

    public void enterDiscountCode(String code) {
        try {
            WebElement discountInput = driver.findElement(By.xpath("//input[@id='reductionCode']"));
            discountInput.clear();
            discountInput.sendKeys(code);
            WebElement applyButton = driver.findElement(By.xpath("//button[@type='button']"));
            applyButton.click();
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("Không thể áp dụng mã giảm giá: " + e.getMessage());
        }
    }

    public void clickCaptchaCheckbox() {
        try {
            WebElement captchaCheckbox = driver.findElement(By.xpath("//*[@id=\"recaptcha-anchor\"]/div[4]"));
            captchaCheckbox.click();
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Không thao tác được với captcha: " + e.getMessage());
        }
    }

    public void placeOrder() throws InterruptedException {
        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[@class='spinner-label'][contains(text(),'ĐẶT HÀNG')])[2]")));
        placeOrderBtn.click();
        Thread.sleep(3000);
    }

    public String getErrorMessage(String type) {
        try {
            By locator = switch (type.toLowerCase()) {
                case "email" -> By.xpath("//p[contains(text(),'Vui lòng nhập email') or contains(text(),'Email không hợp lệ')]");
                case "phone" -> By.xpath("//p[contains(text(),'Số điện thoại không hợp lệ')]");
                case "province" -> By.xpath("(//p[contains(text(),'Bạn chưa chọn tỉnh thành')])[1]");
                case "field" -> By.cssSelector("div[class='field field--error'] p[class='field__message field__message--error']");
                case "alert" -> By.cssSelector("div[class='alert alert--danger']");
                case "payment" -> By.xpath("//div[contains(text(), 'Bạn cần chọn phương thức thanh toán')]");
                default -> null;
            };
            if (locator != null) {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                WebElement errorElement = shortWait.until(ExpectedConditions.presenceOfElementLocated(locator));
                return errorElement.getText().trim();
            }
        } catch (Exception e) {
            System.out.println("Không tìm thấy lỗi loại: " + type + " - " + e.getMessage());
        }
        return "";
    }

    public String getSuccessMessage() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement successMsg = shortWait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h2[contains(text(),'Cảm ơn bạn đã đặt hàng')]")));
            return successMsg.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }
}

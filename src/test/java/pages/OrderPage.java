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

    public void selectProvince(String provinceName) {
        try {
            WebElement provinceDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@id='select2-billingProvince-container'])")));
            provinceDropdown.click();

            WebElement provinceOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + provinceName + "')]")
            ));
            provinceOption.click();
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn tỉnh: " + e.getMessage());
        }
    }

    public void selectDistrict(String districtName) {
        try {
            WebElement districtDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@id='select2-billingDistrict-container'])")));
            districtDropdown.click();

            WebElement districtOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + districtName + "')]")
            ));
            districtOption.click();
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn quận/huyện: " + e.getMessage());
        }
    }

    public void selectWard(String wardName) {
        try {
            WebElement wardDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@id='select2-billingWard-container'])")));
            wardDropdown.click();

            WebElement wardOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//li[contains(text(),'" + wardName + "')]")
            ));
            wardOption.click();
        } catch (TimeoutException e) {
            System.out.println("Không thể chọn phường/xã: " + e.getMessage());
        }
    }

    public void enterNote(String note) {
        driver.findElement(By.id("note")).sendKeys(note);
    }

    public void selectShippingMethod(String method) {
        try {
            // Tìm element radio theo text chứa tên phương thức vận chuyển
            WebElement shippingOption = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//label[contains(normalize-space(),'" + method + "')]")
            ));
            shippingOption.click();
        } catch (Exception e) {
            throw new RuntimeException("Không thể chọn phương thức vận chuyển: " + method, e);
        }
    }


    public void selectPaymentMethod(String method) {
        try {
            // Tìm label theo text
            WebElement label = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//label[contains(.,'" + method + "')]")));


            // Tìm input theo id và click
            WebElement radio = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("paymentMethod-786631")));

            if (!radio.isSelected()) {
                radio.click();
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể chọn phương thức thanh toán: " + method, e);
        }
    }

    public void placeOrder() throws InterruptedException {
        WebElement placeOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//span[@class='spinner-label'][contains(text(),'ĐẶT HÀNG')])[2]")));
        placeOrderBtn.click();
        Thread.sleep(3000);
    }

}

package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;

import java.time.Duration;
import java.util.List;

public class OrderTest extends BaseTest {

    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testPlaceOrder() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/resources/Data_Test.xlsx", "Order");

        for (String[] row : testData) {
            String email = row[0];
            String name = row[1];
            String phone = row[2];
            String province = row[3];
            String district = row[4];
            String ward = row[5];
            String shippingMethod = row[6];
            String paymethod=row[7];
            String note = row[8];
            String expectedMessage = row[9];

            ExtentTest test = extent.createTest("Đặt hàng với email: " + email);

            try {
                // 1. Truy cập sản phẩm và thêm vào giỏ
                driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");
                WebElement addToCartBtn = driver.findElement(By.cssSelector(".btn.btn_base.normal_button.btn_add_cart.add_to_cart.btn-cart"));
                addToCartBtn.click();
                Thread.sleep(2000);

                // 2. Mở giỏ hàng và chuyển đến trang thanh toán
                driver.get("https://dipsoul.vn/cart");
                Thread.sleep(1000);
                WebElement checkoutBtn = driver.findElement(By.xpath("(//button[@id='btn-proceed-checkout'])[2]"));
                checkoutBtn.click();
                Thread.sleep(2000);

                // 3. Nhập thông tin mua hàng
                driver.findElement(By.xpath("//input[@id='email']")).sendKeys(email);
                driver.findElement(By.xpath("//input[@id='billingName']")).sendKeys(name);
                driver.findElement(By.xpath("//input[@id='billingPhone']")).sendKeys(phone);

                // Dropdown: Tỉnh - Quận - Phường
                selectDropdownOption("Tỉnh thành (tùy chọn)", province);
                selectDropdownOption("Quận huyện (tùy chọn)", district);
                selectDropdownOption("Phường xã (tùy chọn)", ward);


                // Ghi chú
                driver.findElement(By.xpath("//textarea[@id='note']")).sendKeys(note);

                // 4. Chọn phương thức vận chuyển (radio button)
                WebElement shippingRadio = driver.findElement(By.xpath("//label[contains(.,'" + shippingMethod + "')]/preceding-sibling::input[@type='radio']"));
                if (!shippingRadio.isSelected()) {
                    shippingRadio.click();
                }

                // 5. Chọn phương thức thanh toán (radio button)
                WebElement paymentRadio = driver.findElement(By.xpath("//label[contains(.,'" + paymethod + "')]/preceding-sibling::input[@type='radio']"));
                if (!paymentRadio.isSelected()) {
                    paymentRadio.click();
                }

                Thread.sleep(1000);

                // 6. Đặt hàng
                WebElement placeOrderBtn = driver.findElement(By.xpath("(//span[@class='spinner-label'][contains(text(),'ĐẶT HÀNG')])[2]"));
                placeOrderBtn.click();
                Thread.sleep(3000);

                // 7. Kiểm tra kết quả
                String bodyText = driver.getPageSource();
                String status = bodyText.contains("Cảm ơn bạn đã đặt hàng") ? "Pass" : "Fail";

                if (status.equals("Pass")) {
                    test.pass("Đặt hàng thành công");
                } else {
                    test.fail("Không tìm thấy xác nhận đặt hàng");
                }

                // Ghi log Excel
                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán", "Ghi chú", "KQ mong muốn", "KQ thực tế", "Status"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod, paymethod, note, expectedMessage, bodyText, status};
                ExcelLogger.logCustomRow("OrderTest", headers, values);

            } catch (Exception e) {
                e.printStackTrace();
                test.fail("Exception: " + e.getMessage());

                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán","Ghi chú", "KQ mong muốn", "KQ thực tế", "Status"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod,paymethod, note, expectedMessage, "Lỗi Exception", "Fail"};
                ExcelLogger.logCustomRow("OrderTest", headers, values);
            }
        }

        extent.flush();
        ExcelLogger.openLogFile();
    }
    // ===== Hàm chọn dropdown theo placeholder
    private void selectDropdownOption(String placeholderText, String optionText) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Mở dropdown
        WebElement container = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//span[@class='select2-selection select2-selection--single' and ancestor::div[.//label[contains(text(), '" + placeholderText + "')]]")));
        container.click();

        // Nhập text tìm kiếm
        WebElement searchInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("input.select2-search__field")
        ));
        searchInput.sendKeys(optionText);

        Thread.sleep(800);
        WebElement result = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//li[contains(@class,'select2-results__option') and contains(text(),'" + optionText + "')]")
        ));
        result.click();

        Thread.sleep(500);
    }
}

package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import pages.OrderPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;
import utils.ScreenshotUtils;

import java.time.Duration;
import java.util.*;

// ... giữ nguyên import

public class OrderTest extends BaseTest {

    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testPlaceOrder() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/resources/Data_Test.xlsx", "Order");

        for (String[] data : testData) {
            String email = data.length > 0 ? data[0].trim() : "";
            String name = data.length > 1 ? data[1].trim() : "";
            String phone = data.length > 2 ? data[2].trim() : "";
            String province = data.length > 3 ? data[3].trim() : "";
            String district = data.length > 4 ? data[4].trim() : "";
            String ward = data.length > 5 ? data[5].trim() : "";
            String shippingMethod = data.length > 6 ? data[6].trim() : "";
            String paymentMethod = data.length > 7 ? data[7].trim() : "";
            String note = data.length > 8 ? data[8].trim() : "";
            String discountCode = data.length > 9 ? data[9].trim() : "";
            String expectedMessage = data.length > 10 ? data[10].trim() : "";

            ExtentTest test = extent.createTest("Đặt hàng với email: " + email);

            try {
                // Mở trang sản phẩm
                driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");
                OrderPage order = new OrderPage(driver);

                // Thêm vào giỏ hàng & tiến hành đặt hàng
                order.addProductToCart();
                order.openCartAndCheckout();

                // Nhập thông tin khách hàng
                order.enterCustomerInfo(email, name, phone);

                boolean selectedProvince = order.selectProvince(province);
                boolean selectedDistrict = selectedProvince && order.selectDistrict(district);
                boolean selectedWard = selectedDistrict && order.selectWard(ward);

                order.enterNote(note);
                boolean shippingSelected = selectedWard && order.selectShippingMethod(shippingMethod);
                boolean paymentSelected = !paymentMethod.isEmpty() && order.selectPaymentMethod(paymentMethod);

                if (!discountCode.isEmpty()) {
                    order.enterDiscountCode(discountCode);
                }

                order.clickCaptchaCheckbox();
                // Đặt hàng
                order.placeOrder();

                // ✅ Dùng Set để loại bỏ lỗi trùng lặp
                Set<String> actualErrors = new LinkedHashSet<>();

                if (!selectedProvince) {
                    actualErrors.add(getFieldError(By.xpath("(//p[contains(text(),'Bạn chưa chọn tỉnh thành')])[1]")));
                } else {
                    actualErrors.add(getFieldError(By.xpath("//p[contains(text(),'Vui lòng nhập email') or contains(text(),'Email không hợp lệ')]")));
                    actualErrors.add(getFieldError(By.xpath("//p[contains(text(),'Số điện thoại không hợp lệ')]")));
                    actualErrors.add(getFieldError(By.cssSelector("div[class='field field--error'] p[class='field__message field__message--error']")));
                    actualErrors.add(getFieldError(By.cssSelector("div[class='alert alert--danger']")));

                    if (!paymentSelected) {
                        actualErrors.add(getFieldError(By.xpath("//div[contains(text(), 'Bạn cần chọn phương thức thanh toán')]")));
                    }
                }

                actualErrors.removeIf(String::isEmpty);

                String resultText;
                if (actualErrors.isEmpty() && selectedProvince && selectedDistrict && selectedWard && shippingSelected && paymentSelected) {
                    try {
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                                By.xpath("//h2[contains(text(),'Cảm ơn bạn đã đặt hàng')]")));
                        resultText = successMsg.getText().trim();
                    } catch (Exception e) {
                        resultText = "Không đặt hàng thành công và không có thông báo lỗi";
                    }
                } else {
                    resultText = String.join("; ", actualErrors);
                }

                String status = resultText.contains(expectedMessage) ? "Pass" : "Fail";

                if (status.equals("Pass")) {
                    test.pass(resultText);
                } else {
                    test.fail("Thông báo không đúng. Mong đợi: " + expectedMessage + " - Thực tế: " + resultText);
                    String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Order_Fail_" + email);
                    test.addScreenCaptureFromPath(screenshotPath);
                }
                String testTime = java.time.LocalDateTime.now().toString();
                // Ghi log vào Excel
                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán", "Ghi chú", "Mã giảm giá", "KQ mong muốn", "KQ thực tế", "Status", "Thời gian"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod, paymentMethod, note, discountCode, expectedMessage, resultText, status, testTime};
                ExcelLogger.logCustomRow("OrderTest", headers, values);

                // Reset cho lần chạy tiếp theo
                driver.manage().deleteAllCookies();
                driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");
                Thread.sleep(2000);

            } catch (Exception e) {
                e.printStackTrace();
                test.fail("Exception xảy ra: " + e.getMessage());
                String testTime = java.time.LocalDateTime.now().toString();
                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán", "Ghi chú", "Mã giảm giá", "KQ mong muốn", "KQ thực tế", "Status","Thời gian"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod, paymentMethod, note, discountCode, expectedMessage, "Exception: " + e.getMessage(), "Fail",testTime};
                ExcelLogger.logCustomRow("OrderTest", headers, values);

                driver.manage().deleteAllCookies();
                driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
        extent.flush();
        ExcelLogger.openLogFile();
    }

    private String getFieldError(By locator) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement errorElement = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            return errorElement.getText().trim();
        } catch (Exception e) {
            System.out.println("Không tìm thấy lỗi với locator: " + locator);
            return "";
        }
    }
}

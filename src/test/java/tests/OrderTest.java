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
import java.util.List;

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
            String expectedMessage = data.length > 9 ? data[9].trim() : "";

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

                // Chọn địa chỉ (tỉnh, quận, phường)
                order.selectProvince(province);
                order.selectDistrict(district);
                order.selectWard(ward);

                // Ghi chú
                order.enterNote(note);

                // Chọn phương thức giao hàng & thanh toán
                order.selectShippingMethod(shippingMethod);
                order.selectPaymentMethod(paymentMethod);

                // Đặt hàng
                order.placeOrder();

                // Kiểm tra kết quả
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement getText = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//h2[contains(text(),'Cảm ơn bạn đã đặt hàng')]")));
                String resultText = getText.getText().trim();
                String status = resultText.contains("Cảm ơn bạn đã đặt hàng") ? "Pass" : "Fail";

                if (status.equals("Pass")) {
                    test.pass("Đặt hàng thành công");
                } else {
                    test.fail("Không tìm thấy xác nhận đặt hàng");
                    String screenshotPath = ScreenshotUtils.takeScreenshot(driver, "Order_Fail_" + email);
                    test.addScreenCaptureFromPath(screenshotPath);
                }

                // Ghi log vào Excel
                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán", "Ghi chú", "KQ mong muốn", "KQ thực tế", "Status"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod, paymentMethod, note, expectedMessage, resultText, status};
                ExcelLogger.logCustomRow("OrderTest", headers, values);

            } catch (Exception e) {
                e.printStackTrace();
                test.fail("Exception xảy ra: " + e.getMessage());

                // Ghi log lỗi vào Excel
                String[] headers = {"Email", "Họ tên", "SĐT", "Tỉnh", "Quận", "Phường", "Vận chuyển", "Thanh toán", "Ghi chú", "KQ mong muốn", "KQ thực tế", "Status"};
                String[] values = {email, name, phone, province, district, ward, shippingMethod, paymentMethod, note, expectedMessage, "Exception: " + e.getMessage(), "Fail"};
                ExcelLogger.logCustomRow("OrderTest", headers, values);
            }
        }
        extent.flush();
        ExcelLogger.openLogFile();
    }
}

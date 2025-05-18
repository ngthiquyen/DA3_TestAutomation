package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;

import java.util.List;

public class CartTest extends BaseTest {

    // Khởi tạo đối tượng ExtentReports để ghi báo cáo kiểm thử
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testCartFunctionality() {
        // Đọc dữ liệu từ file Excel: Sheet "CartTest"
        List<String[]> testData = ExcelUtils.readExcelData("src/test/java/resources/Data_Test.xlsx", "Cart");

        // Duyệt từng dòng dữ liệu để kiểm thử
        for (String[] row : testData) {
            // Gán các giá trị từ từng cột trong Excel
            String action = row[0].trim();                 // Hành động: Thêm, Xóa, Thay đổi số lượng...
            String productName = row[1].trim();            // Tên sản phẩm
            String quantityStr = row[2].trim();            // Số lượng (nếu có)
            String expectedMessage = row[3].trim();        // Kết quả mong muốn hiển thị
            String priceStr = row[4].trim();               // Giá sản phẩm
            String expectedTotalStr = row[5].trim();       // Tổng tiền mong muốn (nếu có)

            // Tạo 1 testcase trong báo cáo ExtentReports
            ExtentTest test = extent.createTest("Hành động: " + action + " | SP: " + productName);

            try {
                // Mở trang đăng nhập
                driver.get("https://dipsoul.vn/account/login");

                // Đăng nhập tài khoản
                LoginPage loginPage = new LoginPage(driver);
                loginPage.login("ngthiquyen102@mail.com", "ngthiquyen102");

                // Mở trang sản phẩm
                driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");

                // Khởi tạo trang giỏ hàng
                CartPage cart = new CartPage(driver);

                // Biến chứa kết quả thực tế và tổng tiền thực tế
                String actualMessage = "";
                String actualTotal = "";

                // Xử lý từng loại hành động dựa trên dữ liệu
                switch (action.toLowerCase()) {
                    case "thêm":
                        cart.addProductToCart(productName);
                        Thread.sleep(2000); // chờ hệ thống cập nhật
                        actualMessage = cart.getActionResultMessage();
                        break;

                    case "thay đổi số lượng":
                        cart.updateQuantity(productName, Integer.parseInt(quantityStr));
                        Thread.sleep(2000);
                        actualMessage = cart.getActionResultMessage();
                        actualTotal = cart.getTotalPriceText(); // lấy tổng tiền sau khi thay đổi
                        break;

                    case "xóa sản phẩm":
                        cart.removeProduct(productName);
                        Thread.sleep(2000);
                        actualMessage = cart.getActionResultMessage();
                        break;

                    case "kiểm tra giỏ hàng trống":
                        actualMessage = cart.getCartMessage();
                        break;
                }

                // Kiểm tra điều kiện pass/fail
                String status = "Fail";
                if (action.equalsIgnoreCase("thay đổi số lượng") && !expectedTotalStr.isEmpty()) {
                    // Kiểm tra tổng tiền có đúng không nếu có yêu cầu
                    status = actualTotal.equals(expectedTotalStr) ? "Pass" : "Fail";
                    test.info("Tổng tiền thực tế: " + actualTotal);
                } else {
                    // Kiểm tra thông báo có chứa kết quả mong muốn không
                    status = actualMessage.toLowerCase().contains(expectedMessage.toLowerCase()) ? "Pass" : "Fail";
                }

                // Ghi vào báo cáo kiểm thử (ExtentReports)
                if (status.equals("Pass")) {
                    test.pass("Thành công: " + expectedMessage);
                } else {
                    test.fail("Thất bại\nExpected: " + expectedMessage + "\nActual: " + actualMessage);
                }

                // Ghi kết quả vào file Excel log (TestResults)
                String[] headers = {"Hành động", "Tên sản phẩm", "Số lượng", "KQ mong muốn", "Giá", "Tổng tiền mong muốn", "KQ thực tế", "Tổng tiền thực tế", "Status"};
                String[] values = {action, productName, quantityStr, expectedMessage, priceStr, expectedTotalStr, actualMessage, actualTotal, status};
                ExcelLogger.logCustomRow("CartTest", headers, values);

                // Đăng xuất sau khi kiểm thử xong từng test case
                driver.get("https://dipsoul.vn/account/logout");

            } catch (Exception e) {
                e.printStackTrace();
                test.fail("Exception: " + e.getMessage());
            }
        }

        // Kết thúc ghi báo cáo kiểm thử
        extent.flush();

        // Mở file log Excel chứa kết quả nếu có
        ExcelLogger.openLogFile();
    }
}

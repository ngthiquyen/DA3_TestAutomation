package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.LoginPage;
import utils.ExtendReportsManager;

public class CartTest extends BaseTest {

    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testCartFunctionalityWithoutExcel() {
        try {
            // Mở trang đăng nhập
            driver.get("https://dipsoul.vn/account/login");

            // Đăng nhập
            LoginPage loginPage = new LoginPage(driver);
            loginPage.login("ngthiquyen102@mail.com", "ngthiquyen102");

            // Mở trang sản phẩm (điều hướng trực tiếp vào sản phẩm mẫu)
            driver.get("https://dipsoul.vn/set-qua-tang-nen-thom-diu-nong-20-10");

            // Khởi tạo trang giỏ hàng
            CartPage cart = new CartPage(driver);

            // ===== CASE 1: Thêm sản phẩm vào giỏ =====
            ExtentTest test1 = extent.createTest("Thêm sản phẩm vào giỏ");
            boolean added = cart.addProductToCart("Set quà tặng nến thơm dịu nồng 20/10");

            if (added) {
                test1.pass("Thêm sản phẩm thành công.");
            } else {
                test1.fail("Không thấy thông báo 'Thêm vào giỏ hàng thành công'.");
            }

            // ===== CASE 2: Thay đổi số lượng =====
            ExtentTest test2 = extent.createTest("Thay đổi số lượng sản phẩm");
            cart.updateQuantity("Set quà tặng nến thơm dịu nóng 20/10", 4);
            Thread.sleep(2000);
            String message2 = cart.getActionResultMessage();
            String total2 = cart.getTotalPriceText();
            if (!total2.isEmpty()) {
                test2.pass("Cập nhật số lượng thành công. Tổng tiền: " + total2);
            } else {
                test2.fail("Cập nhật số lượng thất bại - Msg: " + message2);
            }

            // ===== CASE 3: Xóa sản phẩm =====
            ExtentTest test3 = extent.createTest("Xóa sản phẩm khỏi giỏ");
            String productToRemove = "Set quà tặng nến thơm dịu nóng 20/10";

            cart.removeProduct(productToRemove);
            Thread.sleep(4000);

            // Kiểm tra lại danh sách sản phẩm trong giỏ hàng
            boolean isStillInCart = cart.isProductInCart(productToRemove);

            if (!isStillInCart) {
                test3.pass("Xóa sản phẩm thành công. Sản phẩm không còn trong giỏ hàng.");
            } else {
                test3.fail("Xóa sản phẩm thất bại. Sản phẩm vẫn còn trong giỏ hàng.");
            }


            // ===== CASE 4: Kiểm tra giỏ hàng trống =====
            cart.clearCart();
            ExtentTest test4 = extent.createTest("Kiểm tra giỏ hàng trống");
            String message4 = cart.getCartMessage();
            if (message4.toLowerCase().contains("Không có sản phẩm nào trong giỏ hàng của bạn")) {
                test4.pass("Giỏ hàng đang trống như mong đợi");
            } else {
                test4.fail("Giỏ hàng không trống - Msg: " + message4);
            }

            // Đăng xuất sau khi kiểm thử
            driver.get("https://dipsoul.vn/account/logout");

        } catch (Exception e) {
            e.printStackTrace();
            ExtentTest test = extent.createTest("Exception xảy ra");
            test.fail("Lỗi xảy ra: " + e.getMessage());
        }

        // Kết thúc ghi báo cáo
        extent.flush();
    }
}

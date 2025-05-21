package tests;

import base.BaseTest;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;
import pages.ChangePasswordPage;
import pages.LoginPage;
import utils.ExcelLogger;
import utils.ExcelUtils;
import utils.ExtendReportsManager;

import java.util.List;

public class ChangePasswordTest extends BaseTest {
    ExtentReports extent = ExtendReportsManager.getReportInstance();

    @Test
    public void testChangePasswordWithExcel() {
        List<String[]> testData = ExcelUtils.readExcelData("src/test/resources/Data_Test.xlsx", "ChangePassword");

        for (String[] data : testData) {
            String oldPassword = data.length > 0 ? data[0].trim() : "";
            String newPassword = data.length > 1 ? data[1].trim() : "";
            String confirmPassword = data.length > 2 ? data[2].trim() : "";
            String expectedMessage = data.length > 3 ? data[3].trim() : "";

            ExtentTest test = extent.createTest("Đổi mật khẩu: " + oldPassword + " ➝ " + newPassword);

            try {
                // Truy cập trang đăng nhập và thực hiện login
                driver.get("https://dipsoul.vn/account/login");
                LoginPage loginPage = new LoginPage(driver);
                loginPage.login("ngthiquyen102@mail.com", "ngthiquyen102");

                // Truy cập trang đổi mật khẩu
                driver.get("https://dipsoul.vn/account/changepassword");
                Thread.sleep(6000);
                ChangePasswordPage changePage = new ChangePasswordPage(driver);


                changePage.enterCurrentPassword(oldPassword);
                changePage.enterNewPassword(newPassword);
                changePage.enterConfirmPassword(confirmPassword);
                changePage.submitChange();

                Thread.sleep(3000); // đợi phản hồi
                String actualMessage = "";
                // Kiểm tra thủ công các trường bị bỏ trống
                StringBuilder emptyFieldsMsg = new StringBuilder();
                if (oldPassword.isEmpty()) emptyFieldsMsg.append("Vui lòng điền vào trường này. ");
                if (newPassword.isEmpty()) emptyFieldsMsg.append("Vui lòng điền vào trường này. ");
                if (confirmPassword.isEmpty()) emptyFieldsMsg.append("Vui lòng điền vào trường này.");

                if (emptyFieldsMsg.length() > 0) {
                    actualMessage = emptyFieldsMsg.toString().replaceAll("\\s*\\|\\s*$", "");
                } else {
                    // Nếu không bị bỏ trống, lấy thông báo từ hệ thống
                    actualMessage = changePage.getAllErrorMessages();
                   if (actualMessage.isEmpty()) {
                       actualMessage = changePage.getSuccessMessage();
                   }
                    //}

                    // Nếu vẫn không có gì trả về
                   //if (actualMessage.isEmpty()) {
                   //    actualMessage = "Không có thông báo trả về từ hệ thống.";
                  // }
                }

                // So sánh kết quả
                String status = actualMessage.toLowerCase().contains(expectedMessage.toLowerCase()) ? "Pass" : "Fail";

                if (status.equals("Pass")) {
                    test.pass("Thành công\nExpected: " + expectedMessage + "\nActual: " + actualMessage);
                } else {
                    test.fail("Thất bại\nExpected: " + expectedMessage + "\nActual: " + actualMessage);
                }

                String[] headers = {"OldPassword", "NewPassword", "ConfirmPassword", "Expected", "Actual", "Status"};
                String[] values = {oldPassword, newPassword, confirmPassword, expectedMessage, actualMessage, status};
                ExcelLogger.logCustomRow("ChangePassword", headers, values);

                // Đăng xuất sau mỗi lượt test
                driver.get("https://dipsoul.vn/account/logout");

            } catch (Exception e) {
                e.printStackTrace();
                test.fail("Exception: " + e.getMessage());

                String[] headers = {"OldPassword", "NewPassword", "ConfirmPassword", "Expected", "Actual", "Status"};
                String[] values = {oldPassword, newPassword, confirmPassword, expectedMessage, "Exception", "Fail"};
                ExcelLogger.logCustomRow("ChangePassword", headers, values);

                // Đảm bảo luôn logout nếu có lỗi để chuyển tiếp test khác
                try {
                    driver.get("https://dipsoul.vn/account/logout");
                } catch (Exception ignore) {}
            }
        }

        extent.flush();
        System.out.println("Đã hoàn tất kiểm thử đổi mật khẩu.");
        ExcelLogger.openLogFile();
    }
}

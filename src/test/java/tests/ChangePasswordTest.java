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
                Thread.sleep(6000);
                driver.get("https://dipsoul.vn/account/changepassword");
                Thread.sleep(3000);

                ChangePasswordPage changePage = new ChangePasswordPage(driver);

                // Nhập dữ liệu
                changePage.enterCurrentPassword(oldPassword);
                changePage.enterNewPassword(newPassword);
                changePage.enterConfirmPassword(confirmPassword);

                changePage.submitChange();
                Thread.sleep(5000);

                // Kiểm tra actualMessage
                String actualMessage = "";

                if (oldPassword.isEmpty()) {
                    actualMessage = changePage.getHtml5ValidationMessage(changePage.getOldPasswordLocator());
                } else if (newPassword.isEmpty()) {
                    actualMessage = changePage.getHtml5ValidationMessage(changePage.getNewPasswordLocator());
                } else if (confirmPassword.isEmpty()) {
                    actualMessage = changePage.getHtml5ValidationMessage(changePage.getConfirmPasswordLocator());
                } else {
                    actualMessage = changePage.getAllErrorMessages();
                    if (actualMessage.isEmpty()) {
                        actualMessage = changePage.getSuccessMessage();
                    }
                }

                // So sánh kết quả
                String status = actualMessage.toLowerCase().contains(expectedMessage.toLowerCase()) ? "Pass" : "Fail";

                if (status.equals("Pass")) {
                    test.pass("Thành công\nExpected: " + expectedMessage + "\nActual: " + actualMessage);
                } else {
                    test.fail("Thất bại\nExpected: " + expectedMessage + "\nActual: " + actualMessage);
                }
                String testTime = java.time.LocalDateTime.now().toString();
                String[] headers = {"OldPassword", "NewPassword", "ConfirmPassword", "Expected", "Actual", "Status","Time"};
                String[] values = {oldPassword, newPassword, confirmPassword, expectedMessage, actualMessage, status, testTime};
                ExcelLogger.logCustomRow("ChangePassword", headers, values);

                // Logout sau mỗi test
                driver.get("https://dipsoul.vn/account/logout");

            } catch (Exception e) {
                e.printStackTrace();
                test.fail(" Exception: " + e.getMessage());
                String testTime = java.time.LocalDateTime.now().toString();
                String[] headers = {"OldPassword", "NewPassword", "ConfirmPassword", "Expected", "Actual", "Status","Time"};
                String[] values = {oldPassword, newPassword, confirmPassword, expectedMessage, "Exception", "Fail",testTime};
                ExcelLogger.logCustomRow("ChangePassword", headers, values);

                try {
                    driver.get("https://dipsoul.vn/account/logout");
                } catch (Exception ignore) {
                }
            }
        }

        extent.flush();
        System.out.println("Đã hoàn tất kiểm thử đổi mật khẩu.");
        ExcelLogger.openLogFile();
    }
}

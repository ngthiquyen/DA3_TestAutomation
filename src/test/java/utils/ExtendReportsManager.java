package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;


 //Lớp tiện ích dùng để quản lý instance của ExtentReports (Singleton Pattern)

public class ExtendReportsManager {
    private static ExtentReports extent;

     //Trả về instance duy nhất của ExtentReports

    public static ExtentReports getReportInstance() {
        if (extent == null) {
            createInstance(); // Khởi tạo nếu chưa có
        }
        return extent;
    }


     //Khởi tạo ExtentReports với SparkReporter và thêm thông tin cấu hình

    private static void createInstance() {
        // Tạo tên file báo cáo có timestamp để không bị ghi đè
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportPath = System.getProperty("user.dir") + "/test-output/ExtentReport_" + timestamp + ".html";

        // Cấu hình Spark Reporter
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Báo cáo kiểm thử tự động");
        spark.config().setReportName("Kết quả kiểm thử ");
        spark.config().setTheme(Theme.STANDARD); // Có thể chọn DARK nếu muốn

        // Tạo instance chính
        extent = new ExtentReports();
        extent.attachReporter(spark);

        // Gắn thêm thông tin hệ thống cho báo cáo
        extent.setSystemInfo("Người kiểm thử", "Nguyễn Thị Quyên");
        extent.setSystemInfo("Framework", "Selenium Java - TestNG");
        extent.setSystemInfo("Thời gian", timestamp);
    }
}

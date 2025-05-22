package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    public static String takeScreenshot(WebDriver driver, String screenshotName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = screenshotName + "_" + timestamp + ".png";
        String relativePath = "test-output/ExtentReports/screenshots/" + fileName;
        String absolutePath = System.getProperty("user.dir") + "/" + relativePath;

        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            File destDir = new File(System.getProperty("user.dir") + "/test-output/ExtentReports/screenshots/");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            Files.copy(src.toPath(), new File(absolutePath).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return relativePath.replace("\\", "/"); // Đường dẫn TƯƠNG ĐỐI để đính kèm vào HTML
    }
}

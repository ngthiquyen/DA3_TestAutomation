package base;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.ConfigReader;

import java.time.Duration;

public class BaseTest {
    //Lớp cơ sở cho các test case, dùng để quản lý vòng đời của WebDriver
    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Lấy trình duyệt từ file cấu hình
        String browser = ConfigReader.get("browser");

        // Khởi tạo WebDriver tương ứng
        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            driver = new FirefoxDriver();
        }

        // Cấu hình cửa sổ và thời gian chờ
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getInt("timeout")));

        // Mở trang web gốc từ config
        driver.get(ConfigReader.get("baseUrl"));
    }

    @AfterMethod
    public void tearDown() {
        // Đóng trình duyệt sau mỗi test
        if (driver != null) {
            driver.quit();
        }
    }
}

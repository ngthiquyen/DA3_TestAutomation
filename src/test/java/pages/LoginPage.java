package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private WebDriver driver;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        driver.findElement(By.id("customer_email")).sendKeys(username);
        driver.findElement(By.id("customer_password")).sendKeys(password);
        driver.findElement(By.cssSelector(".btn.btn-style.btn_50")).click();
    }
}

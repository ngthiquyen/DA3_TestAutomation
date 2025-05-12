package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private WebDriver driver;

    private By emailField = By.id("customer_email");
    private By passwordField = By.id("customer_password");
    private By loginButton = By.cssSelector(".btn.btn-style.btn_50");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public void login(String username, String password) {
        getEmailInput().clear();
        getPasswordInput().clear();
        getEmailInput().sendKeys(username);
        getPasswordInput().sendKeys(password);
        getLoginButton().click();
    }

    public WebElement getEmailInput() {
        return driver.findElement(emailField);
    }

    public WebElement getPasswordInput() {
        return driver.findElement(passwordField);
    }

    public WebElement getLoginButton() {
        return driver.findElement(loginButton);
    }
}

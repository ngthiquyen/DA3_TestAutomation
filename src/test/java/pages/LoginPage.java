package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private WebDriver driver;

    // Locators (XPath/ID/CSS tách riêng)
    private By emailField = By.id("customer_email");
    private By passwordField = By.id("customer_password");
    private By loginButton = By.cssSelector(".btn.btn-style.btn_50");
    private By welcomeText = By.xpath("//p/span[contains(text(), 'Nguyễn Thị Quyên')]");
    private By loginErrorMessage=By.xpath("//span[@class='form-signup']");
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Elements
    public WebElement getEmailInput() {
        return driver.findElement(emailField);
    }

    public WebElement getPasswordInput() {
        return driver.findElement(passwordField);
    }

    public WebElement getLoginButton() {
        return driver.findElement(loginButton);
    }

    public WebElement getWelcomeTextElement() {
        return driver.findElement(welcomeText);
    }

    public By getWelcomeTextLocator() {
        return welcomeText;
    }

    public By getLoginErrorMessageLocator() {
        return loginErrorMessage;
    }

    public WebElement getLoginErrorMessageElement() {
        return driver.findElement(loginErrorMessage);
    }


    // Actions
    public void login(String username, String password) {
        getEmailInput().clear();
        getPasswordInput().clear();
        getEmailInput().sendKeys(username);
        getPasswordInput().sendKeys(password);
        getLoginButton().click();
    }
}

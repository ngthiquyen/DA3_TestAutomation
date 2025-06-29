package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SearchPage {
    private WebDriver driver;

    private By searchInput = By.xpath("//form[contains(@class, 'blog-search-form')]//input[@type='text']");
    private By searchButton = By.xpath("//form[@class='blog-search-form input-group search-bar']//button[@title='Tìm kiếm']");
    private By noResultMessage = By.xpath("//p[contains(text(),'Không tìm thấy bất kỳ kết quả nào với từ khóa trên')]");
    private By productTitles = By.cssSelector(".product-name"); // Tiêu đề sản phẩm

    public SearchPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement getSearchInput() {
        return driver.findElement(searchInput);
    }

    public void searchProduct(String keyword) {
        WebElement input = driver.findElement(searchInput);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].focus();", input);
        js.executeScript("arguments[0].value = '';", input);
        js.executeScript("arguments[0].value = arguments[1];", input, keyword);
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.findElement(searchButton).click();
    }
    public String getHtml5ValidationMessage() {
        WebElement input = driver.findElement(searchInput);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (String) js.executeScript("return arguments[0].validationMessage;", input);
    }
    public boolean isNoResultMessageDisplayed() {
        List<WebElement> messages = driver.findElements(noResultMessage);
        return !messages.isEmpty();
    }
    public String getNoResultMessageText() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement msgElement = wait.until(ExpectedConditions.visibilityOfElementLocated(noResultMessage));
            return msgElement.getText().trim();
        } catch (TimeoutException e) {
            return "Không tìm thấy sản phẩm nhưng cũng không có thông báo từ hệ thống.";
        }
    }
    public List<WebElement> getProductTitles() {
        return driver.findElements(productTitles);
    }
}

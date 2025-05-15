package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchPage {
    private WebDriver driver;

    private By searchInput = By.cssSelector("//form[@class='blog-search-form input-group search-bar'])[1]"); // ID hoặc selector của ô input tìm kiếm
    private By searchButton = By.cssSelector("form[class='blog-search-form input-group search-bar'] button[title='Tìm kiếm']"); // Nút tìm kiếm
    private By resultContainer = By.cssSelector(".row.row-fix"); // Vùng hiển thị kết quả

    public SearchPage(WebDriver driver) {
        this.driver = driver;
    }

    public void searchProduct(String keyword) {
        driver.findElement(searchInput).clear();
        driver.findElement(searchInput).sendKeys(keyword);
        driver.findElement(searchButton).click();
    }

    public String getSearchResult() {
        WebElement result = driver.findElement(resultContainer);
        return result.getText().trim();
    }
}
//https://dipsoul.vn/search
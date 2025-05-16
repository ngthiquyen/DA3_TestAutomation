package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SearchPage {
    private WebDriver driver;

    private By searchInput = By.xpath("//form[contains(@class, 'blog-search-form')]//input[@type='text']"); // ID hoặc selector của ô input tìm kiếm
    private By searchButton = By.xpath("//form[@class='blog-search-form input-group search-bar']//button[@title='Tìm kiếm']"); // Nút tìm kiếm
   // private By resultContainer = By.cssSelector(".row.row-fix"); // Vùng hiển thị kết quả

    public SearchPage(WebDriver driver) {
        this.driver = driver;
    }

    public void searchProduct(String keyword) {
        WebElement input = driver.findElement(searchInput);
        // Dùng JS để focus, xóa và nhập liệu
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].focus();", input);
        js.executeScript("arguments[0].value = '';", input);
        js.executeScript("arguments[0].value = arguments[1];", input, keyword);

        // Trigger sự kiện input + change nếu có binding JS
        js.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", input);
        js.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", input);

        // Click nút tìm kiếm sau một khoảng delay nhẹ
        try {
            Thread.sleep(500); // Cho JS của trang xử lý input trước
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement btn = driver.findElement(searchButton);
        btn.click();
    }

    //public String getSearchResult() {
    //    WebElement result = driver.findElement(resultContainer);
    //    return result.getText().trim();
   // }
    public WebElement getSearchInput(){
        return driver.findElement(searchInput);
    }
}
//https://dipsoul.vn/search
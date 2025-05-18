package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage {
    WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // Hàm thêm sản phẩm vào giỏ hàng
    public void addProductToCart(String productName) {
        // Tìm sản phẩm theo tên
        WebElement product = driver.findElement(By.xpath("//h1[contains(text(),'" + productName + "')]"));
        product.click();

        // Click nút Thêm vào giỏ hàng
        WebElement addToCartBtn = driver.findElement(By.xpath("//button[@type='submit'][normalize-space()='']"));
        addToCartBtn.click();
    }

    // Hàm thay đổi số lượng sản phẩm trong giỏ hàng
    public void updateQuantity(String productName, int newQuantity) {
        driver.get("https://dipsoul.vn/cart");

        // Tìm ô nhập số lượng tương ứng với sản phẩm
        WebElement quantityInput = driver.findElement(By.xpath("(//input[@name='updates[]'])[2]"));
        quantityInput.clear();
        quantityInput.sendKeys(String.valueOf(newQuantity));

        // Submit form hoặc click ra ngoài để cập nhật (nếu cần)
        //WebElement updateBtn = driver.findElement(By.name("update"));
        //updateBtn.click();
    }

    // Hàm xóa sản phẩm khỏi giỏ hàng
    public void removeProduct(String productName) {
        driver.get("https://dipsoul.vn/cart");

        // Tìm nút xóa sản phẩm theo tên
        WebElement removeBtn = driver.findElement(By.cssSelector("body > div:nth-child(4) > section:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > form:nth-child(1) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > div:nth-child(1) > a:nth-child(3)"));
        removeBtn.click();
    }

    // Lấy thông báo hiển thị sau khi thực hiện hành động (thêm, xóa, cập nhật...)
    public String getActionResultMessage() {
        try {
            // Lấy thông báo từ toast hoặc element xác nhận
            WebElement message = driver.findElement(By.xpath("//span[normalize-space()='Mua hàng thành công'], "));
            return message.getText();
        } catch (Exception e) {
            return "Không tìm thấy thông báo";
        }
    }

    // Kiểm tra nếu giỏ hàng rỗng
    public String getCartMessage() {
        driver.get("https://dipsoul.vn/cart");

        try {
            WebElement msg = driver.findElement(By.xpath("//div[@class='CartPageContainer']//p[contains(text(),'Không có sản phẩm nào trong giỏ hàng của bạn')]"));
            return msg.getText();
        } catch (Exception e) {
            return "Không tìm thấy thông báo";
        }
    }

    // Lấy tổng tiền hiện tại trong giỏ hàng
    public String getTotalPriceText() {
        driver.get("https://dipsoul.vn/cart");

        try {
            WebElement total = driver.findElement(By.xpath("(//div[@class='text-right cart__totle'])[2]"));
            return total.getText().replace(".", "").replace("₫", "").trim(); // Loại bỏ ký tự tiền tệ để so sánh
        } catch (Exception e) {
            return "0";
        }
    }
}

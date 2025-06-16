package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    WebDriver driver;

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    // Hàm thêm sản phẩm vào giỏ hàng
    public boolean addProductToCart(String productName) {
        try {
            // Nếu chưa ở trang chi tiết sản phẩm thì click vào tiêu đề sản phẩm
            WebElement product = driver.findElement(By.xpath("//h1[contains(text(),'" + productName + "')]"));
            product.click();
            Thread.sleep(1000); // Nên thay bằng WebDriverWait nếu có thể
        } catch (Exception e) {
            // Nếu đã ở trang sản phẩm, không cần click
        }

        try {
            // Click nút Thêm vào giỏ hàng
            WebElement addToCartBtn = driver.findElement(By.cssSelector("button.btn_add_cart"));
            addToCartBtn.click();

            // Đợi thông báo "Mua hàng thành công" xuất hiện
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement successPopup = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//a[contains(text(),'n có')]")
            ));

            // Nếu popup hiển thị đúng, trả về true
            return successPopup.isDisplayed();

        } catch (Exception ex) {
            System.out.println("Không thể xác nhận thêm sản phẩm: " + ex.getMessage());
            return false;
        }
    }


    // Hàm thay đổi số lượng sản phẩm trong giỏ hàng
    public void updateQuantity(String productName, int newQuantity) {
        driver.get("https://dipsoul.vn/cart");

        try {
            WebElement quantityInput = driver.findElement(By.xpath("(//input[@name='updates[]'])[2]"));
            quantityInput.clear();
            quantityInput.sendKeys(String.valueOf(newQuantity));
        } catch (Exception e) {
            System.out.println("Không tìm thấy ô nhập số lượng: " + e.getMessage());
        }
    }
    /// //
    public void updateQuantity1(String productName, int newQuantity) {
        driver.get("https://dipsoul.vn/cart");

        try {
            List<WebElement> productRows = driver.findElements(By.cssSelector(".ajaxcart__product.cart_product"));

            for (WebElement row : productRows) {
                String name = row.findElement(By.cssSelector(".ajaxcart__product-name.h4")).getText();
                if (name.toLowerCase().contains(productName.toLowerCase())) {
                    WebElement qtyInput = row.findElement(By.xpath("input[name='updates[]']"));
                    qtyInput.clear();
                    qtyInput.sendKeys(String.valueOf(newQuantity));
                    qtyInput.sendKeys(Keys.ENTER);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Không cập nhật được số lượng: " + e.getMessage());
        }
    }
    // hàm chuyển đổi giá tiền
    public double parsePrice(String priceText) {
        try {
            // Xóa dấu chấm phân cách ngàn và chữ "đ", sau đó chuyển sang double
            String cleaned = priceText.replace(".", "").replace("₫", "").replace("đ", "").trim();
            return Double.parseDouble(cleaned);
        } catch (Exception e) {
            return 0.0;
        }
    }


    public boolean isProductInCart(String productName) {
        driver.get("https://dipsoul.vn/cart");

        try {
            List<WebElement> productNames = driver.findElements(By.cssSelector("a.ajaxcart__product-name"));
            for (WebElement nameElement : productNames) {
                if (nameElement.getText().trim().equalsIgnoreCase(productName.trim())) {
                    return true; // Sản phẩm vẫn còn trong giỏ hàng
                }
            }
        } catch (Exception e) {
            // Có thể không tìm thấy phần tử vì giỏ rỗng, cũng là OK
        }
        return false; // Không còn thấy sản phẩm
    }


    // Hàm xóa sản phẩm khỏi giỏ hàng
    public void removeProduct(String productName) {
        driver.get("https://dipsoul.vn/cart");

        try {
            // Lấy tất cả block sản phẩm trong giỏ hàng
            List<WebElement> productBlocks = driver.findElements(By.cssSelector("div.ajaxcart__product.cart_product"));

            for (WebElement block : productBlocks) {
                // Tìm tên sản phẩm trong block
                WebElement nameElement = block.findElement(By.cssSelector("a.ajaxcart__product-name"));
                String name = nameElement.getText().trim();

                if (name.equalsIgnoreCase(productName.trim())) {
                    // Nếu đúng tên sản phẩm thì tìm nút xóa
                    WebElement removeBtn = block.findElement(By.cssSelector("a.cart__btn-remove"));
                    removeBtn.click();
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.stalenessOf(block)); // Đợi block sản phẩm biến mất khỏi DOM
                    System.out.println("Đã xóa sản phẩm: " + productName);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Không thể xóa sản phẩm: " + e.getMessage());
        }
    }
    public void clearCart() {
        driver.get("https://dipsoul.vn/cart");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Lặp đến khi không còn nút Xóa nào
            while (true) {
                List<WebElement> removeButtons = driver.findElements(By.cssSelector("a.cart__btn-remove.ajaxifyCart--remove"));

                if (removeButtons.isEmpty()) {
                    break; // Không còn nút Xóa -> giỏ hàng trống
                }

                WebElement removeBtn = removeButtons.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", removeBtn);
                wait.until(ExpectedConditions.elementToBeClickable(removeBtn));
                removeBtn.click();

                // Đợi giỏ hàng cập nhật sau khi xóa
                Thread.sleep(1500); // hoặc chờ thay đổi DOM nếu có thể bắt được
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi xóa sản phẩm khỏi giỏ hàng: " + e.getMessage());
        }
    }


    // Lấy thông báo hiển thị sau khi thực hiện hành động (thêm, xóa, cập nhật...)
    public String getActionResultMessage() {
        try {
            WebElement message = driver.findElement(By.xpath("//span[contains(text(),'thành công') or contains(text(),'đã được thêm vào giỏ')], //div[@class='CartPageContainer']//p[contains(text(),'Không có sản phẩm nào trong giỏ hàng của bạn')]"));
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
            WebElement total = driver.findElement(By.xpath("(//div[contains(@class,'cart__totle')])[last()]"));
            return total.getText().replace(".", "").replace("₫", "").trim(); // Loại bỏ ký tự tiền tệ
        } catch (Exception e) {
            return "0";
        }
    }

    // Trả về WebElement chứa dòng sản phẩm theo tên
    public WebElement getProductRow(String productName) {
        List<WebElement> productRows = driver.findElements(By.cssSelector(".ajaxcart__product.cart_product"));
        for (WebElement row : productRows) {
            String name = row.findElement(By.cssSelector(".ajaxcart__product-name.h4")).getText();
            if (name.toLowerCase().contains(productName.toLowerCase())) {
                return row;
            }
        }
        return null;
    }

    // Lấy đơn giá từ dòng sản phẩm
    public String getUnitPriceFromRow(WebElement row) {
        try {
            return row.findElement(By.cssSelector(".cart-price")).getText();
        } catch (Exception e) {
            return "0";
        }
    }

    // Lấy tổng giá từ dòng sản phẩm
    public String getTotalPriceFromRow(WebElement row) {
        try {
            return row.findElement(By.cssSelector(".CartItemLinePrice")).getText();
        } catch (Exception e) {
            return "0";
        }
    }

    // Hàm xác minh tổng tiền hiển thị có đúng không
    public boolean verifyTotalPrice(String productName, int quantity, double threshold) {
        try {
            WebElement row = getProductRow(productName);
            if (row == null) return false;

            double unitPrice = parsePrice(getUnitPriceFromRow(row));
            double displayedTotal = parsePrice(getTotalPriceFromRow(row));
            double expectedTotal = unitPrice * quantity;

            return Math.abs(displayedTotal - expectedTotal) <= threshold;
        } catch (Exception e) {
            return false;
        }
    }

}

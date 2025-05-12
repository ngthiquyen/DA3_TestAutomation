package utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    //Lớp tiện ích giúp đọc dữ liệu từ file config.properties
    private static Properties properties;

    // Khối static này chỉ chạy 1 lần khi lớp được gọi lần đầu
    static {
        try {
            // Đọc file cấu hình từ đường dẫn tương đối
            FileInputStream file = new FileInputStream("src/test/java/resources/config.properties");
            properties = new Properties();
            properties.load(file); // Nạp file vào đối tượng Properties
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể load file config.properties.");
        }
    }

    // Lấy giá trị kiểu chuỗi (String) theo key
    public static String get(String key) {
        return properties.getProperty(key);
    }

    // Lấy giá trị kiểu số nguyên (int) theo key (ví dụ timeout)
    public static int getInt(String key) {
        return Integer.parseInt(properties.getProperty(key));
    }
}

package utilities;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class CookieManager {

    WebDriver driver;

    public CookieManager(WebDriver driver) {
        this.driver = driver;
    }

    private void setCookie(String domain, String name, String value) {
        Cookie cookie = new Cookie.Builder(name, value)
                .domain(domain)
                .build();
        driver.manage().addCookie(cookie);
    }

}

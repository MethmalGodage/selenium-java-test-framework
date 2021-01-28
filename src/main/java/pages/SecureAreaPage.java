package pages;

import core.PageActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SecureAreaPage extends PageActions {

    private final By statusAlert = By.id("flash");

    public SecureAreaPage(WebDriver driver) {
        super(driver);
    }

    public String getAlertText() {
        return driver.findElement(statusAlert).getText();
    }

}

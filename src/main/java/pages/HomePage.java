package pages;

import core.PageActions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends PageActions {

    private final By formAuthenticationLink = By.linkText("Form Authentication");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    @Step("Click on the 'Form Authentication' link")
    public LoginPage clickFormAuthentication() {
        driver.findElement(formAuthenticationLink).click();
        return new LoginPage(driver);
    }

}

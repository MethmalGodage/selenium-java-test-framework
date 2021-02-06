package pages;

import core.PageActions;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utilities.StringHandler;

public class LoginPage extends PageActions {

    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton = By.cssSelector("#login button");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Enter the username '{0}'")
    public void setUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
        log.debug("Entered the username: '" + username + "'.");
    }

    @Step("Enter the password '--CONTENT HIDDEN--")
    public void setPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
        log.debug("Entered the password: '" + maskPasswordText(password) + "'.");
    }

    @Step("Click on the 'Login' button")
    public SecureAreaPage clickLoginButton() {
        driver.findElement(loginButton).click();
        log.debug("Clicked on the 'Login' button.");
        log.info("Navigating to the SecureAreaPage after successful login attempt.");
        return new SecureAreaPage(driver);
    }

    public String maskPasswordText(String password) {
        StringHandler stringHandler = new StringHandler();
        return stringHandler.maskSensitiveInformation(password, "*");
    }

}
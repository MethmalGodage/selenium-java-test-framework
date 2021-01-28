package core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.concurrent.TimeUnit;

public class PageActions {

    public WebDriver driver;

    public PageActions(WebDriver driver) {
        this.driver = driver;
    }

    public void hoverElement(WebDriver driver, WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    public String getAttributeValue(WebElement element, String attributeName) {
        return element.getAttribute(attributeName);
    }

    public String getAlertText(WebDriver driver) {
        return driver.switchTo().alert().getText();
    }

    public void clickOnAlertAccept(WebDriver driver) {
        driver.switchTo().alert().accept();
    }

    public void clickOnAlertDismiss(WebDriver driver) {
        driver.switchTo().alert().dismiss();
    }

    public void switchToFrame(WebDriver driver, WebElement element) {
        driver.switchTo().frame(element);
    }

    public void switchToMainFrame(WebDriver driver) {
        driver.switchTo().parentFrame();
    }

    public void waitTillGivenTime(WebDriver driver, long timeInSeconds) {
        driver.manage().timeouts().implicitlyWait(timeInSeconds, TimeUnit.SECONDS);
    }

    public void uploadFileUsingAbsolutePath(WebDriver driver, By findElementBy, String absolutePathOfFile) {
        driver.findElement(findElementBy).sendKeys(absolutePathOfFile);
    }

}

package base;

import com.google.common.io.Files;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.HomePage;

import java.io.File;
import java.io.IOException;

public class BaseTests {

    protected HomePage homePage;
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver_87");
        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
        navigateToHomePage();
    }

    @BeforeMethod
    public void navigateToHomePage() {
        deleteAllCookies();
        driver.get("https://the-internet.herokuapp.com/");
        driver.manage().window().maximize();
        homePage = new HomePage(driver);
    }

    @AfterMethod
    public void recordFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File screenshot = scrShot.getScreenshotAs(OutputType.FILE);
            try {
                Files.move(screenshot, new File("screenshots/" + result.getName() + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            deleteAllCookies();
            driver.quit();
        }
    }

    private void deleteAllCookies() {
        driver.manage().deleteAllCookies();
    }

    private ChromeOptions getChromeOptions() {

        boolean isHeadless = false;
        boolean isDisableInfoBars = true;

        ChromeOptions options = new ChromeOptions();

        if (isDisableInfoBars) {
            options.addArguments("disable-infobars");
        }
        options.setHeadless(isHeadless);
        return options;
    }

}

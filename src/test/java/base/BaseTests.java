package base;

import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    protected static Logger log;
    protected HomePage homePage;
    private WebDriver driver;

    public BaseTests() {
        log = LogManager.getLogger(BaseTests.class.getName());
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        log.debug("Initiating the test execution.");
    }

    @BeforeClass
    public void setUp() {
        log.info("Starting the test.");
        System.setProperty("webdriver.chrome.driver", "resources/chromedriver_87");
        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
        navigateToHomePage();
    }

    @BeforeMethod
    private void navigateToHomePage() {
        String url = "https://the-internet.herokuapp.com/";
        deleteAllCookies();
        driver.get(url);
        log.info("Navigated to the application URL: " + url);
        driver.manage().window().maximize();
        log.debug("Maximized the browser window.");
        homePage = new HomePage(driver);
    }

    @AfterMethod
    public void recordFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            log.error("Test case failed.");
            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File screenshot = scrShot.getScreenshotAs(OutputType.FILE);
            try {
                Files.move(screenshot, new File("screenshots/" + result.getName() + ".png"));
                log.info("Saved the screenshot taken at the test failure state.");
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
            log.info("Closed the webdriver.");
        }
    }

    private void deleteAllCookies() {
        driver.manage().deleteAllCookies();
        log.info("Deleted all cookies.");
    }

    private ChromeOptions getChromeOptions() {
        log.debug("Started setting the chrome options.");
        boolean isHeadless = false;
        boolean isDisableInfoBars = true;

        ChromeOptions options = new ChromeOptions();

        if (isDisableInfoBars) {
            log.info("Disable infobars: " + isHeadless);
            options.addArguments("disable-infobars");
        }
        options.setHeadless(isHeadless);
        log.info("Headless: " + isHeadless);
        return options;
    }

}

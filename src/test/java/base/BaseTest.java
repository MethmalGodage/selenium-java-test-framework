package base;

import com.google.common.io.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import pages.HomePage;
import utilities.ConfigurationManager;

import java.io.File;
import java.io.IOException;

public class BaseTest {

    protected ConfigurationManager coreConfig;
    protected static Logger log;
    protected HomePage homePage;
    private WebDriver driver;
    protected String workingDir;

    public BaseTest() {
        log = LogManager.getLogger(BaseTest.class.getName());
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        log.info("Initiating the test execution.");

        workingDir = System.getProperty("user.dir");
        String coreConfigPath = workingDir + "/config/coreconfig.cfg";
        log.debug("Core configurations path: " + coreConfigPath);
        coreConfig = new ConfigurationManager();
        coreConfig.setConfiguration(coreConfigPath);
        log.info("Initialized the configurations.");
    }

    @BeforeClass
    public void setUp() {
        setWebDriver();
        loadApplicationURL();
    }

    @BeforeMethod
    private void loadApplicationURL() {
        String url = coreConfig.getProperty("application_url");
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

        ChromeOptions options = new ChromeOptions();

        boolean isHeadless = Boolean.parseBoolean(coreConfig.getProperty("isHeadless"));
        boolean disableInfoBars = Boolean.parseBoolean(coreConfig.getProperty("disableInfoBars"));

        if (disableInfoBars) {
            options.addArguments("disable-infobars");
            log.info("Disabled the info bars.");
        }
        options.setHeadless(isHeadless);
        log.info("Headless: " + isHeadless);
        return options;
    }

    private FirefoxOptions getFirefoxOptions() {
        log.debug("Started setting the firefox options.");

        FirefoxOptions options = new FirefoxOptions();
        boolean isHeadless = Boolean.parseBoolean(coreConfig.getProperty("isHeadless"));
        options.setHeadless(isHeadless);

        return options;
    }

    private String getOperatingSystem() {
        String os = System.getProperty("os.name");
        log.debug("Detected the system OS: " + os.toUpperCase());
        return os;
    }

    private String getConfiguredWebBrowser() {
        String browser = coreConfig.getProperty("browser");
        log.debug("Configured web Browser: " + browser);
        return browser.toUpperCase();
    }

    private void setWebDriver() {
        switch (getOperatingSystem()) {
            case "Windows 10":
                switch (getConfiguredWebBrowser()) {
                    case "CHROME":
                        System.setProperty("webdriver.chrome.driver", "resources/webdrivers/chrome/chromedriver.exe");
                        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
                        break;
                    case "INTERNET_EXPLORER":
                        System.setProperty("webdriver.ie.driver", "resources/webdrivers/internetexplorer/IEDriverServer.exe");
                        driver = new EventFiringWebDriver(new InternetExplorerDriver());
                        break;
                    case "FIREFOX":
                    default:
                        System.setProperty("webdriver.gecko.driver", "resources/webdrivers/firefox/geckodriver_linux.exe");
                        driver = new EventFiringWebDriver(new FirefoxDriver(getFirefoxOptions()));
                        break;
                }
                break;
            case "Mac OS X":
                driver = new EventFiringWebDriver(new SafariDriver());
                break;
            case "Linux":
            default:
                switch (getConfiguredWebBrowser()) {
                    case "CHROME":
                        System.setProperty("webdriver.chrome.driver", "resources/webdrivers/chrome/chromedriver_87_linux");
                        driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
                        break;
                    case "FIREFOX":
                    default:
                        System.setProperty("webdriver.gecko.driver", "resources/webdrivers/firefox/geckodriver_linux");
                        driver = new EventFiringWebDriver(new FirefoxDriver(getFirefoxOptions()));
                        break;
                }
                break;
        }
    }

}

package base;

import com.google.common.io.Files;
import enums.Browser;
import enums.Environment;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utilities.ConfigurationManager;

import java.io.File;
import java.io.IOException;

import static io.github.bonigarcia.wdm.config.DriverManagerType.*;

public class BaseTest {

    protected ConfigurationManager coreConfig;
    protected static Logger log;
    protected WebDriver driver;
    private Browser browser;
    private Environment environment;
    protected String workingDir;

    public BaseTest() {

        //Initialize the log4j logger
        log = LogManager.getLogger(BaseTest.class.getName());
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

        log.info("Initiating the test execution.");

        //Set the core configurations
        workingDir = System.getProperty("user.dir");
        String coreConfigPath = workingDir + "/config/coreconfig.cfg";
        log.debug("Core configurations path: " + coreConfigPath);
        coreConfig = new ConfigurationManager();
        coreConfig.setConfiguration(coreConfigPath);
        log.info("Initialized the configurations.");
    }

    @BeforeMethod
    public void initializeTestEnvironment() {
        setEnvironment();
        setBrowser();
        setWebDriver();
        loadApplicationURL();
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

    private String getApplicationURL() {
        String url = coreConfig.getProperty(environment + "_url");
        log.info("Application URL: " + url);
        return url;
    }

    private void loadApplicationURL() {
        String url = getApplicationURL();
        deleteAllCookies();
        driver.get(url);
        log.info("Navigated to the application URL: " + url);
        driver.manage().window().maximize();
        log.debug("Maximized the browser window.");
    }

    private void setEnvironment() {
        String env;
        if (coreConfig.getProperty("environment") != null) {
            env = coreConfig.getProperty("environment");
            switch (env) {
                case "STAGING":
                    this.environment = Environment.STAGING;
                    log.debug("Environment: STAGING");
                    break;
                case "PRODUCTION":
                    this.environment = Environment.PRODUCTION;
                    log.debug("Environment: PRODUCTION");
                    break;
                default:
                    this.environment = Environment.STAGING;
                    log.warn("There is a problem with the environment configurations. Please check the configurations again! Environment set to STAGING by default configurations.");
                    break;
            }
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
        log.debug("Headless: " + String.valueOf(isHeadless).toUpperCase());
        options.addArguments("--disable-gpu", "--ignore-certificate-errors", "--disable-extensions", "--disable-dev-shm-usage");
        return options;
    }

    private FirefoxOptions getFirefoxOptions() {
        log.debug("Started setting the firefox options.");

        FirefoxOptions options = new FirefoxOptions();
        boolean isHeadless = Boolean.parseBoolean(coreConfig.getProperty("isHeadless"));
        options.setHeadless(isHeadless);
        log.debug("Headless: " + String.valueOf(isHeadless).toUpperCase());
        return options;
    }

    private void setBrowser() {
        String browser = coreConfig.getProperty("browser");
        if (coreConfig.getProperty("browser") != null) {
            switch (browser) {
                case "CHROME":
                    this.browser = Browser.CHROME;
                    log.debug("Browser: CHROME");
                    break;
                case "FIREFOX":
                    this.browser = Browser.FIREFOX;
                    log.debug("Browser: FIREFOX");
                    break;
                case "INTERNET_EXPLORER":
                    this.browser = Browser.IE;
                    log.debug("Browser: INTERNET_EXPLORER");
                    break;
                case "EDGE":
                    this.browser = Browser.EDGE;
                    log.debug("Browser: EDGE");
                    break;
                default:
                    this.browser = Browser.FIREFOX;
                    log.warn("There is a problem with the browser configurations. Please check the configurations again! Browser set to FIREFOX by default configurations.");
                    break;
            }
        }
    }

    private void setWebDriver() {
        switch (browser) {
            case CHROME:
                WebDriverManager.getInstance(CHROME).setup();
                driver = new ChromeDriver(getChromeOptions());
                break;
            case IE:
                WebDriverManager.getInstance(IEXPLORER).setup();
                driver = new InternetExplorerDriver();
                break;
            case EDGE:
                WebDriverManager.getInstance(EDGE).setup();
                driver = new EdgeDriver();
                break;
            case FIREFOX:
            default:
                WebDriverManager.getInstance(FIREFOX).setup();
                driver = new FirefoxDriver(getFirefoxOptions());
                break;
        }
    }

}

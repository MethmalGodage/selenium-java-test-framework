package base;

import com.google.common.io.Files;
import enums.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.utils.SystemUtils;
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
import org.testng.annotations.BeforeMethod;
import utilities.ConfigurationManager;

import java.io.File;
import java.io.IOException;

public class BaseTest {

    protected ConfigurationManager coreConfig;
    protected static Logger log;
    protected WebDriver driver;
    private String browser;
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
        browser = coreConfig.getProperty("browser").toUpperCase();
    }

    private void setWebDriver() {
        if (SystemUtils.IS_OS_WINDOWS) {
            log.debug("Operating System: WINDOWS");
            switch (browser) {
                case "CHROME":
                    System.setProperty("webdriver.chrome.driver", "resources/webdrivers/chrome/chromedriver.exe");
                    driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
                    log.debug("Web Browser: CHROME");
                    break;
                case "INTERNET_EXPLORER":
                    System.setProperty("webdriver.ie.driver", "resources/webdrivers/internetexplorer/IEDriverServer.exe");
                    driver = new EventFiringWebDriver(new InternetExplorerDriver());
                    log.debug("Web Browser: INTERNET EXPLORER");
                    break;
                case "FIREFOX":
                default:
                    log.warn("There is a problem with the browser configurations. Please check the configurations again! Browser set to firefox by default configurations.");
                    System.setProperty("webdriver.gecko.driver", "resources/webdrivers/firefox/geckodriver.exe");
                    driver = new EventFiringWebDriver(new FirefoxDriver(getFirefoxOptions()));
                    log.debug("Web Browser: FIREFOX");
                    break;
            }
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            log.debug("Operating System: MAC OSX");
            driver = new EventFiringWebDriver(new SafariDriver());
            log.debug("Web Browser: SAFARI");
        } else if (SystemUtils.IS_OS_MAC) {
            log.debug("Operating System: MAC OS");
            driver = new EventFiringWebDriver(new SafariDriver());
            log.debug("Web Browser: SAFARI");
        } else if (SystemUtils.IS_OS_LINUX) {
            log.debug("Operating System: LINUX");
            switch (browser) {
                case "CHROME":
                    System.setProperty("webdriver.chrome.driver", "resources/webdrivers/chrome/chromedriver_87_linux");
                    driver = new EventFiringWebDriver(new ChromeDriver(getChromeOptions()));
                    log.debug("Web Browser: CHROME");
                    break;
                case "FIREFOX":
                default:
                    log.warn("There is a problem with the browser configurations. Please check the configurations again! Browser set to firefox by default configurations.");
                    System.setProperty("webdriver.gecko.driver", "resources/webdrivers/firefox/geckodriver_linux");
                    driver = new EventFiringWebDriver(new FirefoxDriver(getFirefoxOptions()));
                    log.debug("Web Browser: FIREFOX");
                    break;
            }
        } else {
            log.debug("Test Framework doesn't support with this OS '" + System.getProperty("os.name") + "'");
            driver = null;
        }
    }

}

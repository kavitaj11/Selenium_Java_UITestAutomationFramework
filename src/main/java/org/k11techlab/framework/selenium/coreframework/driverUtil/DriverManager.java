package org.k11techlab.framework.selenium.coreframework.driverUtil;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.k11techlab.framework.selenium.coreframework.logger.*;
import org.k11techlab.framework.selenium.coreframework.enums.*;
import org.k11techlab.framework.selenium.coreframework.configManager.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Class that manages the creation of web drivers.
 */
public class DriverManager {

    private static WebDriver driver;
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DriverManager.class.getName());

    /**
     * The platform of the execution machine.
     */
    private static final String platform;

    /**
     * Headless execution switch.
     */
    private static final boolean headless;

    /**
     * Remote driver switch.
     */
    private static final boolean remote;

    /**
     * The selenium host.
     */
    private static final String seleniumHost;

    /**
     * The selenium port.
     */
    private static final String seleniumPort;

    /**
     * The selenium report url.
     */
    private static final String seleniumRemoteUrl;

    /**
     * The selenium browser window dimension.
     */
    private static final Dimension broswerWindowSize;

    /**
     * The browser window width.
     */
    private static final Integer browserWindowWidth;

    /**
     * The browser window height.
     */
    private static final Integer broswerWindowHeigth;

    /**
     * The path to the driver executables. Drivers are downloaded at run time if
     * they do not exists.
     */
    private static final String driverDownloadPath = ConfigurationManager.getBundle().getPropertyValue("wdm.targetPath");

    /**
     * Session Id.
     */
    private final ThreadLocal<String> sessionId = new ThreadLocal<>();

    /**
     * Session Browser.
     */
    private final ThreadLocal<String> sessionBrowser = new ThreadLocal<>();

    /**
     * Session Platform.
     */
    private final ThreadLocal<String> sessionPlatform = new ThreadLocal<>();

    /**
     * Session Version.
     */
    private final ThreadLocal<String> sessionVersion = new ThreadLocal<>();

    static {
        seleniumHost = ApplicationProperties.DRIVER_HOST.getStringVal();
        seleniumPort = ApplicationProperties.DRIVER_PORT.getStringVal();
        platform = ApplicationProperties.PLATFORM.getStringVal();

        headless = ApplicationProperties.HEADLESS.getBooleanVal(false);
        remote = ApplicationProperties.REMOTE.getBooleanVal(false);
        //CHECKSTYLE:OFF
        browserWindowWidth = ApplicationProperties.BROWSER_HEIGTH.getStringVal().isEmpty() ? 1024 : Integer.parseInt(ApplicationProperties.BROWSER_HEIGTH.getStringVal());
        broswerWindowHeigth = ApplicationProperties.BROWSER_WIDTH.getStringVal().isEmpty() ? 1280 : Integer.parseInt(ApplicationProperties.BROWSER_WIDTH.getStringVal());
        //CHECKSTYLE:ON

        broswerWindowSize = new Dimension(browserWindowWidth, broswerWindowHeigth);
        //Applicable For Selenium Grid
        seleniumRemoteUrl = "http://" + seleniumHost + ":" + seleniumPort + "/wd/hub";
    }

    /**
     * creates the browser driver specified in the system property "browser" or
     * the configuration parameter 'browser' specified in the
     * 'test-config.properties' file. if no property is set then a chrome
     * browser driver is created. The allowed properties are CHROME, IE. e.g to
     * run with IE, pass in the option -Dbrowser=IE at runtime
     * 
     * TODO consider - what if this is running on the Linux server which has no IE support?
     *
     * @return WebDriver
     */
    public static WebDriver getBrowser() {
        Browsers browser;
        WebDriver dr;
        System.setProperty("wdm.targetPath", driverDownloadPath);

        if (System.getProperty("browser") == null) {
            Log.LOGGER.info("Browser is Null, setting Browser");
            //LoadEnvironmentProperties prop = new LoadEnvironmentProperties();
            String browsername = ApplicationProperties.BROWSER.getStringVal("browser", "Chrome");
            browser = Browsers.browserForName(browsername);
            Log.LOGGER.info("Browser is set using test-config.properties: " + browsername);
        } else {
            browser = Browsers.browserForName(System.getProperty("browser"));
            Log.LOGGER.info("Browser is set using System.Property: " + System.getProperty("browser"));
        }

        switch (browser) {
            case IE:
            case INTERNETEXPLORER:
                dr = createIEDriver();
                break;
            case CHROME:
                dr = createChromeDriver();
                break;
            case FIREFOX:
                dr = createFireFoxDriver();
                break;

            default:
                dr = createChromeDriver();
                break;
        }
        return dr;
    }

    /**
     * Creates a chrome driver.
     *
     * @return Returns a chrome driver instance.
     */
    private static WebDriver createChromeDriver() {
        //below code lets you switch between a local driver and the grid:

        String isRemoteString = System.getProperty("remote");

        boolean isRemote = isRemoteString != null && !isRemoteString.isEmpty() && isRemoteString.equalsIgnoreCase("true") ? true : remote;

        if (isRemote) {
            WebDriver remoteWebDriver = null;
            try {
                Log.LOGGER.info(MessageFormat.format("Running on remote Grid instance. on {0}", seleniumRemoteUrl));
                remoteWebDriver = new RemoteWebDriver(new URL(seleniumRemoteUrl), getChromeOptions());
            } catch (MalformedURLException e) {
                LOG.info(seleniumRemoteUrl + " Error " + e.getMessage());
                throw new RuntimeException(e);  // just give up
            }
            return remoteWebDriver;
        } else {

            //WebDriverManager.chromedriver().arch32().setup();
            // Set the path to the ChromeDriver executable
            WebDriver driver= launchChromeBrowser();
            return driver;
        }
    }

    public static WebDriver launchChromeBrowser() {

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.setHeadless(false);

        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);

        DesiredCapabilities desiredCapabilities = new DesiredCapabilities(chromeOptions);
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);

        // Set the path to the ChromeDriver executable
        System.setProperty(
                ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY,
                System.getProperty("user.dir") + File.separator + "drivers" + File.separator + "chromedriver.exe"
        );

        // Set the path for the ChromeDriver log file
        System.setProperty(
                ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY,
                System.getProperty("user.dir") + File.separator + "target" + File.separator + "chromedriver.log"
        );

        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder().usingAnyFreePort().withVerbose(true).build();

        try {
            chromeDriverService.start();
        } catch (IOException ioex) {

        }

        driver = new RemoteWebDriver(chromeDriverService.getUrl(), desiredCapabilities);
        return driver;

    }

    /**
     * Creates an IE driver.
     *
     * @return Returns an IE driver instance.
     */
    private static WebDriver createIEDriver() {
        //below code lets you switch between a local driver and the grid:
        WebDriverManager.iedriver().arch32().setup();
        WebDriverManager.getInstance(InternetExplorerDriver.class).setup();
        String isRemoteString = System.getProperty("remote");

        boolean isRemote = isRemoteString != null && !isRemoteString.isEmpty() && isRemoteString.equalsIgnoreCase("true") ? true : remote;

        if (isRemote) {
            WebDriver remoteWebDriver = null;
            try {
                Log.LOGGER.info("Running on remote Grid instance.");
                remoteWebDriver = new RemoteWebDriver(new URL(seleniumRemoteUrl), getInternetExploreOptions());
            } catch (MalformedURLException e) {
                LOG.info(seleniumRemoteUrl + " Error " + e.getMessage());
                throw new RuntimeException(e);  // just give up
            }
            return remoteWebDriver;
        } else {
            return new InternetExplorerDriver(getInternetExploreOptions());
        }

    }

    /**
     * Creates a Firefox web driver.
     *
     * @return Returns a firefox web driver instance.
     */
    private static WebDriver createFireFoxDriver() {
        //below code lets you switch between a local driver and the grid:
        WebDriverManager.firefoxdriver().arch32().setup();
        String isRemoteString = System.getProperty("remote");

        boolean isRemote = isRemoteString != null && !isRemoteString.isEmpty() && isRemoteString.equalsIgnoreCase("true") ? true : remote;

        if (isRemote) {

            WebDriver remoteWebDriver = null;
            try {
                Log.LOGGER.info("Running on remote Grid instance.");
                remoteWebDriver = new RemoteWebDriver(new URL(seleniumRemoteUrl), getFireFoxDesiredCapabilities());
            } catch (MalformedURLException e) {
                LOG.info(seleniumRemoteUrl + " Error " + e.getMessage());
                throw new RuntimeException(e);  // just give up
            }
            return remoteWebDriver;
        } else {
            FirefoxProfile profile = new FirefoxProfile();
           // profile.setPreference("network.negotiate-auth.trusted-uris", "");
           // profile.setPreference("network.negotiate-auth.delegation-uris", "");
           // profile.setPreference("network.automatic-ntlm-auth.trusted-uris", "");
            FirefoxOptions options = new FirefoxOptions();
            options.setCapability("marionette", true);
            options.setHeadless(headless);
            options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT).setAcceptInsecureCerts(true);
            options.setProfile(profile);
            return new FirefoxDriver(options);
        }
    }

    /**
     * Gets the chrome options.
     *
     * @return Returns chrome options.
     */
    private static ChromeOptions getChromeOptions() {

        ChromeOptions options = new ChromeOptions();
        //To start chrome without security warning
        //options.addArguments("test-type");
        //To start the chrome in Maximized mode
        options.addArguments("start-maximized");
        options.addArguments("--ignore-certificate-errors");
        //options.addArguments("--disable-web-security");
        // options.addArguments("--allow-running-insecure-content");
        //options.addArguments("--disable-plugins", "--disable-extensions", "--disable-popup-blocking");
       // options.addArguments("--remote-debugging-port=9222");
       // options.addArguments("--incognito");
        //options.setExperimentalOption("debuggerAddress", "localhost:9222");

        //Support for Headless browser testing
        if ((System.getProperty("headless") != null && System.getProperty("headless").equals("true")) || headless) {

            Log.LOGGER.info("Running Chrome Headless");
            options.addArguments("--headless");
        }

          return options;
    }

    /**
     * Generates download folder capability.
     *
     * @return Map of download capabilities.
     */
    public static HashMap<String, Object> generateDownloadFolderCapability() {
        HashMap<String, Object> chromeAdditionalOptions
                = new HashMap<>();
        chromeAdditionalOptions.put("download.default_directory", "./templates/");
        chromeAdditionalOptions.put("download.prompt_for_download", false);
        chromeAdditionalOptions.put("download.directory_upgrade", true);
        return chromeAdditionalOptions;
    }

   /* *//**
     * Generates Performance Desired Capabilities.
     *
     * @return Desired capabilities.
     *//*
    public static DesiredCapabilities generatePerformamceLoggingCapability() {
        DesiredCapabilities cap = DesiredCapabilities
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        return cap;
    }
*/
    /**
     * Sets the session info.
     *
     * @param webDriver The web driver instance.
     * @param caps The Capabilities.
     */
 /*   public void setSessionInfo(RemoteWebDriver webDriver, DesiredCapabilities caps) throws Exception {
        sessionId.set(((RemoteWebDriver) webDriver).getSessionId().toString());
        sessionBrowser.set(caps.getBrowserName());
        sessionVersion.set(caps.getVersion());
        sessionPlatform.set(caps.getPlatform().toString());

        System.out.println("\n*** TEST ENVIRONMENT = "
                + getSessionBrowser().toUpperCase()
                + "/" + getSessionPlatform().toUpperCase()
                + "/Session ID=" + getSessionId() + "\n");
    }*/

    /**
     * getSessionId method to retrieve active id.
     *
     * @return the session id
     * @throws Exception exception.
     */
    public String getSessionId() throws Exception {
        return sessionId.get();
    }

    /**
     * getSessionBrowser method to retrieve active browser.
     *
     * @return the session browser
     * @throws Exception exception.
     */
    public String getSessionBrowser() throws Exception {
        return sessionBrowser.get();
    }

    /**
     * getSessionVersion method to retrieve active version.
     *
     * @return the session version
     * @throws Exception the error
     */
    public String getSessionVersion() throws Exception {
        return sessionVersion.get();
    }

    /**
     * getSessionPlatform method to retrieve active platform.
     *
     * @return the session platform
     */
    public String getSessionPlatform() {
        return sessionPlatform.get();
    }

    /**
     * Gets the IE browser options.
     *
     * @return the IE browser options
     */
    private static InternetExplorerOptions getInternetExploreOptions() {
        InternetExplorerOptions options = new InternetExplorerOptions();
        // Removed ignore zoom settings
        options.introduceFlakinessByIgnoringSecurityDomains().enablePersistentHovering().destructivelyEnsureCleanSession();
        return options;
    }

    /**
     * Gets the Firefox desired capabilities.
     *
     * @return the desired capabilities for Firefox
     */
    private static DesiredCapabilities getFireFoxDesiredCapabilities() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        capabilities.setBrowserName("firefox");
        capabilities.setCapability("disable-restore-session-state", true);
        return capabilities;
    }
}

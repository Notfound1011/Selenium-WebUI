package com.selenium.webui.core;

import com.selenium.webui.Browser;
import com.selenium.webui.utils.Util;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * create driver with different browser type
 */
public class WebDriverFactory {
    private static final Logger logger = Logger.getLogger(WebDriverFactory.class);


    /**
     * Create web manager web manager.
     *
     * @param browser the browser to create, allowed values: firefox, chrome, ie, edge
     * @return the web manager with correct web browser
     */
    public static WebDriver createWebDriver(Browser browser){
        WebDriver driver;
        Properties properties;
        final String propertyPath;
        propertyPath  = "application.properties";
        properties = Util.loadProperties(propertyPath);
        switch (browser){
            case CHROME:
                driver = new ChromeDriver(Config.getChromeOptions());
                break;
            case IE:
            	DesiredCapabilities cap=DesiredCapabilities.internetExplorer();
            	cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            	System.setProperty("webdriver.ie.driver", properties.getProperty("ie.webdriver.driver"));
                driver = new InternetExplorerDriver(cap);
                break;
            case EDGE:
                driver = new EdgeDriver();
                break;
            case FIREFOX:
            default:
                //driver = new FirefoxDriver(Config.getFirefoxProfile());
            	System.setProperty("webdriver.gecko.driver", properties.getProperty("ff.webdriver.gecko.driver"));
            	//System.setProperty("webdriver.firefox.marionette", properties.getProperty("ff.webdriver.gecko.driver"));
            	driver = new FirefoxDriver();
        }
        driver.manage().timeouts().implicitlyWait(Config.WAIT_FOR_TIMEOUT, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        return driver;
    }
}

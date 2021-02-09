package com.selenium.webui.core;

import com.selenium.webui.utils.Util;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

import java.util.Properties;

/**
 * The type Config.
 */
public class Config {
    private static FirefoxProfile firefoxProfile;
    private static ChromeOptions chromeOptions;
    private static final String propertyPath;
    private static final Properties properties;
    public static final int WAIT_TIME = 10;
    public static final int WAIT_FOR_TIMEOUT;

    static {
        propertyPath  = "application.properties";
        properties = Util.loadProperties(propertyPath);
        System.setProperty("webdriver.gecko.driver", properties.getProperty("ff.webdriver.gecko.driver"));
        System.setProperty("webdriver.firefox.marionette", properties.getProperty("ff.webdriver.gecko.driver"));
        System.setProperty("webdriver.chrome.driver", properties.getProperty("chrome.webdriver.chrome.driver"));

        String env = properties.getProperty("com.env");
        if (env.equals("dev")) {
            WAIT_FOR_TIMEOUT = Integer.valueOf(properties.getProperty("com.env.dev.wait_timeout", "60"));
        } else {
            WAIT_FOR_TIMEOUT = Integer.valueOf(properties.getProperty("com.env.prod.wait_timeout", "30"));
        }
    }

    public static String getPropertyPath() {
        return propertyPath;
    }

    /**
     * Get firefox profile firefox profile.
     *
     * @return the firefox profile
     */
    public synchronized static FirefoxProfile getFirefoxProfile(){
        if (firefoxProfile == null){
            firefoxProfile = new FirefoxProfile();
            firefoxProfile.setAcceptUntrustedCertificates(Boolean.valueOf(properties.getProperty("ff.trust_all_ssl", "true")));
            //firefoxProfile.setEnableNativeEvents(Boolean.valueOf(properties.getProperty("ff.enable_native_events", "true")));
            firefoxProfile.setAssumeUntrustedCertificateIssuer(true);
        }
        return firefoxProfile;
    }

    /**
     * Get chrome options chrome options.
     *
     * @return the chrome options
     */
    public synchronized static ChromeOptions getChromeOptions(){
        if (chromeOptions == null){
            chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--lang=en");
            chromeOptions.addArguments("--no-sandbox");// 解决DevToolsActivePort文件不存在的报错
            chromeOptions.addArguments("window-size=1920x3000"); //指定浏览器分辨率
            chromeOptions.addArguments("--disable-gpu"); // 谷歌文档提到需要加上这个属性来规避bug
            chromeOptions.addArguments("--hide-scrollbars"); //隐藏滚动条, 应对一些特殊页面
            chromeOptions.addArguments("blink-settings=imagesEnabled=false");  //不加载图片, 提升速度
            chromeOptions.addArguments("--headless"); //浏览器不提供可视化页面. linux下如果系统不支持可视化不加这条会启动失败
        }
        return chromeOptions;
    }
}

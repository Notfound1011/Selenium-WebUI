package com.selenium.webui.page;


import com.selenium.webui.core.DriverManager;
import com.selenium.webui.utils.Util;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Properties;

/**
 * @author shiyuyu
 */
public class Page {
    /**
     * The constant logger.
     */
    private static final Logger logger = Logger.getLogger(com.selenium.webui.page.Page.class);
    private String url;
    private String title;
    protected static DriverManager manager;
    protected boolean isDisplayed;

    private static int RETRIES = 0;
    private static final int MAX_RETRIES = 5;

    /**
     * Instantiates a new Page.
     */
    public Page() {
    }

    /**
     * Instantiates a new Page.
     *
     * @param manager the manager
     */
    public Page(DriverManager manager) {
        Page.manager = manager;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets url.
     *
     * @param url the url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Is displayed boolean.
     *
     * @return the boolean
     */
    public boolean isDisplayed() {
        return isDisplayed;
    }

    /**
     * Sets displayed.
     *
     * @param displayed the displayed
     */
    public void setDisplayed(boolean displayed) {
        isDisplayed = displayed;
    }

    /**
     * Gets manager.
     *
     * @return the manager
     */
    public DriverManager getManager() {
        return manager;
    }

    /**
     * Sets manager.
     *
     * @param driver the manager
     */
    public void setManager(DriverManager driver) {
        this.manager = driver;
    }

    /**
     * Navigate to t.
     *
     * @param <T> the type parameter
     * @param url the url
     * @param clz the clz
     * @return the t
     */
    public static <T> T navigateTo(String url, Class<T> clz) {
        logger.info("Navigate to: " + url + "to build Page: " + clz.getSimpleName());
        return manager.navigateTo(url, clz);
    }

    public static void navigateTo(String url){
        logger.info("Navigate to: " + url + "to build Page: " );
        manager.navigateTo(url);
    }

    public static void back() {
        logger.info("Back");
        manager.back();
    }

    public static <T> T initPage(Class<T> clz) {
        logger.info("init page: " + clz.getSimpleName() + " begin...");
        T page = PageFactory.initElements(manager.getDriver(), clz);
        logger.info("init page: " + clz.getSimpleName() + " done!");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return page;
    }

    public String getSimpleName(){
        return this.getClass().getSimpleName();
    }

    public static boolean checkWorkAreaHeader(String confPath, String prefix, WebElement workAreaHeader){
        boolean flag = false;
        Properties properties = Util.loadProperties(confPath);
        Optional<String> optional = properties.stringPropertyNames()
                .stream()
                .filter(name->name.startsWith(prefix) && name.contains("workAreaHeader"))
                .findAny();

        if (manager.weIsDisplayed(workAreaHeader)){
            flag = optional.isPresent();
            if (!flag) {
                logger.info("property with workAreaHeader with prefix: " + prefix + " not found in properties file: " + confPath);
            } else {
                if (workAreaHeader.getText().contains("Loading")) {
                    if (RETRIES < MAX_RETRIES) {
                        logger.info("work area header not displayed, retries: " + RETRIES);
                        Util.sleep(1000 * (1 << RETRIES++));
                        return checkWorkAreaHeader(confPath, prefix, workAreaHeader);
                    }
                }
                RETRIES = 0;
                String workAreaHeaderText = Util.getProperty(confPath, optional.get());
                flag = workAreaHeader.getText().equals(workAreaHeaderText);
                if (!flag){
                    logger.info("workAreaHeader text should be "+ workAreaHeaderText + ", but found " + workAreaHeader.getText());
                    manager.screenshot(Util.getCurrentMethodName());
                }
            }
        }
        return flag;
    }

    public boolean healthCheck() {
        boolean flag = true;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FindBy.class)) {
                field.setAccessible(true);
                try {
                    if (!manager.weIsDisplayed((WebElement)(field.get(this)))) {
                        System.out.println("xxxx"+false);
                        flag = false;
                    }
                } catch (Exception e) {
                    flag = false;
                    logger.warn(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }
}

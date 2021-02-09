package com.selenium.webui;


import com.selenium.webui.core.DriverManager;
import com.selenium.webui.page.Page;
import com.selenium.webui.utils.Constants;
import org.apache.log4j.Logger;
import org.testng.annotations.*;

/**
 * Created by shiyuyu
 */

@Test
public class TestPage {
    private static final Logger logger = Logger.getLogger(TestPage.class);
    protected static final String schema = "https://";
    protected static final int port = 443;
    protected static Page page;
    protected static String host;
    protected String url;

    @BeforeTest
    @Parameters({"host", "browser"})
    public void init(String host, String browser){
        logger.info("init TestPage");
        TestPage.host = host;
        this.url = "";
        if (page == null) {
            DriverManager manager = new DriverManager(Browser.browserOf(browser.toLowerCase()));
            page = new Page(manager);
        }
    }
    @AfterSuite
    public void shutDown(){
    	logger.debug(Constants.REG+"shutDown"+Constants.REG);
        page.getManager().shutDownDriver(); 
    }
    
    @BeforeClass
    public void refreshBrowserIfPopupDialogIsNotClosed(){
    	
    	logger.debug("when class start, refresh browser to make sure it runs smoothly");
    	page.getManager().refresh();
//   	if(!page.getManager().findElement("#hardware_text", LocatorType.CSS).isDisplayed()){
//   		logger.debug("memubar hardware is not displayed, might be a popup dialog is not closed automatically, going to refresh browser");
//   		page.getManager().refresh();
//    	}
    }
}

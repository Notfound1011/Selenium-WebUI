package com.selenium.webui.page;

import com.selenium.webui.TestPage;
import com.selenium.webui.utils.Constants;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static com.selenium.webui.utils.Constants.SUCCESS;

/**
 * Created by shiyuyu
 * /Users/shiyuyu/.m2/repository/org/testng/testng/6.9.13.6/testng-6.9.13.6.jar
 * /Users/shiyuyu/IdeaProjects/Selenium-WebUI/src/main/resources/testng.xml
 */

@Test
public class TestHomeSearchPage extends TestPage {
    private static final Logger logger = Logger.getLogger(TestHomeSearchPage.class);

    @BeforeClass
    public void init() {
        logger.info("init TestHomePage");
        Page.navigateTo(schema + host);
    }


    @Parameters({"words", "browser"})
    public void testSearch(String words, String browser) {
    	logger.debug(Constants.REG+"testLogin"+Constants.REG);
//        page = ((LoginPage)page).login(userId, password);
    	HomeSearchPage homesearchpage = Page.initPage(HomeSearchPage.class);
        ReturnValue rv = homesearchpage.search(words, browser);
        logger.info(page);
       // page = rv.getPage();
        Assert.assertEquals(rv.getError(), SUCCESS);
    }

    @Parameters({"words", "browser"})
    public void testClick(String words, String browser) {
        logger.debug(Constants.REG+"testLogin"+Constants.REG);
//        page = ((LoginPage)page).login(userId, password);
        HomeSearchPage homesearchpage = Page.initPage(HomeSearchPage.class);
        ReturnValue rv = homesearchpage.search(words, browser);
        logger.info(page);
        // page = rv.getPage();
        Assert.assertEquals(rv.getError(), SUCCESS);
    }

}

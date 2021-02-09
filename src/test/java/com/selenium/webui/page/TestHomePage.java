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
public class TestHomePage extends TestPage {
    private static final Logger logger = Logger.getLogger(TestHomePage.class);

    @BeforeClass
    public void init() {
        logger.info("init TestHomePage");
        Page.navigateTo(schema + host);
    }


    @Parameters({"words", "browser"})
    public void testSelectKeyword(String words, String browser) {
    	logger.debug(Constants.REG+"testSelectKeyword"+Constants.REG);
    	HomePage homepage = Page.initPage(HomePage.class);
        ReturnValue rv = homepage.selectKeyword(words, browser);
        logger.info(page);
        Assert.assertEquals(rv.getError(), SUCCESS);
    }

    public void testSelectCity() {
        logger.debug(Constants.REG+"testSelectCity"+Constants.REG);
        HomePage homesearchpage = Page.initPage(HomePage.class);
        ReturnValue rv = homesearchpage.selectCity();
        logger.info(page);
        Assert.assertEquals(rv.getError(), SUCCESS);
    }

}

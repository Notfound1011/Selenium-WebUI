package com.selenium.webui.page;

import com.selenium.webui.LocatorType;
import com.selenium.webui.core.DriverManager;
import com.selenium.webui.utils.Util;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author shiyuyu
 * @date 2021/2/9 11:22 上午
 */
public class HomeSearchPage extends Page {

	private static final String url = "login";
	private static final Logger logger = Logger.getLogger(HomeSearchPage.class);

	@FindBy(name = "wd")
	private WebElement keyword;

	@FindBy(id = "su")
	private WebElement baiduSearch;

	private WebElement baiduLogo;
	/**
	 * Instantiates a new home page.
	 */
	public HomeSearchPage() {
		super();
		logger.info("Create home page");
	}

	/**
	 * Instantiates a new home page.
	 *
	 * @param driver
	 *            the manager
	 */
	public HomeSearchPage(DriverManager driver) {
		super(driver);
	}


	public ReturnValue search(String words, String browser) {
		if (browser.equalsIgnoreCase("chrome")
				&& manager.elementExist("#lg > map > area", LocatorType.CSS)) {
			baiduLogo = manager.findElement("#lg > map > area", LocatorType.CSS);
			if (manager.weIsDisplayed(baiduLogo)) {
				manager.click(keyword);
			}
		}

		logger.info(" search with words: " + words);
//		findElements();

		if (manager.weIsDisplayed(keyword)) {
			manager.sendKeys(keyword, words);
		}
		if (manager.weIsDisplayed(baiduSearch)) {
			manager.click(baiduSearch);
		}
		// sleep to ensure all elements displayed
		Util.sleep(5000);
		return ReturnValue.ofPage(Page.initPage(HomeSearchPage.class));
	}

//	/**
//	 * Navigate to login page.
//	 *
//	 * @param url
//	 *            the url
//	 * @return the login page
//	 */
//	public static void navigateTo(String url) {
//		logger.info("Navigate to: " + url);
//		try {
//			manager.navigateTo(url);
//			// username form
//			manager.waitForElementDisplayed(
//					By.xpath(".//*[@id='dijit_form_Form_0']/div[8]/span[1]/span"),
//					60);
//		} catch (Exception e) {
//			logger.error(e.getMessage());
//			manager.waitForElementDisplayed(
//					By.xpath(".//*[@id='dijit_form_Form_0']/div[8]/span[1]/span"),
//					60);
//		}
//	}


//	private void findElements() {
//		logger.info("find elements for " + getSimpleName());
//		userName = manager.findElement("#idx_form_TextBox_0", LocatorType.CSS);
//		password = manager.findElement("#augusta-login-framePassword",
//				LocatorType.CSS);
//		loginButton = manager.findElement(
//				".//*[@id='dijit_form_Form_0']/div[8]/span[1]/span",
//				LocatorType.XPATH);
//	}

	@Override
	public String toString() {
		return "HomeSearchPage{" + "words=" + keyword  + '}';
	}

	/**
	 * @param  
	 * @return 
	 * @author shiyuyu
	 * @describe: 
	 * @date 2021/2/5 3:42 下午
	 */
	public WebElement getSearchKeyword() {
		return keyword;
	}

	/**
	 * @param searchKeyword 
	 * @return 
	 * @author shiyuyu
	 * @describe: 
	 * @date 2021/2/5 3:42 下午
	 */
	public void setSearchKeyword(WebElement searchKeyword) {
		this.keyword = searchKeyword;
	}

	/**
	 * @param  
	 * @return 
	 * @author shiyuyu
	 * @describe: 
	 * @date 2021/2/5 3:42 下午
	 */
	public WebElement getBaiduSearch() {
		return baiduSearch;
	}

	/**
	 * @param baiduSearch
	 * @return
	 * @author shiyuyu
	 * @describe:
	 * @date 2021/2/5 3:41 下午
	 */
	public void setBaiduSearch(WebElement baiduSearch) {
		this.baiduSearch = baiduSearch;
	}
}

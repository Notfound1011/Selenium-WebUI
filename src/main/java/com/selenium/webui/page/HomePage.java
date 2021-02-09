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
public class HomePage extends Page {
	private static final Logger logger = Logger.getLogger(HomePage.class);

	@FindBy(id = "states-autocomplete")
	private WebElement searchBox;

	private String selectKeyword = "//div[@class=\"item item-type-103\"][1]";

	@FindBy(xpath = "//div[text()=\"搜索民宿\"]")
	private WebElement searchButton;

	@FindBy(xpath = "//div[@class=\"con-city\"]")
	private WebElement cityBox;

	private String selectCity = "//div[@class=\"con-list con-list-s1\"]//a[text()=\"北京\"]";

	/**
	 * Instantiates a new home page.
	 */
	public HomePage() {
		super();
		logger.info("Create home page");
	}

	/**
	 * Instantiates a new home page.
	 *
	 * @param driver
	 *            the manager
	 */
	public HomePage(DriverManager driver) {
		super(driver);
	}


	public ReturnValue selectKeyword(String words, String browser) {
		if (browser.equalsIgnoreCase("chrome")
				&& manager.elementExist("ctriplogo", LocatorType.CLASS_NAME))
		{
			if (manager.weIsDisplayed(searchBox)) {
				manager.click(searchBox);
			}
		}

		logger.info(" search with words: " + words);

		if (manager.weIsDisplayed(searchBox)) {
			manager.sendKeys(searchBox, words);
		}

		manager.findElement(selectKeyword,LocatorType.XPATH).click();

//		if (manager.weIsDisplayed(searchButton)) {
//			manager.click(searchButton);
//		}

		// sleep to ensure all elements displayed
		Util.sleep(5000);
		return ReturnValue.ofPage(Page.initPage(HomePage.class));
	}

	public ReturnValue selectCity() {
		logger.info(" select city cityName");

		if (manager.weIsDisplayed(cityBox)) {
			manager.click(cityBox);
		}

		manager.findElement(selectCity,LocatorType.XPATH).click();

		// sleep to ensure all elements displayed
		Util.sleep(5000);
		return ReturnValue.ofPage(Page.initPage(HomePage.class));
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

	public WebElement getSearchBox() {
		return searchBox;
	}

	public void setSearchBox(WebElement searchBox) {
		this.searchBox = searchBox;
	}

	public WebElement getSearchButton() {
		return searchButton;
	}

	public void setSearchButton(WebElement searchButton) {
		this.searchButton = searchButton;
	}

	@Override
	public String toString() {
		return "HomePage{" + "searchBox=" +  searchBox + '}';
	}

}

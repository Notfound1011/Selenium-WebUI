package com.selenium.webui.core;

import com.selenium.webui.Browser;
import com.selenium.webui.LocatorType;
import com.selenium.webui.utils.Util;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * the driver Capabilities
 */
public class DriverManager implements FailureAction {
	private static final Logger logger = Logger.getLogger(DriverManager.class);
	private static final Long SLEEP_DURATION = 10 * 1000L;
	// private static final Long SLEEP_DURATION = Config.WAIT_FOR_TIMEOUT*1000L;
	private WebDriver driver;
	private static final int MAX_RETRIES = 2;
	private Actions action;

	/**
	 *
	 * @param browser
	 */
	public DriverManager(Browser browser) {
		driver = WebDriverFactory.createWebDriver(browser);
	}

	public Actions getAction() {
		action = new Actions(driver);
		return action;
	}

	/**
	 * Gets manager.
	 *
	 * @return the manager
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * Sets manager.
	 *
	 * @param driver
	 *            the manager
	 */
	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void moveToElement(WebElement element) {
		Actions actions = new Actions(driver);
		actions.moveToElement(element).perform();
	}

	/**
	 * Navigate to Page.
	 *
	 * @param <T>
	 *            the type parameter Of specific Page class
	 * @param url
	 *            the url The url to nagviate
	 * @param clz
	 *            the clz The Class of specific Page class
	 * @return The Page class object as long with given url
	 */
	public <T> T navigateTo(String url, Class<T> clz) {
		logger.info("Navigate to: " + url + " to build Page: "
				+ clz.getSimpleName());
		try {
			driver.get(url);
			Thread.sleep(SLEEP_DURATION);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			screenshot("Exception_navigateTo");
		}
		return PageFactory.initElements(driver, clz);
	}

	public void navigateTo(String url) {
		logger.info("Navigate to: " + url + " to build Page: ");
		try {
			driver.get(url);
			Thread.sleep(SLEEP_DURATION);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			screenshot("Exception_navigateTo");
		}
	}

	public void back() {
		logger.info("back from :" + driver.getCurrentUrl());
		driver.navigate().back();
		logger.info("after back :" + driver.getCurrentUrl());
	}

	public void refresh() {
		logger.info("refresh current page: " + driver.getCurrentUrl());
		driver.navigate().refresh();
		implicitlyWait(30);
	}

	public void implicitlyWait(long timeout) {
		logger.info("Implicitly wait for " + timeout + " seconds");
		driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
	}

	public void shutDownDriver() {
		logger.info("Shutdown webdriver: " + driver);
		driver.quit();
	}

	
	 public void crollIntoView(WebElement element){
    	 WebDriver driver=getDriver();
    	 JavascriptExecutor je = (JavascriptExecutor)driver;
    	 je.executeScript("arguments[0].scrollIntoView(true);",element);
        }
	public void submit(WebElement we) {
		logger.info("submit webElement: " + we);
		try {
			we.submit();
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			screenshot("Exception_submit");
		}
	}

	/**
	 * Click with retries 3 times
	 * 
	 * @param we
	 *            WebElement to click
	 */
	public void click(WebElement we) {
		click(we, 0);
	}

	public void click(WebElement we, int retries) {
		logger.info("click by: " + we);
		if (retries < MAX_RETRIES) {
			try {
				we.click();
				implicitlyWait(60);
			} catch (Exception e) {
				e.printStackTrace();
				screenshot("Exception_click");
				logger.info(e.getMessage());
				long waitTime = (1 << retries++) * 1000;
				logger.info("Retries: " + retries + " after " + waitTime
						+ " milliseconds");
				Util.sleep(waitTime);
				click(we, retries);
				throw e;
			}
		}
	}

	public void sendKeys(WebElement we, CharSequence... keys) {
		logger.info("Send keys: " + keys);
		try {
			// we.clear();
			we.sendKeys(keys);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			screenshot("Exception_sendKeys");
		}
	}

	/**
	 * Find element web element.
	 *
	 * @param query
	 *            the query
	 * @param type
	 *            the type
	 * @return the web element
	 */
	public WebElement findElement(String query, LocatorType type) {
		WebElement element = null;
		By by = null;
		switch (type) {
		case ID:
			by = By.id(query);
			break;
		case XPATH:
			by = By.xpath(query);
			break;
		case NAME:
			by = By.name(query);
			break;
		case TAG_NAME:
			by = By.tagName(query);
			break;
		case CLASS_NAME:
			by = By.className(query);
			break;
		case LINK_TEXT:
			by = By.linkText(query);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(query);
			break;
		case CSS:
		default:
			by = By.cssSelector(query);
			break;
		}

		// /logger.info("Find the element: " + by); //[Clare] rem
		try {
			element = driver.findElement(by);
			weIsDisplayed(element);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			screenshot("Exception_findElement");
			throw e;
		}
		return element;
	}

	public boolean elementExist(String query, LocatorType type) {
		WebElement element = null;
		By by = null;
		switch (type) {
		case ID:
			by = By.id(query);
			break;
		case XPATH:
			by = By.xpath(query);
			break;
		case NAME:
			by = By.name(query);
			break;
		case TAG_NAME:
			by = By.tagName(query);
			break;
		case CLASS_NAME:
			by = By.className(query);
			break;
		case LINK_TEXT:
			by = By.linkText(query);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(query);
			break;
		case CSS:
		default:
			by = By.cssSelector(query);
			break;
		}

		logger.info("Check the element: " + by);
		try {
			element = driver.findElement(by);
			return true;
		} catch (Exception e) {
			screenshot("Exception_elementExist");
			return false;
		}
	}

	public WebElement findElementByParent(WebElement parent, String query,
			LocatorType type) {
		By by;
		WebElement we = null;

		switch (type) {
		case ID:
			by = By.id(query);
			break;
		case CSS:
			by = By.cssSelector(query);
			break;
		case XPATH:
			by = By.xpath(query);
			break;
		case TAG_NAME:
			by = By.tagName(query);
			break;
		case NAME:
			by = By.name(query);
			break;
		case CLASS_NAME:
			by = By.className(query);
			break;
		case LINK_TEXT:
			by = By.linkText(query);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(query);
			break;
		default:
			by = By.cssSelector(query);
		}
		try {
			logger.info("Finding element by parent: " + by);
			we = parent.findElement(by);
			if (we == null)
				screenshot("Exception_findElementByParent_null");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_findElementByParent");
			throw e;
		}
		return we;
	}

	public List<WebElement> findElementsByParent(WebElement parent,
			String query, LocatorType type) {
		By by;
		List<WebElement> wes = new LinkedList<>();

		switch (type) {
		case ID:
			by = By.id(query);
			break;
		case CSS:
			by = By.cssSelector(query);
			break;
		case XPATH:
			by = By.xpath(query);
			break;
		case TAG_NAME:
			by = By.tagName(query);
			break;
		case NAME:
			by = By.name(query);
			break;
		case CLASS_NAME:
			by = By.className(query);
			break;
		case LINK_TEXT:
			by = By.linkText(query);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(query);
			break;
		default:
			by = By.cssSelector(query);
		}
		try {
			logger.info("Finding elements: " + by);
			wes = parent.findElements(by);
			logger.info("debug: the length of wes is " + wes.size());
			if (wes == null || wes.isEmpty())
				screenshot("Exception_findElementsByParent_null");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_findElementsByParent");
			throw e;
		}
		return wes;
	}

	public List<WebElement> findElements(String query, LocatorType type) {
		List<WebElement> wes = new LinkedList<>();
		By by = null;

		switch (type) {
		case ID:
			by = By.id(query);
			break;

		case CSS:
			by = By.cssSelector(query);
			break;
		case XPATH:
			by = By.xpath(query);
			break;
		case TAG_NAME:
			by = By.tagName(query);
			break;
		case NAME:
			by = By.name(query);
			break;
		case CLASS_NAME:
			by = By.className(query);
			break;
		case LINK_TEXT:
			by = By.linkText(query);
			break;
		case PARTIAL_LINK_TEXT:
			by = By.partialLinkText(query);
			break;
		default:
			by = By.cssSelector(query);
		}
		try {
			logger.info("Finding elements: " + by);
			wes = driver.findElements(by);
			logger.info("debug: the length of wes is " + wes.size());
			if (wes == null || wes.isEmpty())
				screenshot("Exception_findElements_null");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_findElements");
			throw e;
		}
		return wes;
	}

	/**
	 * Wait for element displayed web element.
	 *
	 * @param by
	 *            the by
	 * @return the web element
	 */
	public WebElement waitForElementDisplayed(By by) {

		WebElement we = null;
		try {
			we = waitForElementDisplayed(by, Config.WAIT_FOR_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementDisplayed");
			throw e;
		}
		return we;
	}

	/**
	 * Wait for element displayed web element.
	 *
	 * @param by
	 *            the by
	 * @param time
	 *            the time
	 * @return the web element
	 */
	// public WebElement waitForElementDisplayed(By by, int time) throws
	// RuntimeException{
	// WebDriverWait wait = new WebDriverWait(driver, (long)time);
	// return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	// }

	public WebElement waitForElementDisplayed(By by, int time) {
		WebElement we = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, (long) time);
			we = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementDisplayed");
			throw e;
		}
		return we;
	}

	public WebElement waitForElementDisplayed(WebElement we, int time)
			throws RuntimeException {
		WebElement we1 = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, (long) time);
			we1 = wait.until(ExpectedConditions.visibilityOf(we));

		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementDisplayed");
			throw e;
		}
		return we1;
	}

	/**
	 * Wait for element Invisible web element.
	 */

	public boolean waitForElementInvisible(By by) {
		boolean flag = false;
		try {
			flag = waitForElementInvisible(by, Config.WAIT_FOR_TIMEOUT);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementInvisible");
		}
		return flag;
	}

	public boolean waitForElementInvisible(By by, int time) {
		boolean flag = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, (long) time);
			flag = wait.until(ExpectedConditions
					.invisibilityOfElementLocated(by));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementInvisible");
		}
		return flag;
	}

	/**
	 * @param by or XPATH
	 * @param time
	 * @return Element
	 * @throw Exception if element is not present after timeout
	 */
	public WebElement waitForElementPresent(By by, int time){
		WebElement we = null;
		try {
			WebDriverWait wait = new WebDriverWait(driver, (long) time);
			we = wait.until(ExpectedConditions.presenceOfElementLocated(by));
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			screenshot("Exception_waitForElementPresent");
			throw e;
		}
		return we;
	}

	@Override
	public void screenshot(String method) {
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			String dateandtime = Util.getCurrentDateAndTime();
			String fileName = "./screenshot/" + method + "_" + dateandtime+ ".png";

//			String fileName = ".\\screenshot\\" + method + "_" + dateandtime+ ".png";
			logger.info("save screenshot to: " + "screenshot/" + method + "_"
					+ dateandtime + ".png");
			FileUtils.copyFile(file, new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
	}

	// @Override
	// public void screenshot(WebElement we) {
	// File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
	// try {
	// String dir = Util.getCurrentDate();
	// String fileName = ".\\screenshot\\" + dir + "\\" + (we !=null?
	// we.getTagName():"null") + "_" + Instant.now().getEpochSecond() + ".png";
	// logger.info("save screenshot to " + fileName);
	// FileUtils.copyFile(file, new File(fileName));
	// } catch (IOException e){
	// e.printStackTrace();
	// logger.debug(e.getMessage());
	// }
	// }

	public boolean weIsDisplayed(WebElement we) {
		boolean flag = false;
		try {
			flag = we.isDisplayed();
			if (!flag) {
				logger.debug(" web element is not displayed: " + we);
				screenshot("Exception_weIsDisplayed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
			screenshot("Exception_weIsDisplayed");
		}
		return flag;
	}

	/**
	 *
	 * @param wes
	 * @return
	 */
	public boolean wesIsDisplayed(List<WebElement> wes) {
		boolean flag = true;
		for (WebElement we : wes) {
			flag = weIsDisplayed(we);
		}
		return flag;
	}

	/**
	 * To locate one button by its text shown on it
	 * 
	 * @param textOnButton
	 *            , the text shown on the button
	 * @param idx
	 *            , the index of one array of buttons; if idx < 0, then locate
	 *            the |idx| to last one.
	 * @return WebElement
	 */
	public WebElement getButtonViaTxt(String textOnButton, int idx) {
		List<WebElement> lButtons;
		String strXPath;
		WebElement we = null;
		int idxN = 0;

		strXPath = "//span[starts-with(., '" + textOnButton + "')]";
		lButtons = findElements(strXPath, LocatorType.XPATH);

		logger.info("Buttons (" + textOnButton + ") size = " + lButtons.size());

		if (lButtons.size() > 0) {
			if (idx < 0) { // fetch the item from count the last
				idxN = lButtons.size() + idx;
				if (idxN < 0) // in case overflow
					idxN = 0;
				logger.info("Buttons (" + textOnButton + ") idx = " + idx
						+ "; Current idxN = " + idxN);
			} else
				idxN = idx;

			we = lButtons.get(idxN);
		} else
			we = null;

		return we;
	}
}

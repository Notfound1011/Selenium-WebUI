## WebUI自动化框架搭建

> java+selenium+testng+reportng+log4j+jenkins

### 一、设计思路

PO模式

- 对象库层：二次封装Selenium的方法。    core：主要封装driver方法，并加入日志、失败截图
- 页面操作层(逻辑层)：元素对象和元素操作的封装。page：封装页面的元素对象和元素操作
- 业务层：测试用例的操作部分。 test: 业务测试，使用testng框架执行测试用例



### 二、代码层级结构

```
代码层级如下：
.
├── README.md
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── selenium
│   │   │           └── webui
│   │   │               ├── Browser.java       浏览器类型枚举
│   │   │               ├── LocatorType.java   定位方式类型枚举
│   │   │               ├── core
│   │   │               │   ├── Config.java    加载环境配置文件，设置浏览器driver驱动参数
│   │   │               │   ├── DriverManager.java   driver能力封装(点击、滚动、跳转、输入、元素定位查找检查、实现失败处理接口、异常截图等)
│   │   │               │   ├── FailureAction.java   失败处理接口--截图功能 
│   │   │               │   └── WebDriverFactory.java  按浏览器类型创建driver
│   │   │               ├── page
│   │   │               │   ├── HomeSearchPage.java  页面类：继承page，定义页面元素、方法
│   │   │               │   ├── Page.java   基础页面：加载PO，页面元素初始化
│   │   │               │   └── ReturnValue.java  
│   │   │               └── utils
│   │   │                   ├── Constants.java
│   │   │                   ├── FileManager.java   文件处理：查找文件是否存在，删除文件
│   │   │                   ├── JsonFileParser.java  json文件处理
│   │   │                   ├── ResourcePathParser.java 读取path
│   │   │                   ├── SshCmd.java   执行ssh，并执行命令
│   │   │                   └── Util.java   工具类：加载配置文件、日期获取、等待、js脚本
│   │   └── resources
│   │       ├── application.properties  配置文件
│   │       ├── log4j.properties  log4j配置文件
│   │       ├── testng.xml  测试用例集
│   │       └── webdriver   
│   │           ├── IEDriverServer.exe
│   │           ├── chromedriver
│   │           ├── chromedriver.exe
│   │           ├── geckodriver
│   │           ├── geckodriver_0.11.0.exe
│   │           └── geckodriver_0.17.0.exe
│   └── test
│       └── java
│           └── com
│               └── selenium
│                   └── webui
│                       ├── TestPage.java  基础页面测试类：BeforeClass/AfterClass操作
│                       └── page
│                           └── TestHomeSearchPage.java  测试页面类继承TestPage：具体测试用例
```



### 三、核心功能实现

1.工具类：

```java
// Load properties: application.properties.
    public static Properties loadProperties(String path){
        try{
            InputStream is = Util.class.getClassLoader().getResourceAsStream(path);
            if (is != null){
                properties.load(is);
            }
        } catch (IOException e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return properties;
    }

    public static String getProperty(String path, String pName){
        Properties properties = loadProperties(path);
        return (String)properties.get(pName);
    }
```

2.对象库层

```java
// selenium的二次封装
		//选择浏览器，创建driver
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
```

```
	//DriverManager.java
	// driver Capabilities
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
```



3.逻辑层

```java
// PageFactory模式
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

public class HomeSearchPage extends Page {

	private static final String url = "login";
	private static final Logger logger = Logger.getLogger(HomeSearchPage.class);

	@FindBy(name = "wd")
	private WebElement keyword;

	@FindBy(id = "su")
	private WebElement baiduSearch;

	private WebElement baiduLogo;
```

定义页面元素，并在类加载时调用initElements方法实现元素初始化

4.业务层

```
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
```

参数化：testng的@Parameters读取testng.xml中参数



### 四、测试用例执行和测试报告

```
         // pom.xml
					<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.20</version>
                <configuration>
                    <runOrder>alphabetical</runOrder>
                    <suiteXmlFiles>
                        <suiteXmlFile>src/main/resources/testng.xml</suiteXmlFile>
                    </suiteXmlFiles>
                    <properties>
                        <property>
                            <name>usedefaultlisteners</name>
                            <value>false</value>
                        </property>
                        <property>
                            <name>listener</name>
                            <value>org.uncommons.reportng.HTMLReporter,org.uncommons.reportng.JUnitXMLReporter</value>
                        </property>
                    </properties>
                    <forkMode>once</forkMode>

                </configuration>
            </plugin>
            
    // testng.xml        
    <listeners>
        <listener class-name="org.uncommons.reportng.HTMLReporter"/>
        <listener class-name="org.uncommons.reportng.JUnitXMLReporter"/>
    </listeners>
    <classes>
        <class name="com.selenium.webui.page.TestHomeSearchPage">
            <methods>
                <include name="testSearch"/>
                <include name="testClick"/>
            </methods>
        </class>
    </classes>
    <test name ="Teardown Test" preserve-order="true" >
        <classes>
            <class name = "com.selenium.webui.TestPage">
                <methods>
                    <include name="shutDown"/>
                </methods>
            </class>
        </classes>
    </test>

    <parameter name="host" value="www.baidu.com" />
    <parameter name="browser" value="chrome"/>
    <parameter name="words" value="selenium"/>
```

pom.xml中加入插件，使用mvn install clean运行testng.xml中的测试用例

报告目录：test-output/html/index.html

![image-20210209182527553](/Users/shiyuyu/Library/Application Support/typora-user-images/image-20210209182527553.png)

### 五、持续集成

1.Jenkins安装插件：TestNG Results Plugin

2.创建job运行脚本

“构建后操作”->“Publish TestNG Results”

3.查看报告

![image-20210209183223161](/Users/shiyuyu/Library/Application Support/typora-user-images/image-20210209183223161.png)


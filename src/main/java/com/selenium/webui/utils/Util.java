package com.selenium.webui.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.selenium.webui.core.Config;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shiyuyu
 */
public class Util {

    private static Properties properties = new Properties();
    private static final Logger logger = Logger.getLogger(Util.class);
    private static String INPUT_STREAM = "INPUTSTREAM";  
    private static String ERROR_STREAM = "ERRORSTREAM"; 
   
    
    /**
     * ExcuteJs
     * @param jsString 
     * example : String jsString = "return document.getElementById('manage').value;";
     * @param driver the diver
     * @return
     */
	public static Object excuteJs(String jsString, WebDriver driver) {
		logger.info("Execute JavaScript to get return value");
		JavascriptExecutor jsdriver = (JavascriptExecutor) driver;
		Object jloaded = null;
		String needReturn = "";
		if (!jsString.toLowerCase().trim().startsWith("return")
				&& jsString.indexOf(";") == -1) {
			needReturn = "return ";
		}
		jloaded = jsdriver.executeScript("return (function(){ " + needReturn
				+ jsString + "})()");
		if (jloaded != null) {
			if (jloaded.getClass().getName()
					.equals("org.openqa.selenium.remote.RemoteWebElement")
					|| jloaded.getClass().getName()
							.equals("java.util.ArrayList")) {
				// JS executed result return one or more elements
				return jloaded;
			} else {
				// JS executed return type is not element
				return jloaded;
			}
		}
		return null;
	}
    
    
    
    /**
     * Load properties properties.
     *
     * @param path the path to application.properties
     * @return the Properties instance
     */
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

    public static String getCurrentMethodName(){
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        logger.debug("current method name is " + ste[2].getMethodName());
        return ste[2].getMethodName();
    }

    public static String getCurrentDate(String fmt){
        SimpleDateFormat df = new SimpleDateFormat(fmt);
        return df.format(new Date());
    }

    public static String getCurrentDate(){
        return getCurrentDate(getProperty(Config.getPropertyPath(), "com.date.format"));
    }
    
    public static String getCurrentDateAndTime(){
    	return getCurrentDate(getProperty(Config.getPropertyPath(), "com.datetime.format"));
    }

    public static void sleep(long milliseconds, String reason) {
        logger.info("Preparing to sleep for: " + reason);
        sleep(milliseconds);
    }

    public static void sleep(long milliseconds) {
        logger.info("Sleeping " + milliseconds + "ms");
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
    }
    
    public class StreamGobbler extends Thread {  
        InputStream is;
        String type;
      
        StreamGobbler(InputStream is, String type) {  
            this.is = is;
            this.type = type;  
        }

        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null)
                    System.out.println(type + ">" + line);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * @param command  windows exec cmd.exe
     * @return String
     */
	public static String exec(String command)  {
		logger.info("use " + command+ " command");
		String result = null;

		try {
			String s = null;

			Process process = null;
			Runtime runtime = Runtime.getRuntime();
			process = runtime.exec("cmd.exe /C " + command);
			process = runtime.exec("cmd.exe /C echo %errorlevel% " );

			 BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			 s = stdInput.readLine();

			 result =s;
		} catch (IOException e) {
			System.out.println("error: ");
		   e.printStackTrace();
			logger.info("IOException " + e.getMessage());
			result = e.getMessage();

			System.out.println(result);

		}

	   return result;

	}

	public  static  String sshLinuxAndGet(String host, String cmd, String user, String pass,String contains) {
		String s = null;

		try {
			JSch jsch = new JSch();

			Session session = jsch.getSession(user, host);

			session.setConfig("StrictHostKeyChecking", "no");

			session.setPassword(pass);

			session.connect();

			logger.debug("host:"+session.getHost()+" account:" + session.getUserName()+ "connection status: " + session.isConnected());

			ChannelExec channelExec = (ChannelExec) session.openChannel("exec");

			channelExec.setCommand(cmd);
			channelExec.connect();
			InputStream in = channelExec.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
			String buf = null;
			StringBuffer sb = new StringBuffer();
			while ((buf = reader.readLine()) != null) {
				sb.append(buf);
				s=buf;

				if(s.contains(contains)){
					logger.debug(s);
					break;
				}
			}

			reader.close();
			channelExec.disconnect();
			session.disconnect();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static boolean ping(String ipAddress) {
		BufferedReader in = null;
		Runtime r = Runtime.getRuntime();  // 将要执行的ping命令,此命令是windows格式的命令
		String pingCommand = "ping " + ipAddress;
		try {   // 执行命令并获取输出
			System.out.println(pingCommand);
			Process p = r.exec(pingCommand);
			if (p == null) {
				return false;
			}
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));   // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
			int connectedCount = 0;
			String line = null;
			while ((line = in.readLine()) != null) {
				logger.debug(line);
				connectedCount += getCheckResult(line);
			}   // 如果出现类似=23ms TTL=62这样的字样,出现的次数=测试次数则返回真
			return !(connectedCount == 0);
		} catch (Exception ex) {
			ex.printStackTrace();   // 出现异常则返回假
			return false;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
	private static int getCheckResult(String line) {  // System.out.println("控制台输出的结果为:"+line);
		Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()) {
			return 1;
		}
		return 0;
	}

}

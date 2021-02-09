package com.selenium.webui.utils;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Properties;


/**
 * @author shiyuyu
 * @date 2021/2/9 11:46 上午
 */
public class FileManager {

	private static final Logger logger = Logger
			.getLogger(FileManager.class);

	public static String getDefaultDownloadsPath(String os) {
		Properties prop = System.getProperties();
		switch (os) {
			case "win":

				return "C:\\Users\\" + prop.getProperty("user.name")
						+ "\\Downloads\\";
			case "mac":
				return "/Users/"+prop.getProperty("user.name")+"/Downloads";
			default:
				return null;
		}
	}

	/**this is for default path search, it is your browser default downloads
	 * path
	 * @param matchType that string match type that your file satisfied
	 * @param matchString is the key string that your file has
	 */
	public static void deleteFilesFromWin(String matchType, String matchString) {
		deleteFilesFrom(getDefaultDownloadsPath("win"), matchType, matchString);
	}

	public static void deleteFilesFromMac(String matchType, String matchString) {
		deleteFilesFrom(getDefaultDownloadsPath("mac"), matchType, matchString);
	}

	/**
	 * @param filePath Path
	 * @param matchType
	 * @param matchString
	 */
	public static void deleteFilesFrom(String filePath, String matchType, String matchString) {
		File file = new File(filePath);
		File[] files = file.listFiles();
		logger.debug("Start to delete file under: " + filePath);
		for (int i = 0; i < files.length; i++) {
			boolean bFileMatch = false;
			switch (matchType) {
			case "equals":
				bFileMatch = files[i].getName().equals(matchString);
				break;
			case "startwith":
				bFileMatch = files[i].getName().startsWith(matchString);
				break;
			case "endwith":
				bFileMatch = files[i].getName().endsWith(matchString);
				break;
			case "contains":
			default:
				bFileMatch = files[i].getName().contains(matchString);
				break;
			}
			if (files[i].isFile() && bFileMatch) {
				logger.debug(files[i].getName() + " found, going to delete it");
				try{
					if (files[i].delete()) {
						logger.debug("delete file success....");
					} else
						logger.debug("delete file fail...");
				}catch (Exception e){
					e.printStackTrace();
					logger.info(e.getMessage());
					throw e;
				}
			}
		}
	}
	
	/**
	 * @param matchType
	 * @param matchString
	 * @return if file exist in default downloads path
	 */
	public static boolean checkFileExist(String os,String matchType, String matchString){
		return checkFileExist(os,getDefaultDownloadsPath(os), matchType, matchString);
	}

	/**
	 * @param filePath
	 * @param matchType
	 * @param matchString
	 * @return if there is one match file exist, it will return true immediately
	 */
	public static boolean checkFileExist(String os,String filePath, String matchType, String matchString) {
		logger.debug(Constants.REG+"选择操作系统:"+os+Constants.REG);
		boolean bFileExist = false;
		File file = new File(filePath);
		File[] files = file.listFiles();
		for (int i = 0; i < files.length; i++) {
			boolean bFileMatch = false;
			switch (matchType) {
			case "equals":
				bFileMatch = files[i].getName().equals(matchString);
				break;
			case "startswith":
				bFileMatch = files[i].getName().startsWith(matchString);
				break;
			case "endswith":
				bFileMatch = files[i].getName().endsWith(matchString);
				break;
			case "contains":
			default:
				bFileMatch = files[i].getName().contains(matchString);
				break;
			}
			if (files[i].isFile() && bFileMatch) {
				logger.debug(files[i].getName() + " found");
				bFileExist = true;
				return bFileExist;
			}
		}
		return bFileExist;
	}
}

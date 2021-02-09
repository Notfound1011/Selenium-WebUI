package com.selenium.webui.utils;

import org.apache.log4j.Logger;

import java.net.URLClassLoader;

/**
 * ResourcePathParser is used to find resource files' absolute path
 * by parsing fileName relatively. After building all resource files
 * would be archived into target-&;gt classes-&;gt mock_data directory.
 * To get resources' absolute path, the only way is to concat classes
 * directory with relative file path together.
 *
 * The fileName format should be: classpath:mock_data/xxxxxx/xxxxx
 */
public class ResourcePathParser {

    private static final Logger logger = Logger.getLogger(ResourcePathParser.class);
    public static String resourcePath(String fileName) {
        if (!fileName.startsWith("classpath:")) {
//            logger.debug("fileName is not starting with classpath:" + fileName);
            return fileName;
        }
        Class clz = ResourcePathParser.class;
        URLClassLoader loader = (URLClassLoader)clz.getClassLoader();
        return loader.getResource(fileName.substring(10)).getFile().substring(1);
    }
}

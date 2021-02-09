package com.selenium.webui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author shiyuyu
 * @describe: Convert data in file to JSONObject/JSONArray
 * @date 2021/2/9 1:20 下午
 */


public class JsonFileParser {
    private static final Logger logger = Logger.getLogger(JsonFileParser.class);

    @SuppressWarnings("unchecked")
    public static <T> T parse(String filePath, Class<T> clz) {
        Object data = clz.equals(JsonObject.class)?new JsonObject():new JsonArray();
        try {
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(new FileReader(filePath));
            data = clz.equals(JsonObject.class)?jsonElement.getAsJsonObject():
            jsonElement.getAsJsonArray();
        } catch (FileNotFoundException e) {
            logger.debug(e.getMessage());
        }
        return (T)data;
    }

    public static JSONObject getJsonObject(String filePath) {
        filePath = ResourcePathParser.resourcePath(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONObject data = null;
        try {
            data = new JSONObject(jsonParser.parse(new FileReader(filePath)).toString());
        } catch (IOException | ParseException e) {
            logger.debug(e.getMessage());
        }
        return data;
    }

    public static JSONArray getJsonArray(String filePath) {
        filePath = ResourcePathParser.resourcePath(filePath);
        JSONParser jsonParser = new JSONParser();
        JSONArray data = null;
        try {
            data = new JSONArray(jsonParser.parse(new FileReader(filePath)).toString());
        } catch (IOException | ParseException e) {
            logger.debug(e.getMessage());
        }
        return data;
    }

    public static String jsonCompare(String beforeJsonString, String afterJsonString){
        ObjectMapper jackson = new ObjectMapper();
        String diffs = null;
        try{
            JsonNode beforeNode = jackson.readTree(beforeJsonString);
            JsonNode afterNode = jackson.readTree(afterJsonString);
            logger.info("before node is :"+ beforeNode);
            logger.info("after node is :" + afterNode);
            JsonNode patch = JsonDiff.asJson(beforeNode, afterNode);
            diffs = patch.toString();
            logger.info("diffs is :" + diffs);
        } catch (IOException e){
            logger.debug(e.getMessage());
        }
        return diffs;
    }

}

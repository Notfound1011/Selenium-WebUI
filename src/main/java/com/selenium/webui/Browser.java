package com.selenium.webui;

import java.util.HashMap;
import java.util.Map;

/**
 * the type of browser
 */
public enum Browser {
    CHROME("chrome"),
    FIREFOX("firefox"),
    IE("ie"),
    EDGE("edge");

    private static final Map<String, Browser> map = new HashMap<>();
    static {
        for (Browser browser : Browser.values()) {
            map.put(browser.name, browser);
        }
    }
    private String name;

    private Browser(String name) {
        this.name = name;
    }

    public static Browser browserOf(String name) {
        return map.getOrDefault(name, CHROME);
    }
}

package com.selenium.webui;

/**
 * the type of locator
 */

public enum LocatorType {
    ID(1, "By.id"),
    CSS(2, "By.cssSelector"),
    XPATH(3, "By.xpath"),
    CLASS_NAME(4, "By.className"),
    TAG_NAME(5, "By.tagName"),
    NAME(6, "By.name"),
    LINK_TEXT(7, "By.linkText"),
    PARTIAL_LINK_TEXT(8, "By.partialLinkText");

    private int value;
    private String prompt;

    private LocatorType(int value, String prompt){
        this.value = value;
        this.prompt = prompt;
    }
}

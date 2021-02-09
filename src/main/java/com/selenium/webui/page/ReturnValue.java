package com.selenium.webui.page;

/**
 * @author shiyuyu
 */
public class ReturnValue {
    private Integer error;
    private String message;
    private Page page;

   public ReturnValue(){}

    private ReturnValue(Page page) {
        this.page = page;
        this.error = page.healthCheck()?0:-1;
        this.message = this.error == 0?"success":"failed";
    }

    public static ReturnValue ofPage(Page page) {
        return new ReturnValue(page);
    }

    public Integer getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}


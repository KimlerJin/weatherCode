package com.example.demo;
/**
 * @author: Kimler Jin
 * Date: 2021/07/17 19:00
 * Content: DEMO
 */
public class WeatherException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    protected Object[] params;
    private String errorCode;

    public WeatherException(Exception ex) {
        super(ex);
    }

    public WeatherException(String errorMessage) {
        super(errorMessage);
    }

    public WeatherException(Throwable cause) {
        super(cause);
    }

    public WeatherException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

    public WeatherException(String errorCode, String errorMessage, Object... params) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.params = params;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getParams() {
        return this.params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}

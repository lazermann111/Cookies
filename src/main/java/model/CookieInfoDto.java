package model;

public class CookieInfoDto {

    public CookieInfoDto(){}


    private int id;
    private String proxy;
    private String cookie;
    private String userAgent;


    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getProxy() {
        return proxy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }


}

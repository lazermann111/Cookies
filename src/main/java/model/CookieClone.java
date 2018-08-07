package model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CookieClone implements Serializable {

    private  String name;
    private  String value;
    private  String path;
    private  String domain;
    private  Date expiry;


    private  boolean isSecure;
    private  boolean isHttpOnly;

    @JsonCreator
    public CookieClone(){}

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getDomain() {
        return this.domain;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isSecure() {
        return this.isSecure;
    }


    public boolean isHttpOnly() {
        return this.isHttpOnly;
    }

    public Date getExpiry() {
        return this.expiry;
    }

    private static String stripPort(String domain) {
        return domain == null?null:domain.split(":")[0];
    }

    public void validate() {
        if(this.name != null && !"".equals(this.name) && this.value != null && this.path != null) {
            if(this.name.indexOf(59) != -1) {
                throw new IllegalArgumentException("Cookie names cannot contain a ';': " + this.name);
            } else if(this.domain != null && this.domain.contains(":")) {
                throw new IllegalArgumentException("Domain should not contain a port: " + this.domain);
            }
        } else {
            throw new IllegalArgumentException("Required attributes are not set or any non-null attribute set to null");
        }
    }

    public String toString() {
        return this.name + "=" + this.value + (this.expiry == null?"":"; expires=" + (new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z")).format(this.expiry)) + ("".equals(this.path)?"":"; path=" + this.path) + (this.domain == null?"":"; domain=" + this.domain) + (this.isSecure?";secure;":"");
    }



    public int hashCode() {
        return this.name.hashCode();
    }

}
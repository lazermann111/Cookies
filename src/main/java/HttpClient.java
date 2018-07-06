import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

import java.io.IOException;

public class HttpClient
{

    public static String BASE_URL = "https://coffee-shop-test.herokuapp.com/";


    static
    {
        Unirest.setConcurrency(10,10);


        com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        jacksonObjectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Unirest.setObjectMapper(new ObjectMapper() {
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }


    public static void main(String[] args) {

        CookieInfoDto d = new CookieInfoDto();
        d.setProxy("111.1.1.1:111");
        d.setCookie("sdsdsdsdsd");

        HttpClient.addNewCookie(d);
    }

    public static void addNewCookie(CookieInfoDto cookieInfoDto)
    {

        try
        {
            Unirest.post(BASE_URL + "/cookies/addCookie")
                    .field("proxy", cookieInfoDto.getProxy())
                    .field("cookie", cookieInfoDto.getCookie())
            .asString();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            AppiumLogger.log("addNewCookie exception: " + e.getMessage());
        }


    }
}

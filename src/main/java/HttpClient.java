import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

import java.io.IOException;
import java.util.Set;

public class HttpClient
{

    public static String BASE_URL = "https://coffee-shop-test.herokuapp.com/";
    //public static String BASE_URL = "http://localhost:8081/";


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

    public static void main(String[] args) {



        CookieInfoDto a =  HttpClient.getCookie("217.23.3.169:20031");
        CookieInfoDto b =  HttpClient.getCookie("217.23.3.169:20030");
        CookieInfoDto c =  HttpClient.getCookie("1111.3453.345.169:3545345");


        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();



        try {

            CollectionType javaType = objectMapper.getTypeFactory().constructCollectionType(Set.class, CookieClone.class);
            Set<CookieClone> asList = objectMapper.readValue(a.getCookie(), javaType);
            // readValue = objectMapper.readValue(, Set.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static CookieInfoDto getCookie(String proxy)
    {

        try
        {
          return    Unirest.get(BASE_URL + "/cookies/getCookie?proxy={proxy}")
                    .routeParam("proxy", proxy)
                    .asObject(CookieInfoDto.class)
                    .getBody();
        }

        catch (Exception e)
        {
            //e.printStackTrace();
            AppiumLogger.log("getCookie exception: " + e.getMessage());
            return null;
        }


    }
}

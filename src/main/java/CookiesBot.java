import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CookiesBot
{

    private static int THREAD_COUNT = 1;

    static int proxyBasePort= 20000;
    String proxyUrl = "217.23.3.169:";


    public static void main(String[] args) throws Exception {

        int threadsToLaunch =THREAD_COUNT;

        if(args.length > 0)
        {
            threadsToLaunch = Integer.parseInt(args[0]);
            proxyBasePort = Integer.parseInt(args[1]);

            AppiumLogger.log("Launching " + threadsToLaunch + " threads from " + proxyBasePort + " port");
        }

        ExecutorService s = Executors.newFixedThreadPool(threadsToLaunch);

        for(int i =0; i< threadsToLaunch; i++)
        {
            int finalI = i;
            s.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        new CookiesBot().init(null, finalI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            Thread.sleep(2000);
        }
    }


      WebDriver driver = null;


    public CookiesBot init(String url, int instanceNumber )throws AWTException, Exception {

        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments( "--start-maximized" );


        proxyBasePort+=instanceNumber;
        options.addArguments("--proxy-server=socks5://" + proxyUrl+proxyBasePort);


        driver = new ChromeDriver(options);
        //driver.get("https://www.whatismyip.com/"); //todo check if proxy works from your ip

        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get("https://youtube.com");
        Thread.sleep(3000);


        Set<Cookie> cookies = driver.manage().getCookies();

        ObjectMapper objectMapper = new ObjectMapper();


        String s = objectMapper.writeValueAsString(cookies);


        //todo check it its changing time for specific proxy
        CookieInfoDto d = new CookieInfoDto();
        d.setProxy(proxyUrl+proxyBasePort);
        d.setCookie(s);


        HttpClient.addNewCookie(d);

        youTubeActions();

        return this;
    }

    private void youTubeActions()
    {

       driver.findElement(By.className("ytd-thumbnail")).click();

        //todo add rnd video watches, maybe skipping


    }




}

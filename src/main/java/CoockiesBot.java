import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CoockiesBot
{

    private static int THREAD_COUNT = 3;


    public static void main(String[] args) throws Exception {


        ExecutorService s = Executors.newFixedThreadPool(THREAD_COUNT);

        for(int i =0; i< THREAD_COUNT; i++)
        {
            int finalI = i;
            s.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        new CoockiesBot().init(null, finalI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            Thread.sleep(2000);
        }
    }

     int proxyBasePort= 20000;
     String proxyUrl = "217.23.3.169:";

    public CoockiesBot init(String url, int instanceNumber )throws AWTException, Exception {
        //  if (os == "Windows") {
        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");

        WebDriver driver = null;
      //  JavascriptExecutor jse = (JavascriptExecutor)driver;

        //  if (browser_ == "Chrome") {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments( "--start-maximized" );


        proxyBasePort+=instanceNumber;
        options.addArguments("--proxy-server=socks5://" + proxyUrl+proxyBasePort);


        driver = new ChromeDriver(options);


        driver.get("https://www.whatismyip.com/");
        //driver.get("https://youtube.com");
        Thread.sleep(10000);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);




        driver.manage().getCookies();
        driver.close();

        return this;
    }




}

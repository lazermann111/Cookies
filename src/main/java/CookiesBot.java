import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.awt.*;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CookiesBot
{

    private static int THREAD_COUNT = 2;
    private static int TIMEOUT_FOR_ONE_DRIVER = 200000;
    private static int LAUNCH_INTERVAL = 5000;

    static int proxyBasePort= 20000;
    static int instanceNumber =0;
    String proxyUrl = "217.23.3.169:";
   // int instanceNumber;
    WebDriver driver = null;

    public static void main(String[] args) throws Exception {

        int threadsToLaunch =THREAD_COUNT;

        if(args.length > 0)
        {
            threadsToLaunch = Integer.parseInt(args[0]);
            proxyBasePort = Integer.parseInt(args[1]);
            TIMEOUT_FOR_ONE_DRIVER = Integer.parseInt(args[2]);
            LAUNCH_INTERVAL = Integer.parseInt(args[3]);

            AppiumLogger.log("Launching " + threadsToLaunch + " threads from " + proxyBasePort + " port");
            AppiumLogger.log("Launching " + TIMEOUT_FOR_ONE_DRIVER + " TIMEOUT_FOR_ONE_DRIVER and " + LAUNCH_INTERVAL + " LAUNCH_INTERVAL");
        }

        ExecutorService s = Executors.newFixedThreadPool(threadsToLaunch);

        for(int i =0; i< threadsToLaunch; i++)
        {
           // int finalI = i;
            //s.submit(new Runnable() {
                //@Override
              //  public void run() {


            int finalI = i;
            TimerTask tasknew = new TimerTask()
                    {
                        @Override
                        public void run() {

                            CookiesBot c = null;
                            try
                            {
                              c = new CookiesBot();
                                c.init(null);
                                Thread.sleep(TIMEOUT_FOR_ONE_DRIVER);

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                AppiumLogger.log(e.getMessage());
                            }
                            finally {
                                if(c!=null) c.quit();
                            }
                        }
                    };
                    Timer timer = new Timer();

                    // scheduling the task at fixed rate delay
                    timer.scheduleAtFixedRate(tasknew,500,TIMEOUT_FOR_ONE_DRIVER + 10000);
            Thread.sleep(LAUNCH_INTERVAL);
         }


           // });


        }






    public CookiesBot init(String url )throws AWTException, Exception {

        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
        instanceNumber++;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments( "--start-maximized" );

        System.out.println("instanceNumber " + instanceNumber);
        int proxyPort =  proxyBasePort+instanceNumber;
        options.addArguments("--proxy-server=socks5://" + proxyUrl+proxyPort);

        CookieInfoDto dto =  HttpClient.getCookie(proxyUrl+proxyPort);
        if(dto != null && dto.getCookie() != null && !dto.getCookie().isEmpty())
        {
            AppiumLogger.log("We already have cookie for "+proxyUrl+proxyPort + ", using it");
        }


        driver = new ChromeDriver(options);
        //driver.get("https://www.whatismyip.com/"); //todo check if proxy works from your ip

        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);

        driver.get("https://youtube.com");
        Thread.sleep(30000);


        Set<Cookie> cookies = driver.manage().getCookies();

        ObjectMapper objectMapper = new ObjectMapper();


        String s = objectMapper.writeValueAsString(cookies);


        //todo check it its changing time for specific proxy
        CookieInfoDto d = new CookieInfoDto();
        d.setProxy(proxyUrl+proxyPort);
        d.setCookie(s);


        HttpClient.addNewCookie(d);

        youTubeActions();

        return this;
    }

    public void quit()
    {
        driver.close();
    }

    private void youTubeActions()
    {

       driver.findElement(By.className("ytd-thumbnail")).click();

        //todo add rnd video watches, maybe skipping


    }




}

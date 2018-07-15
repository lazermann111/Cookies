import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CookiesBot
{

    private static int THREAD_COUNT = 15;
    private static int TIMEOUT_FOR_ONE_DRIVER = 200000;
    private static int LAUNCH_INTERVAL = 5000;

    static int proxyBasePort= 20000;
    static int instanceNumber =0;
    String proxyUrl = "217.23.3.169:";
   // int instanceNumber;
    WebDriver driver = null;
    static Queue<UserAgent> userAgentsDesktop = new ArrayBlockingQueue<UserAgent>(25000);
    static Queue<UserAgent> userAgentsMobile = new ArrayBlockingQueue<UserAgent>(25000);

    public static void main(String[] args) throws Exception {

        int threadsToLaunch =THREAD_COUNT;

        readDesktop();
        readMobile();


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
    }

    public CookiesBot init(String url )throws Exception {

        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
        instanceNumber++;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
      //  options.addArguments( "--start-maximized" );

        UserAgent ua = userAgentsDesktop.poll();

        System.out.println("instanceNumber " + instanceNumber);
        int proxyPort =  proxyBasePort+instanceNumber;

        CookieInfoDto dto =  HttpClient.getCookie(proxyUrl+proxyPort);
        if(dto != null && dto.getCookie() != null && !dto.getCookie().isEmpty())
        {
            AppiumLogger.log("We already have cookie for "+proxyUrl+proxyPort + ", using it");
        }

        options.addArguments("--proxy-server=socks5://" + proxyUrl+proxyPort);
        options.addArguments("--user-agent=" + ua.getAgent());

        driver = new ChromeDriver(options);

        try {
            int w = Integer.parseInt(ua.getWidth()) ;
            int h = Integer.parseInt(ua.getHeight()) ;

            driver.manage().window().setSize(new Dimension(w,h));

        }catch (Exception e){
            e.printStackTrace();
            AppiumLogger.log("Error while parsing screen resolution! " + ua.getWidth() +" "+ ua.getHeight());
        }

       // driver.get("https://www.whatismyip.com/"); //todo check if proxy works from your ip

        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);

        driver.get("https://youtube.com");
        Thread.sleep(30000);




        //todo check it its changing time after time for specific proxy
        Set<Cookie> cookies = driver.manage().getCookies();

        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(cookies);
        String s2 = objectMapper.writeValueAsString(ua);

        CookieInfoDto d = new CookieInfoDto();
        d.setProxy(proxyUrl+proxyPort);
        d.setCookie(s);
        d.setUserAgent(s2);
        HttpClient.addNewCookie(d);




        youTubeActions();

        return this;
    }

    public void quit()
    {
        if(driver !=null)
        driver.close();
    }

    private void youTubeActions()
    {

       driver.findElement(By.className("ytd-thumbnail")).click();

        //todo add rnd video watches, maybe skipping


    }



    public static void readDesktop() {


        BufferedReader reader = null;
        try {
            InputStream in = CookiesBot.class.getResourceAsStream("/userAgents-pc.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\t");

                userAgentsDesktop.add(new UserAgent(row[0], row[2].substring(0, row[2].lastIndexOf("X") - 3), row[2].substring(row[2].lastIndexOf("X") + 2, row[2].length() - 2)));
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }

    }

    public static Queue  readMobile() {
        Queue resultList = new ArrayDeque();
        BufferedReader reader = null;
        try {
            InputStream in = CookiesBot.class.getResourceAsStream("/allmobileagents.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split("\t");
                resultList.add(row);

                userAgentsMobile.add(new UserAgent(row[0], row[2].substring(0, row[2].lastIndexOf("X") - 3), row[2].substring(row[2].lastIndexOf("X") + 2, row[2].length() - 2)));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }

}

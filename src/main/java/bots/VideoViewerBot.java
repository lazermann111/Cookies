package bots;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.CookieInfoDto;
import model.UserAgent;
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
import java.util.stream.Collectors;

public class VideoViewerBot
{

    private static int THREAD_COUNT = 2;
    private static int TIMEOUT_FOR_ONE_DRIVER = 200000;
    private static int LAUNCH_INTERVAL = 5000;
    private static int UA_TO_SKIP = 1000;

    static int proxyBasePort= 23000;
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
            UA_TO_SKIP = Integer.parseInt(args[4]);




            AppiumLogger.log("Launching " + threadsToLaunch + " threads from " + proxyBasePort + " port");
            AppiumLogger.log("Launching " + TIMEOUT_FOR_ONE_DRIVER + " TIMEOUT_FOR_ONE_DRIVER and " + LAUNCH_INTERVAL + " LAUNCH_INTERVAL");

            AppiumLogger.log("Skipping " + UA_TO_SKIP + " UA");


        }

        if(UA_TO_SKIP > 0)
        {
            List<UserAgent> userAgents = userAgentsDesktop.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            List<UserAgent> userAgents2 = userAgentsMobile.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            userAgentsDesktop.clear();
            userAgentsMobile.clear();
            userAgentsDesktop.addAll(userAgents);
            userAgentsMobile.addAll(userAgents2);
        }
        ExecutorService s = Executors.newFixedThreadPool(threadsToLaunch);

        for(int i =0; i< threadsToLaunch; i++)
        {
            TimerTask tasknew = new TimerTask()
                    {
                        @Override
                        public void run() {

                            VideoViewerBot c = null;
                            try
                            {
                              c = new VideoViewerBot();
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

    public VideoViewerBot init(String url )throws Exception {

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
            //todo
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
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get("https://youtube.com");
        Thread.sleep(30000);


      if(youTubeActions())
      {
          Set<Cookie> cookies = driver.manage().getCookies();

          ObjectMapper objectMapper = new ObjectMapper();
          String s = objectMapper.writeValueAsString(cookies);
          String s2 = objectMapper.writeValueAsString(ua);

          CookieInfoDto d = new CookieInfoDto();
          d.setProxy(proxyUrl+proxyPort);
          d.setCookie(s);
          d.setUserAgent(s2);
          HttpClient.addNewCookie(d);

      }

        return this;
    }

    public void quit()
    {
        if(driver !=null)
        driver.close();
    }

    private boolean youTubeActions()
    {

        boolean clicked = false;

       // driver.findElements(By.className("yt-lockup-thumbnail"))
        for (WebElement e : driver.findElements(By.className("yt-lockup-thumbnail")))
        {
            try
            {
                e.click();
                clicked = true;
                break;
            }
            catch (Exception e1){
                AppiumLogger.log("skipped0");}
        }
        if(!clicked)
        {
            for (WebElement e : driver.findElements(By.className("ytd-thumbnail"))) {
                try {
                    e.click();
                    clicked = true;
                    break;
                } catch (Exception e1) {
                    AppiumLogger.log("skipped1");
                }
            }
        }
            if(!clicked)
            {
                for (WebElement e : driver.findElements(By.className("video-thumbnail-img")))
             {
                  try
                  {
                      e.click();
                      clicked = true;
                      break;
                  }
                   catch (Exception e1){
                       AppiumLogger.log("skipped2");}
              }
            }

            if(!clicked) AppiumLogger.log("STILL NOT CLICKED!");

        return clicked;

    }



    public static void readDesktop() {


        BufferedReader reader = null;
        try {
            InputStream in = VideoViewerBot.class.getResourceAsStream("/userAgents-pc.txt");
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
            InputStream in = VideoViewerBot.class.getResourceAsStream("/allmobileagents.txt");
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

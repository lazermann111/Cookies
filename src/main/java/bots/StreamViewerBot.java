package bots;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StreamViewerBot
{

    private static int THREAD_COUNT = 2;
    private static String VIDEO_URL = null;
   // private static int TIMEOUT_FOR_ONE_DRIVER = 200000;
    private static int LAUNCH_INTERVAL = 10000;
    //private static int UA_TO_SKIP = 1000;

   // static int proxyBasePort= 23000;
  //  static int instanceNumber =0;
   // String proxyUrl = "217.23.3.169:";
   // int instanceNumber;
    WebDriver driver = null;
    static Queue<String> proxiesList = new ArrayBlockingQueue<String>(25000);
   // static Queue<model.UserAgent> userAgentsMobile = new ArrayBlockingQueue<model.UserAgent>(25000);

    public static void main(String[] args) throws Exception {

        int threadsToLaunch =THREAD_COUNT;


        readProxies();


        if(args.length > 0)
        {
            threadsToLaunch = Integer.parseInt(args[0]);
            VIDEO_URL = args[1];
           // proxyBasePort = Integer.parseInt(args[1]);
          //  TIMEOUT_FOR_ONE_DRIVER = Integer.parseInt(args[2]);
           // LAUNCH_INTERVAL = Integer.parseInt(args[3]);
           // UA_TO_SKIP = Integer.parseInt(args[4]);




            AppiumLogger.log("Launching " + threadsToLaunch + " threads ");
           // bots.AppiumLogger.log("Launching " + TIMEOUT_FOR_ONE_DRIVER + " TIMEOUT_FOR_ONE_DRIVER and " + LAUNCH_INTERVAL + " LAUNCH_INTERVAL");

          //  bots.AppiumLogger.log("Skipping " + UA_TO_SKIP + " UA");


        }

       // if(UA_TO_SKIP > 0)
        {
            /*List<model.UserAgent> userAgents = proxiesList.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            List<model.UserAgent> userAgents2 = userAgentsMobile.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            proxiesList.clear();
            userAgentsMobile.clear();
            proxiesList.addAll(userAgents);
            userAgentsMobile.addAll(userAgents2);*/
        }
        ExecutorService s = Executors.newFixedThreadPool(threadsToLaunch);

        for(int i =0; i< threadsToLaunch; i++)
        {
            s.submit(new Runnable()
            {
                @Override
                public void run() {

                    StreamViewerBot c = null;
                    try
                    {
                        c = new StreamViewerBot();
                        c.init(VIDEO_URL);
                        // Thread.sleep(TIMEOUT_FOR_ONE_DRIVER);

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
            });

                   // Timer timer = new Timer();
//
                    // scheduling the task at fixed rate delay
                  //  timer.scheduleAtFixedRate(tasknew,500,TIMEOUT_FOR_ONE_DRIVER + 10000);
            Thread.sleep(LAUNCH_INTERVAL);
         }
    }

    public StreamViewerBot init(String url )throws Exception {

        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
       // instanceNumber++;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
      //  options.addArguments( "--start-maximized" );

        String proxyUrl = proxiesList.poll();



        options.addArguments("--proxy-server=http://" + proxyUrl);
      //  options.addArguments("--user-agent=" + ua.getAgent());

        driver = new ChromeDriver(options);



       // driver.get("https://www.whatismyip.com/"); //todo check if proxy works from your ip

        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor)driver;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get(url);
        Thread.sleep(3000000);


      /*if(youTubeActions())
      {
          Set<Cookie> cookies = driver.manage().getCookies();

          ObjectMapper objectMapper = new ObjectMapper();
          String s = objectMapper.writeValueAsString(cookies);
          String s2 = objectMapper.writeValueAsString(ua);

          model.CookieInfoDto d = new model.CookieInfoDto();
          d.setProxy(proxyUrl+proxyPort);
          d.setCookie(s);
          d.setUserAgent(s2);
          bots.HttpClient.addNewCookie(d);

      }*/

        return this;
    }

    public void quit()
    {
        if(driver !=null)
        driver.close();
    }
/*
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
            catch (Exception e1){bots.AppiumLogger.log("skipped0");}
        }
        if(!clicked)
        {
            for (WebElement e : driver.findElements(By.className("ytd-thumbnail"))) {
                try {
                    e.click();
                    clicked = true;
                    break;
                } catch (Exception e1) {
                    bots.AppiumLogger.log("skipped1");
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
                   catch (Exception e1){bots.AppiumLogger.log("skipped2");}
              }
            }

            if(!clicked) bots.AppiumLogger.log("STILL NOT CLICKED!");

        return clicked;

    }

*/


    public static Queue readProxies() {
        Queue resultList = new ArrayDeque();
        BufferedReader reader = null;
        try {
            InputStream in = StreamViewerBot.class.getResourceAsStream("/proxylist.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                //String[] row = csvLine.split("\t");
              //  resultList.add(row);

                proxiesList.add(csvLine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }

}

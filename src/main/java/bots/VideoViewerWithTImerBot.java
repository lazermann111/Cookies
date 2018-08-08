package bots;

import model.CookieInfoDto;
import model.UserAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VideoViewerWithTImerBot {

    static int proxyBasePort = 23000;
    static int instanceNumber = 0;
    static Queue<UserAgent> userAgentsDesktop = new ArrayBlockingQueue<UserAgent>(25000);
    static Queue<UserAgent> userAgentsMobile = new ArrayBlockingQueue<UserAgent>(25000);
    private static int THREAD_COUNT = 1;
    //
    private static int TIMEOUT_FOR_ONE_DRIVER = 200000;
    private static int LAUNCH_INTERVAL = 5000;
    private static int UA_TO_SKIP = 1000;
    private static String videoId = "_iUCb5b3jlk";
    String proxyUrl = "217.23.3.169:";
    // int instanceNumber;
    WebDriver driver = null;

    public static void main(String[] args) throws Exception {

        int threadsToLaunch = THREAD_COUNT;

        readDesktop();
        readMobile();


        if (args.length > 0) {
            threadsToLaunch = Integer.parseInt(args[0]);
            //  proxyBasePort = Integer.parseInt(args[1]);
            //  TIMEOUT_FOR_ONE_DRIVER = Integer.parseInt(args[2]);
            LAUNCH_INTERVAL = Integer.parseInt(args[1]);
            //   UA_TO_SKIP = Integer.parseInt(args[3]);
            videoId = args[2];


            AppiumLogger.log("Launching " + threadsToLaunch + " threads from " + proxyBasePort + " port");
            AppiumLogger.log("Launching " + TIMEOUT_FOR_ONE_DRIVER + " TIMEOUT_FOR_ONE_DRIVER and " + LAUNCH_INTERVAL + " LAUNCH_INTERVAL");

            AppiumLogger.log("Skipping " + UA_TO_SKIP + " UA");


        }

        if (UA_TO_SKIP > 0) {
            List<UserAgent> userAgents = userAgentsDesktop.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            List<UserAgent> userAgents2 = userAgentsMobile.stream().skip(UA_TO_SKIP).collect(Collectors.toList());
            userAgentsDesktop.clear();
            userAgentsMobile.clear();
            userAgentsDesktop.addAll(userAgents);
            userAgentsMobile.addAll(userAgents2);
        }
        ExecutorService s = Executors.newFixedThreadPool(threadsToLaunch);
        TIMEOUT_FOR_ONE_DRIVER = loadYoutubeViewsCounter();
        for (int i = 0; i < threadsToLaunch; i++) {
            TimerTask tasknew = new TimerTask() {
                @Override
                public void run() {

                    VideoViewerWithTImerBot c = null;
                    try {
                        c = new VideoViewerWithTImerBot();
                        //todo pass video URL
                        c.init(videoId);

                        //todo parse video lenght and wait accordingly
                        Thread.sleep(TIMEOUT_FOR_ONE_DRIVER + 5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AppiumLogger.log(e.getMessage());
                    } finally {
                        if (c != null) c.quit();
                    }
                }
            };
            Timer timer = new Timer();

            // scheduling the task at fixed rate delay
            //todo parse video lenght and wait accordingly
            timer.scheduleAtFixedRate(tasknew, 500, TIMEOUT_FOR_ONE_DRIVER + 10000);
            Thread.sleep(LAUNCH_INTERVAL);
        }
    }

    private static int loadYoutubeViewsCounter() {
        try {
            URL url = new URL("https://m.youtube.com/watch?v=" + videoId);
            HttpsURLConnection uc = (HttpsURLConnection) url.openConnection();
            uc.connect();
            String line = null;
            StringBuffer tmp = new StringBuffer();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = in.readLine()) != null) {
                tmp.append(line);
                //     Log.e("doc", String.valueOf(line));
            }
            Document doc = Jsoup.parse(String.valueOf(tmp));

            Elements script = doc.select("script");

            Pattern viewsPattern = Pattern.compile("\"view_count\":\"(.+?)\"");
            Pattern videoLengthPattern = Pattern.compile("\"length_seconds\":\"(.+?)\"");

            Matcher viewsMatcher = viewsPattern.matcher(script.html());
            Matcher videoLengthMatcher = videoLengthPattern.matcher(script.html());

            viewsMatcher.find();
            videoLengthMatcher.find();

            final String views = viewsMatcher.group().substring(14, viewsMatcher.group().length() - 1);
            final String length = videoLengthMatcher.group().substring(18, videoLengthMatcher.group().length() - 1);

            System.out.println("video_length " + length);
            System.out.println("views " + views);

            return Integer.parseInt(length) * 1000;
            //startCounter(Integer.parseInt(length));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void readDesktop() {


        BufferedReader reader = null;
        try {
            InputStream in = VideoViewerWithTImerBot.class.getResourceAsStream("/userAgents-pc.txt");
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

    public static Queue readMobile() {
        Queue resultList = new ArrayDeque();
        BufferedReader reader = null;
        try {
            InputStream in = VideoViewerWithTImerBot.class.getResourceAsStream("/allmobileagents.txt");
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

    public VideoViewerWithTImerBot init(String url) throws Exception {

        System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
        instanceNumber++;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("ignore-certificate-errors");
        options.addArguments("--ignore-ssl-errors");
        options.addArguments("--no-referrers");
        //  options.addArguments( "--start-maximized" );

        UserAgent ua = userAgentsDesktop.poll();

        System.out.println("instanceNumber " + instanceNumber);
        int proxyPort = proxyBasePort + instanceNumber;

        CookieInfoDto dto = HttpClient.getCookie(proxyUrl + proxyPort);
        if (dto != null && dto.getCookie() != null && !dto.getCookie().isEmpty()) {
            //todo
            AppiumLogger.log("We already have cookie for " + proxyUrl + proxyPort + ", using it");
        }

        options.addArguments("--proxy-server=socks5://" + proxyUrl + proxyPort);
        options.addArguments("--user-agent=" + ua.getAgent());
        //todo add referrer
        //

        driver = new ChromeDriver(options);

        try {
            int w = Integer.parseInt(ua.getWidth());
            int h = Integer.parseInt(ua.getHeight());

            driver.manage().window().setSize(new Dimension(w, h));

        } catch (Exception e) {
            e.printStackTrace();
            AppiumLogger.log("Error while parsing screen resolution! " + ua.getWidth() + " " + ua.getHeight());
        }

        //driver.get("https://www.whatismyip.com/"); //todo check if proxy works from your ip

        Thread.sleep(2000);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get("https://youtube.com/watch?v=" + videoId);
        //todo parse video lenght and wait accordingly



     /* if(youTubeActions())
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

      }*/

        return this;
    }

    public void quit() {
        if (driver != null)
            driver.close();
    }

    private void startCounter(int timeInSeconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    init(videoId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, (timeInSeconds + 30) * 1000);
    }

}

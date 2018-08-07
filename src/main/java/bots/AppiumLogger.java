package bots;

import com.google.common.base.Throwables;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AppiumLogger
{

    static Logger fileLogger;

    static
    {

        fileLogger = Logger.getLogger("MyLog");
        FileHandler fh;
        try
        {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

            DateFormat df = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");
            String requiredDate = df.format(new Date());
            fh = new FileHandler(requiredDate);

            fileLogger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);



        }
        catch (SecurityException e)
        {
              AppiumLogger.logException(e);   e.printStackTrace();
        }
        catch (IOException e)
        {
              AppiumLogger.logException(e);   e.printStackTrace();
        }
    }

    public static void log(String tag, String message)
    {

        if (fileLogger != null)
        {
            fileLogger.log(Level.INFO, Thread.currentThread().getName() + ": "+ tag + ": " + message);
        }
    }
    public static void logException(Exception e)
    {
        String s = Throwables.getStackTraceAsString(e);
        log(s);
    }

    public static void log(String message)
    {


        if (fileLogger != null)
        {
            fileLogger.log(Level.INFO, Thread.currentThread().getName() + ": "+ message);
        }

    }



}

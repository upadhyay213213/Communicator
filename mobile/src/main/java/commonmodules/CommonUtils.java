package commonmodules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nupadhay on 9/20/2016.
 */
public class CommonUtils {


    public static String getTimeDifferance(String savedDateTime){

        DateTimeDifferance dateTimeDifferance = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        if(TimeZone.getDefault().getID().contains("Asia/Calcutta")){
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        try {

            Date oldDate = dateFormat.parse(savedDateTime);
            System.out.println(oldDate);

            Date currentDate = new Date();

            long diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            Calendar a = getCalendar(oldDate);
            Calendar b = getCalendar(currentDate);
            int diffYear = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
            int diffMonth=b.get(Calendar.MONTH)-a.get(Calendar.MONTH);

         dateTimeDifferance=new DateTimeDifferance(days,hours,minutes,diffMonth,seconds,diffYear);
            if (oldDate.before(currentDate)) {

                Log.e("oldDate", "is previous date");
                Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
                        + " hours: " + hours + " days: " + days);

            }

            // Log.e("toyBornTime", "" + toyBornTime);

        } catch (ParseException e) {

            e.printStackTrace();
        }
        return  getTimeAgoMessage(dateTimeDifferance);

    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTime(date);

        return cal;
    }

    public static String getTimeAgoMessage(DateTimeDifferance components){
        if( components.year > 0) {
            if (components.year < 2 ){
                return "Last year";
            } else {
                return components.year+" years ago";
            }
        }

        if (components.month > 0) {
            if(components.month < 2){
                return "Last Month";
            } else {
                return components.month+" months ago";
            }
        }

        // TODO: localize for other calanders
        if(components.days >= 7) {
            int week = (int)components.days/7;
            if (week < 2) {
                return "Last week";
            } else {
                return week+" weeks ago";
            }
        }

        if (components.days > 0) {
            if(components.days < 2) {
                return "Yesterday";
            } else  {
                return components.days+" days ago";
            }
        }

        if (components.hours > 0) {
            if(components.hours < 2) {
                return "An hour ago";
            } else  {
                return components.hours+" hours ago";
            }
        }

        if (components.minutes > 0) {
            if (components.minutes < 2) {
                return components.minutes+" A minute ago";
            } else {
                return components.minutes+" minutes ago";
            }
        }

        if (components.seconds > 0) {
            if (components.seconds < 5) {
                return "Just Now";
            } else {
                return components.seconds+" seconds ago";
            }
        }

        return "";
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}

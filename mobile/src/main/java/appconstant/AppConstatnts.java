package appconstant;

import appmanager.AppController;
import apputils.PrefrensUtils;

public class AppConstatnts {
    public static String LOGIN_URL=getBaseURL()+"wp-json/v1/account/login";

    public static  String LOCATION_UPDATE=getBaseURL()+"wp-json/v1/user/location/";

    public static String LOCATION_ASSISTANCE=getBaseURL()+"wp-json/v1/assistance?token=";

    public static String UPDATE_PUSH_TOKEN=getBaseURL()+"wp-json/v1/push-service?token=";

    public static String GET_MESSAGE=getBaseURL()+"wp-json/v1/push-service/";

    public static String GET_MESSAGE_DETAIL=getBaseURL()+"wp-json/v1/push-service/";

    public static String GET_LOCATION_FLAG=getBaseURL()+"wp-json/v1/user/sendtime?userid=";

    public static String getBaseURL(){
        System.out.println("BASE API"+PrefrensUtils.getBaseURL(AppController.getInstance()));
        return PrefrensUtils.getBaseURL(AppController.getInstance());
    }

}

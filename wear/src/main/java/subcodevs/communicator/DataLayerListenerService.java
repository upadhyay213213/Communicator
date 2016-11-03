package subcodevs.communicator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import apputils.PrefrensUtils;
import model.MessageResposneDatabase;

/**
 * Created by nupadhay on 9/20/2016.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/path")) {
            final String message = new String(messageEvent.getData());


            // do what you want with the json-string

            if(message.contains("tokenfromDetail")){
                try{
                    //  Toast.makeText(this, "MessageFromWear"+message, Toast.LENGTH_SHORT).show();
                    JSONObject json = new JSONObject(message);
                    String token =json.getString("tokenfromDetail");
                    String useid=json.getString("useridfromDeatil");

                    PrefrensUtils.setDeviceToken(getApplicationContext(), token);
                    PrefrensUtils.setUserID(getApplicationContext(),useid);
                }catch (JSONException e){

                }
            }


            if(message.contains("token")){
                try{
                  //  Toast.makeText(this, "MessageFromWear"+message, Toast.LENGTH_SHORT).show();
                    JSONObject json = new JSONObject(message);
                    String token =json.getString("token");
                    String lat = json.getString("lat");
                    String longi = json.getString("long");
                    String useid=json.getString("userid");
                    PrefrensUtils.setDeviceToken(getApplicationContext(),token);
                    PrefrensUtils.setLat(getApplicationContext(), lat);
                    PrefrensUtils.setLong(getApplicationContext(), longi);
                    PrefrensUtils.setUserID(getApplicationContext(),useid);
                }catch (JSONException e){

                }
            }else{
                String mMessageNew = message;
                JSONArray mJson = null;
                try {
                    mJson = new JSONArray(mMessageNew);
                    JSONObject mJsonObject = mJson.getJSONObject(0);
                    PrefrensUtils.setDeviceToken(getApplicationContext(), mJsonObject.getString("deviceval"));
                    PrefrensUtils.setUserID(getApplicationContext(), mJsonObject.getString("useridfromMessage"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                PrefrensUtils.setMessageDetail(getApplicationContext(),message);

            }




        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}

package subcodevs.communicator;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import apputils.PrefrensUtils;

/**
 * Created by nupadhay on 9/20/2016.
 */
public class DataLayerListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals("/path")) {
            final String message = new String(messageEvent.getData());


            // do what you want with the json-string

            if(message.contains("token")){
                try{
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
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.putString("demo_json", message).apply();
            }




        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}

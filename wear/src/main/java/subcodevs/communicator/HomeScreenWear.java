package subcodevs.communicator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import apputils.PrefrensUtils;
import commonmodules.BaseActivity;
import model.PushToken;
import subcodevs.communicator.ui.MessageScreenWear;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

public class HomeScreenWear extends BaseActivity implements
         View.OnClickListener, RequestResponseInterface {

    private ImageView mMessage, mAssistance;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        Intent mIntent = new Intent(this, DataLayerListenerService.class);
        startService(mIntent);
        RequestManager.mRequestManager.responseInterface = this;
        initializeUI();

    }







    private void initializeUI() {
        mMessage = (ImageView) findViewById(R.id.messageID);
        mLogoutButton = (Button) findViewById(R.id.logoutButtonID);
        mAssistance = (ImageView) findViewById(R.id.assistanceID);
        mAssistance.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        mLogoutButton.setOnClickListener(this);
        RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getDeviceToken(this), PrefrensUtils.getLat(this), PrefrensUtils.getLong(this), "UpdateLocationRequest");
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assistanceID:
                startProgress();
                    RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreenWear.this),PrefrensUtils.getUserID(HomeScreenWear.this),PrefrensUtils.getLat(this),PrefrensUtils.getLong(this),"requestAssistance");
                break;

            case R.id.messageID:
                startActivity(new Intent(HomeScreenWear.this, MessageScreenWear.class));
                break;

            case R.id.logoutButtonID:
//                PrefrensUtils.setDeviceToken(HomeScreenWear.this,"");
//                startActivity(new Intent(HomeScreenWear.this, LoginScreen.class));
                this.finish();
                break;

        }
    }

    @Override
    public void responseListener(Object o,String callType) {
        stopProgress();
        System.out.println("ResponseHomeScreenWEAR" + o.toString());
        Gson gson = new Gson();
        if(callType.equals("pushNotificationRequest")){
            PushToken response = gson.fromJson(o.toString(), PushToken.class);
            if(response!=null){
              PrefrensUtils.setMDDeviceToken(HomeScreenWear.this,response.getMdDeviceToken());
            }
        }else if(callType.equals("UpdateLocationRequest")){


        }else if(callType.equals("requestAssistance")){
            try {
                JSONObject json = new JSONObject(o.toString());
                String type =json.getString("type");
                if(type.equals("assistance")){
                    buildAlertMessage(this,"Concerned people have been informed.They will contact you soon.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void errorListener(VolleyError error) {
        System.out.println("ResponseHomeScreen" + error.getMessage());
        stopProgress();
        handleErrorCase(this, error.networkResponse.statusCode);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
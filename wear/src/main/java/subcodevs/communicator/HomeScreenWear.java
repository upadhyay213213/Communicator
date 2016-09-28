package subcodevs.communicator;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private ProgressBar mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);

        //making  android  view shape aware
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
                initializeUI(stub);
            }
        });
        Intent mIntent = new Intent(this, DataLayerListenerService.class);
        startService(mIntent);
        RequestManager.mRequestManager.responseInterface = this;

    }


    public void startProgress() {
        mProgressDialog.setVisibility(View.VISIBLE);
    }

    public void stopProgress(){
        mProgressDialog.setVisibility(View.GONE);
    }





    private void initializeUI(WatchViewStub stub) {
        mProgressDialog= (ProgressBar) stub.findViewById(R.id.timer_progress);
        mMessage = (ImageView) stub.findViewById(R.id.messageID);
        mAssistance = (ImageView) stub.findViewById(R.id.assistanceID);
        mAssistance.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        if(!PrefrensUtils.getDeviceToken(this).isEmpty()){
            RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getDeviceToken(this), PrefrensUtils.getLat(this), PrefrensUtils.getLong(this), "UpdateLocationRequest");
        }else{
            showChangeLangDialog(this,"Something went wrong. Please make sure that your Android Watch is paired properly with your Phone.","Error");
        }

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
        }
    }

    @Override
    public void responseListener(Object o,String callType) {
        stopProgress();
        System.out.println("ResponseHomeScreenWEAR" + o.toString());
         if(callType.equals("UpdateLocationRequest")){


        }else if(callType.equals("requestAssistance")){
            try {
                JSONObject json = new JSONObject(o.toString());
                String type =json.getString("type");
                if(type.equals("assistance")){
                    showChangeLangDialog(this,"Concerned people have been informed. They will contact you soon.","Success!");
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
        try {
            handleErrorCase(this, error.networkResponse.statusCode);
        }catch (Exception e){
            handleErrorCase(this, 101);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
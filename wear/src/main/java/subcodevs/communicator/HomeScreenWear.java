package subcodevs.communicator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.service.carrier.CarrierMessagingService;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
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
         View.OnClickListener, RequestResponseInterface ,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private ImageView mMessage, mAssistance;
    private ProgressBar mProgressDialog;
    private long UPDATE_INTERVAL_MS=10000;
    private long FASTEST_INTERVAL_MS=8000;
    private boolean noGps=false;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addApi(Wearable.API)  // used for data layer API
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }else{
            noGps = true;
        }

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
            if(noGps){

                RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getDeviceToken(this), PrefrensUtils.getLat(this), PrefrensUtils.getLong(this), "UpdateLocationRequest");
            }else{
                if(mLocation!=null)
                RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getDeviceToken(this), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), "UpdateLocationRequest");
            }

        }else{
         //   showChangeLangDialog(this,"Something went wrong. Please make sure that your Android Watch is paired properly with your Android Phone.","Error!");
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assistanceID:

                try {

                    if(!PrefrensUtils.getDeviceToken(this).isEmpty()){
                        startProgress();
                        if(noGps){
                            if(getTimeDifference()){
                                showChangeLangDialog(this, "Please open app on phone and request assistance to ensure accurate location.", "Action Required");
                                RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreenWear.this), PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getLat(this), PrefrensUtils.getLong(this), "requestAssistance");
                            }else{
                                RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreenWear.this), PrefrensUtils.getUserID(HomeScreenWear.this), PrefrensUtils.getLat(this), PrefrensUtils.getLong(this), "requestAssistance");
                            }

                        }else{
                            if(mLocation!=null)
                            RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreenWear.this), PrefrensUtils.getUserID(HomeScreenWear.this), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), "requestAssistance");
                        }

                    }else{
                        showChangeLangDialog(this, "Something went wrong. Please login through your android phone and try again.", "Error!");
                    }
                }catch (Exception e){

                }


                break;

            case R.id.messageID:
                this.finish();
                startActivity(new Intent(HomeScreenWear.this, MessageScreenWear.class));
                break;
        }
    }

    @Override
    public void responseListener(Object o,String callType) {
        try {
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

                if(mProgressDialog.getVisibility()==View.VISIBLE){
                    mProgressDialog.setVisibility(View.GONE);
                }
            }
        }catch (Exception e){

        }

    }

    @Override
    public void errorListener(VolleyError error) {
        try {
            System.out.println("ResponseHomeScreen" + error.getMessage());
            stopProgress();
            try {
                handleErrorCase(this, error.networkResponse.statusCode);
            }catch (Exception e){
                handleErrorCase(this, 101);
            }

            if(mProgressDialog.getVisibility()==View.VISIBLE){
                mProgressDialog.setVisibility(View.GONE);
            }

        }catch (Exception e){

        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            this.finish();
        }catch (Exception e){

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, (com.google.android.gms.location.LocationListener) HomeScreenWear.this);
    }

    @Override
    public void onConnectionSuspended(int i) {



    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private boolean getTimeDifference(){
        boolean isDiff;
        if((System.currentTimeMillis() - (PrefrensUtils.getTime(this))) > 600000 ){
            isDiff = true;
        }else{
            isDiff = false;
        }

         return  isDiff;
    }

}
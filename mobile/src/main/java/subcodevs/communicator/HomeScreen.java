package subcodevs.communicator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import model.DatabaseToJSON;
import model.PushToken;
import subcodevs.communicator.pushcontroller.GCMRegistrationIntentService;
import subcodevs.communicator.ui.LoginScreen;
import subcodevs.communicator.ui.MessageScreen;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

public class HomeScreen extends BaseActivityWear implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener, RequestResponseInterface {

    private ImageView mMessage, mAssistance;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private static final int PERMISSION_REQUEST_CODE = 1;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Button mLogoutButton;
    private String TAG="HomeScreen";
    private int count;
    private String COUNT_KEY="com.example.key.count";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        RequestManager.mRequestManager.responseInterface = this;
        locationStatusCheck();
        if (checkPlayServices()) {
            requestPermission();
            buildGoogleApiClient();
            createLocationRequest();
        }
        initializeUI();

    }

    // Create a data map and put data in it
    private void increaseCounter() {
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count");
        putDataMapReq.getDataMap().putInt(COUNT_KEY, count++);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }





    public void locationStatusCheck() {
        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }

    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Location must be enabled to search for Communicator, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();

    }


    private void initializeUI() {
        mMessage = (ImageView) findViewById(R.id.messageID);
        mLogoutButton = (Button) findViewById(R.id.logoutButtonID);
        mAssistance = (ImageView) findViewById(R.id.assistanceID);
        mAssistance.setOnClickListener(this);
        mMessage.setOnClickListener(this);
        mLogoutButton.setOnClickListener(this);

        if(mGoogleApiClient.isConnected() && mLastLocation!=null){
            RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreen.this), PrefrensUtils.getDeviceToken(this), String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), "UpdateLocationRequest");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RequestManager.mRequestManager.responseInterface = this;
        if (PrefrensUtils.getPushToken(this).isEmpty()) {
            registerPushNotificationServices();
        }else if(PrefrensUtils.getMDDevideToken(HomeScreen.this).isEmpty()){
            RequestManager.getInstance().pushNotificationRequest(PrefrensUtils.getDeviceToken(HomeScreen.this),PrefrensUtils.getPushToken(HomeScreen.this),"Android",PrefrensUtils.getUserName(HomeScreen.this),PrefrensUtils.getUserID(HomeScreen.this),"pushNotificationRequest");
        }
        checkPlayServices();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        //Registering push broadcast n all
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        //Unregistering
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    /**
     * Method to display the location on UI
     */
    private Location displayLocation() {
        if (checkPermission()) {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
        }
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            PrefrensUtils.setLat(HomeScreen.this,String.valueOf(latitude));
            PrefrensUtils.setLong(HomeScreen.this,String.valueOf(longitude));

            System.out.println("LatLong" + latitude + " " + longitude);
        }
        return mLastLocation;
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        } else {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API).build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {

        if (checkPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        displayLocation();
        if(mLastLocation!=null){
            RequestManager.getInstance().UpdateLocationRequest(PrefrensUtils.getUserID(HomeScreen.this), PrefrensUtils.getDeviceToken(this), String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()), "UpdateLocationRequest");
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

      //  DatabaseToJSON dbJson = new DatabaseToJSON(HomeScreen.this);
        try {
          //  JSONObject json = dbJson.getJSON();
            JSONObject mJosn =  new JSONObject();
            mJosn.put("token",PrefrensUtils.getDeviceToken(HomeScreen.this));
            if(mLastLocation==null){
                mJosn.put("lat",PrefrensUtils.getLat(this));
                mJosn.put("long",PrefrensUtils.getLong(this));
            }else{
                mJosn.put("lat",mLastLocation.getLatitude());
                mJosn.put("long",mLastLocation.getLongitude());
            }

            mJosn.put("userid",PrefrensUtils.getUserID(HomeScreen.this));
            new SendToDataLayerThread("/path", mJosn.toString()).start();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Toast.makeText(this, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.assistanceID:
                startLocationUpdates();
                startProgress();
                if (mLastLocation != null) {
                    RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreen.this),PrefrensUtils.getUserID(HomeScreen.this),String.valueOf(mLastLocation.getLatitude()),String.valueOf(mLastLocation.getLongitude()),"requestAssistance");
                } else {
                    startLocationUpdates();
                        RequestManager.getInstance().requestAssistance(PrefrensUtils.getDeviceToken(HomeScreen.this),PrefrensUtils.getUserID(HomeScreen.this),PrefrensUtils.getLat(this),PrefrensUtils.getLong(this),"requestAssistance");
                }
                break;

            case R.id.messageID:
                startActivity(new Intent(HomeScreen.this, MessageScreen.class));
                break;

            case R.id.logoutButtonID:
                PrefrensUtils.setDeviceToken(HomeScreen.this,"");
                startActivity(new Intent(HomeScreen.this, LoginScreen.class));
                this.finish();
                break;

        }
    }

    @Override
    public void responseListener(Object o,String callType) {
        System.out.println("ResponseHomeScreen" + o.toString());
        Gson gson = new Gson();
        if(callType.equalsIgnoreCase("pushNotificationRequest")){
            PushToken response = gson.fromJson(o.toString(), PushToken.class);
            if(response!=null){
              PrefrensUtils.setMDDeviceToken(HomeScreen.this,response.getMdDeviceToken());
            }
            stopProgress();
        } if(callType.equalsIgnoreCase("UpdateLocationRequest")){
            stopProgress();


        } if(callType.equalsIgnoreCase("requestAssistance")){
            try {
                JSONObject json = new JSONObject(o.toString());
                String type =json.getString("type");
                if(type.equalsIgnoreCase("assistance")){
                    buildAlertMessage(this,"Concerned people have been informed.They will contact you soon.");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        stopProgress();
    }

    @Override
    public void errorListener(VolleyError error) {
        System.out.println("ResponseHomeScreen" + error.getMessage());
        stopProgress();
        try{
            handleErrorCase(this, error.networkResponse.statusCode);
        }catch (Exception e){

        }

    }

    private void registerPushNotificationServices() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    String token = intent.getStringExtra("token");
                    PrefrensUtils.setPushToken(HomeScreen.this, token);
                    RequestManager.getInstance().pushNotificationRequest(PrefrensUtils.getDeviceToken(HomeScreen.this),PrefrensUtils.getPushToken(HomeScreen.this),"Android",PrefrensUtils.getUserName(HomeScreen.this),PrefrensUtils.getUserID(HomeScreen.this),"pushNotificationRequest");
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                } else {
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
            }

        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    class SendToDataLayerThread extends Thread {
        String path;
        String message;

        SendToDataLayerThread(String p, String msg) {
            path = p;
            message = msg;
        }

        public void run() {
            NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
            for (Node node : nodes.getNodes()) {
                MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                if (result.getStatus().isSuccess()) {
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }
}
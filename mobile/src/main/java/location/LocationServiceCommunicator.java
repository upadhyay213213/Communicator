package location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import appconstant.AppConstatnts;
import appmanager.AppController;
import apputils.PrefrensUtils;

public class LocationServiceCommunicator extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationServiceCommunicator.class.getCanonicalName();
    private static boolean isLocationTimeCompleted;
    private static boolean isLocationRequestedFromServer;
    private static Runnable r4;
    public static boolean isClickedLocation;
    private GoogleApiClient mGoogleApiClient;
    private static Location mLocation;
    private LocationRequest mLocationRequest;
    private static Handler handler1;
    private String mValue = "false";
    private Runnable r;
    private Handler handler2;
    private Runnable r2;
    private static Runnable r3;
    private Handler handler6;
    private Runnable r6;
    private static Handler handler7;
    private static Runnable r7;
    private static Handler handler8;
    private static Runnable r8;
    private static Handler handler9;
    private boolean isWithoutWear;


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onConnected(Bundle bundle) {
        String mValue1 = PrefrensUtils.getTimeInterval(AppController.getInstance());
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Check this and try to manupulate
        if (mValue.equals("true")) {
            mLocationRequest.setInterval(6000);
            mLocationRequest.setFastestInterval(4000);
        } else {
            mLocationRequest.setInterval(Integer.parseInt(mValue1) * 6000);
            mLocationRequest.setFastestInterval((Integer.parseInt(mValue1) * 5000) - 1000);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
        buildGoogleApiClientWithoutWear();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();

        handler9 = new Handler();
        r8 = new Runnable() {
            public void run() {
                if (mLocation != null) {
                    // This will call location update every two hours to server and will update the user location on server
                    UpdateLocationRequest(PrefrensUtils.getUserID(AppController.getInstance()), PrefrensUtils.getDeviceToken(AppController.getInstance()), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), "");
                }
                //              }
                handler9.postDelayed(r8, (60 * 60 * 1000) + (60 * 60 * 1000));
            }
        };
        handler9.post(r8);


        handler9.post(r8);
        handler6 = new Handler();
        r6 = new Runnable() {
            public void run() {
                if (isClickedLocation) {
                    //If assistance button is tapped this will start location services to fuse the most updated location of the user
                    startLocationUpdates();
                }
                handler6.postDelayed(r6, 1000);
            }
        };
        handler6.post(r6);


        handler1 = new Handler();
        r = new Runnable() {
            public void run() {
                if (isLocationTimeCompleted == true) {
                    //Once Assistance request will be completed this method will stop the periodic location update
                    stopLocationUpdates();
                }
                handler1.postDelayed(r, 1000);
            }
        };
        handler1.post(r);

        handler2 = new Handler();
        r2 = new Runnable() {
            public void run() {
                //This will keep checking the server flag for demanding the location every five minutes to ensure that if server is asking for location then it must provide the same
                checkLocationFlagFromServer();
                handler2.postDelayed(r2, 300000);
            }
        };
        handler1.post(r2);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient.connect();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "Location::" + location);
        mLocation = location;
        if (!isWithoutWear) {
            try {
                JSONObject mJosn = new JSONObject();
                if (mLocation == null) {
                    mJosn.put("latService", PrefrensUtils.getLat(this));
                    mJosn.put("longService", PrefrensUtils.getLong(this));
                } else {
                    mJosn.put("latService", mLocation.getLatitude());
                    mJosn.put("longService", mLocation.getLongitude());
                }
                new SendToDataLayerThread("/path", mJosn.toString()).start();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    //Update location request
    public static void UpdateLocationRequest(String id, String token, String latitude, String longitude, final String calltype) {
        try {
            String url = AppConstatnts.LOCATION_UPDATE + id + "?" + "token=" + token;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    url, jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject mJson = new JSONObject(response.toString());
                                String timeInterval = mJson.getString("timeinterval");
                                PrefrensUtils.setTimeInterval(AppController.getInstance(), timeInterval);
                            } catch (Exception e) {

                            }

                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
        }
    }

    //calling server to check the status of server flag for location
    private void checkLocationFlagFromServer() {
        try {
            String url = AppConstatnts.GET_LOCATION_FLAG + PrefrensUtils.getUserID(AppController.getInstance());
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                JSONObject mJosn = new JSONObject(response.toString());
                                mValue = mJosn.getString("sendtime");
                                if (mValue.equals("true") && !isClickedLocation) {
                                    startLocationUpdates();
                                    requestUpdateLocation(true);
                                } else {
                                }
                            } catch (Exception e) {

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
            };
            AppController.getInstance().getRequestQueue().add(jsonObjReq);

        } catch (Exception e) {
        }

    }

    //This is get called once user taps on assistance and will keep on executing for next 1 hour every minute with updated location of user to server
    public static void requestFromAssistance() {
        isClickedLocation = true;
        handler8 = new Handler();
        r4 = new Runnable() {
            public void run() {
                if (mLocation != null) {
                    isClickedLocation = true;
                    isLocationTimeCompleted = false;
                    UpdateLocationRequest(PrefrensUtils.getUserID(AppController.getInstance()), PrefrensUtils.getDeviceToken(AppController.getInstance()), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), "updatelocation");
                }
                handler8.postDelayed(r4, 60000);
            }
        };
        handler8.post(r4);
    }

    // This method will stop the assistance thread after one hour
    public static void stopAssisatnceService() {
        handler7 = new Handler();
        r7 = new Runnable() {
            public void run() {
                handler8.removeCallbacks(r4);
                isClickedLocation = false;
                isLocationTimeCompleted = true;
            }
        };
        handler7.postDelayed(r7, 60 * 60 * 1000);
    }

    public static void stopAssisatnceServiceFromHomeScreen() {
        if (handler8 != null) {
            handler8.removeCallbacks(r4);
        }

        if (handler7 != null) {
            handler7.removeCallbacks(r7);
        }
    }


    public void requestUpdateLocation(final boolean what) {
        UpdateLocationRequest(PrefrensUtils.getUserID(AppController.getInstance()), PrefrensUtils.getDeviceToken(AppController.getInstance()), String.valueOf(mLocation.getLatitude()), String.valueOf(mLocation.getLongitude()), "updatelocation");
        stopLocationUpdates();
    }

    //This method will start the location updates
    protected void startLocationUpdates() {
        if (!mGoogleApiClient.isConnected() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    //This method will stop the location updates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void buildGoogleApiClientWithoutWear() {
        isWithoutWear = true;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected synchronized void buildGoogleApiClient() {
        isWithoutWear = false;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this).addApi(Wearable.API)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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
                    Log.d(TAG, "Message from HomeScreen: {" + message + "} sent to: " + node.getDisplayName());
                } else {
                    Log.d(TAG, "ERROR: failed to send Message from Home Screen");
                }
            }
        }
    }
}

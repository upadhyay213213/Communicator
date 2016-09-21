package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import adapter.MessageAdapter;
import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
import model.MessageResponseFirst;
import model.MessageResposneDatabase;
import subcodevs.communicator.HomeScreen;
import subcodevs.communicator.R;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageScreen extends BaseActivityWear implements RequestResponseInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private ListView mListView;
    private MessageAdapter messageAdapter;
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);
        initializeUI();
        DatabaseHelper.init(this);
        RequestManager.mRequestManager.responseInterface = this;
        startProgress();
        RequestManager.getInstance().RequestMessage(PrefrensUtils.getUserID(this), PrefrensUtils.getDeviceToken(this), "RequestMessage");
    }

    private void initializeUI() {
        mListView = (ListView) findViewById(R.id.messageListID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }


    @Override
    public void responseListener(Object o, String calltype) {
        System.out.println("ResponseHomeScreen" + o.toString());
        Gson gson = new Gson();
        if (calltype.equals("RequestMessage")) {
            MessageResponseFirst response = gson.fromJson(o.toString(), MessageResponseFirst.class);
            System.out.println("ResponseHomeScreen" + response.getContacts().get(0).getMmSenderUserName());
            intsertToDatabase(response);
        } else {
            handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    stopProgress();
                }
            };
            handler.postDelayed(r, 2000);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (messageAdapter != null) {
            ArrayList<MessageResposneDatabase> msg = DataBaseQuery.getMessageResponse();
            messageAdapter = new MessageAdapter(MessageScreen.this, msg);
            mListView.setAdapter(messageAdapter);
        }
    }

    private void intsertToDatabase(MessageResponseFirst messageResponse) {

        for (int i = 0; i < messageResponse.getContacts().size(); i++) {
            DataBaseQuery.insertMessageToDB(messageResponse.getContacts().get(i), MessageScreen.this);
        }
        ArrayList<MessageResposneDatabase> msg = DataBaseQuery.getMessageResponse();
        messageAdapter = new MessageAdapter(MessageScreen.this, msg);
        mListView.setAdapter(messageAdapter);
        handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                stopProgress();
            }
        };
        handler.postDelayed(r, 2000);


//        try {
//            DatabaseHelper.copyDatabaseToSdCard();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.v("messages",msg.toString());
    }


    @Override
    public void errorListener(VolleyError error) {
        stopProgress();
        try{
            handleErrorCase(this, error.networkResponse.statusCode);
        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //  DatabaseToJSON dbJson = new DatabaseToJSON(HomeScreen.this);
        try {
            //  JSONObject json = dbJson.getJSON();
            JSONObject mJosn = new JSONObject();
//            mJosn.put("token",PrefrensUtils.getDeviceToken(HomeScreen.this));
//            mJosn.put("lat",mLastLocation.getLatitude());
//            mJosn.put("long",mLastLocation.getLongitude());
//            mJosn.put("userid",PrefrensUtils.getUserID(HomeScreen.this));
//            new SendToDataLayerThread("/path", mJosn.toString()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}



package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.MessageAdapter;
import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
import model.DatabaseToJSON;
import model.MessageResponse;
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
    private boolean isWithoutWear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);
        isWithoutWear=false;
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
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
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            if(isWithoutWear==true){
                buildGoogleApiClientWithoutWear();

            }
            mGoogleApiClient.connect();
        }

        if(!isWithoutWear){
            try{
                new SendToDataLayerThread("/path", sendDataToWatch().toString()).start();
            }catch (Exception e){

            }
        }


    }

    private void buildGoogleApiClientWithoutWear() {
        isWithoutWear=true;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private JSONArray sendDataToWatch(){
        JSONArray jsonArray = new JSONArray();
        try {
            ArrayList<MessageResposneDatabase> messageResponseArrayList = DataBaseQuery.getMessageResponse();

            if(messageResponseArrayList.size()!=0){
                if(messageResponseArrayList.size()>10){
                    for(int i=0;i<10;i++){
                        JSONObject mJsonObject = new JSONObject();
                        mJsonObject.put("id",messageResponseArrayList.get(i).getmID());
                        mJsonObject.put("message",messageResponseArrayList.get(i).getmMessage());
                        mJsonObject.put("time",messageResponseArrayList.get(i).getmMessage());
                        mJsonObject.put("senderdisplayname",messageResponseArrayList.get(i).getmSenderDisplayName());
                        mJsonObject.put("messageread",messageResponseArrayList.get(i).isMessageRead());
                        jsonArray.put(mJsonObject);
                    }
                }else{
                    for(int i=0;i<messageResponseArrayList.size();i++){
                        JSONObject mJsonObject = new JSONObject();
                        mJsonObject.put("id",messageResponseArrayList.get(i).getmID());
                        mJsonObject.put("message",messageResponseArrayList.get(i).getmMessage());
                        mJsonObject.put("time",messageResponseArrayList.get(i).getmMessage());
                        mJsonObject.put("senderdisplayname",messageResponseArrayList.get(i).getmSenderDisplayName());
                        mJsonObject.put("messageread",messageResponseArrayList.get(i).isMessageRead());
                        jsonArray.put(mJsonObject);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
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



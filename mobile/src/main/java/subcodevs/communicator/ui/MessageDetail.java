package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import appconstant.AppConstatnts;
import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import commonmodules.CommonUtils;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
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
public class MessageDetail extends BaseActivityWear implements RequestResponseInterface, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {

    private TextView mUserName, mTime, mMessage;
    private String position;

    private String mMessagePush;
    private String mMessageID;
    private TextView mBottomText;
    private LinearLayout mBackButton;
    private GoogleApiClient mGoogleApiClient;
    private boolean isWithoutWear;
    private String TAG="Message_detail";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if(PrefrensUtils.getDeviceToken(this).isEmpty()){
            Intent intent =new Intent(this,LoginScreen.class);
            intent.putExtra("MessageIDLogin","true");
            startActivity(intent);
            this.finish();
        }else{
            setContentView(R.layout.message_detail);
            RequestManager.getInstance().responseInterface = this;
            DatabaseHelper.init(this);
            initializeUI();
            if (!PrefrensUtils.getPushID(this).isEmpty()) {
                startProgress();
                mBottomText.setText("Back");
                RequestManager.getInstance().RequestMessageDetail(PrefrensUtils.getUserID(this), PrefrensUtils.getDeviceToken(this), PrefrensUtils.getPushID(this), "MessageDetail");
            }
            else {
                mBottomText.setText("Back To Messages");
                PrefrensUtils.setPushID(this,"");
                position = getIntent().getStringExtra("clickposition");
                ArrayList<MessageResposneDatabase> messageResposneDatabases = DataBaseQuery.getMessageResponse();
                mUserName.setText(messageResposneDatabases.get(Integer.parseInt(position)).getmSenderDisplayName());
                mMessage.setText(messageResposneDatabases.get(Integer.parseInt(position)).getmMessage());
                mTime.setText(CommonUtils.getTimeDifferance(messageResposneDatabases.get(Integer.parseInt(position)).getmTime()));
            }
        }

    }

    private void initializeUI() {
        mUserName = (TextView) findViewById(R.id.detailuserNameID);
        mTime = (TextView) findViewById(R.id.detailTimeID);
        mMessage = (TextView) findViewById(R.id.detailMessageID);
        mBottomText = (TextView) findViewById(R.id.bottomTextID);
        mBackButton = (LinearLayout) findViewById(R.id.fotterID);


        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
    }

    @Override
    public void responseListener(Object o, String callType) {
        if(callType.equals("MessageDetail")){
            Gson gson = new Gson();
            MessageResponse response = gson.fromJson(o.toString(), MessageResponse.class);
            mUserName.setText(response.getmSenderDisplayName());
            mMessage.setText(response.getmMessage());
            mTime.setText(CommonUtils.getTimeDifferance(response.getmTime()));
            DataBaseQuery.insertMessageToDB(response,MessageDetail.this);
            DataBaseQuery.updateMessageStatus(response.getmID(), "true");
            stopProgress();
        }

    }

    @Override
    public void errorListener(VolleyError error) {
        stopProgress();
        try{
            handleErrorCase(this, error.networkResponse.statusCode);
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!PrefrensUtils.getPushID(this).isEmpty()){
            PrefrensUtils.setPushID(this, "");
            this.finish();
            startActivity(new Intent(MessageDetail.this, HomeScreen.class));

        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"Connected to google client ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"Connection to google client failed");

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
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            if(isWithoutWear==true){
                buildGoogleApiClientWithoutWear();

            }
            mGoogleApiClient.connect();
        }

        if(!isWithoutWear){
            try {
                //  JSONObject json = dbJson.getJSON();
                JSONObject mJosn =  new JSONObject();
                mJosn.put("tokenfromDetail",PrefrensUtils.getDeviceToken(MessageDetail.this));
                mJosn.put("useridfromDeatil",PrefrensUtils.getUserID(MessageDetail.this));
                new SendToDataLayerThread("/path", mJosn.toString()).start();
            } catch (JSONException e) {
                Log.e(TAG,e.toString());
                e.printStackTrace();
            }
        }
        saveLog();
    }
    private void buildGoogleApiClientWithoutWear() {
        isWithoutWear=true;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
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
                    Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                }
                else {
                    Log.v("myTag", "ERROR: failed to send Message");
                }
            }
        }
    }

    public void saveLog(){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            StringBuilder log=new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            writeToFile(log.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }
    }
    private void writeToFile(String data) {
        String root = Environment.getExternalStorageDirectory().toString();

        File oldFile = new File(root + "/Communicator_LOGS/communicator.txt");
        long fileBytes = oldFile.length();
        if(fileBytes > 14000000){
            oldFile.delete();
        }

        File myDir = new File(root + "/Communicator_LOGS");
        myDir.mkdirs();
        String fname = "communicator.txt";
        File file = new File (myDir, fname);



        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }


}

package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import apputils.PrefrensUtils;
import commonmodules.BaseActivity;
import commonmodules.CommonUtils;
import databasequery.DataBaseQuery;
import model.MessageResponse;
import model.MessageResposneDatabase;
import subcodevs.communicator.HomeScreenWear;
import subcodevs.communicator.R;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageDetail extends BaseActivity implements RequestResponseInterface{

    private TextView mUserName, mTime, mMessage;
    private String position;

    private String mMessagePush;
    private String mMessageID;
    private LinearLayout mBackClick;
    private boolean isBackPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail_wear);
        isBackPressed=false;
        RequestManager.getInstance().responseInterface = this;
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
                mUserName = (TextView) stub.findViewById(R.id.usernameHeaderID);
                mTime = (TextView) stub.findViewById(R.id.detailTimeID);
                mMessage = (TextView) stub.findViewById(R.id.detailMessageID);
                mBackClick = (LinearLayout) stub.findViewById(R.id.headerBackID);
                mBackClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isBackPressed){
                            isBackPressed=true;
                            onBackPressed();
                        }
                    }
                });
//                if (getIntent().getStringExtra("messageID") != null) {
//                    startProgress();
//                    mMessageID = getIntent().getStringExtra("messageID");
//                    RequestManager.getInstance().RequestMessageDetail(PrefrensUtils.getUserID(MessageDetail.this),PrefrensUtils.getDeviceToken(MessageDetail.this),mMessageID,"MessageDetail");
//                } else {
                    position = getIntent().getStringExtra("clickposition");
                    String mMessageNew = PrefrensUtils.getMessageDetail(MessageDetail.this);
                    ArrayList<MessageResposneDatabase> messageResposneDatabaseArrayList = new ArrayList<MessageResposneDatabase>();
                    try {
                        JSONArray mJson = new JSONArray(mMessageNew);
                        MessageResposneDatabase messageResponse = new MessageResposneDatabase();
                        JSONObject mJsonObject = mJson.getJSONObject(Integer.parseInt(position));
                        messageResponse.setmID(mJsonObject.getString("id"));
                        messageResponse.setmMessage(mJsonObject.getString("message"));
                        messageResponse.setmTime(mJsonObject.getString("time"));
                        messageResponse.setmSenderDisplayName(mJsonObject.getString("senderdisplayname"));
                        messageResponse.setIsMessageRead(Boolean.parseBoolean(mJsonObject.getString("messageread")));
                        messageResposneDatabaseArrayList.add(messageResponse);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mUserName.setText(messageResposneDatabaseArrayList.get(0).getmSenderDisplayName());
                    mMessage.setText(messageResposneDatabaseArrayList.get(0).getmMessage());
                    mTime.setText(CommonUtils.getTimeDifferance(messageResposneDatabaseArrayList.get(0).getmTime()));
          //      }
            }
        });



    }

    private void initializeUI() {
        //making  android  view shape aware


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

        }
    }

    @Override
    public void onBackPressed() {
        try {
            this.finish();
            startActivity(new Intent(MessageDetail.this, MessageScreenWear.class));
        }catch (Exception e){

        }

    }
}

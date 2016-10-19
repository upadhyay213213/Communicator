package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

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
public class MessageDetail extends BaseActivityWear implements RequestResponseInterface {

    private TextView mUserName, mTime, mMessage;
    private String position;

    private String mMessagePush;
    private String mMessageID;
    private TextView mBottomText;
    private LinearLayout mBackButton;

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
        super.onBackPressed();
        if(!PrefrensUtils.getPushID(this).isEmpty()){
            PrefrensUtils.setPushID(this,"");
            this.finish();
            startActivity(new Intent(MessageDetail.this, HomeScreen.class));

        }
    }
}

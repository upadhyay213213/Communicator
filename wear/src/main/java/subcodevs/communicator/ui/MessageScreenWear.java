package subcodevs.communicator.ui;

import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.MessageAdapter;
import apputils.PrefrensUtils;
import commonmodules.BaseActivity;
import model.MessageResposneDatabase;
import subcodevs.communicator.R;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageScreenWear extends BaseActivity  {

    private ListView mListView;
    private MessageAdapter messageAdapter;
    private TextView mUserName;
    ImageView mBackClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);

        //making  android  view shape aware
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                // Now you can access your views
                initializeUI(stub);
                startProgress();
            }
        });


    }

    private void initializeUI(WatchViewStub stub) {
        mBackClick = (ImageView) findViewById(R.id.headerBackID);
        mBackClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mUserName = (TextView) stub.findViewById(R.id.usernameHeaderID);
        mUserName.setText("Messages");
        mListView = (ListView) stub.findViewById(R.id.messageListID);
        String mMessage=PrefrensUtils.getMessageDetail(this);
        if(mMessage.isEmpty()){
            showChangeLangDialog(this,"Something went wrong. Please make sure that your Android Watch is paired properly with your Android Phone.","Error!");
        }else{
            ArrayList<MessageResposneDatabase> messageResposneDatabaseArrayList = new ArrayList<MessageResposneDatabase>();
            try {

                JSONArray mJson = new JSONArray(mMessage);

                for (int i=0; i<mJson.length();i++){
                    MessageResposneDatabase messageResponse = new MessageResposneDatabase();
                    JSONObject mJsonObject = mJson.getJSONObject(i);
                    messageResponse.setmID(mJsonObject.getString("id"));
                    messageResponse.setmMessage( mJsonObject.getString("message"));
                    messageResponse.setmTime( mJsonObject.getString("time"));
                    messageResponse.setmSenderDisplayName(mJsonObject.getString("senderdisplayname"));
                    messageResponse.setIsMessageRead(Boolean.parseBoolean(mJsonObject.getString("messageread")));
                    messageResposneDatabaseArrayList.add(messageResponse);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mListView.setAdapter(new MessageAdapter(this,messageResposneDatabaseArrayList));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}



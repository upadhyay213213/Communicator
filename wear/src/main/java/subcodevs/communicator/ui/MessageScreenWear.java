package subcodevs.communicator.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.MessageAdapter;
import apputils.PrefrensUtils;
import commonmodules.BaseActivity;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
import model.MessageResponse;
import model.MessageResponseFirst;
import model.MessageResposneDatabase;
import subcodevs.communicator.R;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageScreenWear extends BaseActivity  {

    private ListView mListView;
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);
        initializeUI();
        startProgress();
    }

    private void initializeUI() {
        mListView = (ListView) findViewById(R.id.messageListID);
        String mMessage=PrefrensUtils.getMessageDetail(this);
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



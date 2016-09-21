package subcodevs.communicator.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;

import adapter.MessageAdapter;
import commonmodules.BaseActivity;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
import model.MessageResponseFirst;
import model.MessageResposneDatabase;
import subcodevs.communicator.R;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageScreenWear extends BaseActivity implements RequestResponseInterface {

    private ListView mListView;
    private MessageAdapter messageAdapter;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_screen);
        initializeUI();
        DatabaseHelper.init(this);
        RequestManager.mRequestManager.responseInterface = this;
        startProgress();
      //  RequestManager.getInstance().RequestMessage(PrefrensUtils.getUserID(this),PrefrensUtils.getDeviceToken(this),"RequestMessage");
    }

    private void initializeUI() {
        mListView = (ListView) findViewById(R.id.messageListID);
    }


    @Override
    public void responseListener(Object o,String calltype) {
        System.out.println("ResponseHomeScreen" + o.toString());
        Gson gson = new Gson();
       if(calltype.equals("RequestMessage")){
           MessageResponseFirst response = gson.fromJson(o.toString(), MessageResponseFirst.class);
           System.out.println("ResponseHomeScreen"+response.getContacts().get(0).getMmSenderUserName());
           intsertToDatabase(response);
       }else{
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
      //  if(messageAdapter!=null){
            ArrayList<MessageResposneDatabase>msg=DataBaseQuery.getMessageResponse(MessageScreenWear.this);
           MessageAdapter messageAdapter = new MessageAdapter(MessageScreenWear.this,msg);
            mListView.setAdapter(messageAdapter);
      //  }
    }

    private void intsertToDatabase(MessageResponseFirst messageResponse){

        for (int i=0;i<messageResponse.getContacts().size();i++){
            DataBaseQuery.insertMessageToDB(messageResponse.getContacts().get(i),MessageScreenWear.this);
        }
        ArrayList<MessageResposneDatabase>msg=DataBaseQuery.getMessageResponse(MessageScreenWear.this);
         messageAdapter = new MessageAdapter(MessageScreenWear.this,msg);
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
        System.out.println("ResponseHomeScreen" + error.getMessage());

        startProgress();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}



package subcodevs.communicator.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.TextView;

import java.util.ArrayList;

import commonmodules.BaseActivity;
import databasequery.DataBaseQuery;
import databasequery.DatabaseHelper;
import model.MessageResposneDatabase;
import subcodevs.communicator.R;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class MessageDetailWear extends BaseActivity{

    private TextView mUserName,mTime,mMessage;
    private String  position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        DatabaseHelper.init(this);
//        position = getIntent().getStringExtra("clickposition");
        position="1";
        initializeUI();
    }

    private void initializeUI() {
        mUserName= (TextView) findViewById(R.id.detailuserNameID);
        mTime= (TextView) findViewById(R.id.detailTimeID);
        mMessage= (TextView) findViewById(R.id.detailMessageID);
        ArrayList<MessageResposneDatabase> messageResposneDatabases = DataBaseQuery.getMessageResponse(this);
        mUserName.setText(messageResposneDatabases.get(Integer.parseInt(position)).getmSenderDisplayName());
        mMessage.setText(messageResposneDatabases.get(Integer.parseInt(position)).getmMessage());
    }
}

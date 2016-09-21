package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import appconstant.AppConstatnts;
import commonmodules.CommonUtils;
import databasequery.DataBaseQuery;
import model.MessageResponse;
import model.MessageResposneDatabase;
import subcodevs.communicator.R;
import subcodevs.communicator.ui.MessageDetail;

/**
 * Created by nupadhay on 9/19/2016.
 */
public class MessageAdapter extends BaseAdapter {

    private Context mCtx;
    private  ArrayList<MessageResposneDatabase>  mMessageResponse;
    LayoutInflater mInflater;


    public MessageAdapter(Context context , ArrayList<MessageResposneDatabase> message){
        this.mCtx = context;
        this.mMessageResponse=message;
        mInflater = LayoutInflater.from(mCtx);
    }


    @Override
    public int getCount() {
        return mMessageResponse.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MessageViewHolder mHolder;
        if(convertView==null){
            mHolder = new MessageViewHolder();
            convertView = mInflater.inflate(R.layout.message_adapter,null);
            mHolder.mUserName = (TextView) convertView.findViewById(R.id.usernameID);
            mHolder.mTime= (TextView) convertView.findViewById(R.id.timeID);
            mHolder.mMessage = (TextView) convertView.findViewById(R.id.textmessageID);
            mHolder.mLinearID = (LinearLayout) convertView.findViewById(R.id.messageAdapterLilID);
            convertView.setTag(mHolder);
        }else{
            mHolder = (MessageViewHolder) convertView.getTag();
        }

        mHolder.mUserName.setText(mMessageResponse.get(position).getmSenderDisplayName());
        mHolder.mMessage.setText(mMessageResponse.get(position).getmMessage());
        mHolder.mTime.setText(CommonUtils.getTimeDifferance(mMessageResponse.get(position).getmTime()));

        if(mMessageResponse.get(position).isMessageRead()){
            mHolder.mLinearID.setBackgroundColor(mCtx.getResources().getColor(R.color.white));
        }else{

            mHolder.mLinearID.setBackgroundColor(mCtx.getResources().getColor(R.color.blue));
        }

        mHolder.mLinearID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update the database with true and false about read message
                DataBaseQuery.updateMessageStatus(mMessageResponse.get(position).getmID(), "true");
                Intent intent = new Intent(mCtx, MessageDetail.class);
                intent.putExtra("clickposition", String.valueOf(position));
                mCtx.startActivity(intent);
            }
        });


        return convertView;
    }

     class MessageViewHolder {
         TextView mUserName;
         TextView mTime;
         TextView mMessage;
         LinearLayout mLinearID;

    }
}

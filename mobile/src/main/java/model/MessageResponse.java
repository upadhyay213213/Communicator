package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nupadhay on 9/17/2016.
 */
public class MessageResponse implements Parcelable {

    public static final Creator<MessageResponse> CREATOR
            = new Creator<MessageResponse>() {
        public MessageResponse createFromParcel(Parcel in) {
            return new MessageResponse(in);
        }

        public MessageResponse[] newArray(int size) {
            return new MessageResponse[size];
        }
    };



    private  static final String ID= "id";
    private static final String SUBJECT="subject";
    private static final String MESSAGE="message";
    private static final String NOTIF_MESSAGE= "notif_message";
    private static final String TIME= "time";
    private static final String SENDER_ID="sender_id";
    private static final String SENDER_USERNAME="sender_username";
    private static final String SENDER_EMAIL="sender_email";
    private static final String SENDER_DISPLAY_NAME="sender_display_name";


    @Override
    public String toString() {
        return "MessageResponse{" +
                "mID='" + mID + '\'' +
                ", mSubject='" + mSubject + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mNotif_Message='" + mNotif_Message + '\'' +
                ", mTime='" + mTime + '\'' +
                ", mSenderID='" + mSenderID + '\'' +
                ", mmSenderUserName='" + mmSenderUserName + '\'' +
                ", mSenderEmail='" + mSenderEmail + '\'' +
                ", mSenderDisplayName='" + mSenderDisplayName + '\'' +
                '}';
    }


    @SerializedName(ID)
    private String mID;
    @SerializedName(SUBJECT)
    private String mSubject;
    @SerializedName(MESSAGE)
    private String mMessage;
    @SerializedName(NOTIF_MESSAGE)
    private String mNotif_Message;
    @SerializedName(TIME)
    private String mTime;


    @SerializedName(SENDER_ID)
    private String mSenderID;
    @SerializedName(SENDER_USERNAME)
    private String mmSenderUserName;
    @SerializedName(SENDER_EMAIL)
    private String mSenderEmail;
    @SerializedName(SENDER_DISPLAY_NAME)
    private String mSenderDisplayName;

    public MessageResponse() {

    }


    public String getmID() {
        return mID;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getMmSenderUserName() {
        return mmSenderUserName;
    }

    public String getmNotif_Message() {
        return mNotif_Message;
    }

    public String getmSenderDisplayName() {
        return mSenderDisplayName;
    }

    public String getmSenderEmail() {
        return mSenderEmail;
    }

    public String getmSenderID() {
        return mSenderID;
    }

    public String getmSubject() {
        return mSubject;
    }

    public String getmTime() {
        return mTime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public MessageResponse(Parcel in) {

        Bundle bundle = in.readBundle(getClass().getClassLoader());
        mID = bundle.getString(ID);
        mSubject = bundle.getString(SUBJECT);
        mMessage=bundle.getString(MESSAGE);
        mNotif_Message=bundle.getString(NOTIF_MESSAGE);
        mTime=bundle.getString(TIME);
        mSenderID=bundle.getString(SENDER_ID);
        mmSenderUserName=bundle.getString(SENDER_USERNAME);
        mSenderEmail=bundle.getString(SENDER_EMAIL);
        mSenderDisplayName =bundle.getString(SENDER_DISPLAY_NAME);

    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setmNotif_Message(String mNotif_Message) {
        this.mNotif_Message = mNotif_Message;
    }

    public void setMmSenderUserName(String mmSenderUserName) {
        this.mmSenderUserName = mmSenderUserName;
    }

    public void setmSenderDisplayName(String mSenderDisplayName) {
        this.mSenderDisplayName = mSenderDisplayName;
    }

    public void setmSenderEmail(String mSenderEmail) {
        this.mSenderEmail = mSenderEmail;
    }

    public void setmSenderID(String mSenderID) {
        this.mSenderID = mSenderID;
    }

    public void setmSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    @Override

    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(ID,mID);
        bundle.putString(SUBJECT,mSubject);
        bundle.putString(MESSAGE,mMessage);
        bundle.putString(NOTIF_MESSAGE,mNotif_Message);
        bundle.putString(TIME,mTime);
        bundle.putString(SENDER_ID,mSenderID);
        bundle.putString(SENDER_USERNAME,mmSenderUserName);
        bundle.putString(SENDER_EMAIL,mSenderEmail);
        bundle.putString(SENDER_DISPLAY_NAME, mSenderDisplayName);
        out.writeBundle(bundle);
    }
}

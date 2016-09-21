package model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nupadhay on 9/19/2016.
 */
public class MessageResposneDatabase {

    private String mID;
    private String mSubject;

    public boolean isMessageRead() {
        return isMessageRead;
    }

    public void setIsMessageRead(boolean isMessageRead) {
        this.isMessageRead = isMessageRead;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getMmSenderUserName() {
        return mmSenderUserName;
    }

    public void setMmSenderUserName(String mmSenderUserName) {
        this.mmSenderUserName = mmSenderUserName;
    }

    public String getmNotif_Message() {
        return mNotif_Message;
    }

    public void setmNotif_Message(String mNotif_Message) {
        this.mNotif_Message = mNotif_Message;
    }

    public String getmSenderDisplayName() {
        return mSenderDisplayName;
    }

    public void setmSenderDisplayName(String mSenderDisplayName) {
        this.mSenderDisplayName = mSenderDisplayName;
    }

    public String getmSenderEmail() {
        return mSenderEmail;
    }

    public void setmSenderEmail(String mSenderEmail) {
        this.mSenderEmail = mSenderEmail;
    }

    public String getmSenderID() {
        return mSenderID;
    }

    public void setmSenderID(String mSenderID) {
        this.mSenderID = mSenderID;
    }

    public String getmSubject() {
        return mSubject;
    }

    public void setmSubject(String mSubject) {
        this.mSubject = mSubject;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    private String mMessage;
    private String mNotif_Message;
    private String mTime;
    private String mSenderID;
    private String mmSenderUserName;
    private String mSenderEmail;
    private String mSenderDisplayName;
    private boolean isMessageRead;

}

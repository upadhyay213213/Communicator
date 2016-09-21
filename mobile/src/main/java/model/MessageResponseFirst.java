package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nupadhay on 9/17/2016.
 */
public class MessageResponseFirst implements Parcelable {


    public static final Parcelable.Creator<MessageResponseFirst> CREATOR
            = new Parcelable.Creator<MessageResponseFirst>() {
        public MessageResponseFirst createFromParcel(Parcel in) {
            return new MessageResponseFirst(in);
        }

        public MessageResponseFirst[] newArray(int size) {
            return new MessageResponseFirst[size];
        }
    };
    private final static String CONTACTS = "messages";
    @SerializedName(CONTACTS)
    private ArrayList<MessageResponse> mContacts;

    public MessageResponseFirst(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        mContacts = bundle.getParcelableArrayList(CONTACTS);
    }

    public ArrayList<MessageResponse> getContacts() {
        return mContacts;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(CONTACTS, mContacts);
        out.writeBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

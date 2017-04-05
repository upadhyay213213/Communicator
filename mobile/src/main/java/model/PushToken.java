package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class PushToken implements Parcelable {

    public static final Creator<PushToken> CREATOR
            = new Creator<PushToken>() {
        public PushToken createFromParcel(Parcel in) {
            return new PushToken(in);
        }

        public PushToken[] newArray(int size) {
            return new PushToken[size];
        }
    };

    private static final String MD_DEVICE_TOKEN = "md5device_token";


    @SerializedName(MD_DEVICE_TOKEN)
    private String mdDeviceToken;


    public String getMdDeviceToken() {
        return mdDeviceToken;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public PushToken(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        mdDeviceToken = bundle.getString(MD_DEVICE_TOKEN);
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putString(MD_DEVICE_TOKEN, mdDeviceToken);
        out.writeBundle(bundle);
    }
}

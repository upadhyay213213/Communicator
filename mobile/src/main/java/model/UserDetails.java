package model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UserDetails implements Parcelable {

    public static final Creator<UserDetails> CREATOR
            = new Creator<UserDetails>() {
        public UserDetails createFromParcel(Parcel in) {
            return new UserDetails(in);
        }

        public UserDetails[] newArray(int size) {
            return new UserDetails[size];
        }
    };
    private static final String USER_TOEKN = "user_token";
    private static final String DESCRIPTION = "description";
    private static final String displayname = "displayname";
    private static final String email = "email";
    private static final String firstname = "firstname";
    private static final String id = "id";
    private static final String lastname = "lastname";
    private static final String nicename = "nicename";
    private static final String nickname = "nickname";
    private static final String url = "url";
    private static final String username = "username";


    @SerializedName(USER_TOEKN)
    private String mUsertoken;


    @SerializedName(DESCRIPTION)
    private String mDescription;
    @SerializedName(displayname)
    private String mDisplayName;
    @SerializedName(email)
    private String mEmail;
    @SerializedName(firstname)
    private String mFirstname;
    @SerializedName(lastname)
    private String mLastname;
    @SerializedName(id)
    private int mID;
    @SerializedName(nicename)
    private String mNiceName;
    @SerializedName(nickname)
    private String mNickname;
    @SerializedName(url)
    private String mUrl;
    @SerializedName(username)
    private String mUserName;

    public String getmDescription() {
        return mDescription;
    }

    public String getmDisplayName() {
        return mDisplayName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmFirstname() {
        return mFirstname;
    }

    public int getmID() {
        return mID;
    }

    public String getmLastname() {
        return mLastname;
    }

    public String getmNiceName() {
        return mNiceName;
    }

    public String getmNickname() {
        return mNickname;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmUsertoken() {
        return mUsertoken;
    }


    public UserDetails(Parcel in) {
        Bundle bundle = in.readBundle(getClass().getClassLoader());
        mID = bundle.getInt(id);
        mUsertoken = bundle.getString(USER_TOEKN);
        mDescription = bundle.getString(DESCRIPTION);
        mDisplayName = bundle.getString(displayname);
        mEmail = bundle.getString(email);
        mFirstname = bundle.getString(firstname);
        mLastname = bundle.getString(lastname);
        mNiceName = bundle.getString(nicename);
        mNickname = bundle.getString(nickname);
        mUrl = bundle.getString(url);
        mUserName = bundle.getString(username);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Bundle bundle = new Bundle();
        bundle.putInt(id, mID);
        bundle.putString(USER_TOEKN, mUsertoken);
        bundle.putString(DESCRIPTION, mDescription);
        bundle.putString(displayname, mDisplayName);
        bundle.putString(email, mEmail);
        bundle.putString(firstname, mFirstname);
        bundle.putString(lastname, mLastname);
        bundle.putString(nicename, mNiceName);
        bundle.putString(nickname, mNickname);
        bundle.putString(username, mUserName);
        bundle.putString(url, mUrl);
        out.writeBundle(bundle);
    }

}

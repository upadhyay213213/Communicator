package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import appconstant.AppConstatnts;
import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import commonmodules.CommonUtils;
import model.UserDetails;
import subcodevs.communicator.HomeScreen;
import subcodevs.communicator.R;
import subcodevs.communicator.SettingScreen;
import webclient.RequestManager;
import webclient.RequestResponseInterface;

/**
 * Created by nupadhay on 9/15/2016.
 */
public class LoginScreen extends BaseActivityWear implements View.OnClickListener, RequestResponseInterface {
    private EditText mLogin;
    private EditText mPassword;
    private Button mLoginButton;
    private String mMessagePush;
    private String mMessageID;
    private boolean isFromMmessageDetail = false;
    private ImageView mHeaderImageView;
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (PrefrensUtils.getDeviceToken(this).isEmpty()) {
            setContentView(R.layout.loginscreen);
            initializeUI();
            RequestManager.mRequestManager.responseInterface = this;
        } else {
            setContentView(R.layout.homescreen);
            startActivity(new Intent(this, HomeScreen.class));
        }

        if (getIntent().getStringExtra("MessageIDLogin") != null) {
            mMessageID = getIntent().getStringExtra("MessageIDLogin");
        }
        System.out.println("LOGINURL" + PrefrensUtils.getBaseURL(this));
    }

    private void initializeUI() {
        mLogin = (EditText) findViewById(R.id.loginID);
        mPassword = (EditText) findViewById(R.id.passwordD);
        mLoginButton = (Button) findViewById(R.id.loginButtonID);
        mHeaderImageView = (ImageView) findViewById(R.id.headerImageID);
        mLoginButton.setOnClickListener(this);
        mPassword.setTypeface(mLogin.getTypeface());

        if (!PrefrensUtils.getUserName(this).isEmpty() && !PrefrensUtils.getPassword(this).isEmpty()) {
            mLogin.setText(PrefrensUtils.getUserName(this));
            mLogin.setSelection(mLogin.getText().toString().length());
            mPassword.setText(PrefrensUtils.getPassword(this));
        }


        mHeaderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                if(counter==3){
                    counter=0;
                  showChangeLangDialog();
                }

            }
        });
    }

    private boolean validateLoginDetails(String username, String password) {
        boolean isUserSuccess;
        if (username.isEmpty()) {
            mLogin.setError("Please enter your username/email");
            isUserSuccess = false;
        } else if (password.isEmpty()) {
            mPassword.setError("Please enter your password");
            isUserSuccess = false;
        } else {
            isUserSuccess = true;
        }
        return isUserSuccess;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.loginButtonID:
                if (CommonUtils.isNetworkAvailable(LoginScreen.this)) {
                    if (validateLoginDetails(mLogin.getText().toString().trim(), mPassword.getText().toString().trim())) {
                        PrefrensUtils.setPassword(LoginScreen.this, mPassword.getText().toString().trim());
                        startProgress();
                        RequestManager.getInstance().LoginRequest(mLogin.getText().toString().trim(), mPassword.getText().toString().trim());
                    }
                } else {
                    buildAlertMessageInterNet(LoginScreen.this, "Please check your internet connection.");
                }
                break;

        }
    }

    @Override
    public void responseListener(Object o, String calltype) {
        stopProgress();
        if (o.toString().contains("error")) {

        } else {
            if (calltype.equals("Login")) {
                Gson gson = new Gson();
                UserDetails response = gson.fromJson(o.toString(), UserDetails.class);
                if (response != null) {
                    addLoginData(response);
                }
            }
        }
    }


    private void addLoginData(UserDetails response) {
        PrefrensUtils.setDeviceToken(this, response.getmUsertoken());
        PrefrensUtils.setUserID(this, String.valueOf(response.getmID()));
        PrefrensUtils.setUserName(this, response.getmUserName());
        if (mMessageID != null && !mMessageID.isEmpty()) {
            Intent intent = new Intent(this, MessageDetail.class);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, HomeScreen.class));
        }
        this.finish();
    }

    @Override
    public void errorListener(VolleyError error) {
        stopProgress();
        stopProgress();
        try {
            handleErrorCase(this, error.networkResponse.statusCode);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}

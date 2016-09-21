package subcodevs.communicator.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import org.json.JSONObject;

import appconstant.AppConstatnts;
import apputils.PrefrensUtils;
import commonmodules.BaseActivityWear;
import model.UserDetails;
import subcodevs.communicator.HomeScreen;
import subcodevs.communicator.R;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(PrefrensUtils.getDeviceToken(this).isEmpty()){
            setContentView(R.layout.loginscreen);
            initializeUI();
            RequestManager.mRequestManager.responseInterface = this;
        }else{
            setContentView(R.layout.homescreen);
            startActivity(new Intent(this, HomeScreen.class));
        }

        if (getIntent().getStringExtra("messageID") != null) {
            mMessagePush = getIntent().getStringExtra("message");
            mMessageID = getIntent().getStringExtra("messageID");
        }

    }

    private void initializeUI(){
        mLogin = (EditText) findViewById(R.id.loginID);
        mPassword= (EditText) findViewById(R.id.passwordD);
        mLoginButton = (Button) findViewById(R.id.loginButtonID);
        mLoginButton.setOnClickListener(this);

        if(!PrefrensUtils.getUserName(this).isEmpty() && !PrefrensUtils.getPassword(this).isEmpty()){
            mLogin.setText(PrefrensUtils.getUserName(this));
            mPassword.setText(PrefrensUtils.getPassword(this));
        }
    }

    private boolean validateLoginDetails(String username,String password){
        boolean isUserSuccess;
        if(username.isEmpty()){
            mLogin.setError("Please enter your username/email");
            isUserSuccess=false;
        }else if(password.isEmpty()){
            mPassword.setError("Please enter your password");
            isUserSuccess=false;
        }else{
            isUserSuccess=true;
        }
        return isUserSuccess;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.loginButtonID:
                if(validateLoginDetails(mLogin.getText().toString().trim(),mPassword.getText().toString().trim())){
                    PrefrensUtils.setPassword(LoginScreen.this,mPassword.getText().toString().trim());
                    startProgress();
                    RequestManager.getInstance().LoginRequest(mLogin.getText().toString().trim(),mPassword.getText().toString().trim());
                }

                break;

        }
    }

    @Override
    public void responseListener(Object o,String calltype) {
        stopProgress();
        if(o.toString().contains("error")){

        }else{
            if(calltype.equals("Login")){
                Gson gson = new Gson();
                UserDetails response = gson.fromJson(o.toString(), UserDetails.class);
                if(response!=null){
                    addLoginData(response);
                }
            }
        }
    }


    private void addLoginData(UserDetails response){
        PrefrensUtils.setDeviceToken(this, response.getmUsertoken());
        PrefrensUtils.setUserID(this, String.valueOf(response.getmID()));
        PrefrensUtils.setUserName(this, response.getmUserName());
        if(!mMessageID.isEmpty()){
            Intent intent = new Intent(this, MessageDetail.class);
            intent.putExtra("message", mMessagePush);
            intent.putExtra("messageID", mMessageID);
            startActivity(intent);
        }else {
            startActivity(new Intent(this, HomeScreen.class));
        }

        this.finish();
    }

    @Override
    public void errorListener(VolleyError error) {
        stopProgress();
        handleErrorCase(this,error.networkResponse.statusCode);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}

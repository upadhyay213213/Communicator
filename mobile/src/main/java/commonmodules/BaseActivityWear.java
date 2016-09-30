package commonmodules;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import apputils.PrefrensUtils;
import subcodevs.communicator.ui.LoginScreen;

/**
 * Created by nupadhay on 10/14/2015.
 */
public class BaseActivityWear extends FragmentActivity {

    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog=new ProgressDialog(this);
    }

    public void startProgress(){
        mProgressDialog.setMessage("Please Wait..");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    public void stopProgress(){
        mProgressDialog.dismiss();
    }

    public void buildAlertMessageNoGps(final Activity ctx, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
       .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        if(message.equalsIgnoreCase("Your session is expired. Please login again.")){
                            startActivity(new Intent(ctx, LoginScreen.class));
                            PrefrensUtils.setDeviceToken(ctx,"");
                            ctx.finish();
                            dialog.cancel();
                        }else{
                            dialog.cancel();
                        }
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buildAlertMessage(final Activity ctx, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buildAlertMessageInterNet(final Activity ctx, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void buildAlertMessageNoWearOrPlayServices(final Activity ctx, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        ctx.finish();
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }



    public void handleErrorCase(Activity ctx,int response){
        String message="Something went wrong. Please try again later.";
        switch (response){
            case 404:
            message="User not found.";
                break;
            case 401:
                message = "Your session is expired. Please login again.";
                break;
            case 400:
                message ="Bad Request.";
                        break;
            default: message="Something went wrong. Please try again later.";

        }
        buildAlertMessageNoGps(ctx, message);
    }


}

package commonmodules;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ProgressBar;

import subcodevs.communicator.R;


/**
 * Created by nupadhay on 10/14/2015.
 */
public class BaseActivity extends FragmentActivity {

    private ProgressBar mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base);
        mProgressDialog= (ProgressBar) findViewById(R.id.timer_progress);
    }

    public void startProgress(){
        mProgressDialog.setVisibility(View.VISIBLE);
    }

    public void stopProgress(){
        mProgressDialog.setVisibility(View.GONE);
    }

    public void buildAlertMessageNoGps(final Activity ctx, final String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
       .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {

                        if(message.equals("Your Session is expired.Please login again."))
                        {
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
        builder.setTitle("Success")
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


    public void handleErrorCase(Activity ctx,int response){
        String message="Error Server Please try again";
        switch (response){
            case 404:
            message="User not found";
                break;
            case 401:
                message = "Your Session is expired.Please login again.";
                break;
            case 400:
                message ="Bad Request";
                        break;
            default: message="Some thing got worng with server.Please try again later.";

        }
        buildAlertMessageNoGps(ctx, message);
    }


}

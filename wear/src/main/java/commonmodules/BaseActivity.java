package commonmodules;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    public void buildAlertMessageNoGps(final Activity ctx, final String message,String error) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ctx);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alert, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        final TextView edt = (TextView) dialogView.findViewById(R.id.messageComID);
        final TextView errorMsg = (TextView) dialogView.findViewById(R.id.alertTitle);
        final Button btn = (Button) dialogView.findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
                b.cancel();
            }
        });
        edt.setText(message);
        errorMsg.setText("Error!");

        b.show();
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
        String message="Something went wrong. Please try again later.";
        switch (response){
            case 404:
            message="Please login through Phone before proceeding further.";
                break;
            case 401:
                message = "Please login through Phone before proceeding further.";
                break;
            case 400:
                message ="Bad Request.";
                        break;
            case 101:
            message="Something went wrong. Please try again later.";
                break;
                default: message="Something went wrong. Please try again later.";
               break;
        }
        buildAlertMessageNoGps(ctx, message,"");
    }


    public  void showChangeLangDialog(Context context,String message,String error) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alert, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog b = dialogBuilder.create();
        final TextView edt = (TextView) dialogView.findViewById(R.id.messageComID);
        final TextView errorMsg = (TextView) dialogView.findViewById(R.id.alertTitle);
        final Button btn = (Button) dialogView.findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
                b.cancel();
            }
        });
        edt.setText(message);
        errorMsg.setText(error);

        b.show();
    }


}

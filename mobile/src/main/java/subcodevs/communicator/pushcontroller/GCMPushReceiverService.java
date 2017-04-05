package subcodevs.communicator.pushcontroller;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.gcm.GcmListenerService;

import apputils.PrefrensUtils;
import subcodevs.communicator.R;
import subcodevs.communicator.ui.MessageDetail;



public class GCMPushReceiverService extends GcmListenerService {

    //This method will be called on every new message received
    @Override
    public void onMessageReceived(String from, Bundle data) {
        //Getting the message from the bundle
        String message = data.getString("message");
        String messageID = data.getString("message_id");

        //Displaying a notification with the message
        try{
            sendNotification(message, messageID);
        }catch (Exception e){

        }

    }

    //This method is generating a notification and displaying the notification
    private void sendNotification(String message, String mesID) {
        Intent intent;
        intent = new Intent(this, MessageDetail.class);
        intent.putExtra("messageID",mesID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PrefrensUtils.setPushID(this, mesID);

        int requestCode = 0;
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder noBuilder;
        noBuilder = new NotificationCompat.Builder(this).setColor(this.getResources().getColor(R.color.assi))
                    .setSmallIcon(R.drawable.imageedit).setContentTitle("Communicator")
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(viewPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        notificationManager.notify(0, noBuilder.build());
    }
}
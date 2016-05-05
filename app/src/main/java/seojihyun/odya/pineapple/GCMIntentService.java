package seojihyun.odya.pineapple;

import android.content.Intent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import seojihyun.odya.pineapple.protocol.DataManager;

import static seojihyun.odya.pineapple.CommonUtilities.SENDER_ID;
import static seojihyun.odya.pineapple.CommonUtilities.displayMessage;
public class GCMIntentService extends GCMBaseIntentService {
    DataManager dataManager;

    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);

    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        //Log.d("NAME", MainActivity.name);
        dataManager = (DataManager) context.getApplicationContext();
        ServerUtilities.register(context, dataManager.userData.getUser_phone(), dataManager.userData.getUser_name(), registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");

        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        //Protocol.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.cast_ic_notification_on;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(icon)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setWhen(when);
        Notification notification = builder.build();
        //Notification notification = new Notification(icon, message, when);  ///0503

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, BackgroundActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        //notification.setLatestEventInfo(context, title, message, intent); //0503

        builder = new Notification.Builder(context)
                .setContentIntent(intent)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);

    }

}

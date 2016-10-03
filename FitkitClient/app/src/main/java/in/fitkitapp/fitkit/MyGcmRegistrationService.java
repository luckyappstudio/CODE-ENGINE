package in.fitkitapp.fitkit;

/**
 * Created by Akhilkamsala on 6/17/2016.
 */
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class MyGcmRegistrationService extends IntentService {

    private static final String TAG = "MyGcmRegService";

    //Refer : https://developers.google.com/cloud-messaging/gcm#senderid
    private static final String ANDROID_PROJECT_SENDER_ID ="335269014154";

    public MyGcmRegistrationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // Initially this call goes out to the network to retrieve the token, subsequent calls are local.
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(ANDROID_PROJECT_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            sendRegistrationToServer(this, token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, make sure to attempt the update at a later time.
        }
    }

    private void sendRegistrationToServer(Context context, String token) {
        Hotline.getInstance(context).updateGcmRegistrationToken(token);
    }
}
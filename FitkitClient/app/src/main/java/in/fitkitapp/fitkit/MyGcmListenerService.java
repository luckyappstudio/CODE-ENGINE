package in.fitkitapp.fitkit;

/**
 * Created by Akhilkamsala on 6/17/2016.
 */
import android.os.Bundle;

import com.freshdesk.hotline.Hotline;
import com.google.android.gms.gcm.GcmListenerService;

public class MyGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Hotline instance = Hotline.getInstance(this);
        if (instance.isHotlineNotification(data)) {
            instance.handleGcmMessage(data);
            return;
        } else {
            // Process your app's notification messages
        }
    }
}
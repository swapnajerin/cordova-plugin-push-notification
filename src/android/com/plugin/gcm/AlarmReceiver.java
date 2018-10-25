package com.plugin.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Swapna on 21-09-2017.
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    public Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        try {
			String myType = intent.getStringExtra("myType"); 
			Intent background = new Intent(context, StartTimer.class);
			background.putExtra("myType",myType);  
			context.startService(background); 
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}

package com.plugin.gcm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import com.plugin.gcm.AlarmReceiver;
 

public class StartUpReceiver extends BroadcastReceiver {
    private final String BOOT_COMPLETED_ACTION = "android.intent.action.BOOT_COMPLETED";
	
    public StartUpReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
		try {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent mapTracker = new Intent(context, AlarmReceiver.class);
			mapTracker.putExtra("myType","B");
			PendingIntent pendingIntentMT = PendingIntent.getBroadcast(context, 0, mapTracker, PendingIntent.FLAG_UPDATE_CURRENT);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, 6);
			calendar.set(Calendar.MINUTE, 0);
			alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntentMT);

			AlarmManager alarmManager1 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent mapTracker1 = new Intent(context, AlarmReceiver.class);
			mapTracker1.putExtra("myType","S"); 
			PendingIntent pendingIntentMT1 = PendingIntent.getBroadcast(context, 1,mapTracker1, PendingIntent.FLAG_UPDATE_CURRENT);
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTimeInMillis(System.currentTimeMillis()); 
			calendar1.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			calendar1.set(Calendar.HOUR_OF_DAY, 15);
			calendar1.set(Calendar.MINUTE, 0);
			alarmManager1.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY * 7, pendingIntentMT1);

			AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent mapTracker2 = new Intent(context, AlarmReceiver.class);
			mapTracker2.putExtra("myType","G");  
			PendingIntent pendingIntentMT2 = PendingIntent.getBroadcast(context, 2,mapTracker2, PendingIntent.FLAG_UPDATE_CURRENT);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTimeInMillis(System.currentTimeMillis()); 
			calendar2.set(Calendar.HOUR_OF_DAY, 22);
			calendar2.set(Calendar.MINUTE, 0);
			alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntentMT2);

		} catch (Exception e) { 
		}
	}
}
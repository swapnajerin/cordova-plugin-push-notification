package com.plugin.gcm;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.os.Build;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import com.plugin.gcm.AlarmReceiver;
import com.google.android.gcm.GCMBaseIntentService;

@SuppressLint("NewApi")
public class GCMIntentService extends GCMBaseIntentService {

  private static final String TAG = "GCMIntentService";

  public GCMIntentService() {
    super("GCMIntentService");
  }

  @Override
  public void onRegistered(Context context, String regId) {

    Log.v(TAG, "onRegistered: " + regId);

    JSONObject json;

    try {
	
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
      json = new JSONObject().put("event", "registered");
      json.put("regid", regId); 

      Log.v(TAG, "onRegistered: " + json.toString());

      // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
      // In this case this is the registration ID 
			SharedPreferences.Editor edit = prefs.edit();
			edit.putString("Token", regId);
			edit.commit(); 

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

      PushPlugin.sendJavascript(json);

    } catch (JSONException e) {
      // No message to the user is sent, JSON failed
      Log.e(TAG, "onRegistered: JSON exception");
    }
  }
  
    public int generateRandom()
    {
        Random rn = new Random(10000);
        return  rn.nextInt();

    }
    public String getJsonValue(String jsonKey, String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            if (jsonObject.has(jsonKey) && !jsonObject.get(jsonKey).toString().equals("null"))
                return jsonObject.get(jsonKey).toString();
        } catch (JSONException e) {
        }
        return "";
    }

  @Override
  public void onUnregistered(Context context, String regId) {
    Log.d(TAG, "onUnregistered - regId: " + regId);
  }

  @Override
  protected void onMessage(Context context, Intent intent) {
    Log.d(TAG, "onMessage - context: " + context);

    // Extract the payload from the message
    Bundle extras = intent.getExtras();
    if (extras != null) {
      // if we are in the foreground, just surface the payload, else post it to the statusbar
      if (PushPlugin.isInForeground()) {
        extras.putBoolean("foreground", true);
        PushPlugin.sendExtras(extras);
      } else {
        extras.putBoolean("foreground", false);
		 
      } 
	   
    String title = "TITLE"; 
    String message = "MESSAGE"; 
    int defaults = Notification.DEFAULT_ALL;
    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
   NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                    .setDefaults(defaults)
                    .setSmallIcon(context.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setTicker(title)
                    .setContentIntent(contentIntent)
					.setStyle(new NotificationCompat.BigTextStyle().bigText(message)) 
					.setContentText(message)
                    .setAutoCancel(true);
    String appName = getAppName(this);
    int notId = generateRandom();
    mNotificationManager.notify((String) appName, notId, mBuilder.build());

	  // Send a notification if there is a message
        if (extras.getString("data") != null && extras.getString("data").length() != 0) {
            createNotification(context, extras);
        }
    }
  }

  public void createNotification(Context context, Bundle extras) {
    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    String appName = getAppName(this);

    Intent notificationIntent = new Intent(this, PushHandlerActivity.class);
    notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    notificationIntent.putExtra("pushBundle", extras);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    int defaults = Notification.DEFAULT_ALL;

    if (extras.getString("defaults") != null) {
      try {
        defaults = Integer.parseInt(extras.getString("defaults"));
      } catch (NumberFormatException e) {}
    }

    String title = getJsonValue("title", extras.getString("data")); 
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context)
                    .setDefaults(defaults)
                    .setSmallIcon(context.getApplicationInfo().icon)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setTicker(title)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true);

    String message = getJsonValue("message", extras.getString("data"));// extras.getString("message");
//if (message == null) 
	message = "<missing message content>";
				mBuilder.setStyle(new NotificationCompat.BigTextStyle()
					.bigText(message));
                mBuilder.setContentText(message);


 
    mBuilder.setColor(0xffd10000);
	 

    String msgcnt = extras.getString("msgcnt");
    if (msgcnt != null) {
      mBuilder.setNumber(Integer.parseInt(msgcnt));
    }
	

    int notId = generateRandom();

    mNotificationManager.notify((String) appName, notId, mBuilder.build());
  }

  private static String getAppName(Context context) {
    CharSequence appName = context.getPackageManager()
      .getApplicationLabel(context.getApplicationInfo());

    return (String) appName;
  }

  private int getNotificationIcon(Context context) {
    boolean isLollipop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    if (isLollipop) {
      Resources r = getResources();

      return r.getIdentifier("notification_icon", "raw", context.getPackageName());
    }

    return context.getApplicationInfo().icon;
  }

  @Override
  public void onError(Context context, String errorId) {
    Log.e(TAG, "onError - errorId: " + errorId);
  }

}

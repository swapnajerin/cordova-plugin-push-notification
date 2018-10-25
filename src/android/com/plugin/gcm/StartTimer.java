package com.plugin.gcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import android.widget.Toast;

/**
 * Created by Swapna on 22-09-2017.
 */

public class StartTimer extends Service {
    private boolean isRunning;
    private Context context;
    private String myType; 
    private Thread backgroundThread;
    private Runnable myTask = new Runnable() {
        public void run() {
			if(myType.equals("B")) {
				GetData("GetBirthdayAnniversaryAlerts", "Good Morning", 12);
			}
			else if(myType.equals("S")) {
				GetData("GetMyPendingSundayAttendance", "My Sunday Attendance", 2);
				GetData("GetPendingSundayAttendance", "Sunday Attendance", 3);
			}
			else if(myType.equals("G")) {
				GetData("GetMyPendingBGAttendance", "My BG Attendance", 4);
				GetData("GetPendingBGAttendance", "BG Attendance", 5);
				GetData("GetMyPendingFollowUpNewComer", "My New Comers", 6);
				GetData("GetPendingFollowUpNewComer", "New Comers", 7);
				GetData("GetMyPendingFollowUpSpecialVisit", "My Special Visits", 8);
				GetData("GetPendingFollowUpSpecialVisit", "Special Visits", 9);
				GetData("GetMyPendingFollowUpHouseVisit", "My House Visits", 10);
				GetData("GetPendingFollowUpHouseVisit", "House Visits", 11);
			}
            stopSelf();
        }
    };
	
    public void GetData(String th_Url, String title, int Id_1)
    {
        try {

                InputStream inputStream = null;
                String result = ""; 
				String myUrl = "http://blessingcenter.azurewebsites.net/AppService/" +  th_Url;
                URL url_connection = new URL(myUrl);

                HttpURLConnection conn = (HttpURLConnection) url_connection.openConnection();
                conn.setDoOutput(true);

                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setInstanceFollowRedirects(false);
                conn.setRequestMethod("GET");

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
				String Token = prefs.getString("Token", "");
                conn.setRequestProperty("Token", Token);


                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                result = sb.toString(); 
				if (result != null && !result.isEmpty() && !result.equals("null"))  {
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				   Intent notificationIntent = new Intent(context, PushHandlerActivity.class);
				   notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

				   PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					int defaults = Notification.DEFAULT_ALL;


					NotificationCompat.Builder mBuilder =
							new NotificationCompat.Builder(context)
									.setContentTitle(title)
									.setDefaults(defaults)
									.setSmallIcon(context.getApplicationInfo().icon)
									.setWhen(System.currentTimeMillis())
									.setContentIntent(contentIntent)
									.setAutoCancel(true);
								
					mBuilder.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(result));
					mBuilder.setContentText(result);
					mBuilder.setColor(0xffd10000);
					int notId = Id_1;


					mBuilder.setNumber(notId);

					notId = Id_1;

					String appName = getAppName(context);
					mNotificationManager.notify((String) appName, notId, mBuilder.build()); 
				}
            } catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
    }
    public int generateRandom()
    {
        Random rn = new Random(10000);
        return  rn.nextInt();

    }
    private   String getAppName(Context context) {
        CharSequence appName = context.getPackageManager()
                .getApplicationLabel(context.getApplicationInfo());

        return (String) appName;
    }
    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
			myType = intent.getStringExtra("myType"); 
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
package www.siteurl.in.srsadmin.activities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.api.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 23/4/18.
 */

public class DemoSyncJob extends Job {

    public static final String TAG = "job_demo_tag";

    NotificationManager CnNotificationManager;
    String sessionId, uid;
    SharedPreferences.Editor editor;
    SharedPreferences loginPref;

    @Override
    @NonNull
    protected Result onRunJob(Params params) {

        loginPref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        boolean isConnected = ConnectivityReceiver.isConnected();
        if (!sessionId.equals(null) && (isConnected == true)) {

            testNotification();
        }

        return Result.SUCCESS;
    }


    public static void scheduleJob() {
        new JobRequest.Builder(DemoSyncJob.TAG)
                .setPeriodic(setPeriod())
                .setRequiredNetworkType(JobRequest.NetworkType.ANY)
                .setPersisted(true)
                .build()
                .schedule();
    }

    public static long setPeriod() {
        int currentApi = Build.VERSION.SDK_INT;
        if (currentApi == Build.VERSION_CODES.M) {
            return 60000;
        }
        return 900000; //for Nougat 15 min once it's calling
    }


    public void testNotification() {

        StringRequest testNotification = new StringRequest(Request.Method.POST, Constants.notifyadmin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            String data = jsonObject.getString("List of tasks");
                            JSONArray jsonArray = new JSONArray(data);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                String ticketIds = dataObject.getString("ticket_id");
                                String status = dataObject.getString("ticket_status");

                                if (status.equals("New") || status.equals("new")) {
                                    CounterNotification(getContext(), "SRS Admin", "New Task Received", "New Service Request");

                                }
                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        testNotification.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getContext()).addtorequestqueue(testNotification);
    }

    public void CounterNotification(Context context, String msg, String msgtxt, String msgAlerts) {
        Intent notificationIntent = new Intent(context, ViewTickets.class);
        notificationIntent.putExtra("ticketStatus", "listofallticket");
        PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 1, notificationIntent, 0);
        android.support.v4.app.NotificationCompat.Builder mcBuilder = new android.support.v4.app.NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setTicker(msgAlerts)
                .setContentTitle(msg)
                .setContentText(msgtxt)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent2);
        mcBuilder.setContentIntent(pendingIntent2);
        mcBuilder.setDefaults(android.support.v4.app.NotificationCompat.DEFAULT_SOUND);
        mcBuilder.setAutoCancel(true);
        CnNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        CnNotificationManager.notify(1, mcBuilder.build());
    }
}



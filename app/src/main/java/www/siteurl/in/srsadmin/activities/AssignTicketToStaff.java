package www.siteurl.in.srsadmin.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.adapters.StaffListAdapter;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.objects.StaffList;


public class AssignTicketToStaff extends AppCompatActivity {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    String sessionId, uid;
    SharedPreferences.Editor editor;
    Dialog alertDialog;
    ListView staffView;
    RelativeLayout relativeLayout;
    String ticketId;
    ArrayList<StaffList> staffListForListView = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_ticket_to_staff);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //  ticketId = getIntent().getExtras().getString("ticketID");

        //Toolbar
        mToolbar = findViewById(R.id.assign_tickets_toolbar);
        mToolbar.setTitle("Select The Staff");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        ticketId = loginPref.getString("ticketID", null);
        editor = loginPref.edit();

        //Initializing views
        staffView = findViewById(R.id.staffList);
        relativeLayout = findViewById(R.id.assignTicketsLayout);
        getStaffListFromServer();

        //setOnClickListener for Assign task to Staff
        staffView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                assignTaskToStaff(position);

            }
        });
    }

    //this is the method for assigned task to staff
    private void assignTaskToStaff(final int position) {

        final AlertDialog loadingDialog = new SpotsDialog(AssignTicketToStaff.this, R.style.Loading);
        loadingDialog.show();

        StringRequest taskRequest = new StringRequest(Request.Method.POST,
                Constants.assignTaskToStaff, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();

                try {
                    JSONObject taskObject = new JSONObject(response);
                    String error = taskObject.getString("Error");
                    String message = taskObject.getString("Message");

                    if (error.equals("true")) {

                        editor.putString("loginName", "");
                        editor.putString("loginEmail", "");
                        editor.putString("loginUserId", "");
                        editor.putString("loginSid", "");
                        editor.putString("user_group_id", "");
                        editor.putString("loginPhone", "");
                        editor.putString("loginAddrs", "");
                        editor.putString("loginRole", "");
                        editor.commit();
                        finishAffinity();

                        Toast.makeText(AssignTicketToStaff.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AssignTicketToStaff.this, AdminLogin.class);
                        startActivity(intent);
                    }


                    if (error.equals("false")) {
                        showrDialog(error, message);
                    }

                    StaffListAdapter staffListAdapter = new StaffListAdapter(AssignTicketToStaff.this, R.layout.staff_list_adapter, staffListForListView);
                    staffView.setAdapter(staffListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                }
                if (error instanceof ServerError) {
                    Toast.makeText(AssignTicketToStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AssignTicketToStaff.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AssignTicketToStaff.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AssignTicketToStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AssignTicketToStaff.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AssignTicketToStaff.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AssignTicketToStaff.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                params.put("ticket_id", ticketId);
                params.put("assigned_to", staffListForListView.get(position).getUser_id());
                return params;
            }
        };
        taskRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(taskRequest);
    }


    //this is the method to get staff list from server
    private void getStaffListFromServer() {

        staffListForListView = new ArrayList<>();
        final AlertDialog loadingDialog = new SpotsDialog(AssignTicketToStaff.this, R.style.Loading);
        loadingDialog.show();

        StringRequest staffRequest = new StringRequest(Request.Method.POST,
                Constants.viewStaffList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();

                try {
                    JSONObject staffObject = new JSONObject(response);
                    String error = staffObject.getString("Error");
                    String message = staffObject.getString("Message");

                    if (error.equals("true")) {

                        editor.putString("loginName", "");
                        editor.putString("loginEmail", "");
                        editor.putString("loginUserId", "");
                        editor.putString("loginSid", "");
                        editor.putString("user_group_id", "");
                        editor.putString("loginPhone", "");
                        editor.putString("loginAddrs", "");
                        editor.putString("loginRole", "");
                        editor.commit();
                        finishAffinity();

                        Toast.makeText(AssignTicketToStaff.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AssignTicketToStaff.this, AdminLogin.class);
                        startActivity(intent);
                    }
                    if (error.equals("false")) {
                        JSONArray staffListArray = staffObject.getJSONArray("List of staffs");
                        for (int i = 0; i < staffListArray.length(); i++) {
                            JSONObject eachStaff = staffListArray.getJSONObject(i);

                            staffListForListView.add(new StaffList(eachStaff.getString("user_id"),
                                    eachStaff.getString("name"),
                                    eachStaff.getString("email"),
                                    eachStaff.getString("password"),
                                    eachStaff.getString("user_group_id"),
                                    eachStaff.getString("phone_no"),
                                    eachStaff.getString("address"),
                                    eachStaff.getString("gps_location")
                            ));

                        }
                    }

                    StaffListAdapter staffListAdapter = new StaffListAdapter(AssignTicketToStaff.this, R.layout.staff_list_adapter, staffListForListView);
                    staffView.setAdapter(staffListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                if (error.networkResponse != null) {
                    parseVolleyError(error);
                }
                if (error instanceof ServerError) {
                    Toast.makeText(AssignTicketToStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AssignTicketToStaff.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AssignTicketToStaff.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AssignTicketToStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AssignTicketToStaff.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AssignTicketToStaff.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AssignTicketToStaff.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("user_id", uid);
                params.put("sid", sessionId);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        staffRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(staffRequest);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(AssignTicketToStaff.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(AssignTicketToStaff.this);
            loginErrorBuilder.setTitle("Error");
            loginErrorBuilder.setMessage(message);
            loginErrorBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            loginErrorBuilder.show();
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException errorr) {
        }
    }

    //To show assigned task result in server
    private void showrDialog(final String error, String message) {

        android.app.AlertDialog.Builder resultBuilder = new android.app.AlertDialog.Builder(AssignTicketToStaff.this);
        resultBuilder.setTitle("Srs Admin");
        resultBuilder.setMessage(message);
        resultBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (error.equals("false")) {
                    startActivity(new Intent(AssignTicketToStaff.this, MainActivity.class));
                    finish();
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        resultBuilder.setCancelable(false);
        resultBuilder.show();

    }
}

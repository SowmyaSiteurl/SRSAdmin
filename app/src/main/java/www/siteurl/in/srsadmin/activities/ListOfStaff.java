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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import www.siteurl.in.srsadmin.adapters.ListOfStaffs;
import www.siteurl.in.srsadmin.adapters.StaffListAdapter;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.objects.StaffList;
import www.siteurl.in.srsadmin.objects.Tickets;

public class ListOfStaff extends AppCompatActivity {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    RelativeLayout staffListLayout;
    Dialog alertDialog;
    private ArrayList<StaffList> staffs = new ArrayList<>();
    ListView staffView;
    TextView noStaff;
    StaffList staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_list);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        mToolbar = findViewById(R.id.staff_list_toolbar);
        mToolbar.setTitle("Staff List");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        staffView = findViewById(R.id.staffLists);
        staffListLayout = findViewById(R.id.staffListLayout);
        noStaff = findViewById(R.id.noStaffs);

        toGetStaffListFromServer();
    }


    //this is the method to get StaffList from server
    private void toGetStaffListFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(ListOfStaff.this, R.style.Loading);
        loadingDialog.show();

        StringRequest getAdminDetails = new StringRequest(Request.Method.POST, Constants.staffList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {
                    JSONObject staffObject = new JSONObject(response);
                    String error = staffObject.getString("Error");
                    String message = staffObject.getString("Message");

                    if (error.equals("false")) {
                        JSONArray staffListArray = staffObject.getJSONArray("List of staffs");
                        for (int i = 0; i < staffListArray.length(); i++) {
                            JSONObject eachStaff = staffListArray.getJSONObject(i);

                            String staffId = eachStaff.getString("user_id");
                            String name = eachStaff.getString("name");
                            String email = eachStaff.getString("email");
                            String password = eachStaff.getString("password");
                            String usergroupid = eachStaff.getString("user_group_id");
                            String phone = eachStaff.getString("phone_no");
                            String address = eachStaff.getString("address");
                            String gpslocation = eachStaff.getString("gps_location");

                            editor.putString("staffId", staffId);
                            editor.putString("staffname", name);
                            editor.putString("staffemail", email);
                            editor.putString("staffphone_no", phone);
                            editor.putString("staffaddress", address);
                            editor.putString("stafflocation", gpslocation);
                            editor.commit();

                            staffList = new StaffList(staffId, name, email, password, usergroupid, phone, address, gpslocation);
                            staffs.add(staffList);

                        }
                    }

                    if (staffs.size() > 0) {
                        ListOfStaffs listOfStaffs = new ListOfStaffs(ListOfStaff.this, R.layout.list_of_staffs, staffs);
                        staffView.setAdapter(listOfStaffs);
                    } else {
                        noStaff.setVisibility(View.VISIBLE);
                        staffView.setVisibility(View.GONE);
                    }
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
                    Toast.makeText(ListOfStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ListOfStaff.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ListOfStaff.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ListOfStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ListOfStaff.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ListOfStaff.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ListOfStaff.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

        getAdminDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(getAdminDetails);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(ListOfStaff.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(ListOfStaff.this);
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

}

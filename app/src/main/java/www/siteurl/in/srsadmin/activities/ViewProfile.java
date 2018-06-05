package www.siteurl.in.srsadmin.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.objects.AdminProfile;

public class ViewProfile extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    RelativeLayout rootLayout;
    private MaterialEditText Name, Email, Phone, Address, mLatLong;
    Dialog alertDialog;
    private AdminProfile mAdminProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        mToolbar = findViewById(R.id.profile_toolbar);
        mToolbar.setTitle("Profile");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        Name = findViewById(R.id.edtName);
        Email = findViewById(R.id.edtEmail);
        Phone = findViewById(R.id.edtPhone);
        Address = findViewById(R.id.edtAddress);
        rootLayout = findViewById(R.id.profileRootLayout);

        if (checkInternetConnection())
            toGetAdminDetailsFromServer();
    }

    //this is the method to get Admin Details from server
    private void toGetAdminDetailsFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(ViewProfile.this, R.style.Loading);
        loadingDialog.show();

        StringRequest getAdminDetails = new StringRequest(Request.Method.POST, Constants.viewProfile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();


                try {
                    JSONObject viewProfile = new JSONObject(response);
                    String error = viewProfile.getString("Error");
                    String message = viewProfile.getString("Message");

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

                        Toast.makeText(ViewProfile.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ViewProfile.this, AdminLogin.class);
                        startActivity(intent);
                    }


                    if (error.equals("false")) {

                        JSONObject profileObject = viewProfile.getJSONObject("data");

                        String mName = profileObject.getString("name");
                        String mEmail = profileObject.getString("email");
                        String mPhone = profileObject.getString("phone_no");
                        String mAddress = profileObject.getString("address");
                        String mLatLng = profileObject.getString("gps_location");

                        mAdminProfile = new AdminProfile(mName, mEmail, mPhone, mAddress);

                        Name.setText(mName);
                        Email.setText(mEmail);
                        Phone.setText(mPhone);
                        Address.setText(mAddress);

                    } else {
                        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
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
                    Toast.makeText(ViewProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ViewProfile.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ViewProfile.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ViewProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewProfile.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ViewProfile.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
            Toast.makeText(ViewProfile.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(ViewProfile.this);
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

    //this is the method to change User details in server
    public void saveSrsAdminProfile(View view) {

        if (checkPreviousData()) {
            final AlertDialog loadingDialog = new SpotsDialog(ViewProfile.this, R.style.Loading);
            loadingDialog.show();
            StringRequest saveAdminDetails = new StringRequest(Request.Method.POST, Constants.updateProfile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    loadingDialog.dismiss();

                    try {
                        JSONObject responsefromserver = new JSONObject(response);
                        String error = responsefromserver.getString("Error");
                        String message = responsefromserver.getString("Message");
                        showrDialog(error, message);

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
                        Toast.makeText(ViewProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", String.valueOf(error instanceof ServerError));
                        error.printStackTrace();
                    } else if (error instanceof AuthFailureError) {
                        Toast.makeText(ViewProfile.this, "Authentication Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Authentication Error");
                        error.printStackTrace();
                    } else if (error instanceof ParseError) {
                        Toast.makeText(ViewProfile.this, "Parse Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Parse Error");
                        error.printStackTrace();
                    } else if (error instanceof NoConnectionError) {
                        Toast.makeText(ViewProfile.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "No Connection Error");
                        error.printStackTrace();
                    } else if (error instanceof NetworkError) {
                        Toast.makeText(ViewProfile.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Network Error");
                        error.printStackTrace();
                    } else if (error instanceof TimeoutError) {
                        Toast.makeText(ViewProfile.this, "Timeout Error", Toast.LENGTH_LONG).show();
                        Log.d("Error", "Timeout Error");
                        error.printStackTrace();
                    } else {
                        Toast.makeText(ViewProfile.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
            }) {

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();

                    params.put("user_id", uid);
                    params.put("name", Name.getText().toString().trim());
                    params.put("email", Email.getText().toString().trim());
                    params.put("phone_no", Phone.getText().toString().trim());
                    params.put("address", Address.getText().toString().trim());
                    params.put("sid", sessionId);
                    params.put("gps_location", "");
                    params.put("api_key", Constants.APIKEY);
                    return params;
                }
            };

            saveAdminDetails.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(saveAdminDetails);

        } else {
            Snackbar.make(rootLayout, "No Changes Found", Snackbar.LENGTH_SHORT).show();
        }
    }


    //check previous data of User
    public boolean checkPreviousData() {
        String name, phone, address, email;
        name = Name.getText().toString();
        phone = Phone.getText().toString();
        address = Address.getText().toString();
        email = Email.getText().toString();

        if (name.equals(mAdminProfile.getmName()) && (phone.equals(mAdminProfile.getmPhone())) && (address.equals(mAdminProfile.getmAddress()))
                && email.equals(mAdminProfile.getmEmail())) {
            return false;
        }

        return true;
    }


    //To show admin edit result in server
    private void showrDialog(final String error, String message) {

        android.app.AlertDialog.Builder resultBuilder = new android.app.AlertDialog.Builder(ViewProfile.this);
        resultBuilder.setTitle("Srs Admin");
        resultBuilder.setMessage(message);
        resultBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (error.equals("false")) {
                    startActivity(new Intent(ViewProfile.this, MainActivity.class));
                    finish();
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        resultBuilder.setCancelable(false);
        resultBuilder.show();

    }


    //this is the method to check internet connection
    private boolean checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
        return isConnected;
    }

    private void showDialog(boolean isConnected) {

        if (isConnected) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        } else {
            alertDialog.setContentView(R.layout.check_internet);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCancelable(false);
            Button button = alertDialog.findViewById(R.id.tryAgain);
            Button exit = alertDialog.findViewById(R.id.exit);
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.exit(0);
                    //  finishAffinity();

                }
            });
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    checkInternetConnection();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SRSAdminApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showDialog(isConnected);
    }
}

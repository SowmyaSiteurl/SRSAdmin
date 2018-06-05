package www.siteurl.in.srsadmin.activities;

import android.app.Activity;
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
import android.text.TextUtils;
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
import www.siteurl.in.srsadmin.objects.StaffList;


public class AddStaff extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;
    String sessionId, uid;
    RelativeLayout rootLayout;
    private MaterialEditText Name, Email, Phone, Address, gpsLocation;
    Dialog alertDialog;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String NAME = "[a-zA-Z.? ]*";
    StaffList staffList;
    Button btn_add_user_staff, editStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        mToolbar = findViewById(R.id.add_staff_toolbar);
        mToolbar.setTitle("Add Staff");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing views
        Name = findViewById(R.id.staffName);
        Email = findViewById(R.id.staffEmail);
        Phone = findViewById(R.id.staffPhone);
        Address = findViewById(R.id.staffAddress);
        rootLayout = findViewById(R.id.addStaffRootLayout);
        btn_add_user_staff = findViewById(R.id.btn_add_user_staff);
        editStaff = findViewById(R.id.editStaff);

        checkInternetConnection();

        staffList = (StaffList) getIntent().getSerializableExtra("staffDetails");

        //setonClickListener for add staff
        btn_add_user_staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validationsforStaff();
            }
        });

        if (staffList != null) {

            Name.setText(staffList.getName());
            Email.setText(staffList.getEmail());
            Phone.setText(staffList.getPhone_no());
            Address.setText(staffList.getAddress());

            btn_add_user_staff.setVisibility(View.GONE);
            editStaff.setVisibility(View.VISIBLE);

        }


        editStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditStaff();
            }
        });
    }


    //Validation for add staff
    public void validationsforStaff() {

        String staffName = Name.getText().toString().trim();
        String staffEmail = Email.getText().toString().trim();
        String staffPhone = Phone.getText().toString().trim();
        String staffAddress = Address.getText().toString().trim();

        if (TextUtils.isEmpty(staffName)) {
            Name.setError("Enter Name");
            Name.requestFocus();
            return;
        }

        if (!staffEmail.matches(EMAIL_PATTERN)) {
            Email.setError("Enter Valid Email");
            return;
        }

        if (staffPhone.length() != 10) {
            Phone.setError("Enter valid Phone Number");
            return;
        }


        if (TextUtils.isEmpty(staffAddress)) {
            Address.setError("Enter Address");
            return;
        }


        addStaffToServer(staffName, staffEmail, staffPhone, staffAddress);

    }

    //this is the method for adding Staff
    private void addStaffToServer(final String staffName, final String staffEmail, final String staffPhone, final String staffAddress) {

        final AlertDialog loadingDialog = new SpotsDialog(AddStaff.this, R.style.Loading);
        loadingDialog.show();

        StringRequest addStaff = new StringRequest(Request.Method.POST, Constants.addStaff, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();


                try {
                    JSONObject staffObject = new JSONObject(response);
                    String error = staffObject.getString("Error");
                    String message = staffObject.getString("Message");

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
                    Toast.makeText(AddStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AddStaff.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AddStaff.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AddStaff.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AddStaff.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AddStaff.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AddStaff.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
                params.put("name", staffName);
                params.put("email", staffEmail);
                params.put("phone_no", staffPhone);
                params.put("address", staffAddress);

                return params;
            }
        };

        addStaff.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(addStaff);
    }


    //To show Add Staff result in server
    private void showrDialog(final String error, String message) {

        android.app.AlertDialog.Builder resultBuilder = new android.app.AlertDialog.Builder(AddStaff.this);
        resultBuilder.setTitle("Srs Admin");
        resultBuilder.setMessage(message);
        resultBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (error.equals("false")) {
                    startActivity(new Intent(AddStaff.this, ListOfStaff.class));
                    finish();
                } else {
                    dialogInterface.dismiss();
                }
            }
        });
        resultBuilder.setCancelable(false);
        resultBuilder.show();

    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(AddStaff.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(AddStaff.this);
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


    //this is the method to check internet connection
    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
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


    //this is the method for update staff profile
    public void EditStaff() {

        if (checkPreviousData()) {
            StringRequest deleteProduct = new StringRequest(Request.Method.POST,
                    Constants.updateStaffProfile, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject productObject = new JSONObject(response);
                        String error = productObject.getString("Error");
                        String message = productObject.getString("Message");

                        showAlertDialog(error, message);


                    } catch (JSONException e) {
                        e.printStackTrace();
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
                    params.put("user_id", staffList.getUser_id());
                    params.put("sid", sessionId);
                    params.put("api_key", Constants.APIKEY);
                    params.put("loggedin_user_id", uid);
                    params.put("name", Name.getText().toString().trim());
                    params.put("email", Email.getText().toString().trim());
                    params.put("phone_no", Phone.getText().toString().trim());
                    params.put("address", Address.getText().toString().trim());
                    params.put("gps_location", "");
                    return params;
                }
            };
            deleteProduct.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            SRSSingleton.getInstance(AddStaff.this).addtorequestqueue(deleteProduct);

        } else {
            Snackbar.make(rootLayout, "No Changes Found", Snackbar.LENGTH_SHORT).show();
        }

    }

    //check previous data of staff
    public boolean checkPreviousData() {
        String name, phone, address, email;
        name = Name.getText().toString();
        phone = Phone.getText().toString();
        address = Address.getText().toString();
        email = Email.getText().toString();

        if (name.equals(staffList.getName()) && (phone.equals(staffList.getPhone_no())) && (address.equals(staffList.getAddress()))
                && email.equals(staffList.getEmail())) {
            return false;
        }

        return true;
    }

    //To show alert of Staff response
    private void showAlertDialog(final String error, String message) {
        android.support.v7.app.AlertDialog.Builder errorbuilder = new android.support.v7.app.AlertDialog.Builder(AddStaff.this);
        errorbuilder.setIcon(R.mipmap.ic_launcher);
        errorbuilder.setTitle("Service Request System");
        errorbuilder.setMessage(message);
        errorbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (error.equals("true")) {
                    dialogInterface.dismiss();
                } else {
                    // dialogInterface.dismiss();
                    startActivity(new Intent(AddStaff.this, ListOfStaff.class).
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    //  editor.clear();
                    //  editor.commit();
                    finish();

                }
            }
        });
        errorbuilder.setCancelable(false);
        errorbuilder.show();

    }
}

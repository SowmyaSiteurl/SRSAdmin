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
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.api.Constants;

public class RegisterActivity extends AppCompatActivity {

    private MaterialEditText mRegName, mRegEmail, mRegPhone, mRegAddress, mRegPassword;
    private Button mRegister;
    RelativeLayout RegisterRootLayout;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
   private static final String PHONE = "";
    private CheckBox mRegShowPwd;
    Dialog alertDialog;
    String name, email, phone, address, password;

    private Toolbar mRegToolbar;
    private TextView mToolbarTitle;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing views
        RegisterRootLayout = findViewById(R.id.srsAdminReg);
        mRegName = findViewById(R.id.edtRegName);
        mRegEmail = findViewById(R.id.edtRegEmail);
        mRegPhone = findViewById(R.id.edtRegPhone);
        mRegAddress = findViewById(R.id.edtRegAddress);
        mRegPassword = findViewById(R.id.edtRegPassword);
        mRegister = findViewById(R.id.adminSignIn);
        mRegShowPwd = findViewById(R.id.srsAdminShowPwd);

        mRegToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mRegToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mToolbarTitle = mRegToolbar.findViewById(R.id.srs_toolbar_title);
        mToolbarTitle.setText("Register Here");

        mRegShowPwd = findViewById(R.id.srsAdminShowPwd);
        //Check box to show the password
        mRegShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!isChecked) {
                    mRegPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mRegPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
    }

    //Method to validate Register credentials
    public void srsAdminRegister(View view) {

        name = mRegName.getText().toString().trim();
        email = mRegEmail.getText().toString().trim();
        phone = mRegPhone.getText().toString().trim();
        address = mRegAddress.getText().toString().trim();
        password = mRegPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            mRegName.setError("Please Enter Name");
            Snackbar.make(RegisterRootLayout, "Please Enter Name", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            mRegEmail.setError("Please Enter Email");
            Snackbar.make(RegisterRootLayout, "Please Enter Email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches(EMAIL_PATTERN)) {
            mRegEmail.setError("Please Enter Valid Email");
            Snackbar.make(RegisterRootLayout, "Please Enter Valid Email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (phone.equals("") || (phone.equals(null)) || (phone.length() != 10)) {
            mRegPhone.setError("Please Enter a Valid Phone Number");
            Snackbar.make(RegisterRootLayout, "Please Enter a Valid Phone Number", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            mRegAddress.setError("Please Enter Address");
            Snackbar.make(RegisterRootLayout, "Please Enter Address", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mRegPassword.setError("Please Enter Password");
            Snackbar.make(RegisterRootLayout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
            return;
        }

        sendRegisterDetailsToServer();
    }

    //method to send Register details to server
    private void sendRegisterDetailsToServer() {
        final AlertDialog loadingDialog = new SpotsDialog(RegisterActivity.this, R.style.Loading);
        loadingDialog.show();

        StringRequest registerRequest = new StringRequest(Request.Method.POST,
                Constants.Register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();
                try {
                    JSONObject objectRegister = new JSONObject(response);
                    String error = objectRegister.getString("Error");
                    String message = objectRegister.getString("Message");

                    if (error.equals("true")) {
                        Snackbar.make(RegisterRootLayout, message, Snackbar.LENGTH_SHORT).show();
                        return;
                    } else if (error.equals("false")) {

                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, AdminLogin.class).
                                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();

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
                    Toast.makeText(RegisterActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(RegisterActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(RegisterActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(RegisterActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(RegisterActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(RegisterActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("phone_no", phone);
                params.put("address", address);
                params.put("user_group_id", "1");
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        registerRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(registerRequest);
    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(RegisterActivity.this);
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

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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.api.Constants;

public class AdminLogin extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private MaterialEditText mLoginEmail, mLoginPassword;
    private Button mSingIn;
    RelativeLayout LoginRootLayout;
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private CheckBox mCbShowPwd;
    Dialog alertDialog;
    String email, password, loginUserId;
    private TextView mForgotPassword;
    private Toolbar mLoginToolbar;
    SharedPreferences loginPref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initializing views
        LoginRootLayout = findViewById(R.id.srsAdminLogin);
        mLoginEmail = findViewById(R.id.edtEmail);
        mLoginPassword = findViewById(R.id.edtPassword);
        mSingIn = findViewById(R.id.adminSignIn);
        mForgotPassword = findViewById(R.id.adminForgotPwd);
        mCbShowPwd = findViewById(R.id.srsShowPwd);

        mLoginToolbar = findViewById(R.id.logintoolbar);
        setSupportActionBar(mLoginToolbar);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        editor = loginPref.edit();


        //Check box to show the password
        mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    mLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    mLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });

        //forgot password onClickListener
        mForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });

        //automatic login function
        if (loginPref.contains("loginUserId")) {
            loginUserId = loginPref.getString("loginUserId", null);
            if (loginUserId.equals("") || loginUserId.equals(null)) {
                // Toast.makeText(this, "no session", Toast.LENGTH_SHORT).show();
                return;
            } else {
                startActivity(new Intent(AdminLogin.this, MainActivity.class));
            }
        } else {
            //  Toast.makeText(this, "no session", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    //Register activity code
    /*public void SignUpNow(View view) {
        startActivity(new Intent(AdminLogin.this, RegisterActivity.class).
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }*/

    //To show forgot password dialog
    public void showForgotPasswordDialog() {

        android.app.AlertDialog.Builder forgotPasswordBuilder = new android.app.AlertDialog.Builder(AdminLogin.this);
        forgotPasswordBuilder.setTitle("Forgot Password");
        forgotPasswordBuilder.setMessage("Please Enter Registered Email.");
        View customLayout = getLayoutInflater().inflate(R.layout.forgot_password_layout, null);
        final MaterialEditText email = customLayout.findViewById(R.id.edtEmail);
        forgotPasswordBuilder.setView(customLayout);
        forgotPasswordBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                String forgotEmail = email.getText().toString().trim();
                if (TextUtils.isEmpty(forgotEmail) || (!forgotEmail.matches(EMAIL_PATTERN))) {
                    Toast.makeText(AdminLogin.this, "Enter Valid Email", Toast.LENGTH_LONG).show();
                    showForgotPasswordDialog();
                    return;
                }
                sendForgotPasswordEmailToServer(forgotEmail);

            }
        });

        forgotPasswordBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        forgotPasswordBuilder.show();
    }

    //this is the method to send email to server(for forgot password)
    public void sendForgotPasswordEmailToServer(final String forgotEmail) {
        final AlertDialog loadingDialog = new SpotsDialog(AdminLogin.this, R.style.Loading);
        loadingDialog.show();

        StringRequest forgotPassword = new StringRequest(Request.Method.POST, Constants.forgotPassword,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loadingDialog.dismiss();

                        try {

                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            if (Error.equals("false")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

                            }

                            if (Error.equals("true")) {
                                String alertMessage;
                                alertMessage = Message;
                                emailAlert(alertMessage);

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
                    Toast.makeText(AdminLogin.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AdminLogin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AdminLogin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AdminLogin.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AdminLogin.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AdminLogin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AdminLogin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", forgotEmail);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };

        forgotPassword.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(AdminLogin.this).addtorequestqueue(forgotPassword);
    }


    public void emailAlert(String message) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(AdminLogin.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Service Request System");
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

            }
        });
        builder.setCancelable(false);
        builder.show();

    }


    //Method to validate login credentials
    public void validateSRSAdmin(View view) {

        email = mLoginEmail.getText().toString().trim();
        password = mLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            mLoginEmail.setError("Please Enter Email");
            Snackbar.make(LoginRootLayout, "Please Enter Email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (!email.matches(EMAIL_PATTERN)) {
            mLoginEmail.setError("Please Enter Valid Email");
            Snackbar.make(LoginRootLayout, "Please Enter Valid Email", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mLoginPassword.setError("Please Enter Password");
            Snackbar.make(LoginRootLayout, "Please Enter Password", Snackbar.LENGTH_SHORT).show();
            return;
        }

        checkInternetConnection();
    }

    //this is the method to check internet connection
    private void checkInternetConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showDialog(isConnected);
    }

    private void showDialog(boolean isConnected) {

        if (isConnected) {
            SignIn();

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

    //Method to send login details to server
    private void SignIn() {
        final AlertDialog loadingDialog = new SpotsDialog(AdminLogin.this, R.style.Loading);
        loadingDialog.show();

        StringRequest loginRequest = new StringRequest(Request.Method.POST,
                Constants.login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {
                    JSONObject objectSignUp = new JSONObject(response);
                    String error = objectSignUp.getString("Error");
                    if (error.contains("true"))
                        Toast.makeText(AdminLogin.this, objectSignUp.getString("Message"), Toast.LENGTH_SHORT).show();

                    if (error.equals("false")) {
                        String message = objectSignUp.getString("Message");
                        String sid = objectSignUp.getString("sid");

                        Toast.makeText(AdminLogin.this, message, Toast.LENGTH_SHORT).show();
                        String role = objectSignUp.getString("Role");
                        if (role.equals("Admin")) {

                            String data = objectSignUp.getString("data");
                            JSONObject jsonObject = new JSONObject(data);

                            String name = jsonObject.getString("name");
                            String email = jsonObject.getString("email");
                            String user_id = jsonObject.getString("user_id");
                            //  String sid = jsonObject.getString("sid");
                            String user_group_id = jsonObject.getString("user_group_id");
                            String phone_no = jsonObject.getString("phone_no");
                            String address = jsonObject.getString("address");

                            editor.putString("loginName", name);
                            editor.putString("loginEmail", email);
                            editor.putString("loginUserId", user_id);
                            editor.putString("loginSid", sid);
                            editor.putString("user_group_id", user_group_id);
                            editor.putString("loginPhone", phone_no);
                            editor.putString("loginAddrs", address);
                            editor.putString("loginRole", role);
                            editor.commit();

                            mLoginEmail.getText().clear();
                            mLoginPassword.getText().clear();

                            startActivity(new Intent(AdminLogin.this, MainActivity.class).
                                    setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();

                        }
                    } else {
                        Toast.makeText(AdminLogin.this, "Invalid SRS Admin Credentials", Toast.LENGTH_SHORT).show();
                        Snackbar.make(LoginRootLayout, "Invalid SRS Admin Credentials", Snackbar.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminLogin.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(AdminLogin.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(AdminLogin.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(AdminLogin.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(AdminLogin.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(AdminLogin.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(AdminLogin.this, "Something went wrong", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                params.put("email", email);
                params.put("password", password);
                params.put("api_key", Constants.APIKEY);
                return params;
            }
        };
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(loginRequest);
    }


    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(AdminLogin.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(AdminLogin.this);
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

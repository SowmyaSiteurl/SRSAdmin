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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
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
import www.siteurl.in.srsadmin.adapters.NewTicketsAdapter;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.objects.Tickets;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private DrawerLayout mMainDrawer;
    private ActionBarDrawerToggle mToogle;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    String sessionId, uid;
    private TextView mToolbarTitle;
    Dialog alertDialog;
    private ImageView mLogout;
    SharedPreferences.Editor editor;
    SharedPreferences loginPref;
    private TextView mTotalTickets, mHoldTickets, mCloseTickets;
    RelativeLayout totalTicket, closeTicket, holdTicket;
    TextView openTickets, newTickets;

    private ArrayList<Tickets> mTicketList = new ArrayList<>();
    ListView ticketView;
    NewTicketsAdapter newTicketsAdapter;
    Tickets tickets;
    RelativeLayout relativeLayout;
    TextView noTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alertDialog = new Dialog(this);
        DemoSyncJob.scheduleJob();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //Initializing views
        mMainDrawer = findViewById(R.id.mainDrawer);
        mNavigationView = findViewById(R.id.nv_main);
        mToogle = new ActionBarDrawerToggle(this, mMainDrawer, R.string.open, R.string.close);
        mMainDrawer.addDrawerListener(mToogle);
        mToogle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        mToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);

        mTotalTickets = findViewById(R.id.total_Tickets);
        mHoldTickets = findViewById(R.id.Hold_Tickets);
        mCloseTickets = findViewById(R.id.Close_Tickets);
        totalTicket = findViewById(R.id.totalTicketslayout);
        holdTicket = findViewById(R.id.holdTicketslayout);
        closeTicket = findViewById(R.id.closedTicketlayout);
        openTickets = findViewById(R.id.openTickets);
        // newTickets = findViewById(R.id.newTickets);

        mLogout = mToolbar.findViewById(R.id.srs_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        //toolbar function
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMainDrawer.addDrawerListener(mToogle);
        mToogle.syncState();

        checkInternetConnection();
        getDashboardDetails();

        ticketView = findViewById(R.id.ticketList1);
        noTickets = findViewById(R.id.noTickets1);

        //setonClickListener for total Ticket count
        totalTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewTickets.class);
                intent.putExtra("ticketStatus", "listofallticket");
                startActivity(intent);
            }
        });
        //setonClickListener for total hold count
        holdTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewTickets.class);
                intent.putExtra("ticketStatus", "listofholdtickets");
                startActivity(intent);
            }
        });

        //setonClickListener for close Ticket count
        closeTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewTickets.class);
                intent.putExtra("ticketStatus", "listofclosedtickets");
                startActivity(intent);
            }
        });

        //setonClickListener for open Ticket count
        openTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ViewTickets.class);
                intent.putExtra("ticketStatus", "listofopenticket");
                startActivity(intent);
            }
        });


    }

    //this is the method to get Ticket list from server
    private void getTicketListFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(MainActivity.this, R.style.Loading);
        loadingDialog.show();

        StringRequest ticketRequest = new StringRequest(Request.Method.POST,
                Constants.allTicketDetails, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialog.dismiss();
                try {
                    JSONObject ticketObject = new JSONObject(response);
                    String error = ticketObject.getString("Error");
                    String message = ticketObject.getString("Message");


                    if (error.equals("true")) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    if (error.equals("false")) {

                        JSONArray ticketsArray = ticketObject.getJSONArray("List of new tickets");

                        for (int i = 0; i < ticketsArray.length(); i++) {
                            JSONObject listOfProducts = ticketsArray.getJSONObject(i);

                            String ticketId = listOfProducts.getString("ticket_id");
                            String userId = listOfProducts.getString("user_id");
                            String ticketName = listOfProducts.getString("product_name");
                            String ticketDesc = listOfProducts.getString("ticket_description");
                            String date = listOfProducts.getString("created_at");
                            String status = listOfProducts.getString("ticket_status");
                            String ticket_subject = listOfProducts.getString("ticket_subject");
                            String call_back_date = listOfProducts.getString("call_back_date");
                            String assigned_to = listOfProducts.getString("assigned_to");
                            String assigned_from = listOfProducts.getString("assigned_from");
                            String updated_at = listOfProducts.getString("updated_at");

                            JSONObject userDetails = listOfProducts.getJSONObject("user_details");
                            String userName = userDetails.getString("name");
                            String userPhone = userDetails.getString("phone_no");

                            tickets = new Tickets(ticketId, ticketName, ticketDesc, date, status, ticket_subject, call_back_date, assigned_to, assigned_from, updated_at, userName, userPhone);
                            mTicketList.add(tickets);

                        }

                        if (mTicketList.size() > 0) {
                            newTicketsAdapter = new NewTicketsAdapter(MainActivity.this, R.layout.new_tickets, mTicketList);
                            ticketView.setAdapter(newTicketsAdapter);
                        } else {
                            noTickets.setVisibility(View.VISIBLE);
                            ticketView.setVisibility(View.GONE);
                        }
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
                    Toast.makeText(MainActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        ticketRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(ticketRequest);
    }

    //this is the method to Admin ticket counts details from server
    private void getDashboardDetails() {

        StringRequest dashboardrequest = new StringRequest(Request.Method.POST, Constants.allTicketDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");

                            if (Error.equals("false")) {

                                int totalTicket = Integer.parseInt(String.valueOf(jsonObject.get("Total number of new tickets")));
                                int openTicket = Integer.parseInt(String.valueOf(jsonObject.get("Total number of open tickets")));
                                int holdTicket = Integer.parseInt(String.valueOf(jsonObject.get("Total number of hold tickets")));
                                int closeTicket = Integer.parseInt(String.valueOf(jsonObject.get("Total number of closed tickets")));

                                mTotalTickets.setText(String.valueOf(totalTicket));
                                mHoldTickets.setText(String.valueOf(holdTicket));
                                mCloseTickets.setText(String.valueOf(closeTicket));
                                openTickets.setText(String.valueOf("Open Tickets : " + openTicket));

                            }

                            getTicketListFromServer();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null) {
                    parseVolleyError(error);
                }
                mTotalTickets.setText("0");
                mHoldTickets.setText("0");
                mCloseTickets.setText("0");

                if (error instanceof ServerError) {
                    //Toast.makeText(MainActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(MainActivity.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(MainActivity.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(MainActivity.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(MainActivity.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(MainActivity.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
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
        dashboardrequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(dashboardrequest);
    }

    //this is the method for Admin Logout function
    private void logoutUser() {

        final AlertDialog loadingDialog = new SpotsDialog(MainActivity.this, R.style.Loading);
        loadingDialog.show();

        StringRequest logout = new StringRequest(Request.Method.POST, Constants.Logout,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadingDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Error = jsonObject.getString("Error");
                            String Message = jsonObject.getString("Message");


                            if (Error.equals("false")) {

                                editor.putString("loginName", "");
                                editor.putString("loginEmail", "");
                                editor.putString("loginUserId", "");
                                editor.putString("loginSid", "");
                                editor.putString("user_group_id", "");
                                editor.putString("loginPhone", "");
                                editor.putString("loginAddrs", "");
                                editor.putString("loginRole", "");
                                editor.commit();

                                Toast.makeText(MainActivity.this, Message, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this, AdminLogin.class).
                                        setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();
                            }

                            if (Error.equals("true")) {
                                Toast.makeText(MainActivity.this, Message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
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
        logout.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getApplicationContext()).addtorequestqueue(logout);

    }

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
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


    //menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (mToogle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemid = item.getItemId();

        if (itemid == R.id.view_profile) {
            startActivity(new Intent(MainActivity.this, ViewProfile.class));
        }
        if (itemid == R.id.add_staff) {
            startActivity(new Intent(MainActivity.this, AddStaff.class));
        }
        if (itemid == R.id.staff_list) {
            startActivity(new Intent(MainActivity.this, ListOfStaff.class));
        }

        if (itemid == R.id.view_unassigned_tickets) {
            Intent intent = new Intent(MainActivity.this, ViewTickets.class);
            intent.putExtra("ticketStatus", "listofallticket");
            startActivity(intent);
        }
        if (itemid == R.id.view_hold_tickets) {
            Intent intent = new Intent(MainActivity.this, ViewTickets.class);
            intent.putExtra("ticketStatus", "listofholdtickets");
            startActivity(intent);

        }
        if (itemid == R.id.view_open_tickets) {
            Intent intent = new Intent(MainActivity.this, ViewTickets.class);
            intent.putExtra("ticketStatus", "listofopenticket");
            startActivity(intent);
        }

        if (itemid == R.id.view_close_tickets) {
            Intent intent = new Intent(MainActivity.this, ViewTickets.class);
            intent.putExtra("ticketStatus", "listofclosedtickets");
            startActivity(intent);

        }

        if (itemid == R.id.srs_change_password) {
            startActivity(new Intent(MainActivity.this, ChangePasswordActivity.class));
        }
        if (itemid == R.id.logout_menu) {

            logoutUser();
        }
        mMainDrawer.closeDrawers();
        return false;
    }

    // this is method for BackButton
    @Override
    public void onBackPressed() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("Service Request System");
        builder.setMessage("Do You want to Exit ?");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                System.exit(0);
                finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
        //super.onBackPressed();
    }

}

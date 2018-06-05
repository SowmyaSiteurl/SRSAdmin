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
import www.siteurl.in.srsadmin.adapters.ViewTicketsAdapter;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.fragments.BottomSheetFragment;
import www.siteurl.in.srsadmin.objects.Tickets;

public class ViewTickets extends AppCompatActivity {

    private Toolbar mToolbar;
    SharedPreferences loginPref;
    String sessionId, uid;
    SharedPreferences.Editor editor;
    Dialog alertDialog;
    private ArrayList<Tickets> mTicketList = new ArrayList<>();
    ListView ticketView;
    ViewTicketsAdapter viewTicketsAdapter;
    Tickets tickets;
    RelativeLayout relativeLayout;
    TextView noTickets;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tickets);
        alertDialog = new Dialog(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Toolbar
        mToolbar = findViewById(R.id.view_tickets_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loginPref = getApplicationContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        //Initializing views
        noTickets = findViewById(R.id.noTickets);
        ticketView = findViewById(R.id.ticketList);
        ticketView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*Bundle bundle = new Bundle();
                bundle.putString("ticketID", mTicketList.get(position).getTicketId());

                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());*/

                Intent intent = new Intent(ViewTickets.this, TicketDetailsActivity.class);

                intent.putExtra("ticketId", mTicketList.get(position).getTicketId());
                intent.putExtra("ticketName", mTicketList.get(position).getProdName());
                intent.putExtra("ticket_subject", mTicketList.get(position).getTicket_subject());
                intent.putExtra("ticketDesc", mTicketList.get(position).getTicketDesc());
                intent.putExtra("ticketDate", mTicketList.get(position).getDate());
                intent.putExtra("ticket_status", mTicketList.get(position).getStatus());
                intent.putExtra("callBackDate", mTicketList.get(position).getCall_back_date());
                intent.putExtra("assignedTo", mTicketList.get(position).getAssigned_to());
                intent.putExtra("assignedFrom", mTicketList.get(position).getAssigned_from());
                intent.putExtra("currentStatus", mTicketList.get(position).getStatus());

                startActivity(intent);

            }
        });

        relativeLayout = findViewById(R.id.viewTicketsLayout);
        getTicketListFromServer();

    }

    //this is the method to get Ticket list from server
    private void getTicketListFromServer() {

        final AlertDialog loadingDialog = new SpotsDialog(ViewTickets.this, R.style.Loading);
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

                        Toast.makeText(ViewTickets.this, message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ViewTickets.this, AdminLogin.class);
                        startActivity(intent);
                    }



                    if (error.equals("false")) {


                        JSONArray ticketsArray = new JSONArray("[]");

                        if (getIntent().getStringExtra("ticketStatus").equals("listofallticket"))
                        ticketsArray = ticketObject.getJSONArray("List of new tickets");

                        if (getIntent().getStringExtra("ticketStatus").equals("listofopenticket"))
                        ticketsArray = ticketObject.getJSONArray("List of open tickets");

                        if (getIntent().getStringExtra("ticketStatus").equals("listofclosedtickets"))
                        ticketsArray = ticketObject.getJSONArray("List of closed tickets");

                        if (getIntent().getStringExtra("ticketStatus").equals("listofholdtickets"))
                        ticketsArray = ticketObject.getJSONArray("List of hold tickets");


                        for (int i = 0; i < ticketsArray.length(); i++) {

                            String totalNumOfTickets = ticketObject.getString("Total number of tickets");

                            JSONObject listOfProducts = ticketsArray.getJSONObject(i);

                            String ticketId = listOfProducts.getString("ticket_id");
                            String userId = listOfProducts.getString("user_id");
                            String ticketName = listOfProducts.getString("product_name");
                            String ticketDesc = listOfProducts.getString("ticket_description");
                            String date = listOfProducts.getString("created_at");
                            status = listOfProducts.getString("ticket_status");
                            String ticket_subject = listOfProducts.getString("ticket_subject");
                            String call_back_date = listOfProducts.getString("call_back_date");
                            String assigned_to = listOfProducts.getString("assigned_to");
                            String assigned_from = listOfProducts.getString("assigned_from");
                            String updated_at = listOfProducts.getString("updated_at");

                            if (status.equals("Open")){
                                mToolbar.setTitle("Open Tickets");
                            }

                            if (status.equals("Hold")){
                                mToolbar.setTitle("Hold Tickets");
                            }

                            if (status.equals("Close")){
                                mToolbar.setTitle("Close Tickets");
                            }

                            if (getIntent().getStringExtra("ticketStatus").equals("listofallticket")){
                                mToolbar.setTitle("List Of All Tickets");
                            }


                            editor.putString("ticketID", ticketId);
                            editor.commit();

                            JSONObject userDetails = listOfProducts.getJSONObject("user_details");
                            String userName = userDetails.getString("name");
                            String userPhone = userDetails.getString("phone_no");

                            tickets = new Tickets(ticketId, ticketName, ticketDesc, date, status, ticket_subject, call_back_date, assigned_to, assigned_from, updated_at, userName, userPhone);
                            mTicketList.add(tickets);

                        }

                        if (mTicketList.size() > 0) {
                            viewTicketsAdapter = new ViewTicketsAdapter(ViewTickets.this, R.layout.view_tickets, mTicketList);
                            ticketView.setAdapter(viewTicketsAdapter);
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
                    Toast.makeText(ViewTickets.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", String.valueOf(error instanceof ServerError));
                    error.printStackTrace();
                } else if (error instanceof AuthFailureError) {
                    Toast.makeText(ViewTickets.this, "Authentication Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Authentication Error");
                    error.printStackTrace();
                } else if (error instanceof ParseError) {
                    Toast.makeText(ViewTickets.this, "Parse Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Parse Error");
                    error.printStackTrace();
                } else if (error instanceof NoConnectionError) {
                    Toast.makeText(ViewTickets.this, "Server is under maintenance.Please try later.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "No Connection Error");
                    error.printStackTrace();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(ViewTickets.this, "Please check your connection.", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Network Error");
                    error.printStackTrace();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(ViewTickets.this, "Timeout Error", Toast.LENGTH_LONG).show();
                    Log.d("Error", "Timeout Error");
                    error.printStackTrace();
                } else {
                    Toast.makeText(ViewTickets.this, "Something went wrong", Toast.LENGTH_LONG).show();
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

    //Handling Volley Error
    public void parseVolleyError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("Message");
            Toast.makeText(ViewTickets.this, message, Toast.LENGTH_LONG).show();
            android.app.AlertDialog.Builder loginErrorBuilder = new android.app.AlertDialog.Builder(ViewTickets.this);
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

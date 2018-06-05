package www.siteurl.in.srsadmin.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.activities.AddStaff;
import www.siteurl.in.srsadmin.activities.ListOfStaff;
import www.siteurl.in.srsadmin.activities.SRSSingleton;
import www.siteurl.in.srsadmin.api.Constants;
import www.siteurl.in.srsadmin.objects.StaffList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 25/4/18.
 */

public class ListOfStaffs extends ArrayAdapter<StaffList> {

    Context mcontext;
    int resource;
    ArrayList<StaffList> listOfStaffs = new ArrayList<>();
    StaffList staffList;
    SharedPreferences loginPref;
    String sessionId, uid;
    int positionOfList = 0;
    SharedPreferences.Editor editor;

    public ListOfStaffs(@NonNull Context context, int textViewResourceId, ArrayList<StaffList> staffList) {
        super(context, textViewResourceId, staffList);
        this.listOfStaffs = new ArrayList<StaffList>();
        this.listOfStaffs.addAll(staffList);

        mcontext = context;
    }

    private class ViewHolder {
        TextView staffName, staffEmail, staffAddress, staffPhone;
        ImageView deleteStaff, editStaff;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        positionOfList = position;
        loginPref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        editor = loginPref.edit();

        ViewHolder holder = null;
        staffList = listOfStaffs.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_of_staffs, parent, false);

            holder = new ViewHolder();
            holder.staffName = convertView.findViewById(R.id.staffname);
            holder.staffEmail = convertView.findViewById(R.id.staffemail);
            holder.staffAddress = convertView.findViewById(R.id.staffphone);
            holder.staffPhone = convertView.findViewById(R.id.staffaddress);
            holder.deleteStaff = convertView.findViewById(R.id.deletestaffImage);
            holder.editStaff = convertView.findViewById(R.id.editstaffImage);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.staffName.setText(staffList.getName());
        holder.staffEmail.setText(staffList.getEmail());
        holder.staffPhone.setText(staffList.getPhone_no());
        holder.staffAddress.setText(staffList.getAddress());

        final View finalConvertView = convertView;

        //this is the function to delete staff
        holder.deleteStaff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog alertDialogs = new AlertDialog.Builder(finalConvertView.getRootView().getContext()).create();
                alertDialogs.setTitle("Delete Product");
                alertDialogs.setMessage("Would you like to Delete now ?");
                alertDialogs.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                deleteStaff();
                                dialog.dismiss();
                            }
                        });

                alertDialogs.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                alertDialogs.show();
            }
        });

        holder.editStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), AddStaff.class);
                intent.putExtra("staffDetails", listOfStaffs.get(position));
                mcontext.startActivity(intent);
            }
        });

        return convertView;
    }

    //this is the method for delete Staff
    private void deleteStaff() {
        StringRequest deleteProduct = new StringRequest(Request.Method.POST,
                Constants.deleteStaff, new Response.Listener<String>() {
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
                return params;
            }
        };
        deleteProduct.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        SRSSingleton.getInstance(getContext()).addtorequestqueue(deleteProduct);

    }

    //To show alert dialog of delete Staff response
    private void showAlertDialog(final String error, String message) {
        android.support.v7.app.AlertDialog.Builder errorbuilder = new android.support.v7.app.AlertDialog.Builder(getContext());
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
                    mcontext.startActivity(new Intent(getContext(), ListOfStaff.class).
                            setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    editor.clear();
                    //  editor.commit();
                    ((Activity) mcontext).finish();

                }
            }
        });
        errorbuilder.setCancelable(false);
        errorbuilder.show();

    }
}


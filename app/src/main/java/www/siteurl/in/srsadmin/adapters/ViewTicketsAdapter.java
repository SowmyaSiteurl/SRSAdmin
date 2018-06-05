package www.siteurl.in.srsadmin.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.objects.Tickets;

/**
 * Created by siteurl on 9/4/18.
 */

public class ViewTicketsAdapter extends ArrayAdapter<Tickets> {

    private ArrayList<Tickets> ticketList;
    int positionOfList;
    Context mContext;
    Dialog alertDialog;

    public ViewTicketsAdapter(Context context, int textViewResourceId, ArrayList<Tickets> tickets) {
        super(context, textViewResourceId, tickets);

        this.ticketList = new ArrayList<Tickets>();
        this.ticketList.addAll(tickets);

    }

    private class ViewHolder {
        TextView prodName;
        TextView ticketDesc;
        TextView userName;
        TextView userPhone;
        TextView date;
        Button dots;
        ImageView arrow;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        alertDialog = new Dialog(getContext());

        ViewHolder holder = null;

        final Tickets currentEnquiry = ticketList.get(position);
        if (convertView == null) {

            positionOfList = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_tickets, parent, false);
            holder = new ViewHolder();
            holder.prodName = convertView.findViewById(R.id.prodName);
            holder.ticketDesc = convertView.findViewById(R.id.ticketDesc);
            holder.userName = convertView.findViewById(R.id.userName);
            holder.date = convertView.findViewById(R.id.date);
            holder.arrow = convertView.findViewById(R.id.myImg);

           // holder.dots = convertView.findViewById(R.id.dots);

           /* holder.dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDialog(position);


                }
            });*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String prodName = currentEnquiry.getProdName();
        holder.prodName.setText(prodName);
        holder.ticketDesc.setText(currentEnquiry.getTicketDesc());
        holder.userName.setText("Customer Name : "+currentEnquiry.getUserName());
       // holder.date.setText(currentEnquiry.getDate().substring(0, 10));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date datestart = null;
        String newclldatestart = null;
        try {
            datestart = inputFormat.parse(currentEnquiry.getDate());
            newclldatestart = outputFormat.format(datestart);
            holder.date.setText(newclldatestart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    /*private void showDialog(final int position) {


        alertDialog.setContentView(R.layout.alert_dialog);
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Select Here");
        Button assignATicket = alertDialog.findViewById(R.id.assign_ticket);
        assignATicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                Intent intent = new Intent(getContext(), AssignTicketToStaff.class);
                intent.putExtra("ticketID",ticketList.get(position).getTicketId());
                getContext().startActivity(intent);

            }
        });
        alertDialog.show();


    }*/
}


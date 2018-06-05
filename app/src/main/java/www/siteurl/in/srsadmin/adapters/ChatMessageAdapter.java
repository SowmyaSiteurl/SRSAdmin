package www.siteurl.in.srsadmin.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.objects.chatMessage;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by siteurl on 18/4/18.
 */

public class ChatMessageAdapter extends ArrayAdapter<chatMessage> {

    private ArrayList<chatMessage> messageList;
    int positionOfList;
    Context mContext;
    SharedPreferences loginPref;
    String sessionId, uid, loginName;
    SharedPreferences.Editor editor;

    public ChatMessageAdapter(Context context, int textViewResourceId, ArrayList<chatMessage> messages) {
        super(context, textViewResourceId, messages);

        this.messageList = new ArrayList<chatMessage>();
        this.messageList.addAll(messages);

    }

    private class ViewHolder {
        TextView personName;
        TextView personMessage;
        TextView date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        loginPref = getContext().getSharedPreferences("LoginPref", MODE_PRIVATE);
        sessionId = loginPref.getString("loginSid", null);
        uid = loginPref.getString("loginUserId", null);
        loginName = loginPref.getString("loginName", null);
        editor = loginPref.edit();

        ViewHolder holder = null;

        final chatMessage currentEnquiry = messageList.get(position);
        if (convertView == null) {

            positionOfList = position;
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_details, parent, false);
            holder = new ViewHolder();
            holder.personName = (TextView) convertView.findViewById(R.id.Name);
            holder.personMessage = (TextView) convertView.findViewById(R.id.Desc);
            holder.date = (TextView) convertView.findViewById(R.id.Date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = currentEnquiry.getName();
        if (name.equals(loginName))name = "You";
        holder.personName.setText(name);
        holder.personMessage.setText(currentEnquiry.getPersonMessage());
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

}

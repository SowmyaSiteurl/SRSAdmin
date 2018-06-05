package www.siteurl.in.srsadmin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.objects.StaffList;

/**
 * Created by siteurl on 10/4/18.
 */

public class StaffListAdapter extends ArrayAdapter {

    Context context;
    int resource;
    ArrayList<StaffList> arrayList = new ArrayList<>();

    public StaffListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StaffList> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        arrayList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.staff_list_adapter, parent, false);

        ((TextView) view.findViewById(R.id.nameOfStaff)).setText(arrayList.get(position).getName());
        ((TextView) view.findViewById(R.id.emailOfStaff)).setText(arrayList.get(position).getEmail());
        ((TextView) view.findViewById(R.id.phoneOfStaff)).setText(arrayList.get(position).getPhone_no());
        ((TextView) view.findViewById(R.id.addressOfStaff)).setText(arrayList.get(position).getAddress());

        return view;
    }
}

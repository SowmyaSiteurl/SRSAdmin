package www.siteurl.in.srsadmin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import www.siteurl.in.srsadmin.R;
import www.siteurl.in.srsadmin.activities.AssignTicketToStaff;

/**
 * Created by siteurl on 10/4/18.
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {

    String tickets;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        tickets = getArguments().getString("ticketID");

        LinearLayout assignTicket = (LinearLayout) view.findViewById(R.id.assign_ticket_layout);
        assignTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent = new Intent(getContext(), AssignTicketToStaff.class);
                    intent.putExtra("ticketID", tickets);
                    getContext().startActivity(intent);

            }
        });
        return view;
    }
}

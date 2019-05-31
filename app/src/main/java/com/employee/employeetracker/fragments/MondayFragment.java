package com.employee.employeetracker.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.employee.employeetracker.R;
import com.employee.employeetracker.adapters.ShowAttendanceRecyclerAdapter;
import com.employee.employeetracker.models.Attendance;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MondayFragment extends Fragment {
    private static final String TAG = "MondayFragment";
    private FirebaseAuth mAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference checkedInDb;
    private ShowAttendanceRecyclerAdapter adapter;
    private View view;
    private String uid;
    public MondayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_monday, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        //firebase
        mAuth = FirebaseAuth.getInstance();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        assert mFirebaseUser != null;
        uid = mFirebaseUser.getUid();
        checkedInDb =
                FirebaseDatabase.getInstance().getReference().child("CheckIn/Monday");
        checkedInDb.keepSynced(true);


        setUpRecycler();
    }

    private void setUpRecycler() {

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewMonday);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        //item animator
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(300);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(layoutManager);

        final DividerItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);

        String dayOfTheWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

        //querying the database base of the time posted
        Query query = checkedInDb.orderByChild("userId").equalTo(uid);

        FirebaseRecyclerOptions<Attendance> options =
                new FirebaseRecyclerOptions.Builder<Attendance>().setQuery(query,
                        Attendance.class)
                        .build();
        adapter = new ShowAttendanceRecyclerAdapter(options);
/*
        adapter = new FirebaseRecyclerAdapter<Attendance, ShowAttendanceAdapter>(options) {
            @NonNull
            @Override
            public ShowAttendanceAdapter onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new ShowAttendanceAdapter(getLayoutInflater()
                        .inflate(R.layout.layout_view_employee_check_in, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ShowAttendanceAdapter holder, int position,
                                            @NonNull Attendance model) {

                holder.showCheckInEmployeeName(model.getUserName());
                holder.showCheckInDate(model.getCheckInTimeStamp());

                //get the post position using the positions in each view holder
                final String getPostPosition = getRef(position).getKey();


            }

        };
*/
        //add decorator
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: ");
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        adapter.stopListening();
    }


}
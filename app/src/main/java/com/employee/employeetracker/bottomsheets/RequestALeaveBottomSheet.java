package com.employee.employeetracker.bottomsheets;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.employee.employeetracker.R;
import com.employee.employeetracker.utils.GetDateTime;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class RequestALeaveBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "RequestALeaveBottomShee";
    private final Calendar calendar = Calendar.getInstance();
    private RequestLeaveListener requestLeaveListener;
    private DatePickerDialog datePicker;
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);
    private String getStartDate;
    private String getEndDate;
    private String currentDate;
    private TextInputLayout txtLeaveMsg, txtStartDate, txtEndDate;
    private ProgressBar loading;
    private Button btnSubmit, btnStartDate, btnEndDate;
    private View view;
    private Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_request_leave, container, false);

        txtLeaveMsg = view.findViewById(R.id.textInputLayoutReasonForLeave);
        txtStartDate = view.findViewById(R.id.textInputLayoutStartDate);
        txtEndDate = view.findViewById(R.id.textInputLayoutEndDate);
        loading = view.findViewById(R.id.progressBarLoadingLeave);
        btnSubmit = view.findViewById(R.id.btnSubmitLeaveReport);
        btnStartDate = view.findViewById(R.id.btnStartDate);
        btnEndDate = view.findViewById(R.id.btnEndDate);

        date = calendar.getTime();
        Log.i(TAG, "onCreateView: " + GetDateTime.getFormattedDate(new Date(String.valueOf(date))));

        initListeners();

        return view;
    }


    private void initListeners() {
        btnStartDate.setOnClickListener(this);
        btnEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartDate:
                callStartDate();
                break;
            case R.id.btnEndDate:
                callEndDate();
                break;
            case R.id.btnSubmitLeaveReport:
                validateInputs();
                break;
        }
    }

    private void callEndDate() {
        datePicker = new DatePickerDialog(getContext(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try {

                    //  Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());


                    if (date.before(new Date(currentDate))) {
                        checkSuccessEndDate();


                    } else if (date.after(new Date(currentDate))) {
                        // Log.i(TAG, "onDateSet: " + " today: " + date + " date selected: " + currentDate);
                        displayErrorOnEndDateSelected();
                    }


                    //  txtEndDate.getEditText().setText(GetDateTime.getFormattedDate(new Date(currentDate)));
                    //   getEndDate = txtEndDate.getEditText().getText().toString();

                    Log.i(TAG, "onDateSet: " + getEndDate);
                    Log.i(TAG, "txtEndDate: " + txtEndDate.getEditText().getText().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }, year, month, day);

        datePicker.show();
    }

    private void callStartDate() {
        datePicker = new DatePickerDialog(getContext(), new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());


                    if (date.before(new Date(currentDate))) {
                        checkSuccessSelectStartDate();


                    } else if (date.after(new Date(currentDate))) {
                        // Log.i(TAG, "onDateSet: " + " today: " + date + " date selected: " + currentDate);
                        displayErrorOnStartDateSelected();
                    }


                    Log.i(TAG, "onDateSet: " + getStartDate);
                    Log.i(TAG, "txtStartDate: " + txtStartDate.getEditText().getText().toString());


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }, year, month, day);

        datePicker.show();
    }

    //checks corresponding date from the user and allows them to proceed
    void checkSuccessSelectStartDate() {
        txtStartDate.setError("");
        txtStartDate.setErrorEnabled(false);
        txtStartDate.getEditText().setText(GetDateTime.getFormattedDate(new Date(currentDate)));
        getStartDate = txtStartDate.getEditText().getText().toString();
    }

    //if date selected is before the current date ... display error
    void displayErrorOnStartDateSelected() {
        txtStartDate.setErrorEnabled(true);
        txtStartDate.setError("Start date is not valid");
        txtStartDate.getEditText().getText().clear();
    }

    //Same for end date
    void checkSuccessEndDate() {
        txtEndDate.setError("");
        txtEndDate.setErrorEnabled(false);
        txtEndDate.getEditText().setText(GetDateTime.getFormattedDate(new Date(currentDate)));
        getEndDate = txtStartDate.getEditText().getText().toString();
    }

    //display error when wrong date is selected
    void displayErrorOnEndDateSelected() {
        txtEndDate.setErrorEnabled(true);
        txtEndDate.setError("End date is not valid");
        txtEndDate.getEditText().getText().clear();
    }


    private void validateInputs() {

        try {
            String getLeaveMsg = txtLeaveMsg.getEditText().getText().toString();

            //        check and validate inputs
            if (!TextUtils.isEmpty(getLeaveMsg) && (!TextUtils.isEmpty(txtStartDate.getEditText().getText().toString())) && (!TextUtils.isEmpty(txtEndDate.getEditText().getText().toString())) && getLeaveMsg.length() > 10) {
                txtLeaveMsg.setErrorEnabled(false);
                requestLeaveListener.onButtonClicked(getLeaveMsg, getStartDate, getEndDate);
                loading.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loading.setVisibility(View.INVISIBLE);
                        dismiss();


                    }
                }, 2000);
            } else if (TextUtils.isEmpty(getLeaveMsg)) {

                loading.setVisibility(View.GONE);
                txtLeaveMsg.setError("Required");
                txtLeaveMsg.setErrorEnabled(true);

            } else if (TextUtils.isEmpty(getStartDate)) {

                loading.setVisibility(View.GONE);
                txtStartDate.setError("You must select a start date");
                txtStartDate.setErrorEnabled(true);

            } else if (TextUtils.isEmpty(getEndDate)) {

                loading.setVisibility(View.GONE);
                txtEndDate.setError("You must select an end date");
                txtEndDate.setErrorEnabled(true);

            } else if (getLeaveMsg.length() <= 5) {

                loading.setVisibility(View.GONE);
                txtLeaveMsg.setError("Message too short");
                txtLeaveMsg.setErrorEnabled(true);

            } else if (getLeaveMsg.matches("[-+]?\\d+(\\.\\d+)?")) {

                loading.setVisibility(View.GONE);
                txtLeaveMsg.setError("Invalid inputs");
                txtLeaveMsg.setErrorEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            requestLeaveListener = (RequestLeaveListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement interface");
        }


    }


    public interface RequestLeaveListener {
        void onButtonClicked(String leaveMessage, String startDate, String endDate);
    }
}

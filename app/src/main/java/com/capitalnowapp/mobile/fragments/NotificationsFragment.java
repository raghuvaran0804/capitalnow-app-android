package com.capitalnowapp.mobile.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.activities.BaseActivity;
import com.capitalnowapp.mobile.adapters.NotificationsAdapter;
import com.capitalnowapp.mobile.beans.NotificationObj;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.customviews.CNTextView;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.GenericRequest;
import com.capitalnowapp.mobile.models.GenericResponse;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.capitalnowapp.mobile.util.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {
    private Context context;
    private Activity currentActivity;
    private CNSharedPreferences sharedPreferences;
    private String userId;

    private LinearLayout currentLayout;
    private ImageView ivNoNotification;
    private CNTextView tvNoNotification;
    private LayoutInflater layoutInflater;

    private CNModel cnModel;
    private NotificationsAdapter notificationsAdapter;
    private ArrayList<NotificationObj> allNotifications;
    private RecyclerView rvNotifications;
    private CNTextView btProfile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentLayout = (LinearLayout) inflater.inflate(R.layout.fragment_notifications, container, false);

        return currentLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        btProfile = view.findViewById(R.id.btProfile);
        btProfile.setOnClickListener(v -> {
          startActivity(new Intent(getActivity(), DashboardActivity.class));
        });
        ivNoNotification = view.findViewById(R.id.ivNoNotification);
        tvNoNotification = view.findViewById(R.id.tvNoNotification);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationsAdapter = new NotificationsAdapter(this, allNotifications);
        rvNotifications.setAdapter(notificationsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            context = getContext();
            currentActivity = getActivity();

            sharedPreferences = new CNSharedPreferences(context);
            userId = ((BaseActivity) currentActivity).userDetails.getUserId();

            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            cnModel = new CNModel(context, currentActivity, Constants.RequestFrom.MY_LOANS);
            refreshData(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showAlertDialog(String message) {
        if (CNProgressDialog.isProgressDialogShown)
            CNProgressDialog.hideProgressDialog();

        CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), message);
    }

    public void refreshData(boolean b) {
        if(b)
        CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
        String token = ((BaseActivity)currentActivity).getUserToken();
        cnModel.getNotifications(this, userId, token);
    }

    public void updateNotifications(ArrayList<NotificationObj> notificationsList) {
        if (notificationsList.size() >0) {
            notificationsAdapter.setNotifications(notificationsList);
            notificationsAdapter.notifyDataSetChanged();
            rvNotifications.setVisibility(View.VISIBLE);
            ivNoNotification.setVisibility(View.GONE);
            tvNoNotification.setVisibility(View.GONE);
            btProfile.setVisibility(View.GONE);
        } else {
            ivNoNotification.setVisibility(View.VISIBLE);
            tvNoNotification.setVisibility(View.VISIBLE);
            btProfile.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.GONE);
        }
        CNProgressDialog.hideProgressDialog();
    }

    public void markAsRead(NotificationObj notificationId) {
        //QCProgressDialog.showProgressDialog(getActivity(), Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(getActivity());
        GenericRequest genericRequest = new GenericRequest();
        genericRequest.setDataStr(notificationId.getNotificationId());
        genericRequest.setUserId(userId);
        genericRequest.setDeviceUniqueId(Utility.getInstance().getDeviceUniqueId(currentActivity));
        String token = ((BaseActivity)currentActivity).getUserToken();
        genericAPIService.readNotification(genericRequest, token);

        genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
            @Override
            public void responseData(String responseBody) {
             //   QCProgressDialog.hideProgressDialog();
                GenericResponse genericResponse = new Gson().fromJson(responseBody, GenericResponse.class);
                if (genericResponse.getStatus()) {
                    refreshData(false);
                } else {
                  //  QCAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), genericResponse.getMessage());
                }
            }
        });

        genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
            @Override
            public void errorData(Throwable throwable) {
              //  QCProgressDialog.hideProgressDialog();
              //  QCAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), getString(R.string.error_failure));
            }
        });
    }
}

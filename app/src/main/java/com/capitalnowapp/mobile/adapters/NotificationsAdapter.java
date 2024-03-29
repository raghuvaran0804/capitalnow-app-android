package com.capitalnowapp.mobile.adapters;

import android.net.Uri;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.beans.NotificationObj;
import com.capitalnowapp.mobile.customviews.CNTextView;
import com.capitalnowapp.mobile.fragments.NotificationsFragment;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.util.Utility;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter {
    private NotificationsFragment context;
    private LayoutInflater layoutInflater;
    private Utility utility;
    private ArrayList<NotificationObj> notifications;
    private CNTextView tv_noti_title, tv_noti_details, tvRead, tvTime;
    private ImageView ivNotificationIcon;
    private LinearLayout llNotification;
    private long mLastClickTime = 0;

    public NotificationsAdapter(NotificationsFragment context, ArrayList<NotificationObj> notificationObjArrayList) {
        this.context = context;
        this.notifications = notificationObjArrayList;
    }

    public void setNotifications(ArrayList<NotificationObj> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_list_view_row_item, parent, false);
        return new NotificationVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NotificationObj notificationObj = notifications.get(position);
        tv_noti_title.setText(Html.fromHtml(notificationObj.getTitle().trim()));
        tv_noti_details.setText(Html.fromHtml(notificationObj.getMessage().trim()));

        if (notificationObj.getIcon() != null && !notificationObj.getIcon().equals("")) {
            if (notificationObj.getIcon().contains(".svg")) {
                GlideToVectorYou.justLoadImage(context.getActivity(), Uri.parse(notificationObj.getIcon()), ivNotificationIcon);
            } else {
                Glide.with(holder.itemView.getContext()).load(notificationObj.getIcon()).into(ivNotificationIcon);
            }
        }

        llNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!notificationObj.getReadStatus().equals("1")) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    context.markAsRead(notificationObj);
                }
                ((DashboardActivity) context.getActivity()).redirect(notificationObj);
            }
        });

        tvTime.setText(Utility.getTimeAgo(notificationObj.getCreatedAt(), holder.itemView.getContext()));

        if (notificationObj.getReadStatus().equals("1")) {
            llNotification.setAlpha(0.3f);
        } else {
            llNotification.setAlpha(1);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (notifications == null) {
            return 0;
        }
        return notifications.size();
    }

    private class NotificationVH extends RecyclerView.ViewHolder {
        public NotificationVH(View view) {
            super(view);
            tv_noti_details = view.findViewById(R.id.tv_noti_details);
            tv_noti_title = view.findViewById(R.id.tv_noti_title);
            tvRead = view.findViewById(R.id.tvRead);
            llNotification = view.findViewById(R.id.llNotification);
            tvTime = view.findViewById(R.id.tvTime);
            ivNotificationIcon = view.findViewById(R.id.ivNotificationIcon);
        }
    }
}

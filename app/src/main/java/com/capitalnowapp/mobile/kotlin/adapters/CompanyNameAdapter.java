package com.capitalnowapp.mobile.kotlin.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.beans.MasterData;
import com.capitalnowapp.mobile.kotlin.activities.ProfessionalDetailsActivity;

import java.util.ArrayList;
import java.util.List;

public class CompanyNameAdapter extends ArrayAdapter<MasterData> {
    private final ProfessionalDetailsActivity mContext;
    private final List<MasterData> mMasterData;
    private final List<MasterData> mMasterDataAll;
    private final int mLayoutResourceId;

    public CompanyNameAdapter(ProfessionalDetailsActivity context, int resource, List<MasterData> departments) {
        super(context, resource, departments);
        this.mContext = context;
        this.mLayoutResourceId = resource;
        this.mMasterData = new ArrayList<>(departments);
        this.mMasterDataAll = new ArrayList<>(departments);
    }


    public int getCount() {
        return mMasterData.size();
    }

    public MasterData getItem(int position) {
        return mMasterData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            MasterData department = getItem(position);
            TextView name = convertView.findViewById(R.id.tvName);
            mContext.fromSelection = true;

            name.setText(department.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((MasterData) resultValue).getName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<MasterData> departmentsSuggestion = new ArrayList<>();
                if (constraint != null) {
                    for (MasterData department : mMasterDataAll) {
                        if (department.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            departmentsSuggestion.add(department);
                        }
                    }
                    filterResults.values = departmentsSuggestion;
                    filterResults.count = departmentsSuggestion.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mMasterData.clear();
                if (results != null && results.count > 0) {
                    // avoids unchecked cast warning when using mDepartments.addAll((ArrayList<MasterData>) results.values);
                    for (Object object : (List<?>) results.values) {
                        if (object instanceof MasterData) {
                            mMasterData.add((MasterData) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (constraint == null) {
                    // no filter, add entire original list back in
                    mMasterData.addAll(mMasterDataAll);
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
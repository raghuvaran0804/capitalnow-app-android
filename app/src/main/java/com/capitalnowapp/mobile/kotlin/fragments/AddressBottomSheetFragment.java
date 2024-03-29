package com.capitalnowapp.mobile.kotlin.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.activities.BaseActivity;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.kotlin.activities.BasicDetailsActivity;
import com.capitalnowapp.mobile.kotlin.activities.MapActivity;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.PinCodeData;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddressBottomSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressBottomSheetFragment extends Fragment {
    private TextView tvSubmit;
    private Context context;
    private Activity currentActivity;


    private TextInputEditText etAdr1;
    private TextInputEditText etCity;
    private static TextInputEditText etState;
    private TextInputEditText etPin;
    private TextInputEditText etArea;

    private static PinCodeData pinCodeData;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public CNModel cnModel;
    String locality, city, state, pincode, userId;
    private ImageView ivBack;

    public AddressBottomSheetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddressBottomSheetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressBottomSheetFragment newInstance(String param1, String param2) {
        AddressBottomSheetFragment fragment = new AddressBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            locality = getArguments().getString("locality");
            city = getArguments().getString("city");
            state = getArguments().getString("state");
            pincode = getArguments().getString("pincode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address_bottom_sheet, container, false);

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        currentActivity = getActivity();
        userId = ((BaseActivity) requireActivity()).userDetails.getUserId();
        cnModel = new CNModel(context, currentActivity, Constants.RequestFrom.ADDRESS_PINCODE);

        etAdr1 = view.findViewById(R.id.etAdr1);
        etCity = view.findViewById(R.id.etCity);
        etState = view.findViewById(R.id.etState);
        etPin = view.findViewById(R.id.etPin);
        etArea = view.findViewById(R.id.etArea);
        tvSubmit = view.findViewById(R.id.tvSubmit);
        ivBack = view.findViewById(R.id.ivBack);

        etCity.setText(city);
        etState.setText(state);
        etPin.setText(pincode);
        // etAdr1.setText(locality);
        /*etPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()== 6){

                    pincode = s.toString();
                    //getCityAndState(pincode);
                }
            }
        });*/

        ivBack.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), BasicDetailsActivity.class);
            startActivity(i);
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                JSONObject obj = new JSONObject();
                try {
                    //obj.put("cnid",cnModel.userDetails.getQcId() );
                    obj.put(getString(R.string.interaction_type), "CONFIRM Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.edit_address_page_submitted));
                if (validData()) {
                    if (etAdr1.getText().toString().trim().equals("")) {
                        Toast.makeText(getContext(), "Please enter your House Number/Flat Number/Villa Number", Toast.LENGTH_SHORT).show();
                    } else {
                        String addressLine1 = etAdr1.getText().toString();
                        String city = etCity.getText().toString();
                        String state = etState.getText().toString();
                        String pin = etPin.getText().toString();
                        //String area = etArea.getText().toString();
                        Intent intent = new Intent();
                        intent.putExtra("addressLine1", addressLine1);
                        intent.putExtra("city", city);
                        intent.putExtra("state", state);
                        //intent.putExtra("area",area);
                        intent.putExtra("pin", pin);
                        ((MapActivity) requireActivity()).setResultAndFinish(intent);
                    }
                }else {
                    Toast.makeText(getContext(), "Please enter your House Number/Flat Number/Villa Number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCityAndState(String pincode) {
        try{
            cnModel.pinCode(pincode);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void updateStateAndCity(JSONObject response) {
        try{
            /*JSONObject jsonObject = new JSONObject(String.valueOf(response));
            Log.d("updateStateAndCity", String.valueOf(jsonObject));*/
            String strJson = new String();
            strJson = response.toString();
            pinCodeData = new Gson().fromJson(strJson, PinCodeData.class);




        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private boolean validData() {

        if (etAdr1.getText().toString().trim().length() >= 5) {

            //if (etArea.getText().toString().trim().length() >= 3) {

                if (etPin.getText().toString().trim().length() >= 5) {

                    if (etState.getText().toString().trim().length() >= 3) {

                        if (etCity.getText().toString().trim().length() >= 3) {
                            return true;
                        }
                    }
                }
            //}

        }
        return false;
    }
}
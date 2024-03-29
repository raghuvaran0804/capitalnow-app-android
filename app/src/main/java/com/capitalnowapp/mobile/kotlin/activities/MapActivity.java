package com.capitalnowapp.mobile.kotlin.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.kotlin.fragments.AddressBottomSheetFragment;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.GenericResponse;
import com.capitalnowapp.mobile.models.SendLocationReq;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapActivity extends RegistrationHomeActivity {
    public CNModel cnModel;
    String fullAdr;
    String address1, locality, city,area, state, pincode, adrLine1 = "";
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    TextView Location, EditAddress, Location1, tvFinish;
    private EditText etAdr1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Location = findViewById(R.id.tvLocation);
        Location1 = findViewById(R.id.tvMainAddress);
        EditAddress = findViewById(R.id.tvEditAddress);
        etAdr1 = findViewById(R.id.etAdr1);
        tvFinish = findViewById(R.id.tvFinish);

        tvFinish.setOnClickListener(v -> {
            JSONObject obj = new JSONObject();
            try {
                //obj.put("cnid", cnModel.userDetails.getQcId());
                obj.put(getString(R.string.interaction_type), "CONFIRM Button Clicked");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            TrackingUtil.pushEvent(obj, getString(R.string.current_location_page_interacted));
            if (etAdr1.getText().toString().trim().equals("")) {
                //displayToast("Please enter your House Number/Flat Number/Villa Number");
                Intent intent = new Intent(MapActivity.this, BasicDetailsActivity.class);
                intent.putExtra("addressLine1", etAdr1.getText().toString().trim());
                intent.putExtra("city", city);
                //intent.putExtra("area",area);
                intent.putExtra("state", state);
                intent.putExtra("pin", pincode);
                setResultAndFinish(intent);
            } else {
                Intent intent = new Intent(MapActivity.this, BasicDetailsActivity.class);
                intent.putExtra("addressLine1", etAdr1.getText().toString().trim());
                //intent.putExtra("area", area);
                intent.putExtra("city", city);
                intent.putExtra("state", state);
                intent.putExtra("pin", pincode);
                setResultAndFinish(intent);

            }
            EditAddress.callOnClick();
        });

        EditAddress.setOnClickListener(v -> {
            JSONObject obj = new JSONObject();
            try {
                //obj.put("cnid", cnModel.userDetails.getQcId());
                obj.put(getString(R.string.interaction_type), "EDIT ADDRESS Button Clicked");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            TrackingUtil.pushEvent(obj, getString(R.string.current_location_page_interacted));

                openSheet();
        });

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }

        etAdr1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adrLine1 = editable.toString();
            }
        });
    }

    public void setResultAndFinish(Intent intent) {

        if(intent!=null && intent.getExtras()!=null){
            adrLine1 = intent.getStringExtra("addressLine1");
            //area = intent.getStringExtra("area");
            city = intent.getStringExtra("city");
            state = intent.getStringExtra("state");
            pincode = intent.getStringExtra("pin");
        }
        sendAddress();
    }


    private void getCurrentLocation() {
        EditAddress.callOnClick();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                supportMapFragment.getMapAsync(googleMap -> {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(latLng).title("I am Here");
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                    googleMap.getUiSettings().setZoomControlsEnabled(true);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);
                    googleMap.addMarker(options);
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
                        Address adr = new Address(Locale.getDefault());
                        Address adr1, adr2 = new Address(Locale.getDefault());
                        String mainAddress = "";

                        if (addressList.size() > 0) {
                            adr = addressList.get(0);
                        }

                        if (addressList.size() > 1) {
                            adr1 = addressList.get(1);
                            mainAddress = adr1.getFeatureName();
                        }
                        if (addressList.size() > 3) {
                            adr2 = addressList.get(3);
                        }
                        address1 = adr.getLocality() + "," + adr2.getLocality() + "," + adr.getSubAdminArea() + "," + adr.getAdminArea() + "," + adr.getPostalCode() + ".";
                        String address1 = adr.getLocality() + "," + adr2.getLocality() + "," + adr.getSubAdminArea() + "," + adr.getAdminArea() + "," + adr.getPostalCode() + ".";

                        if(address1.contains("null")){
                            EditAddress.callOnClick();
                        }else {
                            Location.setText(address1);
                            Location1.setText((mainAddress));
                            fullAdr = mainAddress + address1;

                            locality = adr.getLocality();
                            city = adr.getSubAdminArea();
                            state = adr.getAdminArea();
                            pincode = adr.getPostalCode();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

        });
    }

    private void openSheet() {
        Bundle bundle = new Bundle();
        bundle.putString("locality", locality);
        //bundle.putString("area",area);
        bundle.putString("city", city);
        bundle.putString("state", state);
        bundle.putString("pincode", pincode);
        AddressBottomSheetFragment addressBottomSheetFragment = new AddressBottomSheetFragment();
        addressBottomSheetFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(android.R.id.content, addressBottomSheetFragment).commit();

    }

    private void sendAddress() {
        CNProgressDialog.showProgressDialog(this, Constants.LOADING_MESSAGE);
        GenericAPIService genericAPIService = new GenericAPIService(this);
        SendLocationReq sendLocationReq = new SendLocationReq();
        sendLocationReq.setUserId(userDetails.getUserId());
        sendLocationReq.setAddressline1(adrLine1);
        sendLocationReq.setCity(city);
        //sendLocationReq.setArea(area);
        sendLocationReq.setState(state);
        sendLocationReq.setPincode(pincode);
        String token = getUserToken();
        genericAPIService.addressSave(sendLocationReq, token);
        genericAPIService.setOnDataListener(new GenericAPIService.DataInterface() {
            @Override
            public void responseData(String responseBody) {
                CNProgressDialog.hideProgressDialog();
                GenericResponse genericResponse = new Gson().fromJson(responseBody, GenericResponse.class);
                if (genericResponse != null && genericResponse.getStatus()) {
                    Intent intent = new Intent(MapActivity.this, BasicDetailsActivity.class);
                    intent.putExtra("addressLine1",adrLine1);
                    //intent.putExtra("area",area);
                    intent.putExtra("city", city);
                    intent.putExtra("state", state);
                    intent.putExtra("pin", pincode);
                    setResult(RESULT_OK, intent);
                    MapActivity.this.finish();

                } else {
                    //Failure
                    EditAddress.callOnClick();
                    displayToast("Please enter your House Number/Flat Number/Villa Number");


                }
            }
        });
        genericAPIService.setOnErrorListener(new GenericAPIService.ErrorInterface() {
            @Override
            public void errorData(Throwable throwable) {
                //Failure
                CNProgressDialog.hideProgressDialog();
                //displayToast("Please enter your House Number/Flat Number/Villa Number");

            }
        });

    }


}

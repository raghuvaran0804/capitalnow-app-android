package com.capitalnowapp.mobile.fragments;


import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.capitalnowapp.mobile.R;
import com.capitalnowapp.mobile.activities.AppPermissionsActivity;
import com.capitalnowapp.mobile.activities.BaseActivity;
import com.capitalnowapp.mobile.beans.GlobalContent;
import com.capitalnowapp.mobile.beans.LoanAgreementConsent;
import com.capitalnowapp.mobile.beans.UserData;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.customviews.CNAlertDialog;
import com.capitalnowapp.mobile.customviews.CNProgressDialog;
import com.capitalnowapp.mobile.customviews.CNTextView;
import com.capitalnowapp.mobile.kotlin.activities.BBPSWebViewActivity;
import com.capitalnowapp.mobile.kotlin.activities.ChatActivity;
import com.capitalnowapp.mobile.kotlin.activities.DashboardActivity;
import com.capitalnowapp.mobile.kotlin.activities.ENachActivity;
import com.capitalnowapp.mobile.kotlin.activities.FederalRegistrationActivity;
import com.capitalnowapp.mobile.kotlin.activities.MandatoryPermissionsActivity;
import com.capitalnowapp.mobile.kotlin.activities.UploadBankDetailsActivity;
import com.capitalnowapp.mobile.kotlin.adapters.HomeImageSliderAdapter;
import com.capitalnowapp.mobile.models.BannerImageRequest;
import com.capitalnowapp.mobile.models.BannerImageResponse;
import com.capitalnowapp.mobile.models.BannerImages;
import com.capitalnowapp.mobile.models.BbpsBillPayResponse;
import com.capitalnowapp.mobile.models.CNModel;
import com.capitalnowapp.mobile.models.CancelReason;
import com.capitalnowapp.mobile.models.CancelTwlLoanReq;
import com.capitalnowapp.mobile.models.CancelTwlLoanResponse;
import com.capitalnowapp.mobile.models.FileUploadResponse;
import com.capitalnowapp.mobile.models.GenericRequest;
import com.capitalnowapp.mobile.models.GetAdditionalDocReq;
import com.capitalnowapp.mobile.models.GetAdditionalDocResponse;
import com.capitalnowapp.mobile.models.GetPendingDocReq;
import com.capitalnowapp.mobile.models.GetPendingDocResponse;
import com.capitalnowapp.mobile.models.HomeBannerImage;
import com.capitalnowapp.mobile.models.MemberUpgradeConsentReq;
import com.capitalnowapp.mobile.models.MemberUpgradeConsentResponse;
import com.capitalnowapp.mobile.models.NachData;
import com.capitalnowapp.mobile.models.OfferScrollResponse;
import com.capitalnowapp.mobile.models.SaveCCDataResponse;
import com.capitalnowapp.mobile.models.TwlLoanStatusResponse;
import com.capitalnowapp.mobile.models.TwlProcessingFee;
import com.capitalnowapp.mobile.models.loan.LoanStatusResponse;
import com.capitalnowapp.mobile.models.userdetails.UserDetails;
import com.capitalnowapp.mobile.retrofit.GenericAPIService;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.capitalnowapp.mobile.util.TrackingUtil;
import com.capitalnowapp.mobile.util.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private Context context;
    private Activity currentActivity;
    private GlobalContent globalContent;
    private CNModel cnModel;
    private String userId;
    private String loansResponse = "";
    private String currentScreen = "";
    public CNSharedPreferences sharedPreferences;
    public TwlLoanStatusResponse twlLoanStatusResponse;
    public MemberUpgradeConsentResponse memberUpgradeConsentResponse;
    public SaveCCDataResponse saveCCDataResponse;
    public BannerImageResponse bannerImageResponse;

    public GetAdditionalDocResponse getAdditionalDocResponse;
    public GetPendingDocResponse getPendingDocResponse;


    private View currentLayout;
    private CNTextView tv_loan_eligibility_limit, tv_loan_eligibility_limit_cs,
            tv_current_loan_avail_cs, tv_current_loan_avail, tv_current_loan_avail_lock, tv_loan_status_cs, tv_loan_status, tv_loan_status_lock, tvViewConsent, tv_loan_eligibility_limit_lock, tv_loan_eligibility_limit_vehicle_lock, tv_loan_eligibility_limit_vehicle,
            tv_current_loan_avail_vehicle, tv_current_loan_avail_vehicle_lock, tv_loan_status_vehicle, tv_loan_status_vehicle_lock, tvCongrats, tvDelivered;

    private LinearLayout frameCta1, llConsent, llMain, llVerified, llCancelLoan, llUploadQuotation, llLoanStatus,
            llImg, llTwlImg, llTxt, llTwlTxt, llBtn, llCSPLView, llPLView, llVLView, llPLLock, llVlLock, llProcessView,
            llNachSetUp, llCreditCard, llMonitoringView, llTwlLoanStatus, llCSBeforeDis, llCSAfterDis, llLimitEnhance, llAdditionalDocs, llPendingDocs;
    private FrameLayout flCongrats;
    private CNTextView tvC1, tvC2, tvC3, tvC4, tvConsentTitle, tvStar, tvUserName, tvLoanStatus, tvTwlLoanStatusTitle,
            tvTwlLoanId, tvAction, tvVerifiedText, tvLoanId, tv1, tv2, tv3, tv4, tv5, tvRate,
            tvTwl1, tvTwl2, tvTwl3, tvTwl4, tvTwl5, tvGreeting, tvUserAt, tvUserAt2, tvUserAt1, tvApplyNow1, tvApplyNow2, tvUnlockPersonalLoan,
            tvUnlockVehicleLoan, tvIPLoan, tvEligibilityText, tvEligibilityNote, tvTwlEligTitle, tvTwlEligText,
            tvTwlEligNote, tvPaymentPending, tvProcessingFee, tvDownPaymentAmount,
            tvEligibilityAmount, tvGST, tvMonitTitle, tvMonitText, tvMonitBtn, tvNachTitle, tvNachText, tvOnRoadPrice, tvSetUp, limitUpdate, tvTotalAmount,
            tvWhydownPayment, tvPayNow, tvCSApplyNow, tvCSLoanId, tvLoanAmount, tvTenure, tvEmiAmount, tvUtrNumber, tvLHText, tvProceed,
            tvUploadPendingDoc, tvUploadAdditionalDoc, tvAdditionalDocTitle, tvAdditionalDocText, tvPendingDocTitle, tvPendingDocText;
    private ImageView iv1, iv2, iv3, iv4, iv5, ivCreditApply, ivCreditInterested;
    private ImageView ivTwl1, ivTwl2, ivTwl3, ivTwl4, ivTwl5, ivCustomerImg;
    private View view1, view2, view3, view4;
    private View viewTwl1, viewTwl2, viewTwl3, viewTwl4;
    ImageButton btnChat;

    private LinearLayout llBB1, llBB2, llBB3, llBB4, llBB5, llBB6, llBB7, llBB8, llBBPSView;

    private ImageView ivMobile, ivElectricity, ivDTH, ivCCPayment, ivRentPayment, ivLoanRepayment, ivBookACylinder, ivSeeAll, ivBg, ivBg1;

    private TextView tvMobileText, tvElectricity, tvDTH, tvCCPayment, tvRentPayment, tvLoanRepayment, tvBookACylinder, tvSeeAll, tvOffreScroll;

    private SliderView imageSlider;

    private BbpsBillPayResponse bbpsBillPayResponse;
    private OfferScrollResponse offerScrollResponse;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            currentLayout = inflater.inflate(R.layout.fragment_home, container, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            /*JSONObject obj = new JSONObject();
            try {
                obj.put("cnid",((BaseActivity)currentActivity).userDetails.getQcId());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            TrackingUtil.pushEvent(obj, getString(R.string.home_page_landed));
*/

            context = getContext();
            currentActivity = getActivity();
            globalContent = GlobalContent.getInstance();
            userId = ((BaseActivity) currentActivity).userDetails.getUserId();
            cnModel = new CNModel(context, currentActivity, Constants.RequestFrom.PROFILE_PAGE);
            //((DashboardActivity) currentActivity).getApplyLoanData();
            CNProgressDialog.hideProgressDialog();

            imageSlider = currentLayout.findViewById(R.id.imageSlider);

            tv_loan_eligibility_limit = currentLayout.findViewById(R.id.tv_loan_eligibility_limit);
            tv_loan_eligibility_limit_lock = currentLayout.findViewById(R.id.tv_loan_eligibility_limit_lock);
            tv_current_loan_avail = currentLayout.findViewById(R.id.tv_current_loan_avail);
            tv_current_loan_avail_lock = currentLayout.findViewById(R.id.tv_current_loan_avail_lock);
            tv_loan_status = currentLayout.findViewById(R.id.tv_loan_status);
            tv_loan_status_lock = currentLayout.findViewById(R.id.tv_loan_status_lock);
            tv_loan_status_cs = currentLayout.findViewById(R.id.tv_loan_status_cs);

            tv_loan_eligibility_limit_vehicle = currentLayout.findViewById(R.id.tv_loan_eligibility_limit_vehicle);
            tv_loan_eligibility_limit_vehicle_lock = currentLayout.findViewById(R.id.tv_loan_eligibility_limit_vehicle_lock);
            tv_loan_eligibility_limit_cs = currentLayout.findViewById(R.id.tv_loan_eligibility_limit_cs);
            tv_current_loan_avail_cs = currentLayout.findViewById(R.id.tv_current_loan_avail_cs);
            tv_current_loan_avail_vehicle = currentLayout.findViewById(R.id.tv_current_loan_avail_vehicle);
            tv_current_loan_avail_vehicle_lock = currentLayout.findViewById(R.id.tv_current_loan_avail_vehicle_lock);
            tv_loan_status_vehicle = currentLayout.findViewById(R.id.tv_loan_status_vehicle);
            tv_loan_status_vehicle_lock = currentLayout.findViewById(R.id.tv_loan_status_vehicle_lock);

            //frameCta1 = currentLayout.findViewById(R.id.frameCta1);
            tvViewConsent = currentLayout.findViewById(R.id.tvViewConsent);

            llPendingDocs = currentLayout.findViewById(R.id.llPendingDocs);
            llAdditionalDocs = currentLayout.findViewById(R.id.llAdditionalDocs);
            llProcessView = currentLayout.findViewById(R.id.llProcessView);
            llLimitEnhance = currentLayout.findViewById(R.id.llLimitEnhance);
            llNachSetUp = currentLayout.findViewById(R.id.llNachSetUp);
            llCreditCard = currentLayout.findViewById(R.id.llCreditCard);
            llMonitoringView = currentLayout.findViewById(R.id.llMonitoringView);
            tvMonitBtn = currentLayout.findViewById(R.id.tvMonitBtn);
            tvMonitText = currentLayout.findViewById(R.id.tvMonitText);
            tvMonitText = currentLayout.findViewById(R.id.tvMonitText);
            tvSetUp = currentLayout.findViewById(R.id.tvSetUp);
            llConsent = currentLayout.findViewById(R.id.llConsent);
            llVerified = currentLayout.findViewById(R.id.llVerified);
            tvC1 = currentLayout.findViewById(R.id.tvC1);
            tvC2 = currentLayout.findViewById(R.id.tvC2);
            tvC3 = currentLayout.findViewById(R.id.tvC3);
            tvC4 = currentLayout.findViewById(R.id.tvC4);
            tvAction = currentLayout.findViewById(R.id.tvAction);
            tvConsentTitle = currentLayout.findViewById(R.id.tvConsentTitle);
            tvStar = currentLayout.findViewById(R.id.tvStar);
            tvVerifiedText = currentLayout.findViewById(R.id.tvVerifiedText);
            tvCongrats = currentLayout.findViewById(R.id.tvCongrats);
            tvDelivered = currentLayout.findViewById(R.id.tvDelivered);
            tvLHText = currentLayout.findViewById(R.id.tvLHText);
            tvProceed = currentLayout.findViewById(R.id.tvProceed);

            llPLView = currentLayout.findViewById(R.id.llPLView);
            llCSPLView = currentLayout.findViewById(R.id.llCSPLView);
            llPLLock = currentLayout.findViewById(R.id.llPLLock);
            llVLView = currentLayout.findViewById(R.id.llVLView);
            llVlLock = currentLayout.findViewById(R.id.llVLLock);


            llCSBeforeDis = currentLayout.findViewById(R.id.llCSBeforeDis);
            llCSAfterDis = currentLayout.findViewById(R.id.llCSAfterDis);

            llLoanStatus = currentLayout.findViewById(R.id.llLoanStatus);
            flCongrats = currentLayout.findViewById(R.id.flCongrats);
            llMain = currentLayout.findViewById(R.id.llMain);
            llTwlLoanStatus = currentLayout.findViewById(R.id.llTwlLoanStatus);
            llCancelLoan = currentLayout.findViewById(R.id.llCancelLoan);
            llUploadQuotation = currentLayout.findViewById(R.id.llUploadQuotation);
            tvLoanId = currentLayout.findViewById(R.id.tvLoanId);
            tvLoanStatus = currentLayout.findViewById(R.id.tvLoanStatus);
            ivCreditApply = currentLayout.findViewById(R.id.ivCreditApply);
            ivCreditInterested = currentLayout.findViewById(R.id.ivCreditInterested);
            iv1 = currentLayout.findViewById(R.id.iv1);
            iv2 = currentLayout.findViewById(R.id.iv2);
            iv3 = currentLayout.findViewById(R.id.iv3);
            iv4 = currentLayout.findViewById(R.id.iv4);
            iv5 = currentLayout.findViewById(R.id.iv5);

            ivTwl1 = currentLayout.findViewById(R.id.ivTwl1);
            ivTwl2 = currentLayout.findViewById(R.id.ivTwl2);
            ivTwl3 = currentLayout.findViewById(R.id.ivTwl3);
            ivTwl4 = currentLayout.findViewById(R.id.ivTwl4);
            ivTwl5 = currentLayout.findViewById(R.id.ivTwl5);
            tvTwlLoanStatusTitle = currentLayout.findViewById(R.id.tvTwlLoanStatusTitle);
            tvTwlLoanId = currentLayout.findViewById(R.id.tvTwlLoanId);
            ivCustomerImg = currentLayout.findViewById(R.id.ivCustomerImg);

            tv1 = currentLayout.findViewById(R.id.tv1);
            tv2 = currentLayout.findViewById(R.id.tv2);
            tv3 = currentLayout.findViewById(R.id.tv3);
            tv4 = currentLayout.findViewById(R.id.tv4);
            tv5 = currentLayout.findViewById(R.id.tv5);
            tvRate = currentLayout.findViewById(R.id.tvRate);

            tvTwl1 = currentLayout.findViewById(R.id.tvTwl1);
            tvTwl2 = currentLayout.findViewById(R.id.tvTwl2);
            tvTwl3 = currentLayout.findViewById(R.id.tvTwl3);
            tvTwl4 = currentLayout.findViewById(R.id.tvTwl4);
            tvTwl5 = currentLayout.findViewById(R.id.tvTwl5);

            view1 = currentLayout.findViewById(R.id.view1);
            view2 = currentLayout.findViewById(R.id.view2);
            view3 = currentLayout.findViewById(R.id.view3);
            view4 = currentLayout.findViewById(R.id.view4);
            llImg = currentLayout.findViewById(R.id.llImg);
            llTxt = currentLayout.findViewById(R.id.llTxt);
            btnChat = view.findViewById(R.id.btnChat);
            tvUserName = view.findViewById(R.id.tvUserName);

            viewTwl1 = currentLayout.findViewById(R.id.viewTwl1);
            viewTwl2 = currentLayout.findViewById(R.id.viewTwl2);
            viewTwl3 = currentLayout.findViewById(R.id.viewTwl3);
            viewTwl4 = currentLayout.findViewById(R.id.viewTwl4);
            llTwlImg = currentLayout.findViewById(R.id.llTwlImg);
            llTwlTxt = currentLayout.findViewById(R.id.llTwlTxt);
            llBtn = currentLayout.findViewById(R.id.llBtn);


            tvUserAt = view.findViewById(R.id.tvUserAt);
            tvUserAt1 = view.findViewById(R.id.tvUserAt1);
            tvUserAt2 = view.findViewById(R.id.tvUserAt2);
            tvApplyNow1 = view.findViewById(R.id.tvApplyNow1);
            tvApplyNow2 = view.findViewById(R.id.tvApplyNow2);
            tvCSApplyNow = view.findViewById(R.id.tvCSApplyNow);
            tvUnlockPersonalLoan = view.findViewById(R.id.tvUnlockPersonalLoan);
            tvUnlockVehicleLoan = view.findViewById(R.id.tvUnlockVehicleLoan);


            tvCSLoanId = view.findViewById(R.id.tvCSLoanId);
            tvLoanAmount = view.findViewById(R.id.tvLoanAmount);
            tvTenure = view.findViewById(R.id.tvTenure);
            tvEmiAmount = view.findViewById(R.id.tvEmiAmount);
            tvUtrNumber = view.findViewById(R.id.tvUtrNumber);

            tvAdditionalDocText = view.findViewById(R.id.tvAdditionalDocText);
            tvAdditionalDocTitle = view.findViewById(R.id.tvAdditionalDocTitle);
            tvPendingDocTitle = view.findViewById(R.id.tvPendingDocTitle);
            tvPendingDocText = view.findViewById(R.id.tvPendingDocText);
            tvOffreScroll = view.findViewById(R.id.tvOffreScroll);
            tvOffreScroll.setSelected(true);

            llBB1 = currentLayout.findViewById(R.id.llBB1);
            llBB2 = currentLayout.findViewById(R.id.llBB2);
            llBB3 = currentLayout.findViewById(R.id.llBB3);
            llBB4 = currentLayout.findViewById(R.id.llBB4);
            llBB5 = currentLayout.findViewById(R.id.llBB5);
            llBB6 = currentLayout.findViewById(R.id.llBB6);
            llBB7 = currentLayout.findViewById(R.id.llBB7);
            llBB8 = currentLayout.findViewById(R.id.llBB8);
            llBBPSView = currentLayout.findViewById(R.id.llBBPSView);

            ivMobile = currentLayout.findViewById(R.id.ivMobile);
            ivElectricity = currentLayout.findViewById(R.id.ivElectricity);
            ivDTH = currentLayout.findViewById(R.id.ivDTH);
            ivCCPayment = currentLayout.findViewById(R.id.ivCCPayment);
            ivRentPayment = currentLayout.findViewById(R.id.ivRentPayment);
            ivLoanRepayment = currentLayout.findViewById(R.id.ivLoanRepayment);
            ivBookACylinder = currentLayout.findViewById(R.id.ivBookACylinder);
            ivSeeAll = currentLayout.findViewById(R.id.ivSeeAll);
            //ivBg = currentLayout.findViewById(R.id.ivBg);
            //ivBg1 = currentLayout.findViewById(R.id.ivBg1);


            tvMobileText = currentLayout.findViewById(R.id.tvMobileText);
            tvElectricity = currentLayout.findViewById(R.id.tvElectricity);
            tvDTH = currentLayout.findViewById(R.id.tvDTH);
            tvCCPayment = currentLayout.findViewById(R.id.tvCCPayment);
            tvRentPayment = currentLayout.findViewById(R.id.tvRentPayment);
            tvLoanRepayment = currentLayout.findViewById(R.id.tvLoanRepayment);
            tvBookACylinder = currentLayout.findViewById(R.id.tvBookACylinder);
            tvSeeAll = currentLayout.findViewById(R.id.tvSeeAll);

            refreshData();

            tvUnlockPersonalLoan.setOnClickListener(view5 -> {
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "Personal Loan Lock Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("23") && ((DashboardActivity) currentActivity).userDetails.getUserStatus().equals("I")) {
                    // popup
                    CNAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), getResources().getString(R.string.eligible_message));
                } else {
                    if (allPermissionsGranted()) {
                        ((DashboardActivity) currentActivity).launchPanActivity();
                    } else {
                        BaseActivity.permissionsRedirectPage = 5;
                        Intent i = new Intent(getActivity(), MandatoryPermissionsActivity.class);
                        startActivity(i);
                    }
                }
            });
            tvUnlockVehicleLoan.setOnClickListener(view5 -> {
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "TWL Lock Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("23") && ((DashboardActivity) currentActivity).userDetails.getUserStatus().equals("I")) {
                    // popup
                    CNAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), getResources().getString(R.string.eligible_message));
                } else {
                    if (allPermissionsGranted()) {
                        ((DashboardActivity) currentActivity).launchPanActivity();
                    } else {
                        BaseActivity.permissionsRedirectPage = 5;
                        Intent i = new Intent(getActivity(), MandatoryPermissionsActivity.class);
                        startActivity(i);
                    }
                }
            });

            tvApplyNow1.setOnClickListener(v -> {
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "Personal Loan APPLY NOW Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));


                /*if (((DashboardActivity) currentActivity).userData.getPerEligStatusRedirect().equals(32)) {
                    ((DashboardActivity) currentActivity).launchNewSalary();
                } else*/

                if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("23")) {
                    ((DashboardActivity) currentActivity).getApplyLoanData(true);
                } else if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("26")) {
                    CNAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), getResources().getString(R.string.app_hold_message));
                } else if (!allPermissionsGranted()) {
                    ((BaseActivity) currentActivity).permissionsRedirectPage = 4;
                    Intent i = new Intent(getActivity(), MandatoryPermissionsActivity.class);
                    i.putExtra("salary", ((DashboardActivity) currentActivity).getSalary());
                    startActivity(i);
                } else if (Objects.equals(((DashboardActivity) currentActivity).userDetails.getUserStatusId(), "1")) {
                    if ((((DashboardActivity) currentActivity).userDetails.getEmail() == null || Objects.equals(((DashboardActivity) currentActivity).userDetails.getEmail(), ""))) {
                        Intent i = new Intent(getActivity(), FederalRegistrationActivity.class);
                        startActivity(i);
                    } else {
                        ((DashboardActivity) currentActivity).getApplyLoanData(true);
                    }

                } else {
                    ((DashboardActivity) currentActivity).getApplyLoanData(true);
                }
            });

            //tvApplyNow1.setOnClickListener(view5 -> ((DashboardActivity) currentActivity).launchPersonalLon());


            tvApplyNow2.setOnClickListener(v -> {

                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "TWL APPLY NOW Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                /*if (((DashboardActivity) currentActivity).userData.getTwlEligStatusRedirect().equals(32)) {
                    ((DashboardActivity) currentActivity).launchNewSalary();
                } else*/
                if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("23")) {
                    ((DashboardActivity) currentActivity).getApplyLoanData(true);
                } else if (((DashboardActivity) currentActivity).userDetails.getUserStatusId().equals("26")) {
                    CNAlertDialog.showAlertDialog(getActivity(), getResources().getString(R.string.title_alert), getResources().getString(R.string.app_hold_message));
                } else if (!allPermissionsGranted()) {
                    ((BaseActivity) currentActivity).permissionsRedirectPage = 104;
                    Intent i = new Intent(getActivity(), MandatoryPermissionsActivity.class);
                    startActivity(i);
                } else if (Objects.equals(((DashboardActivity) currentActivity).userDetails.getUserStatusId(), "1")) {
                    if ((((DashboardActivity) currentActivity).userDetails.getEmail() == null || Objects.equals(((DashboardActivity) currentActivity).userDetails.getEmail(), ""))) {
                        Intent i = new Intent(getActivity(), FederalRegistrationActivity.class);
                        startActivity(i);
                    } else {
                        Constants.CURRENT_SCREEN = "";
                        ((DashboardActivity) currentActivity).twlRedirect();
                    }

                } else {
                    Constants.CURRENT_SCREEN = "";
                    ((DashboardActivity) currentActivity).twlRedirect();
                }
            });

            tvCSApplyNow.setOnClickListener(v -> {
                if (CSPermissionsGranted()) {
                    ((BaseActivity) currentActivity).getApplyLoanDataBase(true);
                } else {
                    ((BaseActivity) currentActivity).permissionsRedirectPage = 104;
                    Intent i = new Intent(getActivity(), AppPermissionsActivity.class);
                    i.putExtra("from", "csapply");
                    startActivity(i);
                }

            });

            tvProceed.setOnClickListener(v -> {
                ((DashboardActivity) currentActivity).launchMemberUpgrade(memberUpgradeConsentResponse);
            });

            //tvApplyNow2.setOnClickListener(view5 -> ((DashboardActivity) currentActivity).launchVehicleLoan());
            tvIPLoan = view.findViewById(R.id.tvIPLoan);
            tvTwlEligTitle = view.findViewById(R.id.tvTwlEligTitle);
            tvProcessingFee = view.findViewById(R.id.tvProcessingFee);
            tvOnRoadPrice = view.findViewById(R.id.tvOnRoadPrice);
            tvEligibilityAmount = view.findViewById(R.id.tvEligibilityAmount);
            tvDownPaymentAmount = view.findViewById(R.id.tvDownPaymentAmount);
            tvGST = view.findViewById(R.id.tvGST);
            tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
            tvPayNow = view.findViewById(R.id.tvPayNow);
            tvWhydownPayment = view.findViewById(R.id.tvWhydownPayment);

            tvNachTitle = view.findViewById(R.id.tvNachTitle);
            tvMonitTitle = view.findViewById(R.id.tvMonitTitle);

            tvPaymentPending = view.findViewById(R.id.tvPaymentPending);
            limitUpdate = view.findViewById(R.id.limitUpdate);
            tvUploadPendingDoc = view.findViewById(R.id.tvUploadPendingDoc);
            tvUploadAdditionalDoc = view.findViewById(R.id.tvUploadAdditionalDoc);

            tvUploadPendingDoc.setOnClickListener(v -> {
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "Pending Documents Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));
                ((DashboardActivity) currentActivity).launchPendingDocs();
            });

            tvUploadAdditionalDoc.setOnClickListener(v -> {
                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "Additional Documents Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));
                ((DashboardActivity) currentActivity).launchAdditionalDocs();
            });

            ivCreditApply.setOnClickListener(v -> {
                saveCreditCardData();
            });

            tvPayNow.setOnClickListener(v -> {


                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "Pending Payment Pay Now Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                showPopup();
            });

            tvWhydownPayment.setOnClickListener(v -> {
                showWhyDownPayment();
            });
            tvSetUp.setOnClickListener(v -> {

                JSONObject obj1 = new JSONObject();
                try {
                    obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                    obj1.put(getString(R.string.interaction_type), "eNach Setup Button Clicked");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                Intent i = new Intent(getActivity(), ENachActivity.class);
                NachData nachData = new NachData();
                if (((DashboardActivity) getActivity()).applyLoanData.getNachCardData() != null) {
                    nachData = ((DashboardActivity) getActivity()).applyLoanData.getNachCardData().getNachData();
                } else if (((DashboardActivity) getActivity()).applyLoanData.getNachData() != null) {
                    nachData = ((DashboardActivity) getActivity()).applyLoanData.getNachData();
                }
                i.putExtra("nachData", nachData);
                startActivity(i);
            });


            btnChat.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), ChatActivity.class), 10001));

            if (((DashboardActivity) currentActivity).loanAgreementConsent != null) {
                llConsent.setVisibility(View.VISIBLE);
                setConsentData(((BaseActivity) currentActivity).loanAgreementConsent);
            } else {
                llConsent.setVisibility(View.GONE);
            }
            tvRate.setOnClickListener(v -> {
                ((DashboardActivity) currentActivity).rateUs();
            });
            llBB1.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(0).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey1", pbKey);
                    startActivity(intent);

                }
            });
            llBB2.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(1).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey2", pbKey);
                    startActivity(intent);

                }
            });
            llBB3.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(2).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey3", pbKey);
                    startActivity(intent);

                }
            });
            llBB4.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(3).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey4", pbKey);
                    startActivity(intent);

                }
            });
            llBB5.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(4).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey5", pbKey);
                    startActivity(intent);

                }
            });
            llBB6.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(5).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey6", pbKey);
                    startActivity(intent);

                }
            });
            llBB7.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(6).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey7", pbKey);
                    startActivity(intent);

                }
            });
            llBB8.setOnClickListener(v -> {
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                    String pbKey = bbpsBillPayResponse.getData().get(7).getPbKey();
                    Intent intent = new Intent(getActivity(), BBPSWebViewActivity.class);
                    intent.putExtra("pbKey8", pbKey);
                    startActivity(intent);

                }
            });

            String token = ((BaseActivity) currentActivity).getUserToken();

            // Notification Permission

            int permissionState = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                permissionState = ContextCompat.checkSelfPermission(currentActivity, Manifest.permission.POST_NOTIFICATIONS);
            }
            // If the permission is not granted, request it.
            if (permissionState == PackageManager.PERMISSION_DENIED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                }
            }

            BbpsPayBill();
            offerScroll();
            getLoanStatus(token);
            getTwlLoanStatus(token);
            memberUpgradeConsent();


            @SuppressLint({"NewApi", "LocalSuppress", "UseCompatLoadingForDrawables"})
            ObjectAnimator animator = ObjectAnimator.ofInt(llBBPSView, "backgroundColor", context.getColor(R.color.Primary1), context.getColor(R.color.white), context.getColor(R.color.appBackground));
            // duration of one color
            animator.setDuration(700);
            animator.setEvaluator(new ArgbEvaluator());
            // color will be show in reverse manner
            animator.setRepeatCount(Animation.REVERSE);
            // It will be repeated up to infinite time
            animator.setRepeatCount(5);
            animator.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void bannerView(String bannerType, String redirectUrl, String sharetext, Activity currentActivity) {
        try {
            String url = redirectUrl;
            String shareMsg = sharetext;
            ((DashboardActivity) currentActivity).launchBannerWebView(bannerType,url,shareMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressLint({"UseCompatLoadingForColorStateLists", "SetTextI18n"})
    private void offerScroll() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            GenericRequest genericRequest = new GenericRequest();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.offerScroll(genericRequest, token);
            genericAPIService.setOnDataListener(responseBody -> {
                offerScrollResponse = new Gson().fromJson(responseBody, OfferScrollResponse.class);
                CNProgressDialog.hideProgressDialog();
                if (offerScrollResponse != null && offerScrollResponse.getStatus()) {
                    if (offerScrollResponse.getData().length() > 0) {
                        tvOffreScroll.setVisibility(View.VISIBLE);
                        try {
                            if (offerScrollResponse != null && offerScrollResponse.getChangeColor() != null && offerScrollResponse.getData() != "") {
                                String offerText = offerScrollResponse.getData();
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(offerText);
                                Integer StartIndex = offerScrollResponse.getChangeColor().getFirstIndex();
                                Integer EndIndex = offerScrollResponse.getChangeColor().getLastIndex();
                                if (StartIndex != null && EndIndex != null) {
                                    int startIndex = StartIndex;
                                    int endIndex = EndIndex;
                                    // Set the color for the specified part of text
                                    int color = Color.parseColor(offerScrollResponse.getChangeColor().getColor());
                                    spannableStringBuilder.setSpan(new ForegroundColorSpan(color), startIndex, endIndex, 0);
                                    tvOffreScroll.setText(spannableStringBuilder);
                                }
                            } else {
                                tvOffreScroll.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (offerScrollResponse.getColor() != null) {
                            tvOffreScroll.setTextColor(context.getResources().getColor(R.color.black));
                        } else {
                            tvOffreScroll.setTextColor(context.getResources().getColor(R.color.black));
                        }
                        if (offerScrollResponse.getNewOffer() != null) {
                            if (offerScrollResponse.getNewOffer() == 1) {
                                tvOffreScroll.setBackgroundResource(R.drawable.button_border_primary2_5dp);
                            } else {
                                tvOffreScroll.setBackgroundResource(R.drawable.rounded_background_white);
                            }
                        }
                    } else {
                        tvOffreScroll.setVisibility(View.GONE);
                    }

                } /*else {
                    tvOffreScroll.setVisibility(View.GONE);
                }*/
            });
            genericAPIService.setOnErrorListener(throwable -> {
                CNProgressDialog.hideProgressDialog();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BbpsPayBill() {
        try {
            //CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            GenericRequest genericRequest = new GenericRequest();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.bbpsBillPay(genericRequest, token);
            genericAPIService.setOnDataListener(responseBody -> {
                bbpsBillPayResponse = new Gson().fromJson(responseBody, BbpsBillPayResponse.class);
                CNProgressDialog.hideProgressDialog();
                if (bbpsBillPayResponse != null && bbpsBillPayResponse.getStatus() != null && bbpsBillPayResponse.getStatus()) {
                    llBBPSView.setVisibility(View.VISIBLE);
                    setBBPSVisibility(bbpsBillPayResponse);
                    setBBPSView(bbpsBillPayResponse);
                } else {
                    llBBPSView.setVisibility(View.GONE);
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
                CNProgressDialog.hideProgressDialog();
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBBPSVisibility(BbpsBillPayResponse bbpsBillPayResponse) {
        try {
            if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                String pbPriority1 = bbpsBillPayResponse.getData().get(0).getPbPriority().toString();
                String pbPriority2 = bbpsBillPayResponse.getData().get(1).getPbPriority().toString();
                String pbPriority3 = bbpsBillPayResponse.getData().get(2).getPbPriority().toString();
                String pbPriority4 = bbpsBillPayResponse.getData().get(3).getPbPriority().toString();
                String pbPriority5 = bbpsBillPayResponse.getData().get(4).getPbPriority().toString();
                String pbPriority6 = bbpsBillPayResponse.getData().get(5).getPbPriority().toString();
                String pbPriority7 = bbpsBillPayResponse.getData().get(6).getPbPriority().toString();
                String pbPriority8 = bbpsBillPayResponse.getData().get(7).getPbPriority().toString();
                if (pbPriority1 != null && pbPriority1.equals("1")) {
                    llBB1.setVisibility(View.VISIBLE);
                } else {
                    llBB1.setVisibility(View.GONE);
                }
                if (pbPriority2 != null && pbPriority2.equals("2")) {
                    llBB2.setVisibility(View.VISIBLE);
                } else {
                    llBB2.setVisibility(View.GONE);
                }
                if (pbPriority3 != null && pbPriority3.equals("3")) {
                    llBB3.setVisibility(View.VISIBLE);
                } else {
                    llBB3.setVisibility(View.GONE);
                }
                if (pbPriority4 != null && pbPriority4.equals("4")) {
                    llBB4.setVisibility(View.VISIBLE);
                } else {
                    llBB4.setVisibility(View.GONE);
                }
                if (pbPriority5 != null && pbPriority5.equals("5")) {
                    llBB5.setVisibility(View.VISIBLE);
                } else {
                    llBB5.setVisibility(View.GONE);
                }
                if (pbPriority6 != null && pbPriority6.equals("6")) {
                    llBB6.setVisibility(View.VISIBLE);
                } else {
                    llBB6.setVisibility(View.GONE);
                }
                if (pbPriority7 != null && pbPriority7.equals("7")) {
                    llBB7.setVisibility(View.VISIBLE);
                } else {
                    llBB7.setVisibility(View.GONE);
                }
                if (pbPriority8 != null && pbPriority8.equals("8")) {
                    llBB8.setVisibility(View.VISIBLE);
                } else {
                    llBB8.setVisibility(View.GONE);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBBPSView(BbpsBillPayResponse bbpsBillPayResponse) {
        try {
            if (bbpsBillPayResponse != null && bbpsBillPayResponse.getData() != null) {
                String pbUrl1 = bbpsBillPayResponse.getData().get(0).getPbUrl();
                String pbName1 = bbpsBillPayResponse.getData().get(0).getPbName();
                if (pbUrl1 != null && pbUrl1 != "" && pbName1 != null && pbName1 != "") {
                    Glide.with(this).load(pbUrl1).into(ivMobile);
                    tvMobileText.setText(pbName1);
                }
                String pbUrl2 = bbpsBillPayResponse.getData().get(1).getPbUrl();
                String pbName2 = bbpsBillPayResponse.getData().get(1).getPbName();
                if (pbUrl2 != null && pbUrl2 != "" && pbName2 != null && pbName2 != "") {
                    Glide.with(this).load(pbUrl2).into(ivElectricity);
                    tvElectricity.setText(pbName2);
                }
                String pbUrl3 = bbpsBillPayResponse.getData().get(2).getPbUrl();
                String pbName3 = bbpsBillPayResponse.getData().get(2).getPbName();
                if (pbUrl3 != null && pbUrl3 != "" && pbName3 != null && pbName3 != "") {
                    Glide.with(this).load(pbUrl3).into(ivDTH);
                    tvDTH.setText(pbName3);
                }
                String pbUrl4 = bbpsBillPayResponse.getData().get(3).getPbUrl();
                String pbName4 = bbpsBillPayResponse.getData().get(3).getPbName();
                if (pbUrl4 != null && pbUrl4 != "" && pbName4 != null && pbName4 != "") {
                    Glide.with(this).load(pbUrl4).into(ivCCPayment);
                    tvCCPayment.setText(pbName4);
                }
                String pbUrl5 = bbpsBillPayResponse.getData().get(4).getPbUrl();
                String pbName5 = bbpsBillPayResponse.getData().get(4).getPbName();
                if (pbUrl5 != null && pbUrl5 != "" && pbName5 != null && pbName5 != "") {
                    Glide.with(this).load(pbUrl5).into(ivRentPayment);
                    tvRentPayment.setText(pbName5);
                }
                String pbUrl6 = bbpsBillPayResponse.getData().get(5).getPbUrl();
                String pbName6 = bbpsBillPayResponse.getData().get(5).getPbName();
                if (pbUrl6 != null && pbUrl6 != "" && pbName6 != null && pbName6 != "") {
                    Glide.with(this).load(pbUrl6).into(ivLoanRepayment);
                    tvLoanRepayment.setText(pbName6);
                }
                String pbUrl7 = bbpsBillPayResponse.getData().get(6).getPbUrl();
                String pbName7 = bbpsBillPayResponse.getData().get(6).getPbName();
                if (pbUrl7 != null && pbUrl7 != "" && pbName7 != null && pbName7 != "") {
                    Glide.with(this).load(pbUrl7).into(ivBookACylinder);
                    tvBookACylinder.setText(pbName7);
                }
                String pbUrl8 = bbpsBillPayResponse.getData().get(7).getPbUrl();
                String pbName8 = bbpsBillPayResponse.getData().get(7).getPbName();
                if (pbUrl8 != null && pbUrl8 != "" && pbName8 != null && pbName8 != "") {
                    Glide.with(this).load(pbUrl8).into(ivSeeAll);
                    tvSeeAll.setText(pbName8);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveCreditCardData() {
        try {
            //CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            GenericRequest genericRequest = new GenericRequest();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.saveCreditCardData(genericRequest, token);
            genericAPIService.setOnDataListener(responseBody -> {
                saveCCDataResponse = new Gson().fromJson(responseBody, SaveCCDataResponse.class);
                CNProgressDialog.hideProgressDialog();
                if (saveCCDataResponse != null && saveCCDataResponse.getStatus()) {
                    llCreditCard.setVisibility(View.VISIBLE);
                    ivCreditApply.setVisibility(View.GONE);
                    ivCreditInterested.setVisibility(View.VISIBLE);
                } else {

                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
                CNProgressDialog.hideProgressDialog();
            });

        } catch (Exception e) {
            CNProgressDialog.hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void memberUpgradeConsent() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            MemberUpgradeConsentReq memberUpgradeConsentReq = new MemberUpgradeConsentReq();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.memberUpgradeConsent(memberUpgradeConsentReq, token);
            genericAPIService.setOnDataListener(responseBody -> {
                memberUpgradeConsentResponse = new Gson().fromJson(responseBody, MemberUpgradeConsentResponse.class);
                if (memberUpgradeConsentResponse != null && memberUpgradeConsentResponse.getMessage().equals("Success")) {
                    llLimitEnhance.setVisibility(View.VISIBLE);
                    tvLHText.setText(memberUpgradeConsentResponse.getData().getContent());
                } else {
                    //Error
                    llLimitEnhance.setVisibility(View.GONE);
                    //CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), memberUpgradeConsentResponse.getMessage());
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean allPermissionsGranted() {
        int locationPermission =
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);

        int cameraPermission =
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int audioPermission =
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        return locationPermission == PackageManager.PERMISSION_GRANTED &&
                cameraPermission == PackageManager.PERMISSION_GRANTED &&
                audioPermission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean CSPermissionsGranted() {

        int locationPermission =
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        return locationPermission == PackageManager.PERMISSION_GRANTED;

    }

    private void showWhyDownPayment() {
        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.why_downpayment_dialog);
        alertDialog.getWindow().setLayout(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(true);
        TextView tvOk = alertDialog.findViewById(R.id.tvOk);
        TextView tvWhydownPaymentText = alertDialog.findViewById(R.id.tvWhydownPaymentText);
        TwlProcessingFee twlProcessingFee = ((DashboardActivity) currentActivity).twlProcessingFee;
        tvWhydownPaymentText.setText(twlProcessingFee.getWhyDownpayment());
        tvOk.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void showWelcomeBackPopup() {
        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.revoke_deletion);
        alertDialog.getWindow().setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        TextView tvOkay = alertDialog.findViewById(R.id.tvOkay);
        tvOkay.setOnClickListener(view -> {
            ((DashboardActivity) currentActivity).refreshScreen();
            alertDialog.dismiss();

        });
        alertDialog.show();
    }

    private void showPopup() {
        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.payment_dialog);
        alertDialog.getWindow().setLayout(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        TextView tvOk = alertDialog.findViewById(R.id.tvOk);
        tvOk.setOnClickListener(view -> {
            alertDialog.dismiss();
            ((DashboardActivity) currentActivity).invokeVehiclePayment();
        });
        alertDialog.show();
    }

    private void setGreetingMessage() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        TextView tvGreeting = currentLayout.findViewById(R.id.tvGreeting);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            tvGreeting.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            tvGreeting.setText("Good Afternoon");
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            tvGreeting.setText("Good Evening");
        }
    }

    public void getLoanStatus(String token) {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext());
            GenericRequest genericRequest = new GenericRequest();
            genericRequest.setUserId(userId);
            genericRequest.setApiKey(token);
            genericRequest.setDeviceUniqueId(Utility.getInstance().getDeviceUniqueId(currentActivity));
            genericAPIService.getLoanStatus(genericRequest, token);

            genericAPIService.setOnDataListener(responseBody -> {
                LoanStatusResponse loanStatusResponse = new Gson().fromJson(responseBody, LoanStatusResponse.class);
                setStatusViews(loanStatusResponse, loanStatusResponse != null && loanStatusResponse.getStatus() && loanStatusResponse.getData() != null
                        && loanStatusResponse.getData().getLoanStatus().size() > 0);
            });

            genericAPIService.setOnErrorListener(throwable -> {
                setStatusViews(null, false);
            });
        } catch (Exception e) {
            setStatusViews(null, false);
            e.printStackTrace();
        }
    }

    public void getTwlLoanStatus(String token) {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext());
            GenericRequest genericRequest = new GenericRequest();
            genericRequest.setUserId(userId);
            genericRequest.setApiKey(token);
            genericRequest.setDeviceUniqueId(Utility.getInstance().getDeviceUniqueId(currentActivity));
            genericAPIService.getTwlLoanStatus(genericRequest, token);
            genericAPIService.setOnDataListener(responseBody -> {
                twlLoanStatusResponse = new Gson().fromJson(responseBody, TwlLoanStatusResponse.class);
                setTwlStatusViews(twlLoanStatusResponse, twlLoanStatusResponse != null && twlLoanStatusResponse.getStatus() && twlLoanStatusResponse.getData() != null);
                llUploadQuotation.setOnClickListener(view -> {
                    ((DashboardActivity) currentActivity).selectImage1(false);
                });
                llCancelLoan.setOnClickListener(view -> {

                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                        obj1.put(getString(R.string.interaction_type), "eNach Setup Button Clicked");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    TrackingUtil.pushEvent(obj1, getString(R.string.home_page_interacted));

                    showCancelPopup(twlLoanStatusResponse);
                });

            });
            genericAPIService.setOnErrorListener(throwable -> {
                setTwlStatusViews(null, false);
            });
        } catch (Exception e) {
            setTwlStatusViews(null, false);
            e.printStackTrace();
        }
    }

    private void showCancelPopup(TwlLoanStatusResponse twlLoanStatusResponse) {
        Dialog alertDialog = new Dialog(getActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.cancle_loan_dialog);
        alertDialog.getWindow().setLayout(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        Spinner spinnerReason = alertDialog.findViewById(R.id.spinnerReason);
        ArrayList<CancelReason> reasonList = new ArrayList();
        CancelReason cancelReason = new CancelReason();
        cancelReason.setCancelReason("Reasons for Cancel");
        cancelReason.setCancelRid("-1");
        reasonList.add(cancelReason);
        reasonList.addAll(twlLoanStatusResponse.getData().getTwlLoanCancelActions().get(0).getTwlcancelReasons());
        final CancelReason[] selectedReason = {new CancelReason()};
        ArrayAdapter<CancelReason> adapter =
                new ArrayAdapter<CancelReason>(getContext(), android.R.layout.simple_spinner_dropdown_item, reasonList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(adapter);
        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    selectedReason[0] = (CancelReason) adapterView.getItemAtPosition(i);
                } else {
                    selectedReason[0] = new CancelReason();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        TextView tvCancel = alertDialog.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
        TextView tvConfirm = alertDialog.findViewById(R.id.tvConfirm);
        tvConfirm.setOnClickListener(view -> {
            alertDialog.dismiss();
            if (selectedReason[0].getCancelRid() != null && !selectedReason[0].getCancelRid().equals("")) {
                String token = ((BaseActivity) currentActivity).getUserToken();
                cancelTwlLoan(token, Integer.parseInt(selectedReason[0].getCancelRid()), Integer.parseInt(twlLoanStatusResponse.getData().getTwlId()));
            } else {

            }
        });
        alertDialog.show();
    }

    private void cancelTwlLoan(String token, int id, int twlId) {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
            GenericAPIService genericAPIService = new GenericAPIService(getContext());
            CancelTwlLoanReq cancelTwlLoanReq = new CancelTwlLoanReq();
            cancelTwlLoanReq.setApiKey(token);
            cancelTwlLoanReq.setCancelRid(id);
            cancelTwlLoanReq.setTwlId(twlId);
            genericAPIService.cancelTwlLoan(cancelTwlLoanReq, token);
            genericAPIService.setOnDataListener(responseBody -> {
                CancelTwlLoanResponse cancelTwlLoanResponse = new Gson().fromJson(responseBody, CancelTwlLoanResponse.class);
                CNProgressDialog.hideProgressDialog();
                if (cancelTwlLoanResponse != null && cancelTwlLoanResponse.getStatus() == Constants.STATUS_SUCCESS) {
                    llBtn.setVisibility(View.GONE);
                    ((DashboardActivity) currentActivity).refreshScreen();

                } else {
                    //Error
                    CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), cancelTwlLoanResponse.getMessage());
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
            });
        } catch (Exception e) {
            CNProgressDialog.hideProgressDialog();
            e.printStackTrace();
        }
    }

    private void uploadQuotation(String mediaPath) {
        try {
            CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
            GenericAPIService genericAPIService = new GenericAPIService(getContext());
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.uploadTWLQuotation(token, mediaPath, twlLoanStatusResponse.getData().getTwlId());
            genericAPIService.setOnDataListener(responseBody -> {
                FileUploadResponse fileUploadResponse = new Gson().fromJson(responseBody, FileUploadResponse.class);
                CNProgressDialog.hideProgressDialog();
                if (fileUploadResponse != null && fileUploadResponse.getStatus()) {
                    llUploadQuotation.setVisibility(View.GONE);
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {

            });
        } catch (Exception e) {
            CNProgressDialog.hideProgressDialog();
            e.printStackTrace();
        }
    }

    //Personal loan status view displaying
    private void setStatusViews(LoanStatusResponse loanStatusResponse, boolean b) {
        try {
            if (b) {
                llLoanStatus.setVisibility(View.VISIBLE);
                if (loanStatusResponse.getData().getShowRateUs()) {
                    tvRate.setVisibility(View.VISIBLE);
                    tvRate.setPaintFlags(tvRate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    tvRate.setVisibility(View.GONE);
                }
                tvLoanStatus.setText(loanStatusResponse.getData().getTitle());
                tvLoanId.setText(loanStatusResponse.getData().getLid());
                boolean doneStatus = false;
                switch (loanStatusResponse.getData().getLoanStatus().size()) {
                    case 1:
                        view1.setVisibility(View.GONE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);

                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.GONE);
                        iv3.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv5.setVisibility(View.GONE);

                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.GONE);
                        tv3.setVisibility(View.GONE);
                        tv4.setVisibility(View.GONE);
                        tv5.setVisibility(View.GONE);

                        tv1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(iv1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv1);
                                break;
                        }

                        break;
                    case 2:

                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);

                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        iv3.setVisibility(View.GONE);
                        iv4.setVisibility(View.GONE);
                        iv5.setVisibility(View.GONE);

                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.GONE);
                        tv4.setVisibility(View.GONE);
                        tv5.setVisibility(View.GONE);

                        tv1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tv2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(-150, 0, -150, 0);
                        llTxt.setLayoutParams(lp);

                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp1.setMargins(125, 0, 125, 0);
                        llImg.setLayoutParams(lp1);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(iv1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv2);
                                    iv2.getLayoutParams().height = 80;
                                    iv2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv2);
                                break;
                        }

                        break;
                    case 3:

                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.GONE);
                        view4.setVisibility(View.GONE);

                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        iv3.setVisibility(View.VISIBLE);
                        iv4.setVisibility(View.GONE);
                        iv5.setVisibility(View.GONE);

                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.GONE);
                        tv5.setVisibility(View.GONE);

                        tv1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tv2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tv3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());

                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(125, 0, 125, 0);
                        llImg.setLayoutParams(lp);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(iv1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv2);
                                    iv2.getLayoutParams().height = 80;
                                    iv2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv3);
                                    iv3.getLayoutParams().height = 80;
                                    iv3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv3);
                                break;
                        }

                        break;
                    case 4:

                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        view4.setVisibility(View.GONE);

                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        iv3.setVisibility(View.VISIBLE);
                        iv4.setVisibility(View.VISIBLE);
                        iv5.setVisibility(View.GONE);

                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.VISIBLE);
                        tv5.setVisibility(View.GONE);

                        tv1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tv2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tv3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());
                        tv4.setText(loanStatusResponse.getData().getLoanStatus().get(3).getTitle());

                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(-40, 0, -40, 0);
                        llTxt.setLayoutParams(lp);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(iv1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv2);
                                    iv2.getLayoutParams().height = 80;
                                    iv2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv3);
                                    iv3.getLayoutParams().height = 80;
                                    iv3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv3);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(3).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv4);
                                    iv4.getLayoutParams().height = 80;
                                    iv4.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv4);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv4);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv4);
                                break;
                        }

                        break;

                    case 5:
                        view1.setVisibility(View.VISIBLE);
                        view2.setVisibility(View.VISIBLE);
                        view3.setVisibility(View.VISIBLE);
                        view4.setVisibility(View.VISIBLE);

                        iv1.setVisibility(View.VISIBLE);
                        iv2.setVisibility(View.VISIBLE);
                        iv3.setVisibility(View.VISIBLE);
                        iv4.setVisibility(View.VISIBLE);
                        iv5.setVisibility(View.VISIBLE);

                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.VISIBLE);
                        tv3.setVisibility(View.VISIBLE);
                        tv4.setVisibility(View.VISIBLE);
                        tv5.setVisibility(View.VISIBLE);

                        tv1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tv2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tv3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());
                        tv4.setText(loanStatusResponse.getData().getLoanStatus().get(3).getTitle());
                        tv5.setText(loanStatusResponse.getData().getLoanStatus().get(4).getTitle());

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(iv1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv2);
                                    iv2.getLayoutParams().height = 80;
                                    iv2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv3);
                                    iv3.getLayoutParams().height = 80;
                                    iv3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv3);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(3).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv4);
                                    iv4.getLayoutParams().height = 80;
                                    iv4.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv4);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv4);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv4);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(4).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(iv5);
                                    iv5.getLayoutParams().height = 80;
                                    iv5.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(iv5);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_green_new).into(iv5);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(iv5);
                                break;
                        }
                        break;
                }
            } else {
                llLoanStatus.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Twl status view displaying
    private void setTwlStatusViews(TwlLoanStatusResponse loanStatusResponse, boolean b) {
        try {
            if (b) {
                if (loanStatusResponse.getData().getLoanStatus().size() > 0) {
                    llMain.setVisibility(View.VISIBLE);
                    llTwlImg.setVisibility(View.VISIBLE);
                    llTwlTxt.setVisibility(View.VISIBLE);
                    //llBtn.setVisibility(View.VISIBLE);
                } else {
                    llTwlImg.setVisibility(View.GONE);
                    llTwlTxt.setVisibility(View.GONE);
                    //llBtn.setVisibility(View.GONE);
                }
                if (loanStatusResponse.getData().getTwlLoanCancelActions() != null
                        && loanStatusResponse.getData().getTwlLoanCancelActions().size() > 0) {
                    llBtn.setVisibility(View.VISIBLE);
                    if (loanStatusResponse.getData().getTwlLoanCancelActions().size() == 1) {
                        if (loanStatusResponse.getData().getTwlLoanCancelActions().get(0).getTitle().toLowerCase(Locale.ROOT).contains("cancel")) {
                            llCancelLoan.setVisibility(View.VISIBLE);
                            llUploadQuotation.setVisibility(View.GONE);
                        } else {
                            llCancelLoan.setVisibility(View.GONE);
                            llUploadQuotation.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    llBtn.setVisibility(View.GONE);
                }
                if (loanStatusResponse.getData().getDeliveryInfo() != null) {
                    flCongrats.setVisibility(View.VISIBLE);
                    Glide.with(this).load(loanStatusResponse.getData().getDeliveryInfo().getDelImage()).into(ivCustomerImg);
                    tvCongrats.setText(loanStatusResponse.getData().getDeliveryInfo().getDelTitle());
                    tvDelivered.setText(loanStatusResponse.getData().getDeliveryInfo().getDelDiscription());
                } else {
                    flCongrats.setVisibility(View.GONE);
                }
                llTwlLoanStatus.setVisibility(View.VISIBLE);
                if (loanStatusResponse.getData().getShowRateUs()) {
                    tvRate.setVisibility(View.VISIBLE);
                    tvRate.setPaintFlags(tvRate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    tvRate.setVisibility(View.GONE);
                }
                tvTwlLoanStatusTitle.setText(loanStatusResponse.getData().getTitle());
                tvTwlLoanId.setText(loanStatusResponse.getData().getLid());
                boolean doneStatus = false;
                switch (loanStatusResponse.getData().getLoanStatus().size()) {
                    case 1:
                        viewTwl1.setVisibility(View.GONE);
                        viewTwl2.setVisibility(View.GONE);
                        viewTwl3.setVisibility(View.GONE);
                        viewTwl4.setVisibility(View.GONE);

                        ivTwl1.setVisibility(View.VISIBLE);
                        ivTwl2.setVisibility(View.GONE);
                        ivTwl3.setVisibility(View.GONE);
                        ivTwl4.setVisibility(View.GONE);
                        ivTwl5.setVisibility(View.GONE);

                        tvTwl1.setVisibility(View.VISIBLE);
                        tvTwl2.setVisibility(View.GONE);
                        tvTwl3.setVisibility(View.GONE);
                        tvTwl4.setVisibility(View.GONE);
                        tvTwl5.setVisibility(View.GONE);

                        tvTwl1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl1);
                                break;
                        }

                        break;
                    case 2:

                        viewTwl1.setVisibility(View.VISIBLE);
                        viewTwl2.setVisibility(View.GONE);
                        viewTwl3.setVisibility(View.GONE);
                        viewTwl4.setVisibility(View.GONE);

                        ivTwl1.setVisibility(View.VISIBLE);
                        ivTwl2.setVisibility(View.VISIBLE);
                        ivTwl3.setVisibility(View.GONE);
                        ivTwl4.setVisibility(View.GONE);
                        ivTwl5.setVisibility(View.GONE);

                        tvTwl1.setVisibility(View.VISIBLE);
                        tvTwl2.setVisibility(View.VISIBLE);
                        tvTwl3.setVisibility(View.GONE);
                        tvTwl4.setVisibility(View.GONE);
                        tvTwl5.setVisibility(View.GONE);

                        tvTwl1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tvTwl2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());

                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(-150, 0, -150, 0);
                        llTwlTxt.setLayoutParams(lp);

                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp1.setMargins(125, 0, 125, 0);
                        llTwlImg.setLayoutParams(lp1);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl2);
                                    ivTwl2.getLayoutParams().height = 80;
                                    ivTwl2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl2);
                                break;
                        }

                        break;
                    case 3:

                        viewTwl1.setVisibility(View.VISIBLE);
                        viewTwl2.setVisibility(View.VISIBLE);
                        viewTwl3.setVisibility(View.GONE);
                        viewTwl4.setVisibility(View.GONE);

                        ivTwl1.setVisibility(View.VISIBLE);
                        ivTwl2.setVisibility(View.VISIBLE);
                        ivTwl3.setVisibility(View.VISIBLE);
                        ivTwl4.setVisibility(View.GONE);
                        ivTwl5.setVisibility(View.GONE);

                        tvTwl1.setVisibility(View.VISIBLE);
                        tvTwl2.setVisibility(View.VISIBLE);
                        tvTwl3.setVisibility(View.VISIBLE);
                        tvTwl4.setVisibility(View.GONE);
                        tvTwl5.setVisibility(View.GONE);

                        tvTwl1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tvTwl2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tvTwl3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());

                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(125, 0, 125, 0);
                        llTwlImg.setLayoutParams(lp);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl2);
                                    iv2.getLayoutParams().height = 80;
                                    iv2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl3);
                                    ivTwl3.getLayoutParams().height = 80;
                                    ivTwl3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl3);
                                break;
                        }

                        break;
                    case 4:

                        viewTwl1.setVisibility(View.VISIBLE);
                        viewTwl2.setVisibility(View.VISIBLE);
                        viewTwl3.setVisibility(View.VISIBLE);
                        viewTwl4.setVisibility(View.GONE);

                        ivTwl1.setVisibility(View.VISIBLE);
                        ivTwl2.setVisibility(View.VISIBLE);
                        ivTwl3.setVisibility(View.VISIBLE);
                        ivTwl4.setVisibility(View.VISIBLE);
                        ivTwl5.setVisibility(View.GONE);

                        tvTwl1.setVisibility(View.VISIBLE);
                        tvTwl2.setVisibility(View.VISIBLE);
                        tvTwl3.setVisibility(View.VISIBLE);
                        tvTwl4.setVisibility(View.VISIBLE);
                        tvTwl5.setVisibility(View.GONE);

                        tvTwl1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tvTwl2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tvTwl3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());
                        tvTwl4.setText(loanStatusResponse.getData().getLoanStatus().get(3).getTitle());

                        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(-40, 0, -40, 0);
                        llTwlTxt.setLayoutParams(lp);

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl2);
                                    ivTwl2.getLayoutParams().height = 80;
                                    ivTwl2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl3);
                                    ivTwl3.getLayoutParams().height = 80;
                                    ivTwl3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl3);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(3).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl4);
                                    iv4.getLayoutParams().height = 80;
                                    iv4.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl4);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl4);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl4);
                                break;
                        }

                        break;

                    case 5:
                        viewTwl1.setVisibility(View.VISIBLE);
                        viewTwl2.setVisibility(View.VISIBLE);
                        viewTwl3.setVisibility(View.VISIBLE);
                        viewTwl4.setVisibility(View.VISIBLE);

                        ivTwl1.setVisibility(View.VISIBLE);
                        ivTwl2.setVisibility(View.VISIBLE);
                        ivTwl3.setVisibility(View.VISIBLE);
                        ivTwl4.setVisibility(View.VISIBLE);
                        ivTwl5.setVisibility(View.VISIBLE);

                        tvTwl1.setVisibility(View.VISIBLE);
                        tvTwl2.setVisibility(View.VISIBLE);
                        tvTwl3.setVisibility(View.VISIBLE);
                        tvTwl4.setVisibility(View.VISIBLE);
                        tvTwl5.setVisibility(View.VISIBLE);

                        tvTwl1.setText(loanStatusResponse.getData().getLoanStatus().get(0).getTitle());
                        tvTwl2.setText(loanStatusResponse.getData().getLoanStatus().get(1).getTitle());
                        tvTwl3.setText(loanStatusResponse.getData().getLoanStatus().get(2).getTitle());
                        tvTwl4.setText(loanStatusResponse.getData().getLoanStatus().get(3).getTitle());
                        tvTwl5.setText(loanStatusResponse.getData().getLoanStatus().get(4).getTitle());

                        switch (loanStatusResponse.getData().getLoanStatus().get(0).getStatus()) {
                            case 1:
                                Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl1);
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl1);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl1);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(1).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl2);
                                    ivTwl2.getLayoutParams().height = 80;
                                    ivTwl2.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl2);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl2);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl2);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(2).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl3);
                                    ivTwl3.getLayoutParams().height = 80;
                                    ivTwl3.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl3);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl3);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl3);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(3).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl4);
                                    ivTwl4.getLayoutParams().height = 80;
                                    ivTwl4.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl4);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl4);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl4);
                                break;
                        }
                        switch (loanStatusResponse.getData().getLoanStatus().get(4).getStatus()) {
                            case 1:
                                if (doneStatus) {
                                    Glide.with(this).load(R.raw.progress_black).into(ivTwl5);
                                    ivTwl5.getLayoutParams().height = 80;
                                    ivTwl5.getLayoutParams().width = 80;
                                    doneStatus = false;
                                } else {
                                    Glide.with(this).load(R.drawable.ic_tick_grey).into(ivTwl5);
                                }
                                break;
                            case 2:
                                Glide.with(this).load(R.drawable.ic_check_blue).into(ivTwl5);
                                doneStatus = true;
                                break;
                            case 3:
                                Glide.with(this).load(R.drawable.ic_baseline_error_outline_24).into(ivTwl5);
                                break;
                        }
                        break;
                }
            } else {
                llTwlLoanStatus.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getImages() {
        String token = ((BaseActivity) currentActivity).getUserToken();
        cnModel.getBannerImages(this, ((BaseActivity) currentActivity).userDetails.getUserId(), token);
    }

    public void setConsentData(LoanAgreementConsent consentData) {
        try {
            if (consentData != null && llConsent != null) {
                llConsent.setVisibility(View.VISIBLE);
                tvConsentTitle.setText(consentData.getTitle());
                tvAction.setText(consentData.getAction_required());
                if (consentData.getShowStar()) {
                    tvStar.setVisibility(View.VISIBLE);
                } else {
                    tvStar.setVisibility(View.GONE);
                }
                tvC1.setText(consentData.getPasscode().substring(0, 1));
                tvC2.setText(consentData.getPasscode().substring(1, 2));
                tvC3.setText(consentData.getPasscode().substring(2, 3));
                tvC4.setText(consentData.getPasscode().substring(3, 4));

                tvViewConsent.setOnClickListener(v -> {

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                        obj.put(getString(R.string.interaction_type), "Borrower Agreement Consent View Button Clicked");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    TrackingUtil.pushEvent(obj, getString(R.string.home_page_interacted));

                    showConsentOTP(consentData);
                });
                if (consentData.getUbacAcceptStatus()) {
                    llVerified.setVisibility(View.VISIBLE);
                    tvVerifiedText.setText(consentData.getUbac_accept_message());
                    llConsent.setVisibility(View.GONE);
                } else {
                    llVerified.setVisibility(View.GONE);
                    llConsent.setVisibility(View.VISIBLE);
                }
            } else {
                if (llConsent != null) {
                    llConsent.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showConsentOTP(LoanAgreementConsent consentData) {
        try {
            ((DashboardActivity) currentActivity).showConsent(consentData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        //CNProgressDialog.showProgressDialog(context, Constants.LOADING_MESSAGE);
        String token = ((BaseActivity) currentActivity).getUserToken();
        cnModel.getProfileData(this, userId, token);
    }

    public void updateProfileData(UserData profileData) {
        try {
            ((DashboardActivity) currentActivity).userData = profileData;
            UserData userData = profileData;
            if (userData.getShowPlCard()) {
                llPLView.setVisibility(View.VISIBLE);
            } else {
                llPLView.setVisibility(View.VISIBLE);
                llPLLock.setVisibility(View.GONE);
            }
            if (userData.getShowPlOfferCard() && !userData.getShowPlCard()) {
                llPLView.setVisibility((View.GONE));
                llPLLock.setVisibility(View.GONE);
                llCSAfterDis.setVisibility(View.GONE);
                llCSBeforeDis.setVisibility((View.VISIBLE));
                llCSPLView.setVisibility(View.VISIBLE);
            } else {
                llCSPLView.setVisibility(View.GONE);
            }
            if (userData.isShowPlofferLoanDetails()) {
                llCSBeforeDis.setVisibility((View.GONE));
                llCSAfterDis.setVisibility(View.VISIBLE);
                tvCSApplyNow.setVisibility((View.GONE));
            }
            if (userData.getShowTwlCard()) {
                llVLView.setVisibility(View.VISIBLE);
            } else {
                llVLView.setVisibility(View.GONE);
            }
            if (userData.getHasMembership() != null && userData.getHasMembership().equals("1")) {
                tv_loan_eligibility_limit_cs.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                if (userData.getNotEligText() != null) {
                    tv_loan_eligibility_limit.setText(userData.getNotEligText());
                    tv_loan_status.setText(userData.getNotEligText());
                    tv_current_loan_avail.setText(userData.getNotEligText());
                    tv_loan_eligibility_limit_vehicle.setText(userData.getNotEligText());
                    tv_loan_status_vehicle.setText(userData.getNotEligText());
                    tv_current_loan_avail_vehicle.setText(userData.getNotEligText());
                } else {
                    if (userData.getPerEligPreText()) {
                        tv_loan_eligibility_limit.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                        tv_loan_eligibility_limit_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                        tv_loan_status.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                        tv_loan_status_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                        tv_current_loan_avail.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                        tv_current_loan_avail_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                    } else {
                        tv_loan_eligibility_limit.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                        tv_loan_eligibility_limit_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                        tv_loan_status.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                        tv_loan_status_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                        tv_current_loan_avail.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                        tv_current_loan_avail_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));

                    }
                    tv_current_loan_avail_cs.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                    tv_loan_status_cs.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    tv_loan_status_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    if (userData.getTwlEligPreText()) {
                        tv_loan_eligibility_limit_vehicle.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                        tv_loan_eligibility_limit_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                        tv_loan_status_vehicle.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                        tv_loan_status_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                        tv_current_loan_avail_vehicle.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));
                        tv_current_loan_avail_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));

                    } else {
                        tv_loan_eligibility_limit_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                        tv_loan_eligibility_limit_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                        tv_loan_status_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                        tv_loan_status_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                        tv_current_loan_avail_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));
                        tv_current_loan_avail_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));

                    }
                }
                int limit = Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()));
                CNSharedPreferences sharedPreferences = new CNSharedPreferences(context);
                sharedPreferences.putInt(Constants.Loan_Limit, limit);
                tvCSLoanId.setText(userData.getPclLoanId());
                tvLoanAmount.setText(userData.getPclLoanAmount());
                tvTenure.setText(userData.getPclTenure());
                tvEmiAmount.setText(userData.getPclEmiAmount());
                tvUtrNumber.setText(userData.getPclUtrNo());

                // set dynamic text
                //setText for instant personal allplynow button and sub text


                tvIPLoan.setText(userData.getPerEligTitle());
                tvUnlockPersonalLoan.setText(userData.getPerEligCtaText());
                tvTwlEligTitle.setText(userData.getTwlEligTitle());
                tvUnlockVehicleLoan.setText(userData.getTwlEligCtaText());

            } else {
                llPLView.setVisibility(View.VISIBLE);
                llPLLock.setVisibility(View.GONE);
                llVLView.setVisibility(View.VISIBLE);
                llVlLock.setVisibility(View.GONE);

                if (userData.getPerEligPreText()) {
                    tv_loan_eligibility_limit.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                    tv_loan_eligibility_limit_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                    tv_loan_status_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    tv_loan_status.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    tv_current_loan_avail.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                    tv_current_loan_avail_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));

                } else {
                    tv_loan_eligibility_limit.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                    tv_loan_eligibility_limit_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getLoanEligibilityLimit()))));
                    tv_loan_status_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    tv_loan_status.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentEligibleLimit()))));
                    tv_current_loan_avail.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));
                    tv_current_loan_avail_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getCurrentLoanAvailed()))));

                }
                if (userData.getTwlEligPreText()) {
                    tv_loan_eligibility_limit_vehicle.setText(String.format(" Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                    tv_loan_eligibility_limit_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                    tv_loan_status_vehicle.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                    tv_loan_status_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                    tv_current_loan_avail_vehicle.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));
                    tv_current_loan_avail_vehicle_lock.setText(String.format("Upto Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));

                } else {
                    tv_loan_eligibility_limit_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                    tv_loan_eligibility_limit_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlLoanEligibilityLimit()))));
                    tv_loan_status_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                    tv_loan_status_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentEligibleLimit()))));
                    tv_current_loan_avail_vehicle.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));
                    tv_current_loan_avail_vehicle_lock.setText(String.format("Rs. %d", Math.round(Float.parseFloat(userData.getTwlCurrentLoanAvailed()))));

                }

            }


            if (userData != null) {


                JSONObject obj = new JSONObject();
                try {
                    obj.put("cnid", ((BaseActivity) currentActivity).userDetails.getQcId());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                TrackingUtil.pushEvent(obj, getString(R.string.home_page_landed));

                getAdditionalDocuments();
                getPendingDocuments();

                if (userData.getUserBasicData() != null) {
                    TextView tvId = currentLayout.findViewById(R.id.tvId);
                    tvId.setText(userData.getUserBasicData().getQcId());
                    CNSharedPreferences sharedPreferences = new CNSharedPreferences(context);
                    UserDetails userDetails = new Gson().fromJson(sharedPreferences.getString(Constants.USER_DETAILS_DATA), UserDetails.class);
                    if (userDetails.getFirstName() != null && !userDetails.getFirstName().equals("")) {
                        tvUserName.setText(new StringBuilder().append("Hi ").append(userDetails.getFirstName()).append(",").toString());
                    } else {
                        tvUserName.setText(new StringBuilder().append("Hi ").append(userDetails.getFullName()).append(",").toString());
                    }
                }
                tvApplyNow1.setText(userData.getPerEligCtaText());
                tvApplyNow2.setText(userData.getTwlEligCtaText());
                if (userData.getPerEligSubText() != null) {
                    tvUserAt.setText(userData.getPerEligSubText());
                    tvUserAt1.setText(userData.getPerEligSubText());
                } else {
                    tvUserAt.setText("");
                    tvUserAt1.setText("");
                }
                tvUnlockVehicleLoan.setText(userData.getTwlEligCtaText());
                tvUnlockPersonalLoan.setText(userData.getPerEligCtaText());
                String relationManager = userData.getAccount_mng_details();
           /* if (userData.getStakeholder_id() != null && !userData.getStakeholder_id().equals("")) {
                if (userData.getStakeholder_id().equals("2") || userData.getStakeholder_id().equals("3")) {
                    if (userData.getAccount_mng_details() != null) {
                        cvTrustRate.setVisibility(View.GONE);
                        tvRating.setText(userData.getUserBasicData().getUserRating() + "/100");

                    }
                }
            }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPendingDocuments() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            GetPendingDocReq getPendingDocReq = new GetPendingDocReq();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.getPendingDocuments(getPendingDocReq, token);
            genericAPIService.setOnDataListener(responseBody -> {
                getPendingDocResponse = new Gson().fromJson(responseBody, GetPendingDocResponse.class);
                if (getPendingDocResponse != null && getPendingDocResponse.getStatus()) {
                    if (getPendingDocResponse.getData().isDocRequired()) {
                        llPendingDocs.setVisibility(View.VISIBLE);
                        setPendingDocData(getPendingDocResponse);
                    } else {
                        llPendingDocs.setVisibility(View.GONE);
                    }
                } else {
                    //Error
                    llPendingDocs.setVisibility(View.GONE);
                    //CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), memberUpgradeConsentResponse.getMessage());
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPendingDocData(GetPendingDocResponse getPendingDocResponse) {
        try {
            if (getPendingDocResponse != null && !getPendingDocResponse.equals("")) ;
            {
                tvPendingDocTitle.setText(getPendingDocResponse.getData().getTitle());
                tvPendingDocText.setText(getPendingDocResponse.getData().getDescription());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAdditionalDocuments() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            GetAdditionalDocReq getAdditionalDocReq = new GetAdditionalDocReq();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.getAdditionalDocuments(getAdditionalDocReq, token);
            genericAPIService.setOnDataListener(responseBody -> {
                getAdditionalDocResponse = new Gson().fromJson(responseBody, GetAdditionalDocResponse.class);
                if (getAdditionalDocResponse != null && getAdditionalDocResponse.getStatus()) {
                    if (getAdditionalDocResponse.getData().isDocRequired()) {
                        llAdditionalDocs.setVisibility(View.VISIBLE);
                        setAdditionalDocData(getAdditionalDocResponse);
                    } else {
                        llAdditionalDocs.setVisibility(View.GONE);
                    }
                } else {
                    //Error
                    llAdditionalDocs.setVisibility(View.GONE);
                    //CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), memberUpgradeConsentResponse.getMessage());
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdditionalDocData(GetAdditionalDocResponse getAdditionalDocResponse) {
        try {
            if (getAdditionalDocResponse != null && !getAdditionalDocResponse.equals("")) ;
            {
                tvAdditionalDocTitle.setText(getAdditionalDocResponse.getData().getTitle());
                tvAdditionalDocText.setText(getAdditionalDocResponse.getData().getDescription());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showAlertDialog(String message) {
        if (CNProgressDialog.isProgressDialogShown)
            CNProgressDialog.hideProgressDialog();

        CNAlertDialog.showAlertDialog(context, getResources().getString(R.string.title_alert), message);
    }

    public void updateBannerImages(JSONArray images) {
        try {
            ((DashboardActivity) getActivity()).sharedPreferences.putString(Constants.PROFILE_BANNERS, String.valueOf(images));
            if (images != null && images.length() > 0) {
                Type type = new TypeToken<List<BannerImages>>() {
                }.getType();
                List<BannerImages> bannerImagesList = new Gson().fromJson(String.valueOf(images), type);
                List<BannerImages> bannerImagesType1 = new ArrayList<>();
                List<BannerImages> bannerImagesType2 = new ArrayList<>();
                for (int i = 0; i < bannerImagesList.size(); i++) {
                    if (bannerImagesList.get(i).getBType().equalsIgnoreCase("1")) {
                        bannerImagesType1.add(bannerImagesList.get(i));
                    } else {
                        bannerImagesType2.add(bannerImagesList.get(i));
                    }
                }

                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(5));

                if (bannerImagesType1.size() > 0) {
                    Glide.with(this).asBitmap().apply(requestOptions).load(bannerImagesType1.get(0).getBannerLink()).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            frameCta1.setBackground(new BitmapDrawable(getResources(), resource));
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                        }
                    });
                    switch (bannerImagesType1.get(0).getBBtnPosition().toLowerCase()) {
                        case "left_bottom":
                            frameCta1.setGravity(Gravity.BOTTOM | Gravity.START);
                            break;
                        case "right_bottom":
                            frameCta1.setGravity(Gravity.BOTTOM | Gravity.END);
                            break;
                        case "left_top":
                            frameCta1.setGravity(Gravity.TOP | Gravity.START);
                            break;
                        case "right_top":
                            frameCta1.setGravity(Gravity.TOP | Gravity.END);
                            break;
                        case "left_center":
                            frameCta1.setGravity(Gravity.CENTER | Gravity.START);
                            break;
                        case "right_center":
                            frameCta1.setGravity(Gravity.CENTER | Gravity.END);
                            break;
                    }
                    /*tvApplyNow.setEnabled(bannerImagesType1.get(0).getIsClickable().equalsIgnoreCase("1"));
                    if (((BaseActivity) currentActivity).userDetails.getUserStatusId().equals("12")) {
                        tvApplyNow.setVisibility(View.VISIBLE);
                    } else {
                        tvApplyNow.setVisibility(View.INVISIBLE);
                    }*/
                } else {
                    frameCta1.setVisibility(View.GONE);
                }
            } else {
                frameCta1.setVisibility(View.GONE);
                //   cardCta2.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        CNProgressDialog.hideProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentActivity == null) {
            currentActivity = getActivity();
        }
        if (llConsent != null) {
            if (((DashboardActivity) currentActivity).loanAgreementConsent != null) {
                llConsent.setVisibility(View.VISIBLE);
                setConsentData(((DashboardActivity) currentActivity).loanAgreementConsent);
            } else {
                llConsent.setVisibility(View.GONE);
            }
        }

        if (llProcessView != null) {
            if (((DashboardActivity) currentActivity).twlProcessingFee != null) {
                setProcessingFee();
            } else {
                llProcessView.setVisibility(View.GONE);
            }
        }

        setNachVisibility();
        setMonitoringVisibility();
        SetWelcomeBackVisibility();
        setCreditCardData();
        bannerImages();
    }

    public void bannerImages() {
        try {
            GenericAPIService genericAPIService = new GenericAPIService(getContext(), 0);
            BannerImageRequest bannerImageRequest = new BannerImageRequest();
            String token = ((BaseActivity) currentActivity).getUserToken();
            genericAPIService.homePageBannerImages(bannerImageRequest, token);
            genericAPIService.setOnDataListener(responseBody -> {
                bannerImageResponse = new Gson().fromJson(responseBody, BannerImageResponse.class);
                if (bannerImageResponse != null && bannerImageResponse.getStatus()) {
                    setSliderImages(bannerImageResponse.getImages());
                } else {
                    //Toast.makeText(currentActivity, "status false", Toast.LENGTH_SHORT).show();
                }
            });
            genericAPIService.setOnErrorListener(throwable -> {
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSliderImages(List<HomeBannerImage> images) {
        try {
            HomeFragment homeFragment = new HomeFragment();
            HomeImageSliderAdapter homeImageSliderAdapter = new HomeImageSliderAdapter(images, homeFragment,currentActivity);
            homeImageSliderAdapter.renewItems((List<HomeBannerImage>) images);
            imageSlider.setSliderAdapter(homeImageSliderAdapter);
            imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            imageSlider.startAutoCycle();
            if(images.size() == 1){
                imageSlider.setInfiniteAdapterEnabled(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setNachVisibility() {
        if (currentActivity == null) {
            currentActivity = getActivity();
        }
        if (llNachSetUp != null) {
            if (((DashboardActivity) currentActivity).redirectToNach) {
                tvSetUp.callOnClick();
            } else if (((DashboardActivity) currentActivity).applyLoanData != null && ((DashboardActivity) currentActivity).applyLoanData.getNachCardData() != null && ((DashboardActivity) currentActivity).applyLoanData.getNachCardData().getNachData() != null) {
                llNachSetUp.setVisibility(View.VISIBLE);
                setNachData();
            } else {
                llNachSetUp.setVisibility(View.GONE);
            }
        }
    }

    public void setMonitoringVisibility() {
        try {
            if (currentActivity == null) {
                currentActivity = getActivity();
            }
            if (currentActivity != null && ((DashboardActivity) currentActivity).loansResponse != null && ((DashboardActivity) currentActivity).loansResponse.has("monitoring_card_data") && ((DashboardActivity) currentActivity).loansResponse.getJSONObject("monitoring_card_data") != null) {
                llMonitoringView.setVisibility(View.VISIBLE);
                setMonitoringData();
            } else {
                llMonitoringView.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SetWelcomeBackVisibility() {
        try {
            if (currentActivity == null) {
                currentActivity = getActivity();
            }
            if (currentActivity != null && ((DashboardActivity) currentActivity).loansResponse != null && ((DashboardActivity) currentActivity).loansResponse.has("show_delete_cancel_popup")) {
                showWelcomeBackPopup();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showCustomPopUp() {
        try {
            if (currentActivity == null) {
                currentActivity = getActivity();
            }
            if (((DashboardActivity) currentActivity).loansResponse != null &&
                    ((DashboardActivity) currentActivity).loansResponse.has("custom_popup")) {
                loadCustomPopUp();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCustomPopUp() {
        try {
            Dialog alertDialog = new Dialog(getActivity());
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.security_dialog);
            alertDialog.getWindow().setLayout(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCanceledOnTouchOutside(true);
            TextView tvOk = alertDialog.findViewById(R.id.tvOk);
            TextView tvTitle = alertDialog.findViewById(R.id.tvTitle);
            WebView wvText = alertDialog.findViewById(R.id.wvText);
            tvTitle.setText(((DashboardActivity) currentActivity).applyLoanData.getCustomPopUp().getTitle());
            wvText.loadUrl(((DashboardActivity) currentActivity).applyLoanData.getCustomPopUp().getContent());
            tvOk.setText(((DashboardActivity) currentActivity).applyLoanData.getCustomPopUp().getBtn());
            wvText.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);

                    return true;
                }
            });
            tvOk.setOnClickListener(view -> {
                alertDialog.dismiss();
            });
            alertDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSecurityPopUp() {
        try {
            if (currentActivity == null) {
                currentActivity = getActivity();
            }

            if (!(((DashboardActivity) currentActivity).sharedPreferences.getBoolean("showSecuDialog")) && ((DashboardActivity) currentActivity).loansResponse != null &&
                    ((DashboardActivity) currentActivity).loansResponse.has("security_popup")) {
                loadSecurityPopUp();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void loadSecurityPopUp() {
        try {
            Dialog alertDialog = new Dialog(getActivity());
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.security_dialog);
            alertDialog.getWindow().setLayout(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.setCanceledOnTouchOutside(true);
            TextView tvOk = alertDialog.findViewById(R.id.tvOk);
            TextView tvTitle = alertDialog.findViewById(R.id.tvTitle);
            WebView wvText = alertDialog.findViewById(R.id.wvText);
            tvTitle.setText(((DashboardActivity) currentActivity).applyLoanData.getSecurityPopUp().getTitle());
            wvText.loadUrl(((DashboardActivity) currentActivity).applyLoanData.getSecurityPopUp().getContent());
            tvOk.setText(((DashboardActivity) currentActivity).applyLoanData.getSecurityPopUp().getBtn());
            wvText.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);

                    return true;
                }
            });
            tvOk.setOnClickListener(view -> {
                alertDialog.dismiss();
                ((DashboardActivity) currentActivity).sharedPreferences.putBoolean("showSecuDialog", true);
            });
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCreditCardData() {
        try {
            if (currentActivity == null) {
                currentActivity = getActivity();
            }
            if (((DashboardActivity) currentActivity).loansResponse != null &&
                    ((DashboardActivity) currentActivity).loansResponse.has("show_cc_card") &&
                    ((DashboardActivity) currentActivity).loansResponse.getInt("show_cc_card") == 1) {
                llCreditCard.setVisibility(View.VISIBLE);
                ivCreditApply.setVisibility(View.VISIBLE);
                ivCreditInterested.setVisibility(View.GONE);
            } else if (((DashboardActivity) currentActivity).loansResponse != null &&
                    ((DashboardActivity) currentActivity).loansResponse.has("show_cc_card")
                    && ((DashboardActivity) currentActivity).loansResponse.getInt("show_cc_card") == 2) {
                llCreditCard.setVisibility(View.VISIBLE);
                ivCreditInterested.setVisibility(View.VISIBLE);
                ivCreditApply.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMonitoringData() {
        try {
            tvMonitTitle.setText(((DashboardActivity) currentActivity).applyLoanData.getMonitoringCardData().getTitle());
            tvMonitText.setText(((DashboardActivity) currentActivity).applyLoanData.getMonitoringCardData().getDescription());
            tvMonitBtn.setText(((DashboardActivity) currentActivity).applyLoanData.getMonitoringCardData().getButton());
            tvMonitBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), UploadBankDetailsActivity.class);
                String monitType = new String();
                String bankName = new String();
                String bankCode = new String();
                Boolean readonly = new Boolean(false);
                bankName = ((DashboardActivity) getActivity()).applyLoanData.getMonitoringCardData().getBankName();
                bankCode = ((DashboardActivity) getActivity()).applyLoanData.getMonitoringCardData().getBankCode();
                monitType = ((DashboardActivity) getActivity()).applyLoanData.getMonitoringCardData().getType();
                readonly = ((DashboardActivity) getActivity()).applyLoanData.getMonitoringCardData().getReadOnly();
                intent.putExtra("type", monitType);
                intent.putExtra("bank_name", bankName);
                intent.putExtra("bank_code", bankCode);
                intent.putExtra("readonly", true);
                startActivity(intent);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setNachData() {
        try {
            if (((DashboardActivity) currentActivity).applyLoanData != null && ((DashboardActivity) currentActivity).applyLoanData.getNachCardData() != null) {
                llNachSetUp.setVisibility(View.VISIBLE);
                tvNachTitle.setText(((DashboardActivity) currentActivity).applyLoanData.getNachCardData().getTitle());
                tvNachText.setText(((DashboardActivity) currentActivity).applyLoanData.getNachCardData().getDescription());
                tvSetUp.setText(((DashboardActivity) currentActivity).applyLoanData.getNachCardData().getButton());
            } else {
                llNachSetUp.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setProcessingFee() {
        if (llProcessView != null) {
            llProcessView.setVisibility(View.VISIBLE);
            TwlProcessingFee twlProcessingFee = ((DashboardActivity) currentActivity).twlProcessingFee;
            tvPaymentPending.setText(twlProcessingFee.getTitle());
            tvOnRoadPrice.setText(twlProcessingFee.getPriceBreakup().get(0));
            tvEligibilityAmount.setText(twlProcessingFee.getPriceBreakup().get(1));
            tvDownPaymentAmount.setText(twlProcessingFee.getPriceBreakup().get(2));
            tvProcessingFee.setText(twlProcessingFee.getPriceBreakup().get(3));
            tvGST.setText(twlProcessingFee.getPriceBreakup().get(4));
            tvTotalAmount.setText(twlProcessingFee.getTotal().toString());
            limitUpdate.setText(twlProcessingFee.getDescription());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setSelectedImage(@org.jetbrains.annotations.Nullable String selectedFilePath) {
        if (selectedFilePath != null && !selectedFilePath.equals("")) {
            uploadQuotation(selectedFilePath);
        }
    }



}

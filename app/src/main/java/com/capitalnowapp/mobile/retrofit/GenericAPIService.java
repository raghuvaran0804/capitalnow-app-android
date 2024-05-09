package com.capitalnowapp.mobile.retrofit;

import android.content.Context;
import android.util.Log;

import com.capitalnowapp.mobile.beans.OrderData;
import com.capitalnowapp.mobile.constants.Constants;
import com.capitalnowapp.mobile.kotlin.activities.BankDetailsActivity;
import com.capitalnowapp.mobile.kotlin.activities.offer.GetPinCodesReq;
import com.capitalnowapp.mobile.kotlin.activities.offer.GetPinCodesResponse;
import com.capitalnowapp.mobile.kotlin.fragments.LatestDocumentsFrag;
import com.capitalnowapp.mobile.models.*;
import com.capitalnowapp.mobile.models.Registrations.DesignationListResponse;
import com.capitalnowapp.mobile.models.Registrations.GetDesignationListReq;
import com.capitalnowapp.mobile.models.Registrations.GetVerifyAlternateMobileResponse;
import com.capitalnowapp.mobile.models.Registrations.PinCodeReq;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneReq;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationOneResponse;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeReq;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationThreeResponse;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoReq;
import com.capitalnowapp.mobile.models.Registrations.SaveRegistrationTwoResponse;
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPReq;
import com.capitalnowapp.mobile.models.Registrations.VerifyAltMobileByOTPResponse;
import com.capitalnowapp.mobile.models.Registrations.VerifyAlternateMobileReq;
import com.capitalnowapp.mobile.models.coupons.CouponsResponse;
import com.capitalnowapp.mobile.models.coupons.RedeemCouponReq;
import com.capitalnowapp.mobile.models.coupons.RedeemCouponResponse;
import com.capitalnowapp.mobile.models.coupons.SendEmailReq;
import com.capitalnowapp.mobile.models.loan.LoanStatusResponse;
import com.capitalnowapp.mobile.models.login.GetOTPRequest;
import com.capitalnowapp.mobile.models.login.RegisterDeviceRequest;
import com.capitalnowapp.mobile.models.login.RegisterDeviceResponse;
import com.capitalnowapp.mobile.models.login.VerifyOTPRequest;
import com.capitalnowapp.mobile.models.managerdetails.ManagerResponse;
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanReq;
import com.capitalnowapp.mobile.models.offerModel.CSActiveLoanResponse;
import com.capitalnowapp.mobile.models.offerModel.CSGenericResponse;
import com.capitalnowapp.mobile.models.offerModel.PrSaveSignReq;
import com.capitalnowapp.mobile.models.profile.ProfileResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponCategoriesResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetCouponInfoResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetEmailCouponDetailsResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponDetailsResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemCouponResponse;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsReq;
import com.capitalnowapp.mobile.models.rewardsNew.GetRedeemedCouponsResponse;
import com.capitalnowapp.mobile.models.userdetails.UserDetailsResponse;
import com.capitalnowapp.mobile.util.CNSharedPreferences;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GenericAPIService {

    private DataInterface mResponseListener;
    private ErrorInterface mErrorListener;
    private ApiInterface apiService = null;
    public CNSharedPreferences sharedPreferences;

    public GenericAPIService(Context context) {
        try {
            sharedPreferences = new CNSharedPreferences(context);
            String url = Constants.MAIN_URL + "/";
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiInterface.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GenericAPIService(Context context, int flag) {
        try {
            sharedPreferences = new CNSharedPreferences(context);
            String url = null;
            if (flag == 0) {
                url = Constants.MAIN_URL_1 + "/";
            }
            final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiInterface.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnDataListener(DataInterface listener) {
        mResponseListener = listener;
    }

    public void setOnErrorListener(ErrorInterface t) {
        mErrorListener = t;
    }

    public void twlProcessingOrder(TwlProcessingOrderReq twlProcessingOrderReq, String token) {
        try {
            twlProcessingOrderReq.setApiKey(token);
            Call<OrderData> orderDataCall = apiService.twlProcessingOrder(twlProcessingOrderReq);
            orderDataCall.enqueue(new Callback<OrderData>() {
                @Override
                public void onResponse(Call<OrderData> call, Response<OrderData> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<OrderData> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createMandate(CreateMandateReq createMandateReq, String token) {
        try {
            createMandateReq.setApiKey(token);
            Call<CreateMandateResponse> contactUsResponseCall = apiService.createMandate(createMandateReq);
            contactUsResponseCall.enqueue(new Callback<CreateMandateResponse>() {
                @Override
                public void onResponse(Call<CreateMandateResponse> call, Response<CreateMandateResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CreateMandateResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitMandate(SubmitMandateReq submitMandateReq, String token) {
        try {
            submitMandateReq.setApiKey(token);
            Call<SubmitMandateResponse> contactUsResponseCall = apiService.submitMandate(submitMandateReq);
            contactUsResponseCall.enqueue(new Callback<SubmitMandateResponse>() {
                @Override
                public void onResponse(Call<SubmitMandateResponse> call, Response<SubmitMandateResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SubmitMandateResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createVKYC(CreateKycReq createKycReq, String token) {
        try {
            createKycReq.setApiKey(token);
            Call<CreateVKYCResponse> contactUsResponseCall = apiService.createVKYC(createKycReq);
            contactUsResponseCall.enqueue(new Callback<CreateVKYCResponse>() {
                @Override
                public void onResponse(Call<CreateVKYCResponse> call, Response<CreateVKYCResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CreateVKYCResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitVKYC(SubmitVKYCReq submitVKYCReq, String token) {
        try {
            submitVKYCReq.setApiKey(token);
            Call<SubmitVKYCResponse> contactUsResponseCall = apiService.submitVKYC(submitVKYCReq);
            contactUsResponseCall.enqueue(new Callback<SubmitVKYCResponse>() {
                @Override
                public void onResponse(Call<SubmitVKYCResponse> call, Response<SubmitVKYCResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SubmitVKYCResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyAlternateEmail(VerifyAlternateEmailReq verifyAlternateEmail, String token) {

        try {
            verifyAlternateEmail.setApiKey(token);
            Call<GetVerifyAlternateEmailResponse> contactUsResponseCall = apiService.sendVerifyEmail(verifyAlternateEmail);
            Log.d("verifyAlternate", new Gson().toJson(verifyAlternateEmail));
            contactUsResponseCall.enqueue(new Callback<GetVerifyAlternateEmailResponse>() {
                @Override
                public void onResponse(Call<GetVerifyAlternateEmailResponse> call, Response<GetVerifyAlternateEmailResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetVerifyAlternateEmailResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void verifyEmailByOTP(VerifyEmailByOTPReq verifyEmailByOTPReq, String token) {
        try {
            verifyEmailByOTPReq.setApiKey(token);
            Call<VerifyEmailByOTPReqResponse> contactUsResponseCall = apiService.verifyEmailByOTP(verifyEmailByOTPReq);
            contactUsResponseCall.enqueue(new Callback<VerifyEmailByOTPReqResponse>() {
                @Override
                public void onResponse(Call<VerifyEmailByOTPReqResponse> call, Response<VerifyEmailByOTPReqResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<VerifyEmailByOTPReqResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMandateList(GetMandateList getmandateListReq, String token) {
        try {
            getmandateListReq.setApiKey(token);
            Call<GetMandateListResponse> contactUsResponseCall = apiService.getMandateList(getmandateListReq);
            contactUsResponseCall.enqueue(new Callback<GetMandateListResponse>() {
                @Override
                public void onResponse(Call<GetMandateListResponse> call, Response<GetMandateListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetMandateListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePrimaryMandate(SavePrimaryMandateReq savePrimaryMandateReq, String token) {
        try {
            savePrimaryMandateReq.setApiKey(token);
            Call<SavePrimaryMandateResponse> contactUsResponseCall = apiService.savePrimaryMandate(savePrimaryMandateReq);
            contactUsResponseCall.enqueue(new Callback<SavePrimaryMandateResponse>() {
                @Override
                public void onResponse(Call<SavePrimaryMandateResponse> call, Response<SavePrimaryMandateResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SavePrimaryMandateResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void submitBankChange(SubmitBankChangeReq submitBankChangeReq, String token) {
        try {
            submitBankChangeReq.setApiKey(token);
            Call<SubmitBankChangeResponse> contactUsResponseCall = apiService.submitBankChange(submitBankChangeReq);
            contactUsResponseCall.enqueue(new Callback<SubmitBankChangeResponse>() {
                @Override
                public void onResponse(Call<SubmitBankChangeResponse> call, Response<SubmitBankChangeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SubmitBankChangeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveLatestSalSlip(SubmitBankChangeReq submitBankChangeReq, String token) {
        try {
            submitBankChangeReq.setApiKey(token);
            Call<SubmitBankChangeResponse> contactUsResponseCall = apiService.saveLatestSalSlip(submitBankChangeReq);
            contactUsResponseCall.enqueue(new Callback<SubmitBankChangeResponse>() {
                @Override
                public void onResponse(Call<SubmitBankChangeResponse> call, Response<SubmitBankChangeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SubmitBankChangeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public interface DataInterface {
        void responseData(String responseBody);
    }

    public interface ErrorInterface {
        void errorData(Throwable throwable);
    }

    public void uploadFileToServer(String mediaPath, String id, String token) {
        try {
            File file = new File(mediaPath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);

            Call<FileUploadResponse> call = apiService.uploadFile(fileToUpload, apiKey, userId);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null && response.body().getStatus()) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadProfileImage(String mediaPath, String id, String deviceUniqueId, String token) {
        try {
            File file = new File(mediaPath);
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), deviceUniqueId);

            Call<FileUploadResponse> call = apiService.uploadProfileImage(fileToUpload, apiKey, userId, deviceId);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null && response.body().getStatus()) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadTWLQuotation(String token, String mediaPath, String twlId) {
        try {
            MultipartBody.Part fileToUpload = null;
            if (!mediaPath.equals("")) {
                File file = new File(mediaPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            }
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody twlId1 = RequestBody.create(MediaType.parse("text/plain"), twlId);

            Call<FileUploadResponse> call = apiService.uploadTWLQuotation(fileToUpload, apiKey, twlId1);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadPanImage(String mediaPath, String id, String panNumber, String token) {
        try {
            MultipartBody.Part fileToUpload = null;
            if (!mediaPath.equals("")) {
                File file = new File(mediaPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            }
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody pan = RequestBody.create(MediaType.parse("text/plain"), panNumber);

            Call<FileUploadResponse> call = apiService.uploadPanImage(fileToUpload, apiKey, userId, pan);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadAadharImage(String frontFile, String backFile, String id, String aadharNumber, String token) {
        try {
            MultipartBody.Part fileToUploadFront = null;
            MultipartBody.Part fileToUploadback = null;
            if (!frontFile.equals("")) {
                File file = new File(frontFile);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUploadFront = MultipartBody.Part.createFormData("aadhar_frnt_img", file.getName(), requestBody);
            }
            if (!backFile.equals("")) {
                File file = new File(backFile);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUploadback = MultipartBody.Part.createFormData("aadhar_back_img", file.getName(), requestBody);
            }
            String apiKey = token;
            Log.d("tag", apiKey);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody aadharNum = RequestBody.create(MediaType.parse("text/plain"), aadharNumber);

            Call<FileUploadResponse> call = apiService.saveAadharData(fileToUploadFront, fileToUploadback, apiKey, userId, aadharNum);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null) {
                        Log.d("tag", String.valueOf(response.body()));
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {

                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadLatestDocs(String salSlipPath, ArrayList<String> bankStatements, String slipPwd, String stmtPwd, String id, LatestDocumentsFrag latestDocumentsFrag, String deviceUniqueId, String token) {
        try {
            MultipartBody.Part salSlip = null;
            if (salSlipPath != null && !salSlipPath.equals("")) {
                File file = new File(salSlipPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                salSlip = MultipartBody.Part.createFormData("salaryslips", file.getName(), requestBody);
            }

            MultipartBody.Part[] bankStatementsList = null;
            if (bankStatements != null && bankStatements.size() > 0) {
                bankStatementsList = new MultipartBody.Part[bankStatements.size()];
                for (int i = 0; i < bankStatements.size(); i++) {
                    File bankFile = new File(bankStatements.get(i));
                    RequestBody bankBody = RequestBody.create(MediaType.parse("multipart/form-data"), bankFile);
                    bankStatementsList[i] = MultipartBody.Part.createFormData("bankaccountstatement[" + i + "]", bankFile.getName(), bankBody);
                }
            }
            RequestBody salSlipPwd = RequestBody.create(MediaType.parse("text/plain"), slipPwd);
            RequestBody bankPwd = RequestBody.create(MediaType.parse("text/plain"), stmtPwd);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), deviceUniqueId);

            Call<JsonObject> call = apiService.uploadLatestDocs(salSlip, bankStatementsList, salSlipPwd, bankPwd, apiKey, deviceId, userId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("tag", String.valueOf(response.body()));
                    latestDocumentsFrag.updateFileUploadStatus(String.valueOf(response.body()));
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadLatestDocsBank(String salSlipPath, ArrayList<String> bankStatements, String slipPwd, String stmtPwd, String id, BankDetailsActivity bankDetailsActivity, String deviceUniqueId, String token) {
        try {
            MultipartBody.Part salSlip = null;
            if (salSlipPath != null && !salSlipPath.equals("")) {
                File file = new File(salSlipPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                salSlip = MultipartBody.Part.createFormData("salaryslips", file.getName(), requestBody);
            }

            MultipartBody.Part[] bankStatementsList = null;
            if (bankStatements != null && bankStatements.size() > 0) {
                bankStatementsList = new MultipartBody.Part[bankStatements.size()];
                for (int i = 0; i < bankStatements.size(); i++) {
                    File bankFile = new File(bankStatements.get(i));
                    RequestBody bankBody = RequestBody.create(MediaType.parse("multipart/form-data"), bankFile);
                    bankStatementsList[i] = MultipartBody.Part.createFormData("bankaccountstatement[" + i + "]", bankFile.getName(), bankBody);
                }
            }
            RequestBody salSlipPwd = RequestBody.create(MediaType.parse("text/plain"), slipPwd);
            RequestBody bankPwd = RequestBody.create(MediaType.parse("text/plain"), stmtPwd);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), deviceUniqueId);

            Call<JsonObject> call = apiService.uploadLatestDocs(salSlip, bankStatementsList, salSlipPwd, bankPwd, apiKey, deviceId, userId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("tag", String.valueOf(response.body()));
                    mResponseListener.responseData(new Gson().toJson(response.body()));
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLatestPayslip(String salSlipPath, String slipPwd, String id, LatestDocumentsFrag latestDocumentsFrag, String deviceUniqueId, String token) {
        try {

            MultipartBody.Part salSlip = null;
            if (salSlipPath != null && !salSlipPath.equals("")) {
                File file = new File(salSlipPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                salSlip = MultipartBody.Part.createFormData("salaryslips", file.getName(), requestBody);
            }

            RequestBody salSlipPwd = RequestBody.create(MediaType.parse("text/plain"), slipPwd);
            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), id);
            RequestBody deviceId = RequestBody.create(MediaType.parse("text/plain"), deviceUniqueId);


            Call<JsonObject> call = apiService.saveLatestPayslip(salSlip, salSlipPwd, apiKey, deviceId, userId);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.d("saving sal slip", String.valueOf(response.body()));
                    //latestDocumentsFrag.updateFileUploadStatus(String.valueOf(response.body()));
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadFileToServer(FileUploadAjaxRequest fileUploadAjaxRequest, String token) {
        try {
            fileUploadAjaxRequest.setApiKey(token);
            Call<ResponseBody> call = apiService.updateFileAjay(fileUploadAjaxRequest);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //mResponseListener.responseData(new Gson().toJson(response.body()));
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitInitialDocs(SubmitInitialDocsReq submitInitialDocsReq, String token) {
        try {
            submitInitialDocsReq.setApiKey(token);

            Call<GenericResponse> call = apiService.submitInitialDocs(submitInitialDocsReq);
            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null && response.body().getStatus()) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserData(GenericRequest genericRequest) {
        try {
            genericRequest.setApiKey(sharedPreferences.getString(Constants.USER_TOKEN));
            Call<UserDetailsResponse> contactUsResponseCall = apiService.getProfile(genericRequest);
            contactUsResponseCall.enqueue(new Callback<UserDetailsResponse>() {
                @Override
                public void onResponse(Call<UserDetailsResponse> call, Response<UserDetailsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<UserDetailsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLogicalBanyaData(GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<CouponsResponse> contactUsResponseCall = apiService.getLogicalBanyaData(genericRequest);
            contactUsResponseCall.enqueue(new Callback<CouponsResponse>() {
                @Override
                public void onResponse(Call<CouponsResponse> call, Response<CouponsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CouponsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void redeemCoupon(RedeemCouponReq redeemCouponReq, String token) {
        try {
            redeemCouponReq.setApiKey(token);
            Call<RedeemCouponResponse> contactUsResponseCall = apiService.redeemCoupon(redeemCouponReq);
            contactUsResponseCall.enqueue(new Callback<RedeemCouponResponse>() {
                @Override
                public void onResponse(Call<RedeemCouponResponse> call, Response<RedeemCouponResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RedeemCouponResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEmail(SendEmailReq sendEmailReq, String token) {
        try {
            sendEmailReq.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.sendEmail(sendEmailReq);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addressSave(SendLocationReq sendLocationReq, String token) {
        try {
            sendLocationReq.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.addressSave(sendLocationReq);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skipPanData(SkipPanData skipPanData, String token) {
        try {
            skipPanData.setApiKey(token);
            Call<GetCibilResponse> contactUsResponseCall = apiService.skipPanData(skipPanData);
            contactUsResponseCall.enqueue(new Callback<GetCibilResponse>() {
                @Override
                public void onResponse(Call<GetCibilResponse> call, Response<GetCibilResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCibilResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skipAadharData(SkipAadharData skipAadharData, String token) {
        try {
            skipAadharData.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.skipAadharData(skipAadharData);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCityList(GetCityListReq getCityListReq, String token) {
        try {
            getCityListReq.setApiKey(token);
            Call<CityListResponse> cityListResponseCall = apiService.getCityList(getCityListReq);
            Log.d("city req..", new Gson().toJson(getCityListReq));
            cityListResponseCall.enqueue(new Callback<CityListResponse>() {
                @Override
                public void onResponse(Call<CityListResponse> call, Response<CityListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CityListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBrandList(GetBrandListReq getBrandListReq, String token) {
        try {
            getBrandListReq.setApiKey(token);
            Call<GetBrandListResponse> brandListResponseCall = apiService.getBrandList(getBrandListReq);
            Log.d("brand req..", new Gson().toJson(getBrandListReq));
            brandListResponseCall.enqueue(new Callback<GetBrandListResponse>() {
                @Override
                public void onResponse(Call<GetBrandListResponse> call, Response<GetBrandListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetBrandListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAreaList(GetAreaListReq getAreaListReq, String token) {
        try {
            getAreaListReq.setApiKey(token);
            Call<AreaListResponse> areaListResponseCall = apiService.getAreaList(getAreaListReq);
            Log.d("Area req..", new Gson().toJson(getAreaListReq));
            areaListResponseCall.enqueue(new Callback<AreaListResponse>() {
                @Override
                public void onResponse(Call<AreaListResponse> call, Response<AreaListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<AreaListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDealerList(GetDealerListReq getDealerListReq, String token) {
        try {
            getDealerListReq.setApiKey(token);
            Call<DealerListResponse> dealerListResponseCall = apiService.getDealerList(getDealerListReq);
            Log.d("dealer req..", new Gson().toJson(getDealerListReq));
            dealerListResponseCall.enqueue(new Callback<DealerListResponse>() {
                @Override
                public void onResponse(Call<DealerListResponse> call, Response<DealerListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    } else {
                        mErrorListener.errorData(null);
                    }
                }

                @Override
                public void onFailure(Call<DealerListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVehiclesList(GetVehiclesListReq getVehiclesListReq, String token) {
        try {
            getVehiclesListReq.setApiKey(token);
            Call<VehiclesListResponse> vehiclesListResponseCall = apiService.getVehiclesList(getVehiclesListReq);
            Log.d("vehicle req..", new Gson().toJson(getVehiclesListReq));
            vehiclesListResponseCall.enqueue(new Callback<VehiclesListResponse>() {
                @Override
                public void onResponse(Call<VehiclesListResponse> call, Response<VehiclesListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<VehiclesListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getColorList(GetColorListReq getColorListReq, String token) {
        try {
            getColorListReq.setApiKey(token);
            Call<ColorListResponse> colorListResponseCall = apiService.getColorList(getColorListReq);
            Log.d("color req..", new Gson().toJson(getColorListReq));
            colorListResponseCall.enqueue(new Callback<ColorListResponse>() {
                @Override
                public void onResponse(Call<ColorListResponse> call, Response<ColorListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ColorListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVarientList(GetVarientListReq getVarientListReq, String token) {
        try {
            getVarientListReq.setApiKey(token);
            Call<VarientListResponse> varientListResponseCall = apiService.getVarientList(getVarientListReq);
            Log.d("varient req..", new Gson().toJson(getVarientListReq));
            varientListResponseCall.enqueue(new Callback<VarientListResponse>() {
                @Override
                public void onResponse(Call<VarientListResponse> call, Response<VarientListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<VarientListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getCibilScore(GetCibilReq getCibilReq, String token) {
        try {
            getCibilReq.setApiKey(token);
            Call<GetCibilResponse> contactUsResponseCall = apiService.getCibilScore(getCibilReq);
            contactUsResponseCall.enqueue(new Callback<GetCibilResponse>() {
                @Override
                public void onResponse(Call<GetCibilResponse> call, Response<GetCibilResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCibilResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBankData(GetUpdateBankDataReq getUpdateBankDataReq, String token) {
        try {
            getUpdateBankDataReq.setApiKey(token);
            Call<UpdateBankDataResponse> contactUsResponseCall = apiService.updateBankData(getUpdateBankDataReq);
            contactUsResponseCall.enqueue(new Callback<UpdateBankDataResponse>() {
                @Override
                public void onResponse(Call<UpdateBankDataResponse> call, Response<UpdateBankDataResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<UpdateBankDataResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVehicleDetails(GetVehicleDetailsReq getVehicleDetailsReq, String token) {
        try {
            getVehicleDetailsReq.setApiKey(token);
            Call<GetVehicleDetailsResponse> contactUsResponseCall = apiService.getVehicleDetails(getVehicleDetailsReq);
            Log.d("vehicle details req..", new Gson().toJson(getVehicleDetailsReq));
            contactUsResponseCall.enqueue(new Callback<GetVehicleDetailsResponse>() {
                @Override
                public void onResponse(Call<GetVehicleDetailsResponse> call, Response<GetVehicleDetailsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetVehicleDetailsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTwlLoan(CancelTwlLoanReq cancelTwlLoanReq, String token) {
        try {
            cancelTwlLoanReq.setApiKey(token);
            Call<CancelTwlLoanResponse> contactUsResponseCall = apiService.twlCancelLoan(cancelTwlLoanReq);
            Log.d("vehicle details req..", new Gson().toJson(cancelTwlLoanReq));
            contactUsResponseCall.enqueue(new Callback<CancelTwlLoanResponse>() {
                @Override
                public void onResponse(Call<CancelTwlLoanResponse> call, Response<CancelTwlLoanResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CancelTwlLoanResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAadharOtp(AadharOtpReq aadharOtpReq, String token) {
        try {
            aadharOtpReq.setApiKey(token);
            Call<AadharOtpResponse> contactUsResponseCall = apiService.aadharOtpReq(aadharOtpReq);
            contactUsResponseCall.enqueue(new Callback<AadharOtpResponse>() {
                @Override
                public void onResponse(Call<AadharOtpResponse> call, Response<AadharOtpResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<AadharOtpResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyAadharOtp(VerifyAadharOtpReq verifyAadharOtpReq, String token) {
        try {
            verifyAadharOtpReq.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.verifyAadharotp(verifyAadharOtpReq);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLoanConsent(IdTextData idTextData, String token) {
        try {
            idTextData.setApiKey(token);
            Call<SaveLoanConsentResponse> contactUsResponseCall = apiService.saveLoanConsent(idTextData);
            contactUsResponseCall.enqueue(new Callback<SaveLoanConsentResponse>() {
                @Override
                public void onResponse(Call<SaveLoanConsentResponse> call, Response<SaveLoanConsentResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<SaveLoanConsentResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBankList(GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<BankListRes> contactUsResponseCall = apiService.getBankList(genericRequest);
            contactUsResponseCall.enqueue(new Callback<BankListRes>() {
                @Override
                public void onResponse(Call<BankListRes> call, Response<BankListRes> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<BankListRes> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBankWeblink(GetBankLinkReq getBankLinkReq, String token) {
        try {
            getBankLinkReq.setApiKey(token);
            Call<WebLinkRes> contactUsResponseCall = apiService.getBankWeblink(getBankLinkReq);
            contactUsResponseCall.enqueue(new Callback<WebLinkRes>() {
                @Override
                public void onResponse(Call<WebLinkRes> call, Response<WebLinkRes> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<WebLinkRes> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBankWebLinkType(GetBankLinkReq getBankLinkReq, String token) {
        try {
            getBankLinkReq.setApiKey(token);
            Call<WebLinkRes> contactUsResponseCall = apiService.getBankWebLinkType(getBankLinkReq);
            contactUsResponseCall.enqueue(new Callback<WebLinkRes>() {
                @Override
                public void onResponse(Call<WebLinkRes> call, Response<WebLinkRes> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<WebLinkRes> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void analyseCapability(AnalyseCapabilityReq analyseCapabilityReq, String token) {
        try {
            analyseCapabilityReq.setApiKey(token);
            Call<RegisterDeviceResponse> contactUsResponseCall = apiService.analyseCapability(analyseCapabilityReq);
            contactUsResponseCall.enqueue(new Callback<RegisterDeviceResponse>() {
                @Override
                public void onResponse(Call<RegisterDeviceResponse> call, Response<RegisterDeviceResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RegisterDeviceResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyTWLoan(ApplyTWLoanReq applyTWLoanReq, String token) {
        try {
            applyTWLoanReq.setApiKey(token);
            Log.d("twl loan req", new Gson().toJson(applyTWLoanReq));
            Call<ApplyTWLoanResponse> contactUsResponseCall = apiService.applyTWLoan(applyTWLoanReq);
            contactUsResponseCall.enqueue(new Callback<ApplyTWLoanResponse>() {
                @Override
                public void onResponse(Call<ApplyTWLoanResponse> call, Response<ApplyTWLoanResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ApplyTWLoanResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTwlTenureData(GetTwlTenureDataReq twlTenureDataReq, String token) {
        try {
            twlTenureDataReq.setApiKey(token);
            Call<TwlTenureDataResponse> contactUsResponseCall = apiService.getTwlTenureData(twlTenureDataReq);
            contactUsResponseCall.enqueue(new Callback<TwlTenureDataResponse>() {
                @Override
                public void onResponse(Call<TwlTenureDataResponse> call, Response<TwlTenureDataResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<TwlTenureDataResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void twlBannerImages(TwlBannerImagesReq twlBannerImagesReq, String token) {
        try {
            twlBannerImagesReq.setApiKey(token);
            Call<TwlBannerImagesResponse> contactUsResponseCall = apiService.twlBannerImages(twlBannerImagesReq);
            contactUsResponseCall.enqueue(new Callback<TwlBannerImagesResponse>() {
                @Override
                public void onResponse(Call<TwlBannerImagesResponse> call, Response<TwlBannerImagesResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<TwlBannerImagesResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getContactUsData(ContactUsRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<ContactUsResponse> contactUsResponseCall = apiService.getContactUsData(genericRequest);
            contactUsResponseCall.enqueue(new Callback<ContactUsResponse>() {
                @Override
                public void onResponse(Call<ContactUsResponse> call, Response<ContactUsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ContactUsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUserDetails(ContactUsRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<ProfileResponse> contactUsResponseCall = apiService.getUserDetails(genericRequest);
            contactUsResponseCall.enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManagerData(ContactUsRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<ManagerResponse> contactUsResponseCall = apiService.getManagerData(genericRequest);
            contactUsResponseCall.enqueue(new Callback<ManagerResponse>() {
                @Override
                public void onResponse(Call<ManagerResponse> call, Response<ManagerResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ManagerResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestOTP(GetOTPRequest getOTPRequest) {
        try {
            getOTPRequest.setApiKey(Constants.CN_API_KEY);
            Call<GenericResponse> contactUsResponseCall = apiService.requestOTP(getOTPRequest);
            Log.d("requestOTPReq", new Gson().toJson(getOTPRequest));
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        Log.d("requestOTPResponse", new Gson().toJson(response.body()));
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestCallOTP(GetOTPRequest getOTPRequest, boolean isResend) {
        try {
            getOTPRequest.setApiKey(Constants.CN_API_KEY);
            Call<GenericResponse> contactUsResponseCall = null;
            if (isResend) {
                contactUsResponseCall = apiService.resendOTP(getOTPRequest);
            } else {
                contactUsResponseCall = apiService.requestCallOTP(getOTPRequest);
            }
            Log.d("requestOTPReq", new Gson().toJson(getOTPRequest));
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        Log.d("requestOTPResponse", new Gson().toJson(response.body()));
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyOTP(VerifyOTPRequest verifyOTPRequest) {
        try {
            verifyOTPRequest.setApiKey(Constants.CN_API_KEY);
            Log.d("verifyOTPReq", new Gson().toJson(verifyOTPRequest));
            Call<GenericResponse> contactUsResponseCall = apiService.verifyOTP(verifyOTPRequest);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        Log.d("verifyOTPResponse", new Gson().toJson(response.body()));
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoFill(VerifyOTPRequest verifyOTPRequest) {
        try {
            verifyOTPRequest.setApiKey(Constants.CN_API_KEY);
            //Log.d("verifyOTPReq", new Gson().toJson(verifyOTPRequest));
            Call<AutofillResponse> contactUsResponseCall = apiService.autoFill(verifyOTPRequest);
            contactUsResponseCall.enqueue(new Callback<AutofillResponse>() {
                @Override
                public void onResponse(Call<AutofillResponse> call, Response<AutofillResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<AutofillResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerDevice(RegisterDeviceRequest registerDeviceRequest, String token) {
        try {
            registerDeviceRequest.setApiKey(token);
            Log.d("RegisterDevice", new Gson().toJson(registerDeviceRequest));
            Call<RegisterDeviceResponse> contactUsResponseCall = apiService.registerDevice(registerDeviceRequest);
            contactUsResponseCall.enqueue(new Callback<RegisterDeviceResponse>() {
                @Override
                public void onResponse(Call<RegisterDeviceResponse> call, Response<RegisterDeviceResponse> response) {
                    if (response.body() != null) {
                        Log.d("RegisterDevice", new Gson().toJson(response.body()));
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RegisterDeviceResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getNotificationCount(@NotNull GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.getNotificationCount(genericRequest);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<GenericResponse> call, @NotNull Response<GenericResponse> response) {
                        if (response.body() != null && response.body().getStatus()) {
                            mResponseListener.responseData(new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<GenericResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readNotification(@NotNull GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.readNotifications(genericRequest);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<GenericResponse> call, @NotNull Response<GenericResponse> response) {
                        if (response.body() != null && response.body().getStatus()) {
                            mResponseListener.responseData(new Gson().toJson(response.body()));
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<GenericResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLoanStatus(@NotNull GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<LoanStatusResponse> contactUsResponseCall = apiService.getLoanStatus(genericRequest);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<LoanStatusResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<LoanStatusResponse> call, @NotNull Response<LoanStatusResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<LoanStatusResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTwlLoanStatus(@NotNull GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<TwlLoanStatusResponse> contactUsResponseCall = apiService.twlLoanStatus(genericRequest);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<TwlLoanStatusResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<TwlLoanStatusResponse> call, @NotNull Response<TwlLoanStatusResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<TwlLoanStatusResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void twlActiveLoans(TwlActiveLoansReq twlActiveLoansReq, String token) {
        try {
            twlActiveLoansReq.setApiKey(token);
            Call<TwlActiveLoansResponse> contactUsResponseCall = apiService.twlActiveLoans(twlActiveLoansReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<TwlActiveLoansResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<TwlActiveLoansResponse> call, @NotNull Response<TwlActiveLoansResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<TwlActiveLoansResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void twlLoanDetails(TwlLoanDetailsReq twlLoanDetailsReq, String token) {
        try {
            twlLoanDetailsReq.setApiKey(token);
            Call<TwlLoanDetailsResponse> contactUsResponseCall = apiService.twlLoanDetails(twlLoanDetailsReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<TwlLoanDetailsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<TwlLoanDetailsResponse> call, @NotNull Response<TwlLoanDetailsResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<TwlLoanDetailsResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveMessage(SaveMessageReq saveMessageReq, String token) {
        try {
            saveMessageReq.setApiKey(token);
            Call<SaveDataResponse> contactUsResponseCall = apiService.saveSmsData(saveMessageReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<SaveDataResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<SaveDataResponse> call, @NotNull Response<SaveDataResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<SaveDataResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveApps(@NotNull SaveAppReq saveAppReq, @Nullable String token) {
        try {
            saveAppReq.setApiKey(token);
            Call<SaveDataResponse> contactUsResponseCall = apiService.saveAppData(saveAppReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<SaveDataResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<SaveDataResponse> call, @NotNull Response<SaveDataResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<SaveDataResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCalendarEvents(@NotNull SaveCalendarEventsReq saveCalendarEventReq, @Nullable String token) {
        try {
            saveCalendarEventReq.setApiKey(token);
            Call<SaveDataResponse> contactUsResponseCall = apiService.saveEventData(saveCalendarEventReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<SaveDataResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<SaveDataResponse> call, @NotNull Response<SaveDataResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<SaveDataResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPromoCode(CheckPromoCodeReq checkPromoCodeReq, @Nullable String token) {
        try {
            checkPromoCodeReq.setApiKey(token);
            Call<CheckPromoCodeResponse> contactUsResponseCall = apiService.checkPromoCode(checkPromoCodeReq);
            Log.d("promo code req", new Gson().toJson(checkPromoCodeReq));
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<CheckPromoCodeResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<CheckPromoCodeResponse> call, @NotNull Response<CheckPromoCodeResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<CheckPromoCodeResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitContactUs(@NotNull SubmitContactUsReq submitContactUsReq, @Nullable String token) {
        try {
            submitContactUsReq.setApiKey(token);
            Call<SubmitContactUsResponse> contactUsResponseCall = apiService.submitContactUs(submitContactUsReq);
            if (contactUsResponseCall != null) {
                contactUsResponseCall.enqueue(new Callback<SubmitContactUsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<SubmitContactUsResponse> call, @NotNull Response<SubmitContactUsResponse> response) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }

                    @Override
                    public void onFailure(@NotNull Call<SubmitContactUsResponse> call, @NotNull Throwable t) {
                        mErrorListener.errorData(t);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void razorPayCreateMandate(@NotNull RazorPayCreateMandateReq razorPayCreateMandateReq, @Nullable String token) {
        try {
            razorPayCreateMandateReq.setApiKey(token);
            Call<RazorPayCreateMandateResponse> contactUsResponseCall = apiService.razorPayCreateMandate(razorPayCreateMandateReq);
            contactUsResponseCall.enqueue(new Callback<RazorPayCreateMandateResponse>() {
                @Override
                public void onResponse(Call<RazorPayCreateMandateResponse> call, Response<RazorPayCreateMandateResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RazorPayCreateMandateResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void razorPaySubmitMandate(@NotNull RazorPaySubmitMandateReq razorPaySubmitMandateReq, @NotNull String token) {
        try {
            razorPaySubmitMandateReq.setApiKey(token);
            Call<RazorPaySubmitMandateResponse> contactUsResponseCall = apiService.razorPaySubmitMandate(razorPaySubmitMandateReq);
            contactUsResponseCall.enqueue(new Callback<RazorPaySubmitMandateResponse>() {
                @Override
                public void onResponse(Call<RazorPaySubmitMandateResponse> call, Response<RazorPaySubmitMandateResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RazorPaySubmitMandateResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void csActiveLoan(@NotNull CSActiveLoanReq csActiveLoanReq, @Nullable String token) {
        try {
            csActiveLoanReq.setApiKey(token);
            Call<CSActiveLoanResponse> contactUsResponseCall = apiService.csActiveLoan(csActiveLoanReq);
            contactUsResponseCall.enqueue(new Callback<CSActiveLoanResponse>() {
                @Override
                public void onResponse(Call<CSActiveLoanResponse> call, Response<CSActiveLoanResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CSActiveLoanResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCouponCategories(@NotNull GetCouponCategoriesReq couponCategoriesReq, @Nullable String token) {
        try {
            couponCategoriesReq.setApiKey(token);
            Call<GetCouponCategoriesResponse> contactUsResponseCall = apiService.getCouponCategories(couponCategoriesReq);
            contactUsResponseCall.enqueue(new Callback<GetCouponCategoriesResponse>() {
                @Override
                public void onResponse(Call<GetCouponCategoriesResponse> call, Response<GetCouponCategoriesResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCouponCategoriesResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCoupons(@NotNull GetCouponsReq couponsReq, @Nullable String token) {
        try {
            couponsReq.setApiKey(token);
            Call<GetCouponsResponse> contactUsResponseCall = apiService.getCoupon(couponsReq);
            contactUsResponseCall.enqueue(new Callback<GetCouponsResponse>() {
                @Override
                public void onResponse(Call<GetCouponsResponse> call, Response<GetCouponsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCouponsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCouponInfo(@NotNull GetCouponInfoReq couponInfoReq, @Nullable String token) {
        try {

            couponInfoReq.setApiKey(token);
            Call<GetCouponInfoResponse> contactUsResponseCall = apiService.getCouponInfo(couponInfoReq);
            contactUsResponseCall.enqueue(new Callback<GetCouponInfoResponse>() {
                @Override
                public void onResponse(Call<GetCouponInfoResponse> call, Response<GetCouponInfoResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCouponInfoResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRedeemCoupon(@NotNull GetRedeemCouponReq redeemCouponReq, @Nullable String token) {
        try {
            redeemCouponReq.setApiKey(token);
            Call<GetRedeemCouponResponse> contactUsResponseCall = apiService.getRedeemCoupon(redeemCouponReq);
            contactUsResponseCall.enqueue(new Callback<GetRedeemCouponResponse>() {
                @Override
                public void onResponse(Call<GetRedeemCouponResponse> call, Response<GetRedeemCouponResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetRedeemCouponResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRedeemCouponDetails(@NotNull GetRedeemCouponDetailsReq redeemCouponDetailsReq, @Nullable String token) {
        try {
            redeemCouponDetailsReq.setApiKey(token);
            Call<GetRedeemCouponDetailsResponse> contactUsResponseCall = apiService.getRedeemCouponDetails(redeemCouponDetailsReq);
            contactUsResponseCall.enqueue(new Callback<GetRedeemCouponDetailsResponse>() {
                @Override
                public void onResponse(Call<GetRedeemCouponDetailsResponse> call, Response<GetRedeemCouponDetailsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetRedeemCouponDetailsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEmailCouponDetails(@NotNull GetEmailCouponDetailsReq emailCouponDetailsReq, @Nullable String token) {
        try {
            emailCouponDetailsReq.setApiKey(token);
            Call<GetEmailCouponDetailsResponse> contactUsResponseCall = apiService.getEmailCouponDetails(emailCouponDetailsReq);
            contactUsResponseCall.enqueue(new Callback<GetEmailCouponDetailsResponse>() {
                @Override
                public void onResponse(Call<GetEmailCouponDetailsResponse> call, Response<GetEmailCouponDetailsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetEmailCouponDetailsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRedeemedCoupons(@NotNull GetRedeemedCouponsReq redeemedCouponsReq, @Nullable String token) {
        try {
            redeemedCouponsReq.setApiKey(token);
            Call<GetRedeemedCouponsResponse> contactUsResponseCall = apiService.getRedeemedCoupons(redeemedCouponsReq);
            contactUsResponseCall.enqueue(new Callback<GetRedeemedCouponsResponse>() {
                @Override
                public void onResponse(Call<GetRedeemedCouponsResponse> call, Response<GetRedeemedCouponsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetRedeemedCouponsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prSaveSign(@NotNull PrSaveSignReq prSaveSignReq, @Nullable String token) {

        try {
            prSaveSignReq.setApiKey(token);
            Call<CSGenericResponse> contactUsResponseCall = apiService.prSaveSign(prSaveSignReq);
            contactUsResponseCall.enqueue(new Callback<CSGenericResponse>() {
                @Override
                public void onResponse(Call<CSGenericResponse> call, Response<CSGenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CSGenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveReferences(@NotNull SaveReferencesReq saveReferencesReq, @Nullable String token) {
        try {
            saveReferencesReq.setApiKey(token);
            Call<SaveReferencesResponse> contactUsResponseCall = apiService.saveReferences(saveReferencesReq);
            contactUsResponseCall.enqueue(new Callback<SaveReferencesResponse>() {
                @Override
                public void onResponse(Call<SaveReferencesResponse> call, Response<SaveReferencesResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveReferencesResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveSalary(@NotNull SaveSalaryReq saveSalaryReq, @Nullable String token) {
        try {
            saveSalaryReq.setApiKey(token);
            Call<SaveSalaryResponse> contactUsResponseCall = apiService.saveSalary(saveSalaryReq);
            contactUsResponseCall.enqueue(new Callback<SaveSalaryResponse>() {
                @Override
                public void onResponse(Call<SaveSalaryResponse> call, Response<SaveSalaryResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveSalaryResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPincodes(@NotNull GetPinCodesReq pincodesReq, @Nullable String token) {

        try {
            pincodesReq.setApiKey(token);
            Call<GetPinCodesResponse> contactUsResponseCall = apiService.getPincodes(pincodesReq);
            contactUsResponseCall.enqueue(new Callback<GetPinCodesResponse>() {
                @Override
                public void onResponse(Call<GetPinCodesResponse> call, Response<GetPinCodesResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetPinCodesResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void memberUpgradeConsent(MemberUpgradeConsentReq memberUpgradeConsentReq, String token) {

        try {
            memberUpgradeConsentReq.setApiKey(token);
            Call<MemberUpgradeConsentResponse> contactUsResponseCall = apiService.memberUpgradeConsent(memberUpgradeConsentReq);
            contactUsResponseCall.enqueue(new Callback<MemberUpgradeConsentResponse>() {
                @Override
                public void onResponse(Call<MemberUpgradeConsentResponse> call, Response<MemberUpgradeConsentResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<MemberUpgradeConsentResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitContactUsWeb(@NotNull String webCntExistingCustomer, @Nullable String selectedFilePath1, @Nullable String selectedFilePath2, @Nullable String selectedFilePath3, @Nullable String webCntName, @Nullable String webCntMobileNumber, @Nullable String webCntEmail, @NotNull String platform, @Nullable String webCntQuery, @NotNull String webCntSubQuery, @Nullable String webCntMessage, @Nullable String token) {
        try {
            MultipartBody.Part fileToUploadSelectedFile1 = null;
            MultipartBody.Part fileToUploadSelectedFile2 = null;
            MultipartBody.Part fileToUploadSelectedFile3 = null;
            if (!selectedFilePath1.equals("")) {
                File file = new File(selectedFilePath1);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUploadSelectedFile1 = MultipartBody.Part.createFormData("web_issue_file_one", file.getName(), requestBody);
            }
            if (!selectedFilePath2.equals("")) {
                File file = new File(selectedFilePath2);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUploadSelectedFile2 = MultipartBody.Part.createFormData("web_issue_file_two", file.getName(), requestBody);
            }
            if (!selectedFilePath3.equals("")) {
                File file = new File(selectedFilePath3);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                fileToUploadSelectedFile3 = MultipartBody.Part.createFormData("web_issue_file_three", file.getName(), requestBody);
            }

            RequestBody apiKey = RequestBody.create(MediaType.parse("text/plain"), token);
            RequestBody existingCustomer = RequestBody.create(MediaType.parse("text/plain"), webCntExistingCustomer);
            RequestBody cntName = RequestBody.create(MediaType.parse("text/plain"), webCntName);
            RequestBody cntEmail = RequestBody.create(MediaType.parse("text/plain"), webCntEmail);
            RequestBody cntMobileNumber = RequestBody.create(MediaType.parse("text/plain"), webCntMobileNumber);
            RequestBody mobilePlatform = RequestBody.create(MediaType.parse("text/plain"), platform);
            RequestBody cntQuery = RequestBody.create(MediaType.parse("text/plain"), webCntQuery);
            RequestBody cntSubQuery = RequestBody.create(MediaType.parse("text/plain"), webCntSubQuery);
            RequestBody cntMessage = RequestBody.create(MediaType.parse("text/plain"), webCntMessage);
            Call<FileUploadResponse> call = apiService.submitContactUsWeb(fileToUploadSelectedFile1, fileToUploadSelectedFile2, fileToUploadSelectedFile3, apiKey, existingCustomer, cntName, cntEmail,
                    cntMobileNumber, mobilePlatform, cntQuery, cntSubQuery, cntMessage);
            call.enqueue(new Callback<FileUploadResponse>() {
                @Override
                public void onResponse(Call<FileUploadResponse> call, Response<FileUploadResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<FileUploadResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAdditionalDocuments(@NotNull GetAdditionalDocReq getAdditionalDocReq, @Nullable String token) {
        try {
            getAdditionalDocReq.setApiKey(token);
            Call<GetAdditionalDocResponse> contactUsResponseCall = apiService.getAdditionalDocuments(getAdditionalDocReq);
            contactUsResponseCall.enqueue(new Callback<GetAdditionalDocResponse>() {
                @Override
                public void onResponse(Call<GetAdditionalDocResponse> call, Response<GetAdditionalDocResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetAdditionalDocResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadAdditionalDocuments(@NotNull UploadAdditionalDocReq uploadAdditionalDocReq, @NotNull String token) {
        try {
            uploadAdditionalDocReq.setApiKey(token);
            Call<UploadAdditionalDocResponse> contactUsResponseCall = apiService.uploadAdditionalDocuments(uploadAdditionalDocReq);
            contactUsResponseCall.enqueue(new Callback<UploadAdditionalDocResponse>() {
                @Override
                public void onResponse(Call<UploadAdditionalDocResponse> call, Response<UploadAdditionalDocResponse> response) {
                    if (response != null && response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<UploadAdditionalDocResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPendingDocuments(@NotNull GetPendingDocReq getPendingDocReq, @Nullable String token) {
        try {
            getPendingDocReq.setApiKey(token);
            Call<GetPendingDocResponse> contactUsResponseCall = apiService.getPendingDocuments(getPendingDocReq);
            contactUsResponseCall.enqueue(new Callback<GetPendingDocResponse>() {
                @Override
                public void onResponse(Call<GetPendingDocResponse> call, Response<GetPendingDocResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetPendingDocResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadPendingDocuments(@NotNull UploadPendingDocReq uploadPendingDocReq, @NotNull String token) {
        try {
            uploadPendingDocReq.setApiKey(token);
            Call<UploadPendingDocResponse> contactUsResponseCall = apiService.uploadPendingDocuments(uploadPendingDocReq);
            contactUsResponseCall.enqueue(new Callback<UploadPendingDocResponse>() {
                @Override
                public void onResponse(Call<UploadPendingDocResponse> call, Response<UploadPendingDocResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<UploadPendingDocResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAdharManualDocs(@NotNull SkipAadharData updateAdharManualDocsReq, @Nullable String token) {
        try {
            updateAdharManualDocsReq.setApiKey(token);
            Call<GenericResponse> contactUsResponseCall = apiService.updateAdharManualDocs(updateAdharManualDocsReq);
            contactUsResponseCall.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAnalysisType(@NotNull GetAnalysisTypeReq analysisTypeReq, @Nullable String token) {
        try {
            analysisTypeReq.setApiKey(token);
            Call<GetAnalysisTypeResponse> contactUsResponseCall = apiService.getAnalysisType(analysisTypeReq);
            contactUsResponseCall.enqueue(new Callback<GetAnalysisTypeResponse>() {
                @Override
                public void onResponse(Call<GetAnalysisTypeResponse> call, Response<GetAnalysisTypeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetAnalysisTypeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveCreditCardData(GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<SaveCCDataResponse> contactUsResponseCall = apiService.saveCreditCardData(genericRequest);
            contactUsResponseCall.enqueue(new Callback<SaveCCDataResponse>() {
                @Override
                public void onResponse(Call<SaveCCDataResponse> call, Response<SaveCCDataResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveCCDataResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spendsAnalysis(@NotNull GetSpendsDataReq spendsDataReq, @Nullable String token) {
        try {
            spendsDataReq.setApiKey(token);
            Call<GetSpendsResponse> contactUsResponseCall = apiService.spendsAnalysis(spendsDataReq);
            contactUsResponseCall.enqueue(new Callback<GetSpendsResponse>() {
                @Override
                public void onResponse(Call<GetSpendsResponse> call, Response<GetSpendsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetSpendsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCaptcha(@NotNull GetCaptchaReq captchaReq, @Nullable String token) {
        try {
            captchaReq.setApiKey(token);
            Call<GetCaptchaResponse> contactUsResponseCall = apiService.getCaptchaAadhaar(captchaReq);
            contactUsResponseCall.enqueue(new Callback<GetCaptchaResponse>() {
                @Override
                public void onResponse(Call<GetCaptchaResponse> call, Response<GetCaptchaResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetCaptchaResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UpdateNewLocation(UpdateNewLocationReq updateNewLocationReq, String token) {
        try {
            updateNewLocationReq.setApiKey(token);
            Call<UpdateNewLocationResponse> contactUsResponseCall = apiService.updateNewLocation(updateNewLocationReq);
            contactUsResponseCall.enqueue(new Callback<UpdateNewLocationResponse>() {
                @Override
                public void onResponse(Call<UpdateNewLocationResponse> call, Response<UpdateNewLocationResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<UpdateNewLocationResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRegistrationOne(@NotNull SaveRegistrationOneReq saveRegistrationOneReq, @Nullable String token) {
        try {
            saveRegistrationOneReq.setApiKey(token);
            Call<SaveRegistrationOneResponse> contactUsResponseCall = apiService.saveRegistrationOne(saveRegistrationOneReq);
            contactUsResponseCall.enqueue(new Callback<SaveRegistrationOneResponse>() {
                @Override
                public void onResponse(Call<SaveRegistrationOneResponse> call, Response<SaveRegistrationOneResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveRegistrationOneResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRegistrationTwo(@NotNull SaveRegistrationTwoReq saveRegistrationTwoReq, @Nullable String token) {

        try {
            saveRegistrationTwoReq.setApiKey(token);
            Call<SaveRegistrationTwoResponse> contactUsResponseCall = apiService.saveRegistrationTwo(saveRegistrationTwoReq);
            contactUsResponseCall.enqueue(new Callback<SaveRegistrationTwoResponse>() {
                @Override
                public void onResponse(Call<SaveRegistrationTwoResponse> call, Response<SaveRegistrationTwoResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveRegistrationTwoResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRegistrationThree(@NotNull SaveRegistrationThreeReq saveRegistrationThreeReq, @Nullable String token) {
        try {
            saveRegistrationThreeReq.setApiKey(token);
            Call<SaveRegistrationThreeResponse> contactUsResponseCall = apiService.saveRegistrationThree(saveRegistrationThreeReq);
            contactUsResponseCall.enqueue(new Callback<SaveRegistrationThreeResponse>() {
                @Override
                public void onResponse(Call<SaveRegistrationThreeResponse> call, Response<SaveRegistrationThreeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SaveRegistrationThreeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyLoanServiceData(@NotNull ApplyLoanServiceDataReq applyLoanServiceDataReq, @Nullable String token1) {

        try {
            applyLoanServiceDataReq.setApiKey(token1);
            Call<ApplyLoanServiceDataResponse> contactUsResponseCall = apiService.applyLoanServiceData(applyLoanServiceDataReq);
            contactUsResponseCall.enqueue(new Callback<ApplyLoanServiceDataResponse>() {
                @Override
                public void onResponse(Call<ApplyLoanServiceDataResponse> call, Response<ApplyLoanServiceDataResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<ApplyLoanServiceDataResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHoldStatus(@NotNull GetHoldStatusReq holdStatusReq, @Nullable String token) {
        try {
            holdStatusReq.setApiKey(token);
            Call<GetHoldStatusResponse> contactUsResponseCall = apiService.getHoldStatus(holdStatusReq);
            contactUsResponseCall.enqueue(new Callback<GetHoldStatusResponse>() {
                @Override
                public void onResponse(Call<GetHoldStatusResponse> call, Response<GetHoldStatusResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetHoldStatusResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stateAndCityFromPinCode(@NotNull PinCodeReq pinCodeReq, @Nullable String token) {
        try {
            pinCodeReq.setApiKey(token);
            Call<PinCodeResponse> contactUsResponseCall = apiService.stateAndCityFromPinCode(pinCodeReq);
            contactUsResponseCall.enqueue(new Callback<PinCodeResponse>() {
                @Override
                public void onResponse(Call<PinCodeResponse> call, Response<PinCodeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<PinCodeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyAlternateMobile(@NotNull VerifyAlternateMobileReq verifyAlternateMobileReq, @Nullable String token) {
        try {
            verifyAlternateMobileReq.setApiKey(token);
            Call<GetVerifyAlternateMobileResponse> contactUsResponseCall = apiService.verifyAlternateMobile(verifyAlternateMobileReq);
            contactUsResponseCall.enqueue(new Callback<GetVerifyAlternateMobileResponse>() {
                @Override
                public void onResponse(Call<GetVerifyAlternateMobileResponse> call, Response<GetVerifyAlternateMobileResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetVerifyAlternateMobileResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyAltMobileByOTP(@NotNull VerifyAltMobileByOTPReq verifyAltMobileByOTPReq, @Nullable String token) {
        try {
            verifyAltMobileByOTPReq.setApiKey(token);
            Call<VerifyAltMobileByOTPResponse> contactUsResponseCall = apiService.verifyAltMobileByOTP(verifyAltMobileByOTPReq);
            contactUsResponseCall.enqueue(new Callback<VerifyAltMobileByOTPResponse>() {
                @Override
                public void onResponse(Call<VerifyAltMobileByOTPResponse> call, Response<VerifyAltMobileByOTPResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<VerifyAltMobileByOTPResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDesignationList(@NotNull GetDesignationListReq designationListReq, @Nullable String token) {
        try {
            designationListReq.setApiKey(token);
            Call<DesignationListResponse> contactUsResponseCall = apiService.getDesignationList(designationListReq);
            contactUsResponseCall.enqueue(new Callback<DesignationListResponse>() {
                @Override
                public void onResponse(Call<DesignationListResponse> call, Response<DesignationListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<DesignationListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkLoanStatus(@NotNull CheckLoanStatusForDeletionReq checkLoanStatusForDeletionReq, @Nullable String token) {
        try {
            checkLoanStatusForDeletionReq.setApiKey(token);
            Call<CheckLoanStatusForDeletionResponse> contactUsResponseCall = apiService.checkLoanStatus(checkLoanStatusForDeletionReq);
            contactUsResponseCall.enqueue(new Callback<CheckLoanStatusForDeletionResponse>() {
                @Override
                public void onResponse(Call<CheckLoanStatusForDeletionResponse> call, Response<CheckLoanStatusForDeletionResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<CheckLoanStatusForDeletionResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void evSendOTP(@NotNull SendOTPDeletionReq sendOTPDeletionReq, @Nullable String token) {

        try {
            sendOTPDeletionReq.setApiKey(token);
            Call<SendOTPDeletionResponse> contactUsResponseCall = apiService.evSendOTP(sendOTPDeletionReq);
            contactUsResponseCall.enqueue(new Callback<SendOTPDeletionResponse>() {
                @Override
                public void onResponse(Call<SendOTPDeletionResponse> call, Response<SendOTPDeletionResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<SendOTPDeletionResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verifyOtpAccountDelete(@NotNull VerifyOTPDeletionReq verifyOTPDeletionReq, @Nullable String token) {
        try {
            verifyOTPDeletionReq.setApiKey(token);
            Call<VerifyOTPDeletionResponse> contactUsResponseCall = apiService.verifyOtpAccountDelete(verifyOTPDeletionReq);
            contactUsResponseCall.enqueue(new Callback<VerifyOTPDeletionResponse>() {
                @Override
                public void onResponse(Call<VerifyOTPDeletionResponse> call, Response<VerifyOTPDeletionResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<VerifyOTPDeletionResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteConsent(@NotNull DeleteConsentReq deleteConsentReq, @Nullable String token) {
        try {
            deleteConsentReq.setApiKey(token);
            Call<DeleteConsentResponse> contactUsResponseCall = apiService.deleteConsent(deleteConsentReq);
            contactUsResponseCall.enqueue(new Callback<DeleteConsentResponse>() {
                @Override
                public void onResponse(Call<DeleteConsentResponse> call, Response<DeleteConsentResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<DeleteConsentResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLoanRange(@NotNull GetLoanRangeReq loanRangeReq, @Nullable String token) {
        try {
            loanRangeReq.setApiKey(token);
            Call<GetLoanRangeResponse> contactUsResponseCall = apiService.getLoanRange(loanRangeReq);
            contactUsResponseCall.enqueue(new Callback<GetLoanRangeResponse>() {
                @Override
                public void onResponse(Call<GetLoanRangeResponse> call, Response<GetLoanRangeResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetLoanRangeResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerLoan(@NotNull RegisterLoanReq registerLoanReq, @Nullable String token) {
        try {
            registerLoanReq.setApiKey(token);
            Call<RegisterLoanResponse> contactUsResponseCall = apiService.registerLoan(registerLoanReq);
            contactUsResponseCall.enqueue(new Callback<RegisterLoanResponse>() {
                @Override
                public void onResponse(Call<RegisterLoanResponse> call, Response<RegisterLoanResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<RegisterLoanResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homePageBannerImages(BannerImageRequest bannerImageRequest, String token) {
        try {
            bannerImageRequest.setApiKey(token);
            Call<BannerImageResponse> contactUsResponseCall = apiService.homePageBannerImages(bannerImageRequest);
            contactUsResponseCall.enqueue(new Callback<BannerImageResponse>() {
                @Override
                public void onResponse(Call<BannerImageResponse> call, Response<BannerImageResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<BannerImageResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEligibleOffers(@NotNull GetEligibleOffersReq eligibleOffersReq, @Nullable String token) {
        try {
            eligibleOffersReq.setApiKey(token);
            Call<GetEligibleOffersResponse> contactUsResponseCall = apiService.getEligibleOffers(eligibleOffersReq);
            contactUsResponseCall.enqueue(new Callback<GetEligibleOffersResponse>() {
                @Override
                public void onResponse(Call<GetEligibleOffersResponse> call, Response<GetEligibleOffersResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetEligibleOffersResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getEligibleOfferDetails(@NotNull GetEligibleOfferDetailReq eligibleOfferDetailReq, @Nullable String token) {
        try {
            eligibleOfferDetailReq.setApiKey(token);
            Call<GetEligibleOfferDetailsResponse> contactUsResponseCall = apiService.getEligibleOfferDetails(eligibleOfferDetailReq);
            contactUsResponseCall.enqueue(new Callback<GetEligibleOfferDetailsResponse>() {
                @Override
                public void onResponse(Call<GetEligibleOfferDetailsResponse> call, Response<GetEligibleOfferDetailsResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetEligibleOfferDetailsResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bbpsBillPay(GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<BbpsBillPayResponse> contactUsResponseCall = apiService.bbpsBillPay(genericRequest);
            contactUsResponseCall.enqueue(new Callback<BbpsBillPayResponse>() {
                @Override
                public void onResponse(Call<BbpsBillPayResponse> call, Response<BbpsBillPayResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<BbpsBillPayResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bbpsLinkGenerator(@NotNull GetBbpsLinkGeneratorReq bbpsLinkGeneratorReq, @Nullable String token) {
        try {
            bbpsLinkGeneratorReq.setApiKey(token);
            Call<GetBbpsLinkGeneratorResponse> contactUsResponseCall = apiService.bbpsLinkGenerator(bbpsLinkGeneratorReq);
            contactUsResponseCall.enqueue(new Callback<GetBbpsLinkGeneratorResponse>() {
                @Override
                public void onResponse(Call<GetBbpsLinkGeneratorResponse> call, Response<GetBbpsLinkGeneratorResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetBbpsLinkGeneratorResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProcessPageContent(@NotNull GenericRequest genericRequest, @Nullable String token) {
        try {
            genericRequest.setApiKey(token);
            Call<GetProcessPageContentResponse> contactUsResponseCall = apiService.getProcessPageContent(genericRequest);
            contactUsResponseCall.enqueue(new Callback<GetProcessPageContentResponse>() {
                @Override
                public void onResponse(Call<GetProcessPageContentResponse> call, Response<GetProcessPageContentResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetProcessPageContentResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void offerScroll(GenericRequest genericRequest, String token) {
        try {
            genericRequest.setApiKey(token);
            Call<OfferScrollResponse> contactUsResponseCall = apiService.offerScroll(genericRequest);
            contactUsResponseCall.enqueue(new Callback<OfferScrollResponse>() {
                @Override
                public void onResponse(Call<OfferScrollResponse> call, Response<OfferScrollResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<OfferScrollResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void easebuzzResponseHandler(@NotNull EasebuzzResponseRequest easebuzzResponseRequest, @Nullable String token) {
        try {
            easebuzzResponseRequest.setApiKey(token);
            Call<EasebuzzResponseResponse> contactUsResponseCall = apiService.easebuzzResponseHandler(easebuzzResponseRequest);
            contactUsResponseCall.enqueue(new Callback<EasebuzzResponseResponse>() {
                @Override
                public void onResponse(Call<EasebuzzResponseResponse> call, Response<EasebuzzResponseResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<EasebuzzResponseResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTransactionData(@NotNull GetTransactionReq transactionReq, @Nullable String token) {
        try {
            transactionReq.setApiKey(token);
            Call<GetTransactionResponse> contactUsResponseCall = apiService.setTransactionData(transactionReq);
            contactUsResponseCall.enqueue(new Callback<GetTransactionResponse>() {
                @Override
                public void onResponse(Call<GetTransactionResponse> call, Response<GetTransactionResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetTransactionResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDigiLocker(@NotNull OpenDigiLockerReq openDigiLockerReq, @Nullable String token) {

        try {
            openDigiLockerReq.setApiKey(token);
            Call<OpenDigiLockerResponse> contactUsResponseCall = apiService.openDigiLocker(openDigiLockerReq);
            contactUsResponseCall.enqueue(new Callback<OpenDigiLockerResponse>() {
                @Override
                public void onResponse(Call<OpenDigiLockerResponse> call, Response<OpenDigiLockerResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<OpenDigiLockerResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getAnalysisList(@NotNull GetAnalysisTypeReq analysisTypeReq, @Nullable String token) {
        try {
            analysisTypeReq.setApiKey(token);
            Call<GetAnalysisListResponse> contactUsResponseCall = apiService.getAnalysisList(analysisTypeReq);
            contactUsResponseCall.enqueue(new Callback<GetAnalysisListResponse>() {
                @Override
                public void onResponse(Call<GetAnalysisListResponse> call, Response<GetAnalysisListResponse> response) {
                    if (response.body() != null) {
                        mResponseListener.responseData(new Gson().toJson(response.body()));
                    }
                }

                @Override
                public void onFailure(Call<GetAnalysisListResponse> call, Throwable t) {
                    mErrorListener.errorData(t);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

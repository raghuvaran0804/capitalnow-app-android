package com.capitalnowapp.mobile.util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    URL urlObj = null;
    HttpURLConnection con = null;
    String responseObject = null;

    private final String USER_AGENT = "Mozilla/5.0";
    private final String ACCEPT_LANGUAGE = "en-US,en;q=0.5";
    private final String CONTENT_TYPE = "application/json";
    private final String CONTENT_TYPE_FORM_ENCODE = "application/x-www-form-urlencoded";

    public NetworkManager() {

    }

    /**
     * Method to execute post requests.
     *
     * @param url
     * @param params
     * @return String
     */

    public String makeHttpPostConnection(String url, JSONObject params) {
        try {
            urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
            con.setRequestProperty("Content-Type", CONTENT_TYPE);

            // Send post request
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params.toString());
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            printDataInLog("POST Request : ", "URL : " + url);
            printDataInLog("POST Request : ", "Parameters : " + params);
            printDataInLog("POST Request : ", "Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                responseObject = response.toString();

                //JSONObject jsonObject = new JSONObject(responseObject);

            } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                responseObject = "timeout";
            } else {
                responseObject = "failure";
            }

            // print result
            printDataInLog("POST Response : ", responseObject);

        } catch (Exception e) {
            responseObject = "error";
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return responseObject;
    }


    /**
     * Method to execute GET requests.
     *
     * @param url
     * @return String
     */
    public String makeHttpGetConnection(String url) {
        try {
            urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            // add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

            int responseCode = con.getResponseCode();
            printDataInLog("GET Request : ", "URL : " + url);
            printDataInLog("GET Request : ", "Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseObject = response.toString();

                //JSONObject jsonObject = new JSONObject(responseObject);

            } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                responseObject = "timeout";
            } else {
                responseObject = "failure";
            }

            // print result
            printDataInLog("GET Response : ", responseObject);

        } catch (Exception e) {
            // TODO: handle exception
            responseObject = "error";
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return responseObject;
    }

    public void printDataInLog(String tag, String message) {
        Log.d(tag, message);
    }

    /**
     * Method to execute post requests with url encoded data as input params.
     *
     * @param url
     * @param params
     * @return String
     */

    public String makeHttpPostConnectionWithURLEncodeContentType(String url, HashMap<String, String> params) {
        try {
            urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();

            // add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            // con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);
            con.setRequestProperty("Content-Type", CONTENT_TYPE_FORM_ENCODE);

            // Send post request
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(getPostDataString(params));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            printDataInLog("POST Request : ", "URL : " + url);
            printDataInLog("POST Request : ", "Parameters : " + params);
            printDataInLog("POST Request : ", "Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                responseObject = response.toString();

                //JSONObject jsonObject = new JSONObject(responseObject);

            } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                responseObject = "timeout";
            } else {
                responseObject = "failure";
            }

            // print result
            printDataInLog("POST Response : ", responseObject);

        } catch (Exception e) {
            responseObject = "error";
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return responseObject;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String makeHttpGetConnectionWithHeaders(String url, Map<String, String> headers) {
        try {
            urlObj = new URL(url);
            con = (HttpURLConnection) urlObj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            // add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", ACCEPT_LANGUAGE);

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                con.setRequestProperty(entry.getKey(), entry.getValue());
            }

            int responseCode = con.getResponseCode();
            printDataInLog("GET Request : ", "URL : " + url);
            printDataInLog("GET Request : ", "Headers : " + headers);
            printDataInLog("GET Request : ", "Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                responseObject = response.toString();

                //JSONObject jsonObject = new JSONObject(responseObject);

            } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT) {
                responseObject = "timeout";
            } else {
                responseObject = "failure";
            }

            // print result
            printDataInLog("GET Response : ", responseObject);

        } catch (Exception e) {
            // TODO: handle exception
            responseObject = "error";
            e.printStackTrace();
        } finally {
            con.disconnect();
        }

        return responseObject;
    }
}



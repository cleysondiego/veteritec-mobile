package br.com.veteritec.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ApiRequest {
    public static final String BASE_URL = "https://veteritec.herokuapp.com";

    public static final String URL_EMPLOYEES = BASE_URL + "/users";
    public static final String URL_CLINICS = BASE_URL + "/clinics";
    public static final String URL_SESSIONS = BASE_URL + "/sessions";
    public static final String URL_CUSTOMERS = BASE_URL + "/customers";
    public static final String URL_PETS = BASE_URL + "/pets";
    public static final String URL_VACCINES = BASE_URL + "/vaccines";

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CLINIC_ID = "clinicid";
    public static final String AUTHORIZATION = "Authorization";

    public interface OnResponse {
        void onResponse(int statusCode, byte[] response);

        void onFailure(int statusCode);
    }

    public void get(String url, HashMap<String, String> headers, String requestParams, OnResponse callback) {
        request(GET, url, headers, requestParams, callback);
    }

    public void post(String url, HashMap<String, String> headers, String requestParams, OnResponse callback) {
        request(POST, url, headers, requestParams, callback);
    }

    public void put(String url, HashMap<String, String> headers, String requestParams, OnResponse callback) {
        request(PUT, url, headers, requestParams, callback);
    }

    public void delete(String url, HashMap<String, String> headers, String requestParams, OnResponse callback) {
        request(DELETE, url, headers, requestParams, callback);
    }

    private void request(String requestMethod, String url, HashMap<String, String> headers, String requestParams, OnResponse callback) {
        HttpsURLConnection connection = null;

        try {
            connection = getDefaultHttpsRequest(requestMethod, url);

            if (headers != null) {
                List<String> headerKeys = new ArrayList<>(headers.keySet());

                for (String header : headerKeys) {
                    connection.setRequestProperty(header, headers.get(header));
                }
            }

            if (requestMethod.equals(GET) || requestMethod.equals(DELETE)) {
                connection.connect();
            }

            if (requestMethod.equals(POST) || requestMethod.equals(PUT)) {
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(requestParams != null ? requestParams : "");
                outputStream.flush();
                outputStream.close();
            }

            if (connection.getResponseCode() >= 200 && connection.getResponseCode() < 300) {
                callback.onResponse(connection.getResponseCode(), readStream(connection.getInputStream()));
            } else {
                callback.onFailure(connection.getResponseCode());
            }
        } catch (IOException | NullPointerException e) {
            callback.onFailure(HttpsURLConnection.HTTP_INTERNAL_ERROR);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private byte[] readStream(InputStream stream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            int data = stream.read();
            while (data != -1) {
                outputStream.write(data);
                data = stream.read();
            }
        } catch (IOException e) {
            return new byte[0];
        }

        return outputStream.toByteArray();
    }

    private HttpsURLConnection getDefaultHttpsRequest(String requestType, String url) throws IOException {
        URL eventUrl = new URL(url);

        HttpsURLConnection connection = (HttpsURLConnection) eventUrl.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(10000);
        connection.setRequestMethod(requestType);

        return connection;
    }
}
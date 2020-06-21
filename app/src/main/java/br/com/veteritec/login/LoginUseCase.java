package br.com.veteritec.login;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;
import br.com.veteritec.utils.SharedPreferencesUtils;

public class LoginUseCase extends UseCaseAbstract {
    public interface OnLoginCallback {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private LoginUseCase.OnLoginCallback callback;

    private Context context;
    private ApiRequest apiRequest;
    private LoginRequestStructure loginRequestStructure;
    private String clinicId;

    public LoginUseCase(Executor executor,
                        Context context,
                        ApiRequest apiRequest,
                        LoginRequestStructure loginRequestStructure,
                        String clinicId) {
        super(executor);

        this.context = context;
        this.apiRequest = apiRequest;
        this.loginRequestStructure = loginRequestStructure;
        this.clinicId = clinicId;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");
            headers.put(ApiRequest.CLINIC_ID, clinicId);

            String requestParams = loginRequestStructure.getStructureString();

            apiRequest.post(ApiRequest.URL_SESSIONS, headers, requestParams, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LoginResponseStructure loginResponseStructure = new LoginResponseStructure().fromJson(new JSONObject(new String(response)));
                                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils();
                                sharedPreferencesUtils.setLoggedIn(context, true, loginResponseStructure.getUserConfig().getName(), loginResponseStructure.getToken(), clinicId);
                                callback.onSuccess();
                            } catch (JSONException ignored) {
                                callback.onFailure(101);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(final int statusCode) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(statusCode);
                        }
                    });
                }
            });
        } catch (Exception ignored) {
            callback.onFailure(100);
        }
    }

    public void setCallback(LoginUseCase.OnLoginCallback callback) {
        this.callback = callback;
    }
}

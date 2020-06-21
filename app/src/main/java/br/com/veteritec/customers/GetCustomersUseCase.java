package br.com.veteritec.customers;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class GetCustomersUseCase extends UseCaseAbstract {
    public interface OnGetCustomersCallback {
        void onSuccess(GetCustomersResponseStructure getCustomersResponseStructure);

        void onFailure(int statusCode);
    }

    private GetCustomersUseCase.OnGetCustomersCallback callback;

    private ApiRequest apiRequest;
    private String clinicId;
    private String token;

    public GetCustomersUseCase(Executor executor,
                               ApiRequest apiRequest,
                               String clinicId,
                               String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.clinicId = clinicId;
        this.token = token;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CLINIC_ID, clinicId);
            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);;

            apiRequest.get(ApiRequest.URL_CUSTOMERS, headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GetCustomersResponseStructure getCustomersResponseStructure = new GetCustomersResponseStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(getCustomersResponseStructure);
                            } catch (JSONException e) {
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
                            onFailure(statusCode);
                        }
                    });
                }
            });

        } catch (Exception e) {
            callback.onFailure(100);
        }
    }

    public void setCallback(GetCustomersUseCase.OnGetCustomersCallback callback) {
        this.callback = callback;
    }
}

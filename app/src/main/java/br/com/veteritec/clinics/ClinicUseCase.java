package br.com.veteritec.clinics;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class ClinicUseCase extends UseCaseAbstract {

    public interface OnGetClinicCallback {
        void onSuccess(ClinicResponseStructure clinicResponseStructure);

        void onFailure(int statusCode);
    }

    private ClinicUseCase.OnGetClinicCallback callback;

    private ApiRequest apiRequest;

    public ClinicUseCase(Executor executor,
                         ApiRequest apiRequest) {
        super(executor);

        this.apiRequest = apiRequest;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");

            apiRequest.get(ApiRequest.BASE_URL + "/clinics", headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ClinicResponseStructure clinicResponseStructure = new ClinicResponseStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(clinicResponseStructure);
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

    public void setCallback(ClinicUseCase.OnGetClinicCallback callback ) {
        this.callback = callback;
    }
}

package br.com.veteritec.vaccines;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class GetVaccinesUseCase extends UseCaseAbstract {
    public interface OnGetVaccinesCallback {
        void onSuccess(GetVaccinesResponseStructure getVaccinesResponseStructure);

        void onFailure(int statusCode);
    }

    private GetVaccinesUseCase.OnGetVaccinesCallback callback;

    private ApiRequest apiRequest;
    private String clinicId;
    private String token;

    public GetVaccinesUseCase(Executor executor,
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
            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);

            apiRequest.get(ApiRequest.URL_VACCINES, headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GetVaccinesResponseStructure getVaccinesResponseStructure = new GetVaccinesResponseStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(getVaccinesResponseStructure);
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

    public void setCallback(GetVaccinesUseCase.OnGetVaccinesCallback callback) {
        this.callback = callback;
    }
}

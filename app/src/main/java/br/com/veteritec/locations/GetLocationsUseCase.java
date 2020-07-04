package br.com.veteritec.locations;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class GetLocationsUseCase extends UseCaseAbstract {
    public interface OnGetLocationsCallback {
        void onSuccess(LocationsStructure locationsStructure);

        void onFailure(int statusCode);
    }

    private GetLocationsUseCase.OnGetLocationsCallback callback;

    private ApiRequest apiRequest;
    private String clinicId;
    private String token;
    private String petId;

    public GetLocationsUseCase(Executor executor,
                               ApiRequest apiRequest,
                               String clinicId,
                               String token,
                               String petId) {
        super(executor);

        this.apiRequest = apiRequest;
        this.clinicId = clinicId;
        this.token = token;
        this.petId = petId;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");
            headers.put(ApiRequest.CLINIC_ID, clinicId);
            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);

            apiRequest.get(ApiRequest.URL_LOCATIONS + "/" + petId, headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                LocationsStructure locationsStructure = new LocationsStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(locationsStructure);
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

    public void setCallback(OnGetLocationsCallback callback) {
        this.callback = callback;
    }
}

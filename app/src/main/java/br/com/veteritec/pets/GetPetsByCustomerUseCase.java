package br.com.veteritec.pets;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class GetPetsByCustomerUseCase extends UseCaseAbstract {
    public interface OnGetPetsByCustomerCallback {
        void onSuccess(GetPetsResponseStructure getPetsResponseStructure);

        void onFailure(int statusCode);
    }

    private GetPetsByCustomerUseCase.OnGetPetsByCustomerCallback callback;

    private ApiRequest apiRequest;
    private String clinicId;
    private String token;
    private String customerId;

    public GetPetsByCustomerUseCase(Executor executor,
                                    ApiRequest apiRequest,
                                    String clinicId,
                                    String token,
                                    String customerId) {
        super(executor);

        this.apiRequest = apiRequest;
        this.clinicId = clinicId;
        this.token = token;
        this.customerId = customerId;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();

            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);
            headers.put(ApiRequest.CLINIC_ID, clinicId);

            apiRequest.get(ApiRequest.URL_PETS + "/" + customerId, headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GetPetsResponseStructure getPetsResponseStructure = new GetPetsResponseStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(getPetsResponseStructure);
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

    public void setCallback(GetPetsByCustomerUseCase.OnGetPetsByCustomerCallback callback) {
        this.callback = callback;
    }
}

package br.com.veteritec.pets;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class ChangePetUseCase extends UseCaseAbstract {
    public interface OnChangePet {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private ChangePetUseCase.OnChangePet callback;

    private ApiRequest apiRequest;
    private ChangePetRequestStructure changePetRequestStructure;
    private String clinicId;
    private String token;

    public ChangePetUseCase(Executor executor,
                            ApiRequest apiRequest,
                            ChangePetRequestStructure changePetRequestStructure,
                            String clinicId,
                            String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.changePetRequestStructure = changePetRequestStructure;
        this.clinicId = clinicId;
        this.token = token;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");
            headers.put(ApiRequest.CLINIC_ID, clinicId);
            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);

            String requestParams = changePetRequestStructure.getStructureString();

            apiRequest.put(ApiRequest.URL_PETS, headers, requestParams, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess();
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

    public void setCallback(ChangePetUseCase.OnChangePet callback) {
        this.callback = callback;
    }
}

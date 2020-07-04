package br.com.veteritec.pets;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class CreatePetUseCase extends UseCaseAbstract {
    public interface OnCreatePet {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private CreatePetUseCase.OnCreatePet callback;

    private ApiRequest apiRequest;
    private CreatePetRequestStructure createPetRequestStructure;
    private String clinicId;
    private String token;

    public CreatePetUseCase(Executor executor,
                            ApiRequest apiRequest,
                            CreatePetRequestStructure createPetRequestStructure,
                            String clinicId,
                            String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.createPetRequestStructure = createPetRequestStructure;
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

            String requestParams = createPetRequestStructure.getStructureString();

            apiRequest.post(ApiRequest.URL_PETS, headers, requestParams, new ApiRequest.OnResponse() {
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

    public void setCallback(CreatePetUseCase.OnCreatePet callback) {
        this.callback = callback;
    }
}

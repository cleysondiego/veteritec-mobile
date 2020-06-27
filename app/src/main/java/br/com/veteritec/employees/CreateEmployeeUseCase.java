package br.com.veteritec.employees;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class CreateEmployeeUseCase extends UseCaseAbstract {
    public interface OnCreateEmployeeCallback {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private CreateEmployeeUseCase.OnCreateEmployeeCallback callback;

    private ApiRequest apiRequest;
    private CreateEmployeeRequestStructure createEmployeeRequestStructure;
    private String clinicId;

    public CreateEmployeeUseCase(Executor executor,
                                 ApiRequest apiRequest,
                                 CreateEmployeeRequestStructure createEmployeeRequestStructure,
                                 String clinicId) {
        super(executor);

        this.apiRequest = apiRequest;
        this.createEmployeeRequestStructure = createEmployeeRequestStructure;
        this.clinicId = clinicId;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");
            headers.put(ApiRequest.CLINIC_ID, clinicId);

            String requestParams = createEmployeeRequestStructure.getStructureString();

            apiRequest.post(ApiRequest.URL_EMPLOYEES, headers, requestParams, new ApiRequest.OnResponse() {
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

    public void setCallback(OnCreateEmployeeCallback callback) {
        this.callback = callback;
    }
}

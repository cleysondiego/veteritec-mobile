package br.com.veteritec.customers;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class CreateCustomerUseCase extends UseCaseAbstract {
    public interface OnCreateCustomer {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private CreateCustomerUseCase.OnCreateCustomer callback;

    private ApiRequest apiRequest;
    private CreateCustomerRequestStructure createCustomerRequestStructure;
    private String clinicId;
    private String token;

    public CreateCustomerUseCase(Executor executor,
                                 ApiRequest apiRequest,
                                 CreateCustomerRequestStructure createCustomerRequestStructure,
                                 String clinicId,
                                 String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.createCustomerRequestStructure = createCustomerRequestStructure;
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

            String requestParams = createCustomerRequestStructure.getStructureString();

            apiRequest.post(ApiRequest.URL_CUSTOMERS, headers, requestParams, new ApiRequest.OnResponse() {
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
        } catch (Exception e) {
            callback.onFailure(100);
        }
    }

    public void setCallback(CreateCustomerUseCase.OnCreateCustomer callback) {
        this.callback = callback;
    }
}

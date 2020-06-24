package br.com.veteritec.customers;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class ChangeCustomerUseCase extends UseCaseAbstract {
    public interface OnChangeCustomer {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private ChangeCustomerUseCase.OnChangeCustomer callback;

    private ApiRequest apiRequest;
    private CustomerRequestStructure customerRequestStructure;
    private String clinicId;
    private String token;

    public ChangeCustomerUseCase(Executor executor,
                                 ApiRequest apiRequest,
                                 CustomerRequestStructure customerRequestStructure,
                                 String clinicId,
                                 String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.customerRequestStructure = customerRequestStructure;
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

            String requestParams = customerRequestStructure.getStructureString();

            apiRequest.put(ApiRequest.URL_CUSTOMERS, headers, requestParams, new ApiRequest.OnResponse() {
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

    public void setCallback(ChangeCustomerUseCase.OnChangeCustomer callback) {
        this.callback = callback;
    }
}

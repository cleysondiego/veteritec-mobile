package br.com.veteritec.customers;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.DeleteStructure;
import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class DeleteCustomerUseCase extends UseCaseAbstract {
    public interface OnDeleteCustomerCallback {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private DeleteCustomerUseCase.OnDeleteCustomerCallback callback;

    private ApiRequest apiRequest;
    private DeleteStructure deleteStructure;
    private String customerId;
    private String clinicId;
    private String token;

    public DeleteCustomerUseCase(Executor executor,
                                 ApiRequest apiRequest,
                                 DeleteStructure deleteStructure,
                                 String customerId,
                                 String clinicId,
                                 String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.deleteStructure = deleteStructure;
        this.customerId = customerId;
        this.clinicId = clinicId;
        this.token = token;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CLINIC_ID, clinicId);
            headers.put(ApiRequest.AUTHORIZATION, "Bearer " + token);
            headers.put(ApiRequest.CONTENT_TYPE, "application/json");

            apiRequest.delete(ApiRequest.URL_CUSTOMERS + "/" + customerId, headers, deleteStructure.getStructureString(), new ApiRequest.OnResponse() {
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

    public void setCallback(DeleteCustomerUseCase.OnDeleteCustomerCallback callback) {
        this.callback = callback;
    }
}

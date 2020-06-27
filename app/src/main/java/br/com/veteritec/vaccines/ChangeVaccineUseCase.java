package br.com.veteritec.vaccines;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class ChangeVaccineUseCase extends UseCaseAbstract {
    public interface OnChangeVaccine {
        void onSuccess();

        void onFailure(int statusCode);
    }

    private ChangeVaccineUseCase.OnChangeVaccine callback;

    private ApiRequest apiRequest;
    private ChangeVaccineRequestStructure changeVaccineRequestStructure;
    private String clinicId;
    private String token;

    public ChangeVaccineUseCase(Executor executor,
                                ApiRequest apiRequest,
                                ChangeVaccineRequestStructure changeVaccineRequestStructure,
                                String clinicId,
                                String token) {
        super(executor);

        this.apiRequest = apiRequest;
        this.changeVaccineRequestStructure = changeVaccineRequestStructure;
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

            String requestParams = changeVaccineRequestStructure.getStructureString();

            apiRequest.put(ApiRequest.URL_VACCINES, headers, requestParams, new ApiRequest.OnResponse() {
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

    public void setCallback(ChangeVaccineUseCase.OnChangeVaccine callback) {
        this.callback = callback;
    }
}

package br.com.veteritec.employees;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import br.com.veteritec.usecase.Executor;
import br.com.veteritec.usecase.UseCaseAbstract;
import br.com.veteritec.utils.ApiRequest;

public class GetEmployeesUseCase extends UseCaseAbstract {
    public interface OnGetEmployeesCallback {
        void onSuccess(GetEmployeesResponseStructure getEmployeesResponseStructure);

        void onFailure(int statusCode);
    }

    private GetEmployeesUseCase.OnGetEmployeesCallback callback;

    private ApiRequest apiRequest;
    private String clinicId;

    public GetEmployeesUseCase(Executor executor,
                               ApiRequest apiRequest,
                               String clinicId) {
        super(executor);

        this.apiRequest = apiRequest;
        this.clinicId = clinicId;
    }

    @Override
    public void run() {
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put(ApiRequest.CLINIC_ID, clinicId);

            apiRequest.get(ApiRequest.URL_EMPLOYEES, headers, null, new ApiRequest.OnResponse() {
                @Override
                public void onResponse(int statusCode, final byte[] response) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                GetEmployeesResponseStructure getEmployeesResponseStructure = new GetEmployeesResponseStructure().fromJson(new JSONObject(new String(response)));
                                callback.onSuccess(getEmployeesResponseStructure);
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
                            onFailure(statusCode);
                        }
                    });
                }
            });
        } catch (Exception ignored) {
            callback.onFailure(100);
        }
    }

    public void setCallback(GetEmployeesUseCase.OnGetEmployeesCallback callback) {
        this.callback = callback;
    }
}

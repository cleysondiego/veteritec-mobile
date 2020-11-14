package br.com.veteritec.usecase;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DeleteStructure {
    @SerializedName("message")
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStructureString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

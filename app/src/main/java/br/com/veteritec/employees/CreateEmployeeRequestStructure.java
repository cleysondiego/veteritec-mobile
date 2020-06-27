package br.com.veteritec.employees;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CreateEmployeeRequestStructure {
    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStructureString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

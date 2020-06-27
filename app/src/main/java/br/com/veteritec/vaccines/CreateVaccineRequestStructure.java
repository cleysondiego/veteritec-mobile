package br.com.veteritec.vaccines;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CreateVaccineRequestStructure {
    @SerializedName("date")
    private String date;

    @SerializedName("hour")
    private String hour;

    @SerializedName("description")
    private String description;

    @SerializedName("veterinary")
    private String veterinary;

    @SerializedName("customer")
    private String customer;

    @SerializedName("pet")
    private String pet;

    public void setDate(String date) {
        this.date = date;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVeterinary(String veterinary) {
        this.veterinary = veterinary;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public String getStructureString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

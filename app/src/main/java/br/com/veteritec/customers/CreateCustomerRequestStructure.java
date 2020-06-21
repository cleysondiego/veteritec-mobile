package br.com.veteritec.customers;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CreateCustomerRequestStructure {
    @SerializedName("cpf")
    private String cpf;

    @SerializedName("name")
    private String name;

    @SerializedName("zipcode")
    private String zipCode;

    @SerializedName("street")
    private String street;

    @SerializedName("number")
    private String number;

    @SerializedName("neighborhood")
    private String neighborhood;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("cellNumber")
    private String cellNumber;

    @SerializedName("email")
    private String email;

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStructureString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

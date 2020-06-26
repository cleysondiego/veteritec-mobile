package br.com.veteritec.pets;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class ChangePetRequestStructure {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("birth")
    private String birth;

    @SerializedName("species")
    private String species;

    @SerializedName("breed")
    private String breed;

    @SerializedName("size")
    private String size;

    @SerializedName("weight")
    private String weight;

    @SerializedName("comments")
    private String comments;

    @SerializedName("customer")
    private String customer;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getStructureString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
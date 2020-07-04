package br.com.veteritec.pets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetPetsResponseStructure {
    private List<Pet> pets = new ArrayList<>();

    public List<Pet> getPets() {
        return pets;
    }

    public GetPetsResponseStructure fromJson(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("pets");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                pets.add(new Pet().fromJson(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Pet implements Serializable {
        private String id;
        private String name;
        private String birth;
        private String species;
        private String breed;
        private String size;
        private String weight;
        private String comments;
        private String customer;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getBirth() {
            return birth;
        }

        public String getSpecies() {
            return species;
        }

        public String getBreed() {
            return breed;
        }

        public String getSize() {
            return size;
        }

        public String getWeight() {
            return weight;
        }

        public String getComments() {
            return comments;
        }

        public String getCustomer() {
            return customer;
        }

        public Pet fromJson(JSONObject jsonObject) {
            try {
                this.id = jsonObject.getString("_id");
                this.name = jsonObject.getString("name");
                this.birth = jsonObject.getString("birth");
                this.species = jsonObject.getString("species");
                this.breed = jsonObject.getString("breed");
                this.size = jsonObject.getString("size");
                this.weight = jsonObject.getString("weight");
                this.comments = jsonObject.getString("comments");
                this.customer = jsonObject.getString("customer");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}

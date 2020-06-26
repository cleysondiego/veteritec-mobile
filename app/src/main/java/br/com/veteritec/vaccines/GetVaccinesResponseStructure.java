package br.com.veteritec.vaccines;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetVaccinesResponseStructure {
    private List<Vaccine> vaccineList = new ArrayList<>();

    public List<Vaccine> getVaccineList() {
        return vaccineList;
    }

    public GetVaccinesResponseStructure fromJson(JSONObject jsonObject) {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("vaccines");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                vaccineList.add(new Vaccine().fromJson(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static class Vaccine implements Serializable {
        private String id;
        private String date;
        private String hour;
        private String description;
        private String displayName;
        private String veterinary;
        private String customer;
        private String pet;

        public String getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public String getHour() {
            return hour;
        }

        public String getDescription() {
            return description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getVeterinary() {
            return veterinary;
        }

        public String getCustomer() {
            return customer;
        }

        public String getPet() {
            return pet;
        }

        public Vaccine fromJson(JSONObject jsonObject) {
            try {
                this.id = jsonObject.getString("_id");
                this.date = jsonObject.getString("date");
                this.hour = jsonObject.getString("hour");
                this.description = jsonObject.getString("description");
                this.displayName = jsonObject.getString("displayName");
                this.veterinary = jsonObject.getString("veterinary");
                this.customer = jsonObject.getString("customer");
                this.pet = jsonObject.getString("pet");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }
}
